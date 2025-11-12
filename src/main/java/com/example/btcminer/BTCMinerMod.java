package com.example.btcminer;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class BTCMinerMod implements ModInitializer {
    public static final String MOD_ID = "btcminer";

    // Blocks - initialized in onInitialize
    public static Block BTC_MINER_BLOCK;
    public static Block COOLING_BLOCK;

    // Items
    public static Item BITCOIN_ITEM;
    public static Item MINER_ITEM;
    public static Item COOLING_ITEM;

    // Block entities
    public static BlockEntityType<BTCMinerBlockEntity> BTC_MINER_BLOCK_ENTITY;
    public static BlockEntityType<CoolingBlockEntity> COOLING_BLOCK_ENTITY;

    // Screen handlers
    public static ScreenHandlerType<BTCMinerScreenHandler> BTC_MINER_SCREEN_HANDLER;
    public static ScreenHandlerType<CoolingScreenHandler> COOLING_SCREEN_HANDLER;

    @Override
    public void onInitialize() {
        // Initialize blocks
        BTC_MINER_BLOCK = new BTCMinerBlock(
                AbstractBlock.Settings.create()
                        .strength(2.0f)
                        .mapColor(MapColor.IRON_GRAY)
                        .sounds(BlockSoundGroup.METAL)
                        .requiresTool()
                        .luminance(state -> state.get(BTCMinerBlock.POWERED) ? 7 : 0)
        );
        
        COOLING_BLOCK = new CoolingBlock(
                AbstractBlock.Settings.create()
                        .strength(1.5f)
                        .mapColor(MapColor.LIGHT_BLUE)
                        .sounds(BlockSoundGroup.GLASS)
        );

        // Initialize items
        BITCOIN_ITEM = new Item(new Item.Settings());
        MINER_ITEM = new BlockItem(BTC_MINER_BLOCK, new Item.Settings());
        COOLING_ITEM = new BlockItem(COOLING_BLOCK, new Item.Settings());

        // Register blocks
        Registry.register(Registries.BLOCK, id("btc_miner_block"), BTC_MINER_BLOCK);
        Registry.register(Registries.BLOCK, id("cooling_block"), COOLING_BLOCK);

        // Register items
        Registry.register(Registries.ITEM, id("bitcoin"), BITCOIN_ITEM);
        Registry.register(Registries.ITEM, id("btc_miner_block"), MINER_ITEM);
        Registry.register(Registries.ITEM, id("cooling_block"), COOLING_ITEM);

        // Register block entities
        BTC_MINER_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                id("btc_miner_block_entity"),
                BlockEntityType.Builder.create(BTCMinerBlockEntity::new, BTC_MINER_BLOCK).build()
        );

        COOLING_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                id("cooling_block_entity"),
                BlockEntityType.Builder.create(CoolingBlockEntity::new, COOLING_BLOCK).build()
        );

        // Register screen handlers
        BTC_MINER_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                id("btc_miner_screen_handler"),
                new ScreenHandlerType<>(BTCMinerScreenHandler::new, null)
        );

        COOLING_SCREEN_HANDLER = Registry.register(
                Registries.SCREEN_HANDLER,
                id("cooling_screen_handler"),
                new ScreenHandlerType<>(CoolingScreenHandler::new, null)
        );

        // Add to creative tab
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(BITCOIN_ITEM);
            entries.add(MINER_ITEM);
            entries.add(COOLING_ITEM);
        });
    }

    public static Identifier id(String name) {
        return Identifier.of(MOD_ID, name);
    }
}