package com.codedinmyhead.monitormc.monitormc.monitoring;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricService {

    private static MetricService INSTANCE;
    private static PrometheusMeterRegistry registry;

    private static Map<String, Object> metricMap = new HashMap<>();

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
        metrics.forEach(this::initializeMetric);
    }

    private void initializeMetric(IMonitoringMetric metric) {
        metricMap.put(metric.getKey(), registry.counter(metric.getKey(), metric.getTags()));
    }

    public void incrementCounter(IMonitoringMetric metric) {
        incrementCounter(metric, 1);
    }

    public void incrementCounter(IMonitoringMetric metric, int count) {
        ((Counter) metricMap.get(metric.getKey())).increment(count);
    }

}
