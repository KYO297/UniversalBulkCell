package com.KYO297.UniversalBulkCell;

import appeng.api.config.FuzzyMode;
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
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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
        // TODO check if correct
        return CellConfig.create(null, is, 1);
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack is) {
        // TODO check if correct (only want void card allowed - how?)
        return UpgradeInventories.forItem(is, 1);
    }

    // TODO tooltip display amount

    @Override
    public FuzzyMode getFuzzyMode(ItemStack is) {
        return null;
    }

    @Override
    public void setFuzzyMode(ItemStack is, FuzzyMode fzMode) {
    }

    // TODO handler copied from mega item bulk cell - correct?
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
