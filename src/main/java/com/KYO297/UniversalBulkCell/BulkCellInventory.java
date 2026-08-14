package com.KYO297.UniversalBulkCell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.core.definitions.AEItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class BulkCellInventory implements StorageCell {
    private final UInt128 counter;
    private final ItemStack cellStack;
    private final ISaveProvider host;
    private final String storageKeyTag = "key";
    private final String amtHiTag = "hi";
    private final String amtLoTag = "lo";
    private AEKey storageKey;
    private AEKey filterKey;
    private boolean isPersisted = true;
    private boolean voidCardInstalled = false;


    public BulkCellInventory(ItemStack cellStack, ISaveProvider host) {
        this.cellStack = cellStack;
        this.host = host;
        final var tag = cellStack.getTag();
        final var cell = (BulkCellItem) cellStack.getItem();
        filterKey = cell.getConfigInventory(cellStack).getKey(0);
        voidCardInstalled = cell.getUpgrades(cellStack).isInstalled(AEItems.VOID_CARD);
        if (tag != null && tag.contains(storageKeyTag)) {
            storageKey = AEKey.fromTagGeneric(tag.getCompound(storageKeyTag));
            final long hi = tag.getLong(amtHiTag);
            final long lo = tag.getLong(amtLoTag);
            counter = new UInt128(hi, lo);
        } else {
            storageKey = null;
            counter = new UInt128();
        }
    }

    @Override
    public CellState getStatus() {
        if (counter.isEmpty()) return CellState.EMPTY;
        if (counter.isFull() || isFilterMismatched()) return CellState.FULL;
        return CellState.NOT_EMPTY;
    }

    @Override
    public double getIdleDrain() {
        return 2048.0d;
    }

    @Override
    public void persist() {
        if (isPersisted) return;

        var tag = cellStack.getOrCreateTag();
        if (storageKey != null) {
            tag.put(storageKeyTag, storageKey.toTagGeneric());
            tag.putLong(amtHiTag, counter.getHi());
            tag.putLong(amtLoTag, counter.getLo());
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
        if (filterKey == null || isFilterMismatched()) return false;
        return filterKey.equals(what);
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!what.equals(filterKey) || isFilterMismatched()) return 0;

        switch (mode) {
            case MODULATE -> {
                if (storageKey == null) {
                    storageKey = what;
                    filterKey = what;
                    // TODO set item in filter slot (how?)
                }
                final long ret = counter.insert(amount, false);
                saveChanges();
                if (voidCardInstalled) return amount;
                return ret;
            }
            case SIMULATE -> {
                if (voidCardInstalled) return amount;
                return counter.insert(amount, true);
            }
            default -> {
                // TODO log warn unknown mode (or ignore?)
                return 0;
            }
        }
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!what.equals(storageKey)) return 0;

        switch (mode) {
            case MODULATE -> {
                final long ret = counter.extract(amount, false);
                if (counter.isEmpty()) storageKey = null;
                saveChanges();
                return ret;
            }
            case SIMULATE -> {
                return counter.extract(amount, true);
            }
            default -> {
                // TODO log warn
                return 0;
            }
        }
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (storageKey != null) out.add(storageKey, counter.longValue());
    }

    @Override
    public KeyCounter getAvailableStacks() {
        final KeyCounter out = new KeyCounter();
        getAvailableStacks(out);
        return out;
    }

    @Override
    public Component getDescription() {
        // TODO
        return null;
    }

    private boolean isFilterMismatched() {
        return storageKey != null && !storageKey.equals(filterKey);
    }
}
