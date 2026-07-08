package com.evuncapped.net;

/**
 * Set around each per-player Cobblemon packet send (see NetworkCapabilityMixin) so the
 * EVs/Pokemon codec wrappers know whether the current recipient's client has this mod
 * installed and can therefore be trusted with real, uncapped EV data.
 */
public final class NetworkCapability {
    private static final ThreadLocal<Boolean> CAPABLE = ThreadLocal.withInitial(() -> false);

    private NetworkCapability() {}

    public static void set(boolean capable) {
        CAPABLE.set(capable);
    }

    public static boolean isCapable() {
        return CAPABLE.get();
    }

    public static void clear() {
        CAPABLE.remove();
    }
}
