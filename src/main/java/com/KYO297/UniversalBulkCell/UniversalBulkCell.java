package com.KYO297.UniversalBulkCell;

import appeng.api.client.StorageCellModels;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import com.KYO297.UniversalBulkCell.Cell.BulkCellItem;
import com.KYO297.UniversalBulkCell.Event.ClearFilterPayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.KYO297.UniversalBulkCell.Components.DataComponents.DR;

@SuppressWarnings("unused")
@Mod(UniversalBulkCell.MODID)
public class UniversalBulkCell {
    public static final String MODID = "universalbulkcell";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<Item> CELL = ITEMS.register("universal_bulk_cell", () -> new BulkCellItem(new Item.Properties()));
    public static final DeferredItem<Item> COMPONENT = ITEMS.register("universal_bulk_component", () -> new Item(new Item.Properties()));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.universalbulkcell"))
                    .icon(() -> new ItemStack(CELL.get()))
                    .displayItems((params, output) -> {
                        output.accept(CELL.get());
                        output.accept(COMPONENT.get());
                    })
                    .build());

    public UniversalBulkCell(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        DR.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);
    }

    @SubscribeEvent
    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BulkCellItem.registerHandler();
            Upgrades.add(AEItems.VOID_CARD, CELL.get(), 1);
            StorageCellModels.registerModel(CELL.get(), ResourceLocation.fromNamespaceAndPath(MODID, "block/drive_cell"));
        });
    }

    @SubscribeEvent
    public void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(ClearFilterPayload.TYPE, ClearFilterPayload.STREAM_CODEC, ClearFilterPayload::handle);
    }
}
