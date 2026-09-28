package com.davidjmacdonald.improved_anvils;

import com.mojang.serialization.Dynamic;

/** Normalize pre-registry game rules before the registry codec rejects the old key. */
public final class LegacyGameRuleMigration {
    public static final String LEGACY = "repairNetheriteWithDiamonds";
    public static final String CANONICAL = "improved_anvils:repair_netherite_with_diamonds";

    private LegacyGameRuleMigration() {}

    public static <T> Dynamic<T> migrate(Dynamic<T> rules) {
        var legacy = rules.get(LEGACY).result();
        if (legacy.isEmpty()) return rules;
        if (rules.get(CANONICAL).result().isEmpty()) {
            // Old GameRules stored boolean values as strings. Also accept native booleans.
            var value = legacy.get();
            boolean enabled = value.asString().result().map(Boolean::parseBoolean)
                .orElseGet(() -> value.asBoolean(true));
            rules = rules.set(CANONICAL, rules.createBoolean(enabled));
        }
        return rules.remove(LEGACY);
    }
}
