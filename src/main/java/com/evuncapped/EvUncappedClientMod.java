package com.evuncapped;

import com.evuncapped.net.EvUncappedPresencePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Registering a receiver here is the whole point - it's never actually invoked (the
 * server never sends this payload), but it makes the client announce the channel during
 * the networking handshake, which is how the server detects this mod's presence.
 */
public final class EvUncappedClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(EvUncappedPresencePayload.ID, (payload, context) -> {});
    }
}
