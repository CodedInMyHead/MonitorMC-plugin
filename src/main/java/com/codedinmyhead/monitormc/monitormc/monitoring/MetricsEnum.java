package com.codedinmyhead.monitormc.monitormc.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import lombok.Getter;
import org.bukkit.Material;
@Getter
public enum MetricsEnum implements IMonitoringMetric {

    ARROW_HIT("arrowhit", Counter.class, Tags.empty(), false, true, "Arrows hit", new String[]{"Count of Arrows hit"}, Material.ARROW),
    ARROW_MISS("arrowmiss", Counter.class, Tags.empty(), false, true, "Arrows missed",  new String[]{"Count of Arrows missed"}, Material.ARROW),
    TIMES_SLEPT("times_slept", Counter.class, Tags.empty(), false, true, "Times Slept",  new String[]{"How many times a player has slept"}, Material.RED_BED),
    HOSTILE_MOBS_KILLED("hostile_mobs_killed",Counter.class, Tags.empty(), false, true, "Enemies Mobs Killed", new String[] {"How many hostile mobs","or players killed"}, Material.ZOMBIE_HEAD ),
    // GOLD, IRON, COPPER-Blocks, Comparator, OAK_SIGN and SUNFLOWER are not valid and will bug the leaderboard out
    DIAMONDS_MINED("diamonds_mined", Counter.class, Tags.empty(), false, true, "Diamond Ore mined", new String[]{"How many Diamond Ores a player has mined"}, Material.DIAMOND_ORE),
    NUMBERS_SAID_COUNT("numbers_said", Counter.class, Tags.empty(), false, true, "Sum of numbers said in Chat", new String[]{"Sums up all of the numbers", "that a player said in chat"}, Material.COMMAND_BLOCK),
    DUMMY3("times_slept", Counter.class, Tags.empty(), false, true, "Times Slept",  new String[]{"How many times a player has slept"}, Material.RED_BED),
    DUMMY4("times_slept", Counter.class, Tags.empty(), false, true, "Times Slept",  new String[]{"How many times a player has slept"}, Material.RED_BED),
    DUMMY5("times_slept", Counter.class, Tags.empty(), false, true, "Times Slept",  new String[]{"How many times a player has slept"}, Material.RED_BED),
    DUMMY6("times_slept", Counter.class, Tags.empty(), false, true, "Times Slept",  new String[]{"How many times a player has slept"}, Material.RED_BED),
    DUMMY7("times_slept", Counter.class, Tags.empty(), false, true, "Times Slept",  new String[]{"How many times a player has slept"}, Material.RED_BED),
    DUMMY8("times_slept", Counter.class, Tags.empty(), false, true, "Times Slept",  new String[]{"How many times a player has slept"}, Material.RED_BED);

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
