package com.davidjmacdonald.improved_anvils.mixin;

import com.davidjmacdonald.improved_anvils.ImprovedAnvils;
import com.davidjmacdonald.improved_anvils.ImprovedEnchants;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    @Unique
    private static final Set<Item> NETHERITE_ITEMS = Set.of(
            Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS,
            Items.NETHERITE_HOE, Items.NETHERITE_AXE, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_SWORD
    );

    // Level cost represents xp points, not levels. Name cannot be changed due to Mixin.
    @Shadow
    @Final
    private Property levelCost;

    @Shadow
    private @Nullable String newItemName;

    @Shadow
    private int repairItemUsage;

    public AnvilScreenHandlerMixin(
            @Nullable ScreenHandlerType<?> type,
            int syncId,
            PlayerInventory playerInventory,
            ScreenHandlerContext context
    ) {
        super(type, syncId, playerInventory, context);
    }

    /**
     * @author DavidJMacDonald
     * @reason To change anvil cost to points instead of levels
     */
    @Overwrite
    public boolean canTakeOutput(PlayerEntity player, boolean present) {
        var cost = this.levelCost.get();
        var totalXP = ImprovedAnvils.getTotalPlayerXP(player);
        return cost > 0 && (player.isInCreativeMode() || totalXP >= cost);
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"), cancellable = true)
    public void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!player.getAbilities().creativeMode) {
            player.addExperience(-this.levelCost.get());
        }
        this.input.setStack(0, ItemStack.EMPTY);
        if (this.repairItemUsage > 0) {
            ItemStack itemStack = this.input.getStack(1);
            if (!itemStack.isEmpty() && itemStack.getCount() > this.repairItemUsage) {
                itemStack.decrement(this.repairItemUsage);
                this.input.setStack(1, itemStack);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
        } else {
            this.input.setStack(1, ItemStack.EMPTY);
        }
        this.levelCost.set(0);
        this.context.run((world, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            if (!player.isInCreativeMode() && blockState.isIn(BlockTags.ANVIL) && player.getRandom().nextFloat() < 0.12f) {
                BlockState blockState2 = AnvilBlock.getLandingState(blockState);
                if (blockState2 == null) {
                    world.removeBlock(pos, false);
                    world.syncWorldEvent(WorldEvents.ANVIL_DESTROYED, pos, 0);
                } else {
                    world.setBlockState(pos, blockState2, Block.NOTIFY_LISTENERS);
                    world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
                }
            } else {
                world.syncWorldEvent(WorldEvents.ANVIL_USED, pos, 0);
            }
        });

        ci.cancel();
    }

    @Inject(method = "updateResult", at = @At("HEAD"), cancellable = true)
    public void updateResult(CallbackInfo ci) {
        var input = this.input.getStack(0);
        if (input.isEmpty()) {
            this.levelCost.set(0);
            this.output.setStack(0, ItemStack.EMPTY);
            this.sendContentUpdates();
            return;
        }

        var totalCost = 0;
        var item = input.copy();
        if (this.newItemName != null && !StringHelper.isBlank(this.newItemName)) {
            if (!this.newItemName.equals(input.getName().getString())) {
                totalCost++;
                item.set(DataComponentTypes.CUSTOM_NAME, Text.literal(this.newItemName));
            }
        } else if (input.contains(DataComponentTypes.CUSTOM_NAME)) {
            totalCost++;
            item.remove(DataComponentTypes.CUSTOM_NAME);
        }

        var modifier = this.input.getStack(1);
        if (!modifier.isEmpty()) {
            var cost = repairAndEnchantItem(item, modifier);
            totalCost = (cost == 0) ? 0 : totalCost + cost;
        }

        if (totalCost == 0) {
            item = ItemStack.EMPTY;
        }

        this.levelCost.set(totalCost);
        this.output.setStack(0, item);
        this.sendContentUpdates();

        ci.cancel();
    }

    @Unique
    private int repairAndEnchantItem(ItemStack item, ItemStack modifier) {
        if (canRepairItem(item, modifier)) {
            return repairItem(item, modifier);
        }

        if (modifier.isOf(Items.ENCHANTED_BOOK)) {
            return enchantItem(item, modifier);
        }

        if (!item.isOf(Items.ENCHANTED_BOOK)) {
            return combineItems(item, modifier);
        }

        return 0;
    }

    @Unique
    private boolean canRepairItem(ItemStack item, ItemStack modifier) {
        if (NETHERITE_ITEMS.contains(item.getItem())) {
            return modifier.isOf(Items.DIAMOND);
        }

        var ironRepairableItems = Set.of(Items.SHIELD, Items.CROSSBOW, Items.FLINT_AND_STEEL, Items.SHEARS);
        if (ironRepairableItems.contains(item.getItem())) {
            return modifier.isOf(Items.IRON_INGOT) || modifier.isOf(Items.IRON_NUGGET);
        }

        var stringRepairableItems = Set.of(Items.BOW, Items.FISHING_ROD, Items.CARROT_ON_A_STICK, Items.WARPED_FUNGUS_ON_A_STICK);
        if (stringRepairableItems.contains(item.getItem())) {
            return modifier.isOf(Items.STRING);
        }

        if (item.isOf(Items.TRIDENT)) {
            return modifier.isOf(Items.NAUTILUS_SHELL);
        }

        if (modifier.isOf(Items.GOLD_NUGGET)) {
            return item.getItem().canRepair(item, Items.GOLD_INGOT.getDefaultStack());
        }

        if (modifier.isOf(Items.IRON_NUGGET)) {
            return item.getItem().canRepair(item, Items.IRON_INGOT.getDefaultStack());
        }

        return item.getItem().canRepair(item, modifier);
    }

    @Unique
    private int repairItem(ItemStack item, ItemStack modifier) {
        var maxHealth = item.getMaxDamage();
        var health = maxHealth - item.getDamage();
        var maxRepairs = modifier.getCount();

        var singleRepair = singleItemRepairPercent(item) * maxHealth;
        if (NETHERITE_ITEMS.contains(item.getItem())) {
            singleRepair /= 2;
        }

        if (modifier.isOf(Items.IRON_NUGGET) || modifier.isOf(Items.GOLD_NUGGET)) {
            singleRepair /= 9;
        }

        var repaired = Math.min(maxHealth - health, maxRepairs * singleRepair);
        var newHealth = (int) (health + repaired);
        item.setDamage(maxHealth - newHealth);
        this.repairItemUsage = (int) Math.ceil(repaired / singleRepair);

        var costFactor = isItemInfinityBow(item) ? 4 : 1;
        return (int) Math.ceil(costFactor * repaired / 4.0);
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
        return item.isOf(Items.BOW) && enchants.has(Enchantments.INFINITY);
    }

    @Unique
    private int enchantItem(ItemStack item, ItemStack modifier) {
        var enchants = new ImprovedEnchants(item);
        var isBook = item.isOf(Items.ENCHANTED_BOOK);
        var totalCost = 0;

        var modifierEnchants = EnchantmentHelper.getEnchantments(modifier);
        for (var enchant : modifierEnchants.getEnchantments()) {
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
        if (!item.isOf(modifier.getItem())) {
            return 0;
        }

        var totalCost = enchantItem(item, modifier);
        if (!item.isDamaged()) {
            return totalCost;
        }

        var maxHealth = item.getMaxDamage();
        var health = maxHealth - item.getDamage();
        var modifierHealth = maxHealth - modifier.getDamage();
        var newHealth = Math.min(maxHealth, (int) (health + modifierHealth + .12 * maxHealth));

        item.setDamage(maxHealth - newHealth);
        var costFactor = (isItemInfinityBow(item) || isItemInfinityBow(modifier)) ? 4 : 1;
        return totalCost + (int) Math.ceil(costFactor * (newHealth - health) / 4.0);
    }
}

