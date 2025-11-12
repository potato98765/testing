package com.example.btcminer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class BTCMinerScreenHandler extends ScreenHandler {
    private final BTCMinerBlockEntity blockEntity;

    public BTCMinerScreenHandler(int syncId, PlayerInventory playerInventory, BTCMinerBlockEntity blockEntity) {
        super(BTCMinerMod.BTC_MINER_SCREEN_HANDLER, syncId);
        this.blockEntity = blockEntity;
    }

    public BTCMinerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, null);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return blockEntity == null || blockEntity.getPos().isWithinDistance(player.getPos(), 8.0);
    }

    public BTCMinerBlockEntity getBlockEntity() {
        return blockEntity;
    }
}