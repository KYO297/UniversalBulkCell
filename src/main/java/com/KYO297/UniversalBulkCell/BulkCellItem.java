package com.KYO297.UniversalBulkCell;

import appeng.api.config.FuzzyMode;
import appeng.api.stacks.AEKey;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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

    @Override
    public ConfigInventory getConfigInventory(ItemStack is) {
        return CellConfig.create(null, is, 1);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        return UpgradeInventories.forItem(is, 1);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        final BulkCellInventory inv = (BulkCellInventory) HANDLER.getCellInventory(stack, null);
        if (inv == null) return;

        if (inv.isNew()) {// TODO EMPTY
        } else if (inv.isPreFiltered()) {// TODO EMPTY BUT FILTERED
        } else if (inv.isFilterMismatched()) {// TODO show error
        } else {
            AEKey stored = inv.getStorageKey();
            AEKey filter = inv.getFilterKey();

            // TODO display stored amount, metric default, exact on shift
        }
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
    }

    public static class BulkCellHandler implements ICellHandler {
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
