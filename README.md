# Improved Anvils

A personal fork of [Improved Anvils](https://gitlab.com/DavidJMacDonald/improved-anvils) by David J MacDonald.
Removes "TOO EXPENSIVE" and re-balances enchantment/repair costs of anvils.

All credit and copyright for the original mod goes to David — see the [LICENSE](LICENSE) for details.

**Current version:** 1.1.2 for Minecraft 26.2 (Fabric)

## Installation

Requires [Fabric API](https://modrinth.com/mod/fabric-api). Must be installed on the server.

If not installed on the client, you'll still see "TOO EXPENSIVE",
but it won't prevent the action if you have the XP.

## Purpose

Anvils in vanilla Minecraft are plagued with a few issues:

1. Each time an item is repaired or enchanted using an anvil,
   the cost of the next anvil usage for that item goes up exponentially.
2. The game will continue to use this elevated cost for renaming an item;
   a purely cosmetic action.
3. Once the cost goes above 40 levels, you are greeted with "TOO EXPENSIVE",
   even if the player is in possession of upwards of 200 levels.
   Further enchantments or repairs are now impossible in survival mode.
4. Since anvils take levels, actions are more expensive if you have many levels.
   For example, an enchant that costs 10 levels will cost 15x more XP at level 50 than at level 10.
5. Netherite tools are repaired using netherite, which is very expensive.
   Tridents, among other tools, cannot be repaired using an anvil.

These issues make it so that repairing items with an anvil and
combining low level enchantments together are functionally useless,
as you quickly approach the "TOO EXPENSIVE" cap with half-enchanted gear.
They also encourage or force the use of Mending, which is already overpowered.

This mod seeks to fix these issues.

## Changes

### 1.21

Due to the nature of new enchantments, the mace enchantment costs may not be
in the correct place for game balance.
If you have suggestions, please let me know.

### General

- Anvil costs do not accumulate from anvil usages.
- Anvil costs are no longer capped at 40 levels.
- Anvil costs are now based on points, not levels.

### Enchanting Items

- Enchantment cost is fixed per enchantment, per tier of the enchantment.
- Enchantment costs max out at 100xp for vanilla enchantments, and 150xp for any additional enchantments

### Combining Items

- Enchantments will be transferred at the same cost it would take to apply them from a book.
- Durability will be combined in the vanilla way,
  with the cost being 1 XP per 4 durability added (half the cost of Mending).
    - If you combine any bow with an Infinity bow, to balance it with Mending,
      the cost of the repair will be 1 XP per 1 durability added (twice the cost of Mending).

### Repairing Items

Repair cost is **1 XP per 4 durability** restored (half the cost of Mending).

| Item | Material | Repair % |
|------|----------|----------|
| Shovels, Maces | Standard | 100% |
| Shields, Crossbows, Flint & Steel | Iron Ingot | 100% |
| Swords, Hoes | Standard | 50% |
| Shears | Iron Ingot | 50% |
| Fishing Rods, Carrot on a Stick, Warped Fungus on a Stick | String | 50% |
| Axes, Pickaxes | Standard | 33% |
| Bows | String | 33% |
| Tridents | Prismarine Shard | 25% |
| Everything else | Standard | 25% |

**Netherite:** Repaired with diamonds at half the repair rate (e.g. a netherite shovel takes 2 diamonds for full repair instead of 1). Set gamerule `repairNetheriteWithDiamonds` to `false` to revert to vanilla netherite ingot repairs.

**Nuggets:** Any item repairable with iron or gold ingots can also use nuggets at 1/9th the durability per nugget.

**Infinity bows:** Repair cost increases to 1 XP per 1 durability (4× normal) to balance against Mending.

### Renaming Items

- Renaming simply costs 1 XP.

### Customization Options

- Gamerule `repairNetheriteWithDiamonds`:
    - `true` (default): Netherite tools/armor can be repaired using diamonds and diamonds alone.
    - `false`: Netherite tools/armor can be repaired using netherite ingots and netherite ingots alone.

## Credit

This mod was heavily inspired by [AnvilFix](https://github.com/googleooer/AnvilFix).


## Building and testing (26.2)

The pinned toolchain is Java 25, Fabric Loom 1.17.21, Gradle 9.6.0,
Fabric Loader 0.19.5, and Fabric API 0.161.0+26.2. Minecraft 26.2 is
unobfuscated; this project uses the official names, not Yarn mappings.

On x86_64 Linux with Nix:

```sh
nix develop path:. -c ./gradlew clean build
nix flake check path:.
```

The in-repo `flake.nix` and `flake.lock` supply a pinned Java 25 environment.
They do not change the host configuration. The Gradle wrapper downloads Gradle
and project dependencies. This is a development/build shell, not an offline
`nix build` package. Without Nix, install Java 25 and run `./gradlew clean build`.
The distributable is `build/libs/Improved_Anvils-1.1.2-26.2.jar` (not `-sources.jar`).

`build` runs repair/XP arithmetic regression checks, Fabric Loader JUnit tests
with runtime mixin transformation, and a JAR-content check. These do not replace
an in-game client/server smoke test. Sources use the standard `src/main/java`,
`src/client/java`, and `src/main/resources` directories.

### Gamerule migration

26.x stores gamerules as registry identifiers. The canonical name is now
`improved_anvils:repair_netherite_with_diamonds`, still **true by default**.
The original command remains an alias:

```mcfunction
/gamerule repairNetheriteWithDiamonds false
/gamerule improved_anvils:repair_netherite_with_diamonds false
```

The saved legacy `repairNetheriteWithDiamonds` string is converted to the
canonical boolean during game-rule decoding. Both `true` and `false` are
preserved; an existing canonical value takes precedence. Back up worlds before
upgrading. Upgrade with this mod installed: a world already saved by 26.x
without this mod may have lost its unknown custom rule, which cannot be
recovered automatically. Check the rule after upgrading.

### Port references

- [Fabric 26.2 port guide](https://fabricmc.net/2026/06/15/262.html)
- [Fabric automated testing](https://docs.fabricmc.net/develop/automatic-testing)
- [Mojang version metadata](https://piston-meta.mojang.com/mc/game/version_manifest_v2.json)
