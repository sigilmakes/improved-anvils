package com.davidjmacdonald.improved_anvils;

/** Pure XP and repair arithmetic shared by the menu and regression tests. */
public final class AnvilMath {
    private AnvilMath() {}

    public record Repair(int damage, int materials, int cost) {}

    public static Repair repair(int maxDamage, int damage, int count, double fraction,
                                boolean diamondNetherite, boolean nugget, boolean infinity) {
        double perMaterial = fraction * maxDamage;
        if (diamondNetherite) perMaterial /= 2;
        if (nugget) perMaterial /= 9;
        if (perMaterial <= 0 || damage <= 0 || count <= 0) return new Repair(damage, 0, 0);
        double restored = Math.min(damage, count * perMaterial);
        int newHealth = (int) (maxDamage - damage + restored);
        return new Repair(maxDamage - newHealth, (int) Math.ceil(restored / perMaterial),
            repairCost(restored, infinity));
    }

    public static int repairCost(double restored, boolean infinity) {
        return (int) Math.ceil((infinity ? 4 : 1) * restored / 4.0);
    }

    public static int totalExperience(int level, float progress, int nextLevelCost) {
        double square = (double) level * level;
        double points = nextLevelCost * progress;
        if (level <= 16) return (int) (points + square + 6 * level);
        if (level <= 31) return (int) (points + 2.5 * square - 40.5 * level + 360.0);
        return (int) (points + 4.5 * square - 162.5 * level + 2220.0);
    }
}
