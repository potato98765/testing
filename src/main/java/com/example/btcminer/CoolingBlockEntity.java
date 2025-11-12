package com.example.btcminer;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CoolingBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int waterAmount = 0;
    private static final int MAX_WATER = 1000;
    private int tickCounter = 0;

    public CoolingBlockEntity(BlockPos pos, BlockState state) {
        super(BTCMinerMod.COOLING_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CoolingBlockEntity entity) {
        if (world.isClient) return;

        entity.tickCounter++;

        // Check for water bucket in inventory
        if (!entity.inventory.get(0).isEmpty() &&
                entity.inventory.get(0).getItem() == Items.WATER_BUCKET &&
                entity.waterAmount < MAX_WATER) {
            entity.waterAmount = Math.min(entity.waterAmount + 100, MAX_WATER);
            entity.inventory.set(0, new ItemStack(Items.BUCKET));
            entity.markDirty();
        }

        // Consume water if there's a miner below
        if (entity.tickCounter >= 20 && entity.waterAmount > 0) {
            BlockPos below = pos.down();
            if (world.getBlockState(below).getBlock() instanceof BTCMinerBlock) {
                entity.waterAmount = Math.max(0, entity.waterAmount - 1);
                entity.tickCounter = 0;
                entity.markDirty();
            }
        }
    }

    public int getWaterAmount() {
        return waterAmount;
    }

    public int getMaxWater() {
        return MAX_WATER;
    }

    public void useWater(int amount) {
        if (this.waterAmount >= amount) {
            this.waterAmount -= amount;
            markDirty();
        }
    }

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
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return pos.isWithinDistance(player.getPos(), 8.0);
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, inventory, registries);
        nbt.putInt("WaterAmount", waterAmount);
        nbt.putInt("TickCounter", tickCounter);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, inventory, registries);
        waterAmount = nbt.getInt("WaterAmount");
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
        return Text.literal("Cooling Block");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CoolingScreenHandler(syncId, inv, this);
    }
}