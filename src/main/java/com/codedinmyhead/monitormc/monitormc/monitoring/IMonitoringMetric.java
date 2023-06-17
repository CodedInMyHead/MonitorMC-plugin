package com.codedinmyhead.monitormc.monitormc.monitoring;

import io.micrometer.core.instrument.Tags;

public interface IMonitoringMetric {

    String getKey();
    Class<?> getMetricType();
    Tags getTags();

    boolean getGlobal();

}
