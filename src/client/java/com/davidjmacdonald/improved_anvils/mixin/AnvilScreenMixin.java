package com.davidjmacdonald.improved_anvils.mixin;

import com.davidjmacdonald.improved_anvils.ImprovedAnvils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu> {
    @Shadow
    @Final
    private Player player;

    public AnvilScreenMixin(
            AnvilMenu handler,
            Inventory playerInventory,
            Component title,
            Identifier texture
    ) {
        super(handler, playerInventory, title, texture);
    }

    /**
     * @author DavidJMacDonald
     * @reason To remove "TOO EXPENSIVE" message
     */
    @Overwrite
    public void extractLabels(GuiGraphicsExtractor context, int mouseX, int mouseY) {
        var totalXP = ImprovedAnvils.getTotalPlayerXP(this.player);
        var title = Component.translatable("container.improved_anvils.repair", totalXP);
        context.text(this.font, title, this.titleLabelX, this.titleLabelY, -12566464, false);
        context.text(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);

        var handler = (AnvilMenu) this.menu;
        var cost = handler.getCost();

        var result = handler.getSlot(2);
        if (cost <= 0 || !result.hasItem()) {
            return;
        }

        var text = Component.translatable("container.improved_anvils.repair.cost", cost);
        var j = !result.mayPickup(this.player) ? -40864 : -8323296;
        var k = this.imageWidth - 8 - this.font.width(text) - 2;

        context.fill(k - 2, 67, this.imageWidth - 8, 79, 1325400064);
        context.text(this.font, text, k, 69, j);
    }
}
