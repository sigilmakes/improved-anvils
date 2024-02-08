package com.davidjmacdonald.improved_anvils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

import java.util.*;

public class ImprovedEnchants {
    private static final Map<Enchantment, Integer> MAX_COSTS = new HashMap<>();

    private static void putEnchant(Enchantment e, int maxCost) {
        MAX_COSTS.put(e, maxCost);
    }

    static {
        // 1 tier
        putEnchant(Enchantments.MENDING, 550);
        putEnchant(Enchantments.INFINITY, 400);
        putEnchant(Enchantments.CHANNELING, 350);
        putEnchant(Enchantments.SILK_TOUCH, 350);
        putEnchant(Enchantments.FLAME, 300);
        putEnchant(Enchantments.AQUA_AFFINITY, 300);
        putEnchant(Enchantments.MULTISHOT, 250);
        putEnchant(Enchantments.VANISHING_CURSE, 200);
        putEnchant(Enchantments.BINDING_CURSE, 200);

        // 2 tiers
        putEnchant(Enchantments.FROST_WALKER, 400);
        putEnchant(Enchantments.FIRE_ASPECT, 300);
        putEnchant(Enchantments.KNOCKBACK, 250);
        putEnchant(Enchantments.PUNCH, 250);

        // 3 tiers
        putEnchant(Enchantments.UNBREAKING, 500);
        putEnchant(Enchantments.SOUL_SPEED, 450);
        putEnchant(Enchantments.SWIFT_SNEAK, 450);
        putEnchant(Enchantments.LOOTING, 400);
        putEnchant(Enchantments.DEPTH_STRIDER, 400);
        putEnchant(Enchantments.FORTUNE, 350);
        putEnchant(Enchantments.LOYALTY, 350);
        putEnchant(Enchantments.RESPIRATION, 350);
        putEnchant(Enchantments.QUICK_CHARGE, 350);
        putEnchant(Enchantments.SWEEPING, 350);
        putEnchant(Enchantments.THORNS, 300);
        putEnchant(Enchantments.LUCK_OF_THE_SEA, 300);
        putEnchant(Enchantments.LURE, 300);

        // 4 tiers
        putEnchant(Enchantments.PROTECTION, 450);
        putEnchant(Enchantments.FEATHER_FALLING, 450);
        putEnchant(Enchantments.BLAST_PROTECTION, 350);
        putEnchant(Enchantments.PROJECTILE_PROTECTION, 300);
        putEnchant(Enchantments.FIRE_PROTECTION, 300);
        putEnchant(Enchantments.PIERCING, 250);

        // 5 tiers
        putEnchant(Enchantments.EFFICIENCY, 400);
        putEnchant(Enchantments.POWER, 350);
        putEnchant(Enchantments.SHARPNESS, 350);
        putEnchant(Enchantments.SMITE, 300);
        putEnchant(Enchantments.IMPALING, 300);
        putEnchant(Enchantments.BANE_OF_ARTHROPODS, 250);
    }

    private static int getCost(Enchantment e, int level) {
        return MAX_COSTS.getOrDefault(e, 300) * level / e.getMaxLevel();
    }

    private final Map<Enchantment, Integer> enchants;

    public ImprovedEnchants(ItemStack item) {
        this.enchants = new HashMap<>(EnchantmentHelper.get(item));
    }

    public int add(Enchantment enchant, int level) {
        var cost = getCost(enchant, level);

        if (!this.enchants.containsKey(enchant)) {
            if (!canAdd(enchant)) {
                return 0;
            }

            this.enchants.put(enchant, level);
            return cost;
        }

        int oldLevel = this.enchants.get(enchant);
        if (oldLevel > level || oldLevel >= enchant.getMaxLevel()) {
            return 0;
        }

        this.enchants.put(enchant, level + ((level == oldLevel) ? 1 : 0));
        return cost;
    }

    public void set(ItemStack item) {
        EnchantmentHelper.set(this.enchants, item);
    }

    private boolean canAdd(Enchantment e2) {
        for (var e1 : this.enchants.keySet()) {
            if (!e1.canCombine(e2)) {
                return false;
            }
        }

        return true;
    }
}

