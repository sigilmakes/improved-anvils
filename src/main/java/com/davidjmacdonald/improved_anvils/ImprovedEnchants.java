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
        putEnchant(Enchantments.WIND_BURST, 400);
        putEnchant(Enchantments.FORTUNE, 350);
        putEnchant(Enchantments.LOYALTY, 350);
        putEnchant(Enchantments.RESPIRATION, 350);
        putEnchant(Enchantments.QUICK_CHARGE, 350);
        putEnchant(Enchantments.SWEEPING_EDGE, 350);
        putEnchant(Enchantments.THORNS, 300);
        putEnchant(Enchantments.LUCK_OF_THE_SEA, 300);
        putEnchant(Enchantments.LURE, 300);

        // 4 tiers
        putEnchant(Enchantments.PROTECTION, 450);
        putEnchant(Enchantments.FEATHER_FALLING, 450);
        putEnchant(Enchantments.BREACH, 400);
        putEnchant(Enchantments.BLAST_PROTECTION, 350);
        putEnchant(Enchantments.PROJECTILE_PROTECTION, 300);
        putEnchant(Enchantments.FIRE_PROTECTION, 300);
        putEnchant(Enchantments.PIERCING, 250);

        // 5 tiers
        putEnchant(Enchantments.EFFICIENCY, 400);
        putEnchant(Enchantments.DENSITY, 400);
        putEnchant(Enchantments.POWER, 350);
        putEnchant(Enchantments.SHARPNESS, 350);
        putEnchant(Enchantments.SMITE, 300);
        putEnchant(Enchantments.IMPALING, 300);
        putEnchant(Enchantments.BANE_OF_ARTHROPODS, 250);
    }

    private static int getCost(RegistryEntry<Enchantment> e, int level) {
        if (e.getKey().isEmpty()) {
            return 1000;
        }
        return MAX_COSTS.getOrDefault(e.getKey().get(), 300) * level / e.value().getMaxLevel();
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

