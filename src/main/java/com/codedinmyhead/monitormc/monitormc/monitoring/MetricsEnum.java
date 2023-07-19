package com.codedinmyhead.monitormc.monitormc.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import lombok.Getter;
import org.bukkit.Material;
@Getter
public enum MetricsEnum implements IMonitoringMetric {

    ARROW_HIT("arrowhit", Counter.class, Tags.empty(), false, true, "Arrows hit", new String[]{"Count of Arrows hit"}, Material.ARROW),
    ARROW_MISS("arrowmiss", Counter.class, Tags.empty(), false, true, "Arrows missed",  new String[]{"Count of Arrows missed"}, Material.ARROW),
    TIMES_SLEPT("times_slept", Counter.class, Tags.empty(), false, true, "Times Slept",  new String[]{"How many times a player has slept"}, Material.RED_BED);
    private final String key;
    private final Class<?> metricType;
    private final Tags tags;
    private final boolean global;
    private final boolean leaderboard;
    private final String name;
    private final String[] lore;
    private final Material material;

    MetricsEnum(String key, Class<?> metricType, Tags tags, boolean global, boolean leaderboard, String name, String[] lore, Material material) {
        this.key = key;
        this.metricType = metricType;
        this.tags = tags;
        this.global = global;
        this.leaderboard = leaderboard;
        this.name = name;
        this.lore = lore;
        this.material = material;
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
