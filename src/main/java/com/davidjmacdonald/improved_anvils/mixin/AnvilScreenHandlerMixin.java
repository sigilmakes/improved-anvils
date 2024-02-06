package com.davidjmacdonald.improved_anvils.mixin;

import com.davidjmacdonald.improved_anvils.ImprovedEnchants;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.*;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
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
     * @reason To change anvil behavior
     */
    @Overwrite
    public void updateResult() {
        var input = this.input.getStack(0);
        if (input.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            return;
        }

        var item = input.copy();
        var totalCost = renameItem(item);

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
    }

    @Unique
    private int renameItem(ItemStack item) {
        int renameCost = 1;

        if (this.newItemName == null || StringUtils.isBlank(this.newItemName)) {
            if (item.hasCustomName()) {
                item.removeCustomName();
                return renameCost;
            }

            return 0;
        }

        if (this.newItemName.equals(item.getName().getString())) {
            return 0;
        }

        item.setCustomName(Text.literal(this.newItemName));
        return renameCost;
    }

    @Unique
    private int repairAndEnchantItem(ItemStack item, ItemStack modifier) {
        if (item.getItem().canRepair(item, modifier)) {
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
    private int repairItem(ItemStack item, ItemStack modifier) {
        var maxHealth = item.getMaxDamage();
        var health = maxHealth - item.getDamage();
        var maxRepairs = modifier.getCount();
        var newHealth = Math.min(maxHealth, (int) (health + maxRepairs * maxHealth / 4.0));

        item.setDamage(maxHealth - newHealth);
        this.repairItemUsage = (int) Math.ceil(4.0 * (newHealth - health) / maxHealth);
        return 2 * this.repairItemUsage;
    }

    @Unique
    private int enchantItem(ItemStack item, ItemStack modifier) {
        var enchants = new ImprovedEnchants(item);
        var isBook = item.isOf(Items.ENCHANTED_BOOK);
        var totalCost = 0;

        for (var entry : EnchantmentHelper.get(modifier).entrySet()) {
            var enchant = entry.getKey();
            if (!isBook && !enchant.isAcceptableItem(item)) {
                continue;
            }

            totalCost += enchants.add(enchant, entry.getValue());
        }

        enchants.set(item);
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
        return (int) Math.ceil(8.0 * (newHealth - health) / maxHealth);
    }
}

