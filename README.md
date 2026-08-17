# Universal Bulk Storage Cell

![UBSC](https://github.com/KYO297/UniversalBulkCell/blob/master/icon.png)

Universal Bulk Storage Cell is an add-on for [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2).
It adds a single functional item - the titular Universal Bulk Storage Cell.
It is a bulk cell - it stores a large amount of a single thing.
It is universal - it can store anything that is storable within AE2, not just items. Thanks to AE2's API, it supports every present and future storable type.

It's powered by a custom 128-bit integer class, giving the cell a capacity of 3.4 × 10³⁸ units of whatever you wish to store inside. For perfomance reasons, it's not infinite.

It features automatic filtering logic - an empty cell will automatically lock onto the first thing inserted into it, and will stay filtered even when fully emptied. The filter can be set beforehand, or changed afterward, in a Cell Workbench.

Should the stored amount exceed AE2's display limit (9.2 × 10¹⁸), the full amount is displayed in the item tooltip.

#### vs MEGA Bulk Item Storage Cell

Advantages:

1. The Universal Cell can store any type, not just items.
2. The cell automatically filters itself to the first item inserted, there's no need to manually set the filter on each cell when they're used on a subnet where every item is stored in bulk cells.
3. Doesn't use Java's BigInteger class - minimized impact on performance

Disadvantages:
1. The capacity is technically not infinite (though not realistically fillable)
2. There's no compression card (and it is not planned to be added)

#### Licensing
Assets in this mod are derived from Applied Energistics 2 (Copyright AlgorithmX2 and contributors), licensed under CC BY-NC-SA 3.0.
These derived assets are released under the same license.

As portions of the code are inspired by/derived from AE2's own code, this mod's code is also licensed under LGPLv3 - see LICENSE.


#### AI Use Disclaimer
As this is my first modding project, I have extensively used LLMs to understand Forge's and AE2's API and code.
However, the majority of the code is written by me, and the parts that are partially/entirely AI generated have been proofread and verified to work as intended.
AI has not been involved in any way in creating the recipes, textures and models.