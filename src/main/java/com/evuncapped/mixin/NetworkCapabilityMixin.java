package com.evuncapped.mixin;

import com.cobblemon.mod.common.api.net.NetworkPacket;
import com.evuncapped.net.EvUncappedPresencePayload;
import com.evuncapped.net.NetworkCapability;
import io.netty.channel.Channel;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Every per-player Cobblemon packet send (party sync, PC, battle, trade, summary, the
 * single-field EVsUpdatePacket) funnels through this one method, regardless of which of
 * Cobblemon's dozen packet classes triggered it. Bracketing it with the recipient's
 * capability lets EVsMixin/PokemonNetworkMixin's codec wrappers decide, per player,
 * whether to send the real EVs or a clamped-to-510 copy.
 *
 * The actual encode happens later, asynchronously, on that connection's own Netty event
 * loop thread - not the thread this method runs on - so a plain ThreadLocal set here
 * never survives to reach it. Instead, both the mark and the clear are submitted as
 * tasks on that same event loop, ahead of the send this method is about to trigger.
 * Netty's per-connection event loop runs its queued tasks strictly in submission order,
 * so mark → (the write, wherever it fires the codec) → clear stays correctly sequenced
 * on the one thread that matters, no matter how many other connections share the pool.
 */
@Mixin(targets = "com.cobblemon.mod.fabric.net.CobblemonFabricNetworkManager", remap = false)
public abstract class NetworkCapabilityMixin {

    @Inject(method = "sendPacketToPlayer", at = @At("HEAD"))
    private void evuncapped$markCapability(ServerPlayerEntity player, NetworkPacket<?> packet, CallbackInfo ci) {
        boolean capable = ServerPlayNetworking.canSend(player, EvUncappedPresencePayload.ID);
        Channel channel = evuncapped$channelOf(player);
        if (channel == null) {
            return;
        }
        channel.eventLoop().execute(() -> NetworkCapability.set(capable));
    }

    @Inject(method = "sendPacketToPlayer", at = @At("RETURN"))
    private void evuncapped$clearCapability(ServerPlayerEntity player, NetworkPacket<?> packet, CallbackInfo ci) {
        Channel channel = evuncapped$channelOf(player);
        if (channel == null) {
            return;
        }
        channel.eventLoop().execute(NetworkCapability::clear);
    }

    private static Channel evuncapped$channelOf(ServerPlayerEntity player) {
        if (player.networkHandler == null) {
            return null;
        }
        ClientConnection connection = ((ServerCommonNetworkHandlerAccessor) player.networkHandler).evuncapped$getConnection();
        return ((ClientConnectionAccessor) connection).evuncapped$getChannel();
    }
}
