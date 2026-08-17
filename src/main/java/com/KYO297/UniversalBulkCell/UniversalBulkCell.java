package com.KYO297.UniversalBulkCell;

import appeng.api.client.StorageCellModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import com.KYO297.UniversalBulkCell.Cell.BulkCellItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(UniversalBulkCell.MODID)
public class UniversalBulkCell {
    public static final String MODID = "universalbulkcell";

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> CELL = ITEMS.register("universal_bulk_cell", () -> new BulkCellItem(new Item.Properties()));
    public static final RegistryObject<Item> COMPONENT = ITEMS.register("universal_bulk_component", () -> new Item(new Item.Properties()));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.universalbulkcell"))
                    .icon(() -> new ItemStack(CELL.get()))
                    .displayItems((params, output) -> {
                        output.accept(CELL.get());
                        output.accept(COMPONENT.get());
                    })
                    .build());

    public UniversalBulkCell(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BulkCellItem.registerHandler();
            Upgrades.add(AEItems.VOID_CARD, CELL.get(), 1);
            StorageCellModels.registerModel(CELL.get(), ResourceLocation.fromNamespaceAndPath(MODID, "block/drive_cell"));
        });
    }
}
