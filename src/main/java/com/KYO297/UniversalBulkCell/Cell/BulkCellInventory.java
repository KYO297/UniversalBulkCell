package com.KYO297.UniversalBulkCell.Cell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.core.definitions.AEItems;
import com.KYO297.UniversalBulkCell.StringFormatting.ExactFormatter;
import com.KYO297.UniversalBulkCell.StringFormatting.MetricFormatter;
import com.KYO297.UniversalBulkCell.StringFormatting.PercentageFormatter;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class BulkCellInventory implements StorageCell {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final UInt128 storage;
    private final ItemStack cellStack;
    private final BulkCellItem cellItem;
    private final ISaveProvider host;
    private final String storageKeyTag = "key";
    private final String amtHiTag = "hi";
    private final String amtLoTag = "lo";
    private final boolean voidCardInstalled;
    private AEKey storageKey;
    private AEKey filterKey;
    private boolean isPersisted = true;


    public BulkCellInventory(ItemStack is, ISaveProvider host) {
        this.cellStack = is;
        this.host = host;
        final var tag = cellStack.getTag();
        cellItem = (BulkCellItem) cellStack.getItem();
        filterKey = cellItem.getConfigInventory(cellStack).getKey(0);
        voidCardInstalled = cellItem.getUpgrades(cellStack).isInstalled(AEItems.VOID_CARD);
        if (tag != null && tag.contains(storageKeyTag)) {
            storageKey = AEKey.fromTagGeneric(tag.getCompound(storageKeyTag));
            final long hi = tag.getLong(amtHiTag);
            final long lo = tag.getLong(amtLoTag);
            storage = new UInt128(hi, lo);
        } else {
            storageKey = null;
            storage = new UInt128();
        }
    }

    public AEKey getStorageKey() {
        return storageKey;
    }

    public AEKey getFilterKey() {
        return filterKey;
    }

    public boolean isFilterMismatched() {
        return storageKey != null && !storageKey.equals(filterKey);
    }

    public boolean isNew() {
        return storageKey == null && filterKey == null;
    }

    public boolean isPreFiltered() {
        return storageKey == null && filterKey != null;
    }

    @Override
    public CellState getStatus() {
        if (storage.isEmpty()) return CellState.EMPTY;
        if (storage.isFull() || isFilterMismatched()) return CellState.FULL;
        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return 2048.0d;
    }

    @Override
    public boolean canFitInsideCell() {
        return storage.isEmpty();
    }

    @Override
    public void persist() {
        if (isPersisted) return;

        var tag = cellStack.getOrCreateTag();
        if (storageKey != null) {
            tag.put(storageKeyTag, storageKey.toTagGeneric());
            tag.putLong(amtHiTag, storage.getHi());
            tag.putLong(amtLoTag, storage.getLo());
        } else {
            tag.remove(storageKeyTag);
            tag.remove(amtHiTag);
            tag.remove(amtLoTag);
        }
        isPersisted = true;
    }

    private void saveChanges() {
        isPersisted = false;
        if (host != null) {
            host.saveChanges();
        } else {
            persist();
        }
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        if (isFilterMismatched() || what == null) return false;
        else return what.equals(filterKey);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if ((filterKey != null && !what.equals(filterKey)) || isFilterMismatched()) return 0;

        switch (mode) {
            case MODULATE -> {
                if (storageKey == null) {
                    storageKey = what;
                    filterKey = what;
                    cellItem.getConfigInventory(cellStack).setStack(0, new GenericStack(what, 1));
                }
                final long ret = storage.insert(amount, false);
                saveChanges();
                if (voidCardInstalled) return amount;
                return ret;
            }
            case SIMULATE -> {
                if (voidCardInstalled) return amount;
                return storage.insert(amount, true);
            }
            default -> {
                LOGGER.warn("Attempted to insert with an unknown or null Actionable mode: {}. No action taken.", mode);
                return 0;
            }
        }
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!what.equals(storageKey)) return 0;

        switch (mode) {
            case MODULATE -> {
                final long ret = storage.extract(amount, false);
                if (storage.isEmpty()) storageKey = null;
                saveChanges();
                return ret;
            }
            case SIMULATE -> {
                return storage.extract(amount, true);
            }
            default -> {
                LOGGER.warn("Attempted to extract with an unknown or null Actionable mode: {}. No action taken.", mode);
                return 0;
            }
        }
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (storageKey != null) out.add(storageKey, storage.longValue());
    }

    @Override
    public KeyCounter getAvailableStacks() {
        final KeyCounter out = new KeyCounter();
        getAvailableStacks(out);
        return out;
    }

    @Override
    public Component getDescription() {
        return cellStack.getHoverName();
    }

    public String toExactString() {
        if (storageKey == null) return appendUnit("0");
        final long[] div = storage.divideByInt(storageKey.getAmountPerUnit());
        final long hi = div[0];
        final long lo = div[1];
        final double rem = (double) div[2] / storageKey.getAmountPerUnit();

        String ret = ExactFormatter.format(new UInt128(hi, lo));

        if (rem != 0) {
            final int digits = (int) Math.ceil(Math.log10(storageKey.getAmountPerUnit()));
            String format = "%." + digits + "f";
            ret = ret + String.format(format, rem).substring(1);
        }

        return appendUnit(ret);
    }

    public String toMetricString() {
        if (storageKey == null && filterKey != null) return appendUnit("0");

        String ret = MetricFormatter.format(storage.doubleValue() / storageKey.getAmountPerUnit());

        if (Character.isDigit(ret.charAt(ret.length() - 1))) {
            ret = appendUnit(ret);
        } else if (storageKey.getUnitSymbol() != null) {
            ret = ret + storageKey.getUnitSymbol();
        }

        return ret;
    }

    public String percentageFilled() {
        return PercentageFormatter.format(storage.fillPercentage());
    }

    private String appendUnit(String s) {
        String unitSymbol = null;
        if (storageKey != null) unitSymbol = storageKey.getUnitSymbol();
        else if (filterKey != null) unitSymbol = filterKey.getUnitSymbol();
        if (unitSymbol == null || unitSymbol.isEmpty()) return s;
        else return s + " " + unitSymbol;
    }
}
