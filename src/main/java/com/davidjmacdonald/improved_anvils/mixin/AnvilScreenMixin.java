package com.davidjmacdonald.improved_anvils.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> {
    @Shadow
    @Final
    private PlayerEntity player;

    public AnvilScreenMixin(
            AnvilScreenHandler handler,
            PlayerInventory playerInventory,
            Text title,
            Identifier texture
    ) {
        super(handler, playerInventory, title, texture);
    }

    /**
     * @author DavidJMacDonald
     * @reason To remove "TOO EXPENSIVE" message
     */
    @Overwrite
    public void drawForeground(DrawContext context, int mouseX, int mouseY) {
        var title = Text.translatable("container.improved_anvils.repair", this.player.totalExperience);
        context.drawText(this.textRenderer, title, this.titleX, this.titleY, 0x404040, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 0x404040, false);

        var handler = (AnvilScreenHandler) this.handler;
        var cost = handler.getLevelCost();

        var result = handler.getSlot(2);
        if (cost <= 0 || !result.hasStack()) {
            return;
        }

        var text = Text.translatable("container.improved_anvils.repair.cost", cost);
        var j = !result.canTakeItems(this.player) ? 0xFF6060 : 8453920;
        var k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;

        context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 0x4F000000);
        context.drawTextWithShadow(this.textRenderer, text, k, 69, j);
    }
}
