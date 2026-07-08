package com.evuncapped.net;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Carries no data - its only purpose is to exist as a registered channel. A client that
 * has this mod registers a receiver for it, which makes Fabric's networking handshake
 * announce the channel; the server then uses ServerPlayNetworking.canSend(player, ID) to
 * tell which connected players are running the mod, with no packet ever actually sent.
 */
public record EvUncappedPresencePayload() implements CustomPayload {
    public static final CustomPayload.Id<EvUncappedPresencePayload> ID =
            new CustomPayload.Id<>(Identifier.of("cobblemon-ev-uncapped", "presence"));
    public static final PacketCodec<RegistryByteBuf, EvUncappedPresencePayload> CODEC =
            PacketCodec.unit(new EvUncappedPresencePayload());

    @Override
    public CustomPayload.Id<EvUncappedPresencePayload> getId() {
        return ID;
    }
}
