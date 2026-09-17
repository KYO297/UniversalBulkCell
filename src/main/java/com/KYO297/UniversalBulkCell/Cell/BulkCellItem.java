package com.KYO297.UniversalBulkCell.Cell;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.items.AEBaseItem;
import appeng.items.contents.CellConfig;
import appeng.util.ConfigInventory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class BulkCellItem extends AEBaseItem implements ICellWorkbenchItem {
    private static final ICellHandler HANDLER = new BulkCellHandler();

    public BulkCellItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void registerHandler() {
        StorageCells.addCellHandler(HANDLER);
    }

    public static BulkCellInventory getInventory(ItemStack stack) {
        return (BulkCellInventory) HANDLER.getCellInventory(stack, null);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(AEKeyTypes.getAll(), is, 1);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 1);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltip,
                                TooltipFlag advancedTooltips) {

        BulkCellInventory inv = (BulkCellInventory) HANDLER.getCellInventory(stack, null);
        if (inv == null) return;

        boolean detailed = Screen.hasShiftDown();
        Component attackKey = Minecraft.getInstance().options.keyAttack.getTranslatedKeyMessage();
        Component crouchKey = Minecraft.getInstance().options.keyShift.getTranslatedKeyMessage();

        if (inv.isNew()) {
            tooltip.add(Component.translatable("tooltip.universalbulkcell.empty"));
            tooltip.add(Component.translatable("tooltip.universalbulkcell.locks_on_insert"));
            tooltip.add(Component.translatable("tooltip.universalbulkcell.editable"));
            if (inv.isVoidCardInstalled()) {
                tooltip.add(Component.translatable("tooltip.universalbulkcell.void_card_installed"));
            }
            tooltip.add(Component.translatable("tooltip.universalbulkcell.clear_config", crouchKey, attackKey));
            return;
        }

        Component contents = Component.literal(detailed ? inv.toExactString() : inv.toMetricString()).withStyle(ChatFormatting.BLUE);

        if (inv.isPreFiltered()) {
            tooltip.add(Component.translatable("tooltip.universalbulkcell.contents", inv.getFilterKey().getDisplayName().copy().withStyle(ChatFormatting.BLUE)));
            tooltip.add(Component.translatable("tooltip.universalbulkcell.quantity", contents));
            if (inv.isVoidCardInstalled()) {
                tooltip.add(Component.translatable("tooltip.universalbulkcell.void_card_installed"));
            }
            tooltip.add(Component.translatable("tooltip.universalbulkcell.clear_config", crouchKey, attackKey));
            return;
        }

        tooltip.add(Component.translatable("tooltip.universalbulkcell.contents", inv.getStorageKey().getDisplayName().copy().withStyle(ChatFormatting.BLUE)));
        tooltip.add(Component.translatable("tooltip.universalbulkcell.quantity", contents));

        if (detailed) {
            tooltip.add(Component.translatable("tooltip.universalbulkcell.percentage_filled", inv.percentageFilled()));
        } else {
            tooltip.add(Component.translatable("tooltip.universalbulkcell.details").withStyle(ChatFormatting.DARK_GRAY));
        }

        if (inv.isVoidCardInstalled()) {
            tooltip.add(Component.translatable("tooltip.universalbulkcell.void_card_installed"));
        }

        if (inv.isFilterMismatched()) {
            tooltip.add(Component.translatable("tooltip.universalbulkcell.filter_mismatch").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
    }

    private static class BulkCellHandler implements ICellHandler {
        private BulkCellHandler() {
        }

        @Override
        public boolean isCell(ItemStack is) {
            return is != null && is.getItem() instanceof BulkCellItem;
        }

        @Override
        public @Nullable StorageCell getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
            return isCell(is) ? new BulkCellInventory(is, host) : null;
        }
    }
}
