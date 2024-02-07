# Improved Anvils

Removes "TOO EXPENSIVE" and re-balances enchantment and repair costs.

## Installation

This mod must be installed on the server.

If you do not install it on the client, you will still see "TOO EXPENSIVE",
but it will not prevent you from performing the action if you have the XP to do it.

The Fabric API is required.

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

There is also, what can only be described as, a bug in vanilla.
The game will prevent you from combining two items that cannot be combined;
however, it will gladly allow you to rename the first item, destroying the second.

This mod seeks to fix these issues.

## Changes

### General

- Anvil costs do not accumulate from anvil usages.
- Anvil costs are no longer capped at 40 levels.
- Anvil costs are now based on points, not levels.

### Enchanting Items

- Enchantment cost is fixed per enchantment, per tier of the enchantment.
  Enchantments that are more "rare" or more over-powered are more expensive.
  Examples:
    - Mending, the most OP enchantment, costs 550 XP (level 0 to level 20).
    - Protection, objectively better for armor, costs 112.5 XP per tier. Protection IV costs 450 XP.
    - Fortune, a solid enchantment, costs 116.7 XP per tier. Fortune III costs 350 XP.
    - Bane of Arthropods, a niche enchantment, costs 50 XP per tier. Bane of Arthropods V costs 250 XP.

### Combining Items

- Enchantments will be transferred at the same cost it would take to apply them from a book.
- Durability will be combined in the vanilla way,
  with the cost being 1 XP per 4 durability added (half the cost of Mending).
    - If you combine any bow with an Infinity bow, to balance it with Mending,
      the cost of the repair will be 1 XP per 1 durability added (twice the cost of Mending).

### Repairing Items

- Shovels can now be completely repaired using 1 material.
- Swords and hoes repair 50% per material.
- Axes and pickaxes repair 33% per material.
- Everything else repairs 25% per material.
- The cost of a repair is 1 XP per 4 durability added (half the cost of Mending).


- Netherite items are now repaired using diamonds, for twice as many diamonds as the equivalent diamond item.
- Shields, Crossbows, and Flint & Steel can now be repaired using iron ingots; 100% per material.
- Shears can now be repaired using iron ingots; 50% per material.
- Tridents can now be repaired using nautilus shells; 25% per material.
- Fishing rods, Carrot on a stick, and Warped fungus on a stick can now be repaired using string; 50% per material.
- Bows can now be repaired using string; 33% per material.
    - If the bow has Infinity, to balance it with Mending,
      the cost of the repair will be 1 XP per 1 durability added (twice the cost of Mending).


- Any tool that can be repaired using iron or gold ingots can be repaired using nuggets for 1/9th as much durability per
  material.

### Renaming Items

- Renaming simply costs 1 XP.
- Renaming will no longer allow you to delete an item while getting nothing from it.

## Credit

This mod was heavily inspired by [AnvilFix](https://github.com/googleooer/AnvilFix).

