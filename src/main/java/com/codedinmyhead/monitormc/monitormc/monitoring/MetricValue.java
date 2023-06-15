package com.codedinmyhead.monitormc.monitormc.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

public enum MetricValue implements IMonitoringMetric {

    ARROW_HIT("minecraft.arrowhit", Counter.class, Tags.of(Tag.of("player", "baum")));
    private final String key;
    private final Class<?> metricType;
    private final Tags tags;

    MetricValue(String key, Class<?> metricType, Tags tags) {
        this.key = key;
        this.metricType = metricType;
        this.tags = tags;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Class<?> getMetricType() {
        return metricType;
    }

    @Override
    public Tags getTags() {
        return tags;
    }
}
