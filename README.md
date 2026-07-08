# Cobblemon EV Limitless

Removes Cobblemon's 510 total EV cap on Fabric 1.21.1 (per-stat cap of 252 is untouched,
so the max total becomes 6 * 252 = 1512).

Server-required, client-optional: install on the server alone and every player gets a
safe, clamped-to-510 view of any Pokemon over the cap. Also install on a client and that
player sees the real, uncapped values instead.

## Compatibility

Boot-tested against every Cobblemon Fabric release for Minecraft 1.21.1:

| Cobblemon version | Works? |
|---|---|
| 1.6.0 | No - `EVs.performAdd` doesn't exist in this version, mixin fails to apply |
| 1.6.1 | No - same reason |
| 1.7.0 | Yes |
| 1.7.1 | Yes |
| 1.7.2 | Yes |
| 1.7.3 | Yes |

Untested against anything newer than 1.7.3. The mixins target Cobblemon's internal method
names directly (including Kotlin-compiler-generated synthetic names), so any future
Cobblemon release can break this without warning - if it does, the server will fail to
boot with a clear Mixin error rather than silently misbehaving.

## Building

Requires a local copy of a compatible `Cobblemon-fabric-*.jar` (from
[Modrinth](https://modrinth.com/mod/cobblemon)) placed at `libs/Cobblemon-fabric-1.7.3+1.21.1.jar`
before building - it's not redistributed in this repo or resolved via Modrinth's Maven
(that coordinate resolves to a NeoForge build, not Fabric, for this version).

```
./gradlew build
```

Output jar lands in `build/libs/`.
