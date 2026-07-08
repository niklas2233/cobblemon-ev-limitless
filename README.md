# Cobblemon EV Limitless

Removes Cobblemon's 510 total EV cap on Fabric 1.21.1 (per-stat cap of 252 is untouched,
so the max total becomes 6 * 252 = 1512).

Server-required, client-optional: install on the server alone and every player gets a
safe, clamped-to-510 view of any Pokemon over the cap. Also install on a client and that
player sees the real, uncapped values instead.

## Building

Requires a local copy of `Cobblemon-fabric-1.7.3+1.21.1.jar` (from
[Modrinth](https://modrinth.com/mod/cobblemon)) placed at `libs/Cobblemon-fabric-1.7.3+1.21.1.jar`
before building - it's not redistributed in this repo or resolved via Modrinth's Maven
(that coordinate resolves to a NeoForge build, not Fabric, for this version).

```
./gradlew build
```

Output jar lands in `build/libs/`.
