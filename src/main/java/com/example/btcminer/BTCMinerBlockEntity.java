package com.example.btcminer;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BTCMinerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    private double btcMined = 0.0;
    private boolean powered = false;
    private int tickCounter = 0;

    public BTCMinerBlockEntity(BlockPos pos, BlockState state) {
        super(BTCMinerMod.BTC_MINER_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BTCMinerBlockEntity entity) {
        if (world.isClient) return;

        entity.tickCounter++;

        // Check for cooling block above
        boolean hasCooling = entity.hasCoolingBlock();

        // Only mine if cooling block is present
        if (hasCooling && entity.tickCounter >= 20) { // Every second
            entity.btcMined += 0.001; // Mine 0.001 BTC per second
            entity.tickCounter = 0;
            entity.markDirty();

            if (!entity.powered) {
                entity.powered = true;
                world.setBlockState(pos, state.with(BTCMinerBlock.POWERED, true));
            }
        } else if (!hasCooling && entity.powered) {
            entity.powered = false;
            world.setBlockState(pos, state.with(BTCMinerBlock.POWERED, false));
        }
    }

    private boolean hasCoolingBlock() {
        if (world == null) return false;
        BlockPos above = pos.up();
        return world.getBlockState(above).getBlock() instanceof CoolingBlock;
    }

    public double getBtcMined() {
        return btcMined;
    }

    public boolean isPowered() {
        return powered;
    }

    public boolean needsCooling() {
        return !hasCoolingBlock();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        nbt.putDouble("BtcMined", btcMined);
        nbt.putBoolean("Powered", powered);
        nbt.putInt("TickCounter", tickCounter);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        btcMined = nbt.getDouble("BtcMined");
        powered = nbt.getBoolean("Powered");
        tickCounter = nbt.getInt("TickCounter");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("BTC Miner");
    }

    @Override
    public ScreenHandler createMenu(int syncId, net.minecraft.entity.player.PlayerInventory inv,
                                     net.minecraft.entity.player.PlayerEntity player) {
        return new BTCMinerScreenHandler(syncId, inv, this);
    }
}