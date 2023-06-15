package com.codedinmyhead.monitormc.monitormc.monitoring;

import io.micrometer.core.instrument.Tags;

public interface IMonitoringMetric {

    String getKey();
    Class<?> getMetricType();
    Tags getTags();

    /*
    default String getName() {
        if(this.getClass().isEnum()) {
            return ((Enum) this).name();
        }
        return null;
    }

     */

}
