---
navigation:
  title: Universal Bulk Storage Cell
  position: 100
  icon: universalbulkcell:universal_bulk_cell
item_ids:
  - universalbulkcell:universal_bulk_cell
---

# Universal Bulk Storage Cell

## Features

The universal bulk storage cell is capable of holding an enormous quantity of a single type. The capacity is not
infinite (see
Technical Details), but it's large enough that its fineniteness should never be a limitation. If, somehow, you manage to
fill one regardless, it supports Overflow Destruction Cards.

The cell is capable of storing any registered AEKey - Items and Fluids but also any type added by an addon, which
includes but is not limited to,
[Mekanism Chemicals](https://www.curseforge.com/minecraft/mc-mods/applied-mekanistics),
[FE](https://www.curseforge.com/minecraft/mc-mods/applied-flux),
[Botania Mana](https://www.curseforge.com/minecraft/mc-mods/applied-botanics-addon),
[Ars Nouveau Source](https://www.curseforge.com/minecraft/mc-mods/ars-energistique)
and [Industrial Foregoing Souls](https://www.curseforge.com/minecraft/mc-mods/soulplied-energistics).

The cell features automatic filtering – when something is inserted into an empty cell, the cell's filter will be set
accordingly and will remain set even when the cell is emptied afterward. If you wish to remove the filter, that can be
done in a Cell Workbench or by crouch-attacking (default shift + LMB) with an empty but filtered cell in your main hand
(this will also remove an installed card). This will not work if the cell has anything stored. The filter can also be
pre-set in the Workbench if you want to make sure the unfiltered cell doesn't lock onto and store anything else when
inserted into an ME Drive or Chest.

## Technical Details

The cell uses a custom UInt128 class to store the cell's contents. It is not infinite, but it holds up to
2&#x00B9;&#x00B2;&#x2078;-1 units (&#x2248;3.4&#x00D7;10&#x00B3;&#x2078;). Keep in mind that for some types the
storage unit differs from the display unit (for example, fluids are stored in mB but displayed in B).

Calculations are performed using basic addition/subtraction and bitwise operations, and not using Java's immutable
BigInteger which saves performance and garbage collector time.

Also for performance, compression is not included as a feature (it also donesn't make much sense for anything other than
items)