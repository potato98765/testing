package com.example.btcminer.client;

import com.example.btcminer.BTCMinerBlockEntity;
import com.example.btcminer.BTCMinerMod;
import com.example.btcminer.BTCMinerScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BTCMinerScreen extends HandledScreen<BTCMinerScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(BTCMinerMod.MOD_ID, "textures/gui/container/btc_miner.png");

    public BTCMinerScreen(BTCMinerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 166;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);

        BTCMinerBlockEntity entity = handler.getBlockEntity();
        if (entity != null) {
            // Status
            String status = entity.isPowered() ? "§aON" : "§cOFF";
            context.drawText(this.textRenderer, Text.literal("Status: " + status),
                    10, 20, 0x404040, false);

            // BTC Mined
            String btcText = String.format("BTC Mined: %.6f", entity.getBtcMined());
            context.drawText(this.textRenderer, Text.literal(btcText),
                    10, 35, 0x404040, false);

            // Cooling status
            String coolingText = entity.needsCooling() ?
                    "§cNeeds Cooling Block Above!" : "§aCooling: Active";
            context.drawText(this.textRenderer, Text.literal(coolingText),
                    10, 50, 0x404040, false);
        }
    }
}