package com.codedinmyhead.monitormc.monitormc.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;

public enum MetricsEnum implements IMonitoringMetric {

    ARROW_HIT("minecraft.arrowhit", Counter.class, null, true),
    TIMES_SLEPT("times_slept", Counter.class, null, false);
    private final String key;
    private final Class<?> metricType;
    private final Tags tags;
    private final boolean global;

    MetricsEnum(String key, Class<?> metricType, Tags tags, boolean global) {
        this.key = key;
        this.metricType = metricType;
        this.tags = tags;
        this.global = global;
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

    @Override
    public boolean getGlobal() {
        return global;
    }
}
