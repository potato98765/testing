package com.example.btcminer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BTCMinerMod implements ModInitializer {
    public static final String MOD_ID = "btcminer";

    // Blocks
    public static final Block BTC_MINER_BLOCK = new Block(
            AbstractBlock.Settings.create().strength(2.0f).mapColor(MapColor.IRON_GRAY).sounds(BlockSoundGroup.METAL));
    public static final Block COOLING_BLOCK = new Block(
            AbstractBlock.Settings.create().strength(1.5f).mapColor(MapColor.LIGHT_BLUE).sounds(BlockSoundGroup.GLASS));

    // Items
    public static final Item BITCOIN_ITEM = new Item(new Item.Settings());
    public static final Item MINER_ITEM = new BlockItem(BTC_MINER_BLOCK, new Item.Settings());
    public static final Item COOLING_ITEM = new BlockItem(COOLING_BLOCK, new Item.Settings());

    // Block entities
    public static final net.minecraft.block.entity.BlockEntityType<BTCMinerBlockEntity> BTC_MINER_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, id("btc_miner_block_entity"),
                    FabricBlockEntityTypeBuilder.create(BTCMinerBlockEntity::new, BTC_MINER_BLOCK).build());

    public static final net.minecraft.block.entity.BlockEntityType<CoolingBlockEntity> COOLING_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, id("cooling_block_entity"),
                    FabricBlockEntityTypeBuilder.create(CoolingBlockEntity::new, COOLING_BLOCK).build());

    // Screen handlers
    public static final ScreenHandlerType<BTCMinerScreenHandler> BTC_MINER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, id("btc_miner_screen_handler"),
                    new ScreenHandlerType<>(BTCMinerScreenHandler::new, PacketCodec.unit(null)));

    public static final ScreenHandlerType<CoolingScreenHandler> COOLING_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, id("cooling_screen_handler"),
                    new ScreenHandlerType<>(CoolingScreenHandler::new, PacketCodec.unit(null)));

    @Override
    public void onInitialize() {
        // Register blocks and items
        Registry.register(Registries.BLOCK, id("btc_miner_block"), BTC_MINER_BLOCK);
        Registry.register(Registries.BLOCK, id("cooling_block"), COOLING_BLOCK);
        Registry.register(Registries.ITEM, id("bitcoin"), BITCOIN_ITEM);
        Registry.register(Registries.ITEM, id("btc_miner_block"), MINER_ITEM);
        Registry.register(Registries.ITEM, id("cooling_block"), COOLING_ITEM);

        // Creative tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(BITCOIN_ITEM);
            entries.add(MINER_ITEM);
            entries.add(COOLING_ITEM);
        });

        // Fuel
        FuelRegistry.INSTANCE.add(BITCOIN_ITEM, 200);
    }

    private static Identifier id(String name) {
        return new Identifier(MOD_ID, name);
    }
}
