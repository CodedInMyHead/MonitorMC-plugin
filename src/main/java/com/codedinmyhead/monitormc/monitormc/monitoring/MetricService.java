package com.codedinmyhead.monitormc.monitormc.monitoring;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MetricService {

    final Logger logger = Logger.getLogger("Metrics");

    final static String EMPTY = "";

    final static String NAME_WARNING = "Attempted to increase player-specific metric but no UUID was supplied. Skipping..";

    private static MetricService INSTANCE;
    private static PrometheusMeterRegistry registry;

    private static final Map<String, Map<String, Meter>> playerSpecificMap = new HashMap<>();
    private static final Map<String, Meter> globalMetricMap = new HashMap<>();

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
        metrics.stream().filter(IMonitoringMetric::getGlobal).forEach(this::initializeMetric);
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
            if (name == null || name.equals(EMPTY)) {
                logger.warning(NAME_WARNING + metric);
                return;
            }
            playerSpecificMap.computeIfAbsent(name, k -> new HashMap<>());
            playerSpecificMap.get(name).computeIfAbsent(metric.getKey(), k -> registry.counter(metric.getKey(), metric.getTags().and("player", name)));

            ((Counter) playerSpecificMap.get(name).get(metric.getKey())).increment(count);
        }


    }

}
