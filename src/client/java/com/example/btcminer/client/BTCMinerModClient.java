package com.example.btcminer.client;

import com.example.btcminer.BTCMinerMod;
import com.example.btcminer.BTCMinerScreenHandler;
import com.example.btcminer.CoolingScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BTCMinerModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(BTCMinerMod.BTC_MINER_SCREEN_HANDLER, BTCMinerScreen::new);
        HandledScreens.register(BTCMinerMod.COOLING_SCREEN_HANDLER, CoolingScreen::new);
    }
}