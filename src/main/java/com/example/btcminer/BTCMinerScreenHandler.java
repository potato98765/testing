package com.example.btcminer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.RegistryByteBuf;

public class BTCMinerScreenHandler extends ScreenHandler {
    private final ScreenHandlerContext context;
    private final BlockPos pos;

    public BTCMinerScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        super(BTCMinerMod.BTC_MINER_SCREEN_HANDLER, syncId);
        this.context = playerInventory != null
                ? ScreenHandlerContext.create(playerInventory.player.getWorld(), pos)
                : ScreenHandlerContext.EMPTY;
        this.pos = pos;

        // Add player inventory slots (example layout)
        if (playerInventory != null) {
            int m, l;
            for (m = 0; m < 3; ++m) {
                for (l = 0; l < 9; ++l) {
                    this.addSlot(new net.minecraft.screen.slot.Slot(
                            playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
                }
            }
            for (l = 0; l < 9; ++l) {
                this.addSlot(new net.minecraft.screen.slot.Slot(
                        playerInventory, l, 8 + l * 18, 142));
            }
        }
    }

    public BlockPos getPos() {
        return pos;
    }

    public static void writeBuf(BTCMinerScreenHandler handler, RegistryByteBuf buf) {
        buf.writeBlockPos(handler.pos);
    }

    public static BTCMinerScreenHandler readBuf(RegistryByteBuf buf) {
        return new BTCMinerScreenHandler(0, null, buf.readBlockPos());
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, BTCMinerMod.BTC_MINER_BLOCK);
    }
}
