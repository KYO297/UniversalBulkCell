package com.KYO297.UniversalBulkCell;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(UniversalBulkCell.MODID)
public class UniversalBulkCell {
    public static final String MODID = "UniversalBulkCell";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> CELL = ITEMS.register("universal_bulk_cell", () -> new BulkCellItem(new Item.Properties()));
    public static final RegistryObject<Item> HOUSING = ITEMS.register("universal_bulk_cell_housing", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT = ITEMS.register("universal_bulk_cell_component", () -> new Item(new Item.Properties()));


    private static void init() {
        Upgrades.add(AEItems.VOID_CARD,null, 1); // TODO item
    }

}
