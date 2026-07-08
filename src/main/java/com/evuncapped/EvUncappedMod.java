package com.evuncapped;

import com.evuncapped.net.EvUncappedPresencePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class EvUncappedMod implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(EvUncappedPresencePayload.ID, EvUncappedPresencePayload.CODEC);
    }
}
