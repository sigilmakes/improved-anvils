package com.davidjmacdonald.improved_anvils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

import java.util.*;

public class ImprovedEnchants {
    private static final Map<Enchantment, Integer> MAX_COSTS = new HashMap<>();
    private static final Set<Set<Enchantment>> INCOMPATIBILITY_SETS = new HashSet<>();

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

        // Incompatibility sets
        INCOMPATIBILITY_SETS.add(Set.of(Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS));
        INCOMPATIBILITY_SETS.add(Set.of(Enchantments.PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.FIRE_PROTECTION));
        INCOMPATIBILITY_SETS.add(Set.of(Enchantments.FORTUNE, Enchantments.SILK_TOUCH));
        INCOMPATIBILITY_SETS.add(Set.of(Enchantments.FROST_WALKER, Enchantments.DEPTH_STRIDER));
        INCOMPATIBILITY_SETS.add(Set.of(Enchantments.INFINITY, Enchantments.MENDING));
        INCOMPATIBILITY_SETS.add(Set.of(Enchantments.MULTISHOT, Enchantments.PIERCING));
        INCOMPATIBILITY_SETS.add(Set.of(Enchantments.RIPTIDE, Enchantments.LOYALTY));
        INCOMPATIBILITY_SETS.add(Set.of(Enchantments.RIPTIDE, Enchantments.CHANNELING));
    }

    private static int getCost(Enchantment e, int level) {
        return MAX_COSTS.getOrDefault(e, 300) * level / e.getMaxLevel();
    }

    private static Set<Enchantment> getIncompatibilitySet(Enchantment e) {
        var set = new HashSet<Enchantment>();
        for (var s : INCOMPATIBILITY_SETS) {
            if (s.contains(e)) {
                set.addAll(s);
            }
        }

        return set;
    }

    private final Map<Enchantment, Integer> enchants;
    private final Set<Enchantment> incompatibleSet;

    public ImprovedEnchants(ItemStack item) {
        this.enchants = new HashMap<>(EnchantmentHelper.get(item));
        this.incompatibleSet = new HashSet<>();

        for (var e : this.enchants.keySet()) {
            this.incompatibleSet.addAll(getIncompatibilitySet(e));
        }
    }

    public int add(Enchantment enchant, int level) {
        var cost = getCost(enchant, level);

        if (!this.enchants.containsKey(enchant)) {
            if (this.incompatibleSet.contains(enchant)) {
                return 0;
            }

            this.incompatibleSet.addAll(getIncompatibilitySet(enchant));
            this.enchants.put(enchant, level);
            return cost;
        }

        int oldLevel = this.enchants.get(enchant);
        if (oldLevel > level || oldLevel >= enchant.getMaxLevel()) {
            return 0;
        }

        if (level == oldLevel) {
            level++;
        }

        this.enchants.put(enchant, level);
        return cost;
    }

    public void set(ItemStack item) {
        EnchantmentHelper.set(this.enchants, item);
    }
}

