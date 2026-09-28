package com.davidjmacdonald.improved_anvils.mixin;

import com.davidjmacdonald.improved_anvils.ImprovedAnvils;
import com.davidjmacdonald.improved_anvils.AnvilMath;
import com.davidjmacdonald.improved_anvils.ImprovedEnchants;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(AnvilMenu.class)
public abstract class AnvilScreenHandlerMixin extends ItemCombinerMenu {
    @Unique
    private static final Set<Item> NETHERITE_ITEMS = Set.of(
            Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS,
            Items.NETHERITE_HOE, Items.NETHERITE_AXE, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_SWORD
    );

    // Vanilla syncs this field to the client; its value now represents XP points.
    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    private @Nullable String itemName;

    @Shadow
    private int repairItemCountCost;

    public AnvilScreenHandlerMixin(
            @Nullable MenuType<?> type,
            int syncId,
            Inventory playerInventory,
            ContainerLevelAccess context,
            ItemCombinerMenuSlotDefinition forgingSlotsManager
    ) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    /**
     * @author DavidJMacDonald
     * @reason To change anvil cost to points instead of levels
     */
    @Overwrite
    public boolean mayPickup(Player player, boolean present) {
        var cost = this.cost.get();
        var totalXP = ImprovedAnvils.getTotalPlayerXP(player);
        return cost > 0 && (player.hasInfiniteMaterials() || totalXP >= cost);
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
        method = "onTake",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;giveExperienceLevels(I)V")
    )
    private void spendExperiencePoints(Player player, int points) {
        player.giveExperiencePoints(points);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    public void createResult(CallbackInfo ci) {
        ci.cancel();
        this.repairItemCountCost = 0;
        var input = this.inputSlots.getItem(0);
        if (input.isEmpty()) {
            this.cost.set(0);
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.broadcastChanges();
            return;
        }

        var totalCost = 0;
        var item = input.copy();
        if (this.itemName != null && !StringUtil.isBlank(this.itemName)) {
            if (!this.itemName.equals(input.getHoverName().getString())) {
                totalCost++;
                item.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
            }
        } else if (input.has(DataComponents.CUSTOM_NAME)) {
            totalCost++;
            item.remove(DataComponents.CUSTOM_NAME);
        }

        var modifier = this.inputSlots.getItem(1);
        if (!modifier.isEmpty()) {
            var cost = repairAndEnchantItem(item, modifier);
            totalCost = (cost == 0) ? 0 : totalCost + cost;
        }

        if (totalCost == 0) {
            item = ItemStack.EMPTY;
        }

        this.cost.set(totalCost);
        this.resultSlots.setItem(0, item);
        this.broadcastChanges();
    }

    @Unique
    private int repairAndEnchantItem(ItemStack item, ItemStack modifier) {
        if (canRepairItem(item, modifier)) {
            return repairItem(item, modifier);
        }

        if (modifier.is(Items.ENCHANTED_BOOK)) {
            return enchantItem(item, modifier);
        }

        if (!item.is(Items.ENCHANTED_BOOK)) {
            return combineItems(item, modifier);
        }

        return 0;
    }

    @Unique
    private boolean canRepairItem(ItemStack item, ItemStack modifier) {
        if (NETHERITE_ITEMS.contains(item.getItem())) {
            return modifier.is(canRepairNetheriteWithDiamonds() ? Items.DIAMOND : Items.NETHERITE_INGOT);
        }

        var ironRepairableItems = Set.of(Items.SHIELD, Items.CROSSBOW, Items.FLINT_AND_STEEL, Items.SHEARS);
        if (ironRepairableItems.contains(item.getItem())) {
            return modifier.is(Items.IRON_INGOT) || modifier.is(Items.IRON_NUGGET);
        }

        var stringRepairableItems = Set.of(Items.BOW, Items.FISHING_ROD, Items.CARROT_ON_A_STICK, Items.WARPED_FUNGUS_ON_A_STICK);
        if (stringRepairableItems.contains(item.getItem())) {
            return modifier.is(Items.STRING);
        }

        if (item.is(Items.TRIDENT)) {
            return modifier.is(Items.PRISMARINE_SHARD);
        }

        if (modifier.is(Items.GOLD_NUGGET)) {
            return item.isValidRepairItem(Items.GOLD_INGOT.getDefaultInstance());
        }

        if (modifier.is(Items.IRON_NUGGET)) {
            return item.isValidRepairItem(Items.IRON_INGOT.getDefaultInstance());
        }

        return item.isValidRepairItem(modifier);
    }

    @Unique
    private int repairItem(ItemStack item, ItemStack modifier) {
        var repair = AnvilMath.repair(item.getMaxDamage(), item.getDamageValue(), modifier.getCount(),
            singleItemRepairPercent(item),
            NETHERITE_ITEMS.contains(item.getItem()) && canRepairNetheriteWithDiamonds(),
            modifier.is(Items.IRON_NUGGET) || modifier.is(Items.GOLD_NUGGET), isItemInfinityBow(item));
        item.setDamageValue(repair.damage());
        this.repairItemCountCost = repair.materials();
        return repair.cost();
    }

    @Unique
    private double singleItemRepairPercent(ItemStack item) {
        var one = Set.of(
                Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.GOLDEN_SHOVEL, Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL,
                Items.SHIELD, Items.CROSSBOW, Items.FLINT_AND_STEEL, Items.MACE
        );
        if (one.contains(item.getItem())) {
            return 1.0;
        }

        var two = Set.of(
                Items.WOODEN_HOE, Items.STONE_HOE, Items.GOLDEN_HOE, Items.IRON_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE,
                Items.WOODEN_SWORD, Items.STONE_SWORD, Items.GOLDEN_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD,
                Items.SHEARS, Items.FISHING_ROD, Items.CARROT_ON_A_STICK, Items.WARPED_FUNGUS_ON_A_STICK
        );
        if (two.contains(item.getItem())) {
            return 1 / 2.0;
        }

        var three = Set.of(
                Items.WOODEN_AXE, Items.STONE_AXE, Items.GOLDEN_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE,
                Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.GOLDEN_PICKAXE, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE,
                Items.BOW
        );
        if (three.contains(item.getItem())) {
            return 1 / 3.0;
        }

        return 1 / 4.0;
    }

    @Unique
    private boolean isItemInfinityBow(ItemStack item) {
        var enchants = new ImprovedEnchants(item);
        return item.is(Items.BOW) && enchants.has(Enchantments.INFINITY);
    }

    @Unique
    private int enchantItem(ItemStack item, ItemStack modifier) {
        var enchants = new ImprovedEnchants(item);
        var isBook = item.is(Items.ENCHANTED_BOOK);
        var totalCost = 0;

        var modifierEnchants = EnchantmentHelper.getEnchantmentsForCrafting(modifier);
        for (var enchant : modifierEnchants.keySet()) {
            if (!isBook && !item.canBeEnchantedWith(enchant, EnchantingContext.ACCEPTABLE)) {
                continue;
            }

            totalCost += enchants.add(enchant, modifierEnchants.getLevel(enchant));
        }

        enchants.setEnchantments(item);
        return totalCost;
    }

    @Unique
    private int combineItems(ItemStack item, ItemStack modifier) {
        if (!item.is(modifier.getItem())) {
            return 0;
        }

        var totalCost = enchantItem(item, modifier);
        if (!item.isDamaged()) {
            return totalCost;
        }

        var maxHealth = item.getMaxDamage();
        var health = maxHealth - item.getDamageValue();
        var modifierHealth = maxHealth - modifier.getDamageValue();
        var newHealth = Math.min(maxHealth, (int) (health + modifierHealth + .12 * maxHealth));

        item.setDamageValue(maxHealth - newHealth);
        return totalCost + AnvilMath.repairCost(newHealth - health, isItemInfinityBow(item) || isItemInfinityBow(modifier));
    }

    @Unique
    private boolean canRepairNetheriteWithDiamonds() {
        return this.access.evaluate((world, pos) -> {
            final var gameRule = ImprovedAnvils.REPAIR_NETHERITE_WITH_DIAMONDS;
            final var logger = ImprovedAnvils.LOGGER;

            final var server = world.getServer();
            if (server == null) {
                logger.error("cannot get game server to check game rules!");
                return false;
            }

            return ((net.minecraft.server.level.ServerLevel) world).getGameRules().get(gameRule);
        }).orElse(false);
    }
}
