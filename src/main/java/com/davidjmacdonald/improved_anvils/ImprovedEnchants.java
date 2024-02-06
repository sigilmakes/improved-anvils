package com.davidjmacdonald.improved_anvils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;

import java.util.*;

public class ImprovedEnchants {
    private static final Map<Enchantment, Integer> COSTS = new HashMap<>();
    private static final Set<Set<Enchantment>> INCOMPATIBILITY_SETS = new HashSet<>();

    static {
        // 1 tier
        COSTS.put(Enchantments.MENDING, 20);
        COSTS.put(Enchantments.INFINITY, 15);
        COSTS.put(Enchantments.CHANNELING, 12);
        COSTS.put(Enchantments.FLAME, 10);
        COSTS.put(Enchantments.MULTISHOT, 10);
        COSTS.put(Enchantments.SILK_TOUCH, 10);
        COSTS.put(Enchantments.AQUA_AFFINITY, 9);
        COSTS.put(Enchantments.VANISHING_CURSE, 5);
        COSTS.put(Enchantments.BINDING_CURSE, 5);

        // 2 tiers
        COSTS.put(Enchantments.FIRE_ASPECT, 5);
        COSTS.put(Enchantments.KNOCKBACK, 4);
        COSTS.put(Enchantments.PUNCH, 4);
        COSTS.put(Enchantments.FROST_WALKER, 4);

        // 3 tiers
        COSTS.put(Enchantments.SOUL_SPEED, 5);
        COSTS.put(Enchantments.SWIFT_SNEAK, 5);
        COSTS.put(Enchantments.UNBREAKING, 5);
        COSTS.put(Enchantments.LOOTING, 5);
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
        COSTS.put(Enchantments.FEATHER_FALLING, 4);
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
        return COSTS.getOrDefault(e, 3) * level;
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
        if (!this.enchants.containsKey(enchant)) {
            if (this.incompatibleSet.contains(enchant)) {
                return 0;
            }

            this.incompatibleSet.addAll(getIncompatibilitySet(enchant));
        } else {
            int oldLevel = this.enchants.get(enchant);
            if (oldLevel > level || oldLevel >= enchant.getMaxLevel()) {
                return 0;
            }

            if (level == oldLevel) {
                level++;
            }
        }

        this.enchants.put(enchant, level);
        return getCost(enchant, level);
    }

    public void set(ItemStack item) {
        EnchantmentHelper.set(this.enchants, item);
    }
}

