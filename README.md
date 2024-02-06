# Improved Anvils

Removes "TOO EXPENSIVE" and re-balances enchantment and repair costs.

## Installation

This mod must be installed on the server.

If you do not install it on the client, you will still see "TOO EXPENSIVE",
but it will not prevent you from performing the action if you have the levels to do it.

The Fabric API is required.

## Purpose

Anvils in vanilla Minecraft are plagued with two main issues:

1. Each time an item is repaired or enchanted using an anvil,
   the cost of the next anvil usage for that item goes up exponentially.
2. The game will continue to use this elevated cost for renaming an item;
   a purely cosmetic action.
3. Once the cost goes above 40 levels, you are greeted with "TOO EXPENSIVE",
   even if the player is in possession of upwards of 200 levels.
   Further enchantments or repairs are now impossible in survival mode.

These issues make it so that repairing items with an anvil and
combining low level enchantments together are functionally useless,
as you quickly approach the "TOO EXPENSIVE" cap with half-enchanted gear.

There is also, what can only be described as, a bug in vanilla.
The game will prevent you from combining two items that cannot be combined;
however, it will gladly allow you to rename the first item, destroying the second.

This mod seeks to fix these issues.

## Changes

### General

- Anvil costs do not accumulate from anvil usages.
- Anvil costs are no longer capped at 40 levels.

### Enchanting Items

- Enchantment cost is fixed per enchantment, per tier of the enchantment.
  Enchantments that are more "rare" or more over-powered are more expensive.
  Examples:
    - Mending, the most OP enchantment, costs 20 levels.
    - Protection, objectively better for armor, costs 4 levels per tier. Protection IV costs 16 levels.
    - Fortune, a solid enchantment, costs 3 levels per tier. Fortune III costs 9 levels.

### Combining Items

- Enchantments will be transferred at the same cost it would take to apply them from a book.
- Durability will be shared in the vanilla way,
  with the cost being 1 level per 1/8th of the item's max durability added.

### Repairing Items

- As with vanilla, each material will repair 1/4th of the item's max durability.
- The cost of a repair is 2 levels per material.

### Renaming Items

- Renaming simply costs 1 level.
- Renaming will no longer allow you to delete an item while getting nothing from it.

## Credit

This mod was heavily inspired by [AnvilFix](https://github.com/googleooer/AnvilFix).

