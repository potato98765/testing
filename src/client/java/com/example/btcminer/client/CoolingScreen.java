package com.example.btcminer.client;

import com.example.btcminer.BTCMinerMod;
import com.example.btcminer.CoolingBlockEntity;
import com.example.btcminer.CoolingScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CoolingScreen extends HandledScreen<CoolingScreenHandler> {
    // use resource path without 'textures' and extension
    private static final Identifier TEXTURE = BTCMinerMod.id("gui/cooling_block");

    public CoolingScreen(CoolingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        // full background
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);

        int water = handler.getWaterAmount();
        int max = handler.getMaxWater();

        // Water amount text
        String waterText = String.format("Water: %d / %d mB", water, max);
        context.drawText(this.textRenderer, Text.literal(waterText), 10, 20, 0x404040, false);

        // Water bar
        int barWidth = 156;
        int barHeight = 8;
        int barX = 10;
        int barY = 60;

        // Background
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF8B8B8B);

        // Water fill
        int fillWidth = max == 0 ? 0 : (int)((double)water / (double)max * barWidth);
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, 0xFF3B82F6);

        // Border (thin)
        context.fill(barX, barY, barX + barWidth, barY + 1, 0xFF000000);
        context.fill(barX, barY + barHeight - 1, barX + barWidth, barY + barHeight, 0xFF000000);
        context.fill(barX, barY, barX + 1, barY + barHeight, 0xFF000000);
        context.fill(barX + barWidth - 1, barY, barX + barWidth, barY + barHeight, 0xFF000000);

        // Instructions
        context.drawText(this.textRenderer, Text.literal("Place water bucket in slot above"), 10, 35, 0x808080, false);
    }
}
