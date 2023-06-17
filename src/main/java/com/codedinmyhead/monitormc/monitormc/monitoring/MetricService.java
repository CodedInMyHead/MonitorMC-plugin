package com.codedinmyhead.monitormc.monitormc.monitoring;

import com.codedinmyhead.monitormc.monitormc.MonitorMC;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MetricService {

    final Logger logger = Logger.getLogger("Metrics");

    final static String NAME_WARNING = "Attempted to increase player-specific metric but no UUID was supplied. Skipping.."

    private static MetricService INSTANCE;
    private static PrometheusMeterRegistry registry;

    private static Map<String, Map<String, Meter>> playerSpecificMap = new HashMap<>();
    private static Map<String, Meter> globalMetricMap = new HashMap<>();

    private MetricService() {
        registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        try {
            final HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/metrics", httpExchange -> {
                final String response = registry.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });

            new Thread(server::start).start();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static MetricService getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new MetricService();
        }
        return INSTANCE;
    }

    public void initializeMetrics(List<IMonitoringMetric> metrics) {
        metrics.stream().filter(metric -> metric.getGlobal()).forEach(this::initializeMetric);
    }

    private void initializeMetric(IMonitoringMetric metric) {
        globalMetricMap.put(metric.getKey(), registry.counter(metric.getKey(), metric.getTags()));
    }

    public void incrementCounter(IMonitoringMetric metric) {
        incrementCounter(metric, 1);
    }

    public void incrementCounter(IMonitoringMetric metric, String name) {
        incrementCounter(metric, 1, name);
    }

    public void incrementCounter(IMonitoringMetric metric, int count) {
       incrementCounter(metric, count, null);
    }

    public void incrementCounter(IMonitoringMetric metric, int count, String name) {

        if (metric.getGlobal()) {
            ((Counter) globalMetricMap.get(metric.getKey())).increment(count);
        } else {
            if (name == null || name == "") {
                logger.warning(NAME_WARNING + metric);
            }
            if (playerSpecificMap.get(name) == null) {
                playerSpecificMap.put(name, new HashMap<>());
            }
            if (playerSpecificMap.get(name).get(metric.getKey()) == null) {
                playerSpecificMap.get(name).put(metric.getKey(), registry.counter(metric.getKey(), metric.getTags().and("player", name)));
            }
            ((Counter) playerSpecificMap.get(name).get(metric.getKey())).increment(count);
        }


    }

}
