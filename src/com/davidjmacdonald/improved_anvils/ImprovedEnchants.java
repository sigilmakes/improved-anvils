package com.davidjmacdonald.improved_anvils;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.Map;

public class ImprovedEnchants {
    private static final Map<RegistryKey<Enchantment>, Integer> MAX_COSTS = new HashMap<>();

    private static void putEnchant(RegistryKey<Enchantment> e, int maxCost) {
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

    private static int getCost(RegistryEntry<Enchantment> e, int level) {
        if (e.getKey().isEmpty()) {
            return 1000;
        }
        return MAX_COSTS.getOrDefault(e.getKey().get(), 150) * level / e.value().getMaxLevel();
    }

    private final ItemEnchantmentsComponent.Builder enchants;

    public ImprovedEnchants(ItemStack item) {
        this.enchants = new ItemEnchantmentsComponent.Builder(EnchantmentHelper.getEnchantments(item));
    }

    public int add(RegistryEntry<Enchantment> enchant, int level) {
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

    public boolean has(RegistryKey<Enchantment> e) {
        for (var entry : this.enchants.getEnchantments()) {
            if (entry.getKey().isPresent() && entry.getKey().get().equals(e)) {
                return true;
            }
        }
        return false;
    }

    public void setEnchantments(ItemStack item) {
        EnchantmentHelper.set(item, this.enchants.build());
    }

    private boolean canAdd(RegistryEntry<Enchantment> e2) {
        for (var e1 : this.enchants.getEnchantments()) {
            if (e1.value().exclusiveSet().contains(e2)) {
                return false;
            }
        }
        return true;
    }
}

