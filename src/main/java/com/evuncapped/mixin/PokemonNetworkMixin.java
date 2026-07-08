package com.evuncapped.mixin;

import com.evuncapped.NetworkEvClamp;
import com.evuncapped.net.NetworkCapability;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Full-Pokemon sync packets (party, PC, battle, trade, summary) all funnel through
 * Pokemon.S2C_CODEC. Unlike EVsMixin's stream codec (which encodes an EVs value
 * directly), this one encodes a whole Pokemon, so the EVs field has to be swapped
 * to a clamped copy for the duration of the real encode call, then restored.
 */
@Mixin(targets = "com.cobblemon.mod.common.pokemon.Pokemon", remap = false)
public abstract class PokemonNetworkMixin {

    // access$getS2C_CODEC$cp is the single real source behind both Pokemon.getS2C_CODEC()
    // and Pokemon.Companion.getS2C_CODEC() - both delegate here, so patching it covers every caller.
    @Inject(method = "access$getS2C_CODEC$cp", at = @At("RETURN"), cancellable = true)
    private static void evuncapped$networkSafeS2CCodec(CallbackInfoReturnable<PacketCodec<RegistryByteBuf, Pokemon>> cir) {
        PacketCodec<RegistryByteBuf, Pokemon> real = cir.getReturnValue();
        cir.setReturnValue(new PacketCodec<RegistryByteBuf, Pokemon>() {
            @Override
            public Pokemon decode(RegistryByteBuf buffer) {
                return real.decode(buffer);
            }

            @Override
            public void encode(RegistryByteBuf buffer, Pokemon value) {
                if (NetworkCapability.isCapable()) {
                    real.encode(buffer, value);
                    return;
                }
                EVs original = value.getEvs();
                value.setEvs$common(NetworkEvClamp.clamp(original));
                try {
                    real.encode(buffer, value);
                } finally {
                    value.setEvs$common(original);
                }
            }
        });
    }
}
