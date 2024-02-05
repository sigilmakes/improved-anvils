package com.davidjmacdonald.improved_anvils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;

import java.util.HashMap;
import java.util.Map;

public class ImprovedEnchants {
    private static final Map<Enchantment, Integer> COSTS = new HashMap<>();

    static {
        // 1 tier
        COSTS.put(Enchantments.MENDING, 20);
        COSTS.put(Enchantments.INFINITY, 15);
        COSTS.put(Enchantments.CHANNELING, 12);
        COSTS.put(Enchantments.FLAME, 10);
        COSTS.put(Enchantments.MULTISHOT, 10);
        COSTS.put(Enchantments.SILK_TOUCH, 10);
        COSTS.put(Enchantments.AQUA_AFFINITY, 6);
        COSTS.put(Enchantments.VANISHING_CURSE, 3);
        COSTS.put(Enchantments.BINDING_CURSE, 3);

        // 2 tiers
        COSTS.put(Enchantments.FIRE_ASPECT, 5);
        COSTS.put(Enchantments.KNOCKBACK, 4);
        COSTS.put(Enchantments.PUNCH, 4);
        COSTS.put(Enchantments.FROST_WALKER, 4);

        // 3 tiers
        COSTS.put(Enchantments.SOUL_SPEED, 5);
        COSTS.put(Enchantments.SWIFT_SNEAK, 5);
        COSTS.put(Enchantments.UNBREAKING, 4);
        COSTS.put(Enchantments.LOOTING, 4);
        COSTS.put(Enchantments.LOYALTY, 4);
        COSTS.put(Enchantments.RIPTIDE, 4);
        COSTS.put(Enchantments.RESPIRATION, 3);
        COSTS.put(Enchantments.THORNS, 3);
        COSTS.put(Enchantments.DEPTH_STRIDER, 3);
        COSTS.put(Enchantments.LUCK_OF_THE_SEA, 3);
        COSTS.put(Enchantments.LURE, 3);
        COSTS.put(Enchantments.FORTUNE, 3);
        COSTS.put(Enchantments.QUICK_CHARGE, 3);
        COSTS.put(Enchantments.SWEEPING, 3);

        // 4 tiers
        COSTS.put(Enchantments.PROTECTION, 4);
        COSTS.put(Enchantments.FEATHER_FALLING, 3);
        COSTS.put(Enchantments.PROJECTILE_PROTECTION, 2);
        COSTS.put(Enchantments.BLAST_PROTECTION, 2);
        COSTS.put(Enchantments.FIRE_PROTECTION, 2);
        COSTS.put(Enchantments.PIERCING, 2);

        // 5 tiers
        COSTS.put(Enchantments.POWER, 3);
        COSTS.put(Enchantments.SHARPNESS, 3);
        COSTS.put(Enchantments.SMITE, 2);
        COSTS.put(Enchantments.BANE_OF_ARTHROPODS, 2);
        COSTS.put(Enchantments.EFFICIENCY, 2);
        COSTS.put(Enchantments.IMPALING, 2);
    }

    public static int getCost(Enchantment e, int level) {
        return COSTS.getOrDefault(e, 3) * level;
    }
}
