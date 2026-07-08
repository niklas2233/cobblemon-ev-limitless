package com.evuncapped;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.pokemon.EVs;

import java.util.Map;

/**
 * Vanilla Cobblemon clients (anyone without this mod) still reject any EVs whose
 * total exceeds 510 when decoding network packets, even though this mod's server-side
 * fix allows Pokemon to actually hold more. Rather than let that decode fail (which
 * disconnects the client), every packet that sends EVs over the wire uses this to send
 * a lossy, clamped-to-510 snapshot instead. The real, uncapped values stay untouched
 * in the Pokemon's persisted data - only what's put on the wire is capped.
 */
public final class NetworkEvClamp {
    private NetworkEvClamp() {}

    public static EVs clamp(EVs source) {
        EVs clamped = EVs.createEmpty();
        int remaining = EVs.MAX_TOTAL_VALUE;
        for (Map.Entry<? extends Stat, ? extends Integer> entry : source) {
            if (remaining <= 0) break;
            int value = Math.min(entry.getValue(), Math.min(EVs.MAX_STAT_VALUE, remaining));
            if (value > 0) {
                clamped.set(entry.getKey(), value);
                remaining -= value;
            }
        }
        return clamped;
    }
}
