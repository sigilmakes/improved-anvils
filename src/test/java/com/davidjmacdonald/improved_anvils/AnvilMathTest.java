package com.davidjmacdonald.improved_anvils;

public final class AnvilMathTest {
    private static void equal(Object expected, Object actual) {
        if (!expected.equals(actual)) throw new AssertionError("expected " + expected + ", got " + actual);
    }

    @org.junit.jupiter.api.Test
    void regressionChecks() {
        main(new String[0]);
    }

    public static void main(String[] args) {
        equal(new AnvilMath.Repair(0, 1, 25), AnvilMath.repair(100, 100, 1, 1, false, false, false));
        equal(new AnvilMath.Repair(50, 1, 13), AnvilMath.repair(100, 100, 1, .5, false, false, false));
        equal(new AnvilMath.Repair(200, 1, 25), AnvilMath.repair(300, 300, 1, 1.0 / 3, false, false, false));
        equal(new AnvilMath.Repair(75, 1, 7), AnvilMath.repair(100, 100, 1, .25, false, false, false));
        // Netherite diamond repair is half rate; ingot repair retains standard rate.
        equal(new AnvilMath.Repair(0, 2, 508), AnvilMath.repair(2031, 2031, 2, 1, true, false, false));
        equal(new AnvilMath.Repair(1016, 1, 254), AnvilMath.repair(2031, 2031, 1, 1, true, false, false));
        equal(new AnvilMath.Repair(0, 1, 508), AnvilMath.repair(2031, 2031, 1, 1, false, false, false));
        // Both nugget types use the same ninth-rate arithmetic, including fractional durability.
        equal(new AnvilMath.Repair(0, 9, 25), AnvilMath.repair(100, 100, 9, 1, false, true, false));
        equal(new AnvilMath.Repair(89, 1, 3), AnvilMath.repair(100, 100, 1, 1, false, true, false));
        equal(new AnvilMath.Repair(0, 1, 2), AnvilMath.repair(100, 5, 64, 1, false, true, false));
        equal(new AnvilMath.Repair(0, 0, 0), AnvilMath.repair(100, 0, 64, 1, false, false, false));
        equal(4, AnvilMath.repairCost(13, false));
        equal(13, AnvilMath.repairCost(13, true));
        equal(0, AnvilMath.totalExperience(0, 0, 7));
        equal(352, AnvilMath.totalExperience(16, 0, 42));
        equal(394, AnvilMath.totalExperience(17, 0, 47));
        equal(1507, AnvilMath.totalExperience(31, 0, 121));
        equal(1628, AnvilMath.totalExperience(32, 0, 130));
        equal(360, AnvilMath.totalExperience(16, .2f, 42));
        for (String legacy : new String[]{"true", "false"}) {
            var data = new com.google.gson.JsonObject();
            data.addProperty(LegacyGameRuleMigration.LEGACY, legacy);
            var migrated = LegacyGameRuleMigration.migrate(new com.mojang.serialization.Dynamic<>(
                com.mojang.serialization.JsonOps.INSTANCE, data));
            equal(Boolean.parseBoolean(legacy), migrated.get(LegacyGameRuleMigration.CANONICAL).asBoolean(true));
            equal(true, migrated.get(LegacyGameRuleMigration.LEGACY).result().isEmpty());
            equal(migrated, LegacyGameRuleMigration.migrate(migrated));
        }
        var both = new com.google.gson.JsonObject();
        both.addProperty(LegacyGameRuleMigration.LEGACY, "true");
        both.addProperty(LegacyGameRuleMigration.CANONICAL, false);
        equal(false, LegacyGameRuleMigration.migrate(new com.mojang.serialization.Dynamic<>(
            com.mojang.serialization.JsonOps.INSTANCE, both))
            .get(LegacyGameRuleMigration.CANONICAL).asBoolean(true));
        System.out.println("AnvilMath regression checks passed");
    }
}
