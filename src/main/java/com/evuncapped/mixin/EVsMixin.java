package com.evuncapped.mixin;

import com.evuncapped.NetworkEvClamp;
import com.evuncapped.net.NetworkCapability;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.PokemonStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Removes Cobblemon's 510 total-EV cap. Per-stat cap (252) is untouched, so max total
 * becomes 6 * 252 = 1512. Patches both the live gain path (canSet/performAdd) and the
 * save/load codec, which independently rejects any EV map summing above 510.
 */
@Mixin(targets = "com.cobblemon.mod.common.pokemon.EVs", remap = false)
public abstract class EVsMixin {

    @Inject(method = "canSet", at = @At("HEAD"), cancellable = true)
    private void evuncapped$canSet(Stat stat, int value, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(value >= 0 && value <= EVs.MAX_STAT_VALUE);
    }

    @Inject(method = "performAdd", at = @At("HEAD"), cancellable = true)
    private void evuncapped$performAdd(Stat stat, int value, CallbackInfoReturnable<Integer> cir) {
        PokemonStats self = (PokemonStats) (Object) this;
        int currentStat = self.getOrDefault(stat);
        int possibleForStat = EVs.MAX_STAT_VALUE - currentStat;
        int coercedValue = Math.max(-currentStat, Math.min(value, possibleForStat));
        int newValue = currentStat + coercedValue;
        if (newValue != currentStat) {
            self.set(stat, newValue);
            cir.setReturnValue(coercedValue);
        } else {
            cir.setReturnValue(0);
        }
    }

    @ModifyConstant(method = "CODEC$lambda$0", constant = @Constant(intValue = 510))
    private static int evuncapped$codecTotalCap(int originalCap) {
        return Integer.MAX_VALUE;
    }

    // access$getSTREAM_CODEC$cp is the single real source behind both EVs.getSTREAM_CODEC()
    // and EVs.Companion.getSTREAM_CODEC() - both delegate here, so patching it covers every caller.
    @Inject(method = "access$getSTREAM_CODEC$cp", at = @At("RETURN"), cancellable = true)
    private static void evuncapped$networkSafeStreamCodec(CallbackInfoReturnable<PacketCodec<ByteBuf, EVs>> cir) {
        PacketCodec<ByteBuf, EVs> real = cir.getReturnValue();
        cir.setReturnValue(new PacketCodec<ByteBuf, EVs>() {
            @Override
            public EVs decode(ByteBuf buffer) {
                return real.decode(buffer);
            }

            @Override
            public void encode(ByteBuf buffer, EVs value) {
                real.encode(buffer, NetworkCapability.isCapable() ? value : NetworkEvClamp.clamp(value));
            }
        });
    }
}
