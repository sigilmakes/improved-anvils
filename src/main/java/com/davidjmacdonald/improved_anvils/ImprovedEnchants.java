package com.davidjmacdonald.improved_anvils;

import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;

import java.util.HashMap;
import java.util.Map;

public class ImprovedEnchants {
    private static final Map<ResourceKey<Enchantment>, Integer> MAX_COSTS = new HashMap<>();

    private static void putEnchant(ResourceKey<Enchantment> e, int maxCost) {
        MAX_COSTS.put(e, maxCost);
    }

    static {
        // 1 tier
        putEnchant(Enchantments.MENDING, 100);
        putEnchant(Enchantments.INFINITY, 100);
        putEnchant(Enchantments.CHANNELING, 100);
        putEnchant(Enchantments.SILK_TOUCH, 100);
        putEnchant(Enchantments.FLAME, 100);
        putEnchant(Enchantments.AQUA_AFFINITY, 100);
        putEnchant(Enchantments.MULTISHOT, 100);
        putEnchant(Enchantments.VANISHING_CURSE, 10);
        putEnchant(Enchantments.BINDING_CURSE, 10);

        // 2 tiers
        putEnchant(Enchantments.FROST_WALKER, 10);
        putEnchant(Enchantments.FIRE_ASPECT, 100);
        putEnchant(Enchantments.KNOCKBACK, 100);
        putEnchant(Enchantments.PUNCH, 100);

        // 3 tiers
        putEnchant(Enchantments.UNBREAKING, 100);
        putEnchant(Enchantments.SOUL_SPEED, 100);
        putEnchant(Enchantments.SWIFT_SNEAK, 100);
        putEnchant(Enchantments.LOOTING, 100);
        putEnchant(Enchantments.DEPTH_STRIDER, 100);
        putEnchant(Enchantments.WIND_BURST, 100);
        putEnchant(Enchantments.FORTUNE, 100);
        putEnchant(Enchantments.LOYALTY, 100);
        putEnchant(Enchantments.RESPIRATION, 100);
        putEnchant(Enchantments.QUICK_CHARGE, 100);
        putEnchant(Enchantments.SWEEPING_EDGE, 100);
        putEnchant(Enchantments.THORNS, 100);
        putEnchant(Enchantments.LUCK_OF_THE_SEA, 100);
        putEnchant(Enchantments.LURE, 100);

        // 4 tiers
        putEnchant(Enchantments.PROTECTION, 100);
        putEnchant(Enchantments.FEATHER_FALLING, 100);
        putEnchant(Enchantments.BREACH, 100);
        putEnchant(Enchantments.BLAST_PROTECTION, 100);
        putEnchant(Enchantments.PROJECTILE_PROTECTION, 100);
        putEnchant(Enchantments.FIRE_PROTECTION, 100);
        putEnchant(Enchantments.PIERCING, 100);

        // 5 tiers
        putEnchant(Enchantments.EFFICIENCY, 100);
        putEnchant(Enchantments.DENSITY, 100);
        putEnchant(Enchantments.POWER, 100);
        putEnchant(Enchantments.SHARPNESS, 100);
        putEnchant(Enchantments.SMITE, 100);
        putEnchant(Enchantments.IMPALING, 100);
        putEnchant(Enchantments.BANE_OF_ARTHROPODS, 100);
    }

    private static int getCost(Holder<Enchantment> e, int level) {
        if (e.unwrapKey().isEmpty()) {
            return 1000;
        }
        return MAX_COSTS.getOrDefault(e.unwrapKey().get(), 150) * level / e.value().getMaxLevel();
    }

    private final ItemEnchantments.Mutable enchants;

    public ImprovedEnchants(ItemStack item) {
        this.enchants = new ItemEnchantments.Mutable(EnchantmentHelper.getEnchantmentsForCrafting(item));
    }

    public int add(Holder<Enchantment> enchant, int level) {
        var cost = getCost(enchant, level);
        var oldLevel = this.enchants.getLevel(enchant);

        if (oldLevel < 1) {
            if (!canAdd(enchant)) {
                return 0;
            }

            this.enchants.set(enchant, level);
            return cost;
        }

        if (oldLevel > level || oldLevel >= enchant.value().getMaxLevel()) {
            return 0;
        }

        this.enchants.set(enchant, level + ((level == oldLevel) ? 1 : 0));
        return cost;
    }

    public boolean has(ResourceKey<Enchantment> e) {
        for (var entry : this.enchants.keySet()) {
            if (entry.unwrapKey().isPresent() && entry.unwrapKey().get().equals(e)) {
                return true;
            }
        }
        return false;
    }

    public void setEnchantments(ItemStack item) {
        EnchantmentHelper.setEnchantments(item, this.enchants.toImmutable());
    }

    private boolean canAdd(Holder<Enchantment> e2) {
        for (var e1 : this.enchants.keySet()) {
            if (e1.value().exclusiveSet().contains(e2)) {
                return false;
            }
        }
        return true;
    }
}

