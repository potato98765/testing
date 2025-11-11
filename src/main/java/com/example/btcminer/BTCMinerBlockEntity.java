package com.example.btcminer;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.network.RegistryByteBuf;

public class BTCMinerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int progress = 0;
    private int maxProgress = 200; // ticks to generate 1 BTC
    private int waterAmount = 0;
    private static final int MAX_WATER = 1000;

    public BTCMinerBlockEntity(BlockPos pos, BlockState state) {
        super(BTCMinerMod.BTC_MINER_BLOCK_ENTITY, pos, state);
    }
    
    
    // inside BTCMinerBlockEntity
    public static void writeBuf(BTCMinerBlockEntity entity, RegistryByteBuf buf) {
        buf.writeBlockPos(entity.getPos());
   }

    public static BlockPos readBuf(RegistryByteBuf buf) {
        return buf.readBlockPos();
    }

    

    // --- Tick Logic ---
    public static void tick(World world, BlockPos pos, BlockState state, BTCMinerBlockEntity entity) {
        if (world.isClient) return;

        boolean hasCooling = false;
        BlockPos above = pos.up();
        if (world.getBlockEntity(above) instanceof CoolingBlockEntity cooling) {
            if (cooling.getWaterAmount() > 0) {
                cooling.useWater(1);
                hasCooling = true;
            }
        }

        if (hasCooling) {
            entity.progress++;
            if (entity.progress >= entity.maxProgress) {
                entity.progress = 0;
                ItemStack output = entity.inventory.get(2);
                if (output.isEmpty()) {
                    entity.inventory.set(2, new ItemStack(BTCMinerMod.BITCOIN_ITEM));
                } else if (output.getItem() == BTCMinerMod.BITCOIN_ITEM && output.getCount() < output.getMaxCount()) {
                    output.increment(1);
                }
                entity.markDirty();
            }
        } else {
            if (entity.progress > 0) entity.progress--;
        }

        entity.markDirty();
    }

    // --- Inventory Implementation ---
    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(inventory, slot, amount);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) stack.setCount(getMaxCountPerStack());
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return player.squaredDistanceTo(
            (double) pos.getX() + 0.5D,
            (double) pos.getY() + 0.5D,
            (double) pos.getZ() + 0.5D
        ) <= 64.0D;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    // --- NBT Save/Load ---
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, inventory, registries);
        nbt.putInt("Progress", progress);
        nbt.putInt("MaxProgress", maxProgress);
        nbt.putInt("WaterAmount", waterAmount);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, inventory, registries);
        progress = nbt.getInt("Progress");
        maxProgress = nbt.getInt("MaxProgress");
        waterAmount = nbt.getInt("WaterAmount");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    // --- GUI ---
    @Override
    public Text getDisplayName() {
        return Text.literal("Bitcoin Miner");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        PropertyDelegate delegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> progress;
                    case 1 -> maxProgress;
                    case 2 -> waterAmount;
                    case 3 -> MAX_WATER;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> progress = value;
                    case 1 -> maxProgress = value;
                    case 2 -> waterAmount = value;
                }
            }

            @Override
            public int size() {
                return 4;
            }
        };
        return new BTCMinerScreenHandler(syncId, inv, getPos());
    }

    // --- Custom Getters/Setters ---
    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getMaxWater() {
        return MAX_WATER;
    }

    public void addWater(int amount) {
        this.waterAmount = Math.min(this.waterAmount + amount, MAX_WATER);
        markDirty();
    }

    public void useWater(int amount) {
        this.waterAmount = Math.max(0, this.waterAmount - amount);
        markDirty();
    }

}
