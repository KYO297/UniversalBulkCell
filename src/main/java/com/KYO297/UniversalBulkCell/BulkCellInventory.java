package com.KYO297.UniversalBulkCell;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import appeng.core.definitions.AEItems;
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
        if (filterKey == null) {
            if (storageKey == null || what == null) return false;
            else return what.equals(storageKey);
        }
        if (isFilterMismatched()) return false;
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

    // STRING CONVERSIONS

    public String toExactString() {
        if (storageKey == null) return "0";
        final long[] div = storage.divideByInt(storageKey.getAmountPerUnit());
        final long hi = div[0];
        final long lo = div[1];
        final double rem = (double) div[2] / storageKey.getAmountPerUnit();
        if (hi == 0 && lo >= 0 && rem == 0) return appendUnit(Long.toString(lo));

        final long M32 = 0xFFFFFFFFL;   // isolates the low 32 bits as unsigned
        char[] buf = new char[39];      // 2^128 − 1 has 39 decimal digits
        int pos = 39;

        long cHi = hi, cLo = lo;

        while (cHi != 0 || cLo != 0) {

            // Decompose into four unsigned 32-bit limbs, most-significant first.
            long a3 = cHi >>> 32, a2 = cHi & M32;
            long a1 = cLo >>> 32, a0 = cLo & M32;

            // --- base-2^32 long division by 10 ---
            // n = (remainder_from_previous_limb << 32) | current_limb
            // n is always < 10 * 2^32 < 2^36, so it fits in a positive long.
            long n, r, q3, q2, q1, q0;

            n = a3;
            q3 = n / 10;
            r = n % 10;

            n = (r << 32) | a2;
            q2 = n / 10;
            r = n % 10;

            n = (r << 32) | a1;
            q1 = n / 10;
            r = n % 10;

            n = (r << 32) | a0;
            q0 = n / 10;
            r = n % 10;

            buf[--pos] = (char) ('0' + r);    // lowest decimal digit of current value
            cHi = (q3 << 32) | q2;
            cLo = (q1 << 32) | q0;
        }

        String ret = new String(buf, pos, 39 - pos);

        if (rem != 0) {
            final int digits = (int) Math.ceil(Math.log10(storageKey.getAmountPerUnit()));
            ret = ret + String.format("%." + digits + "f", rem).substring(1);
        }

        return appendUnit(ret);
    }

    public String toMetricString() {
        if (storageKey == null) {
            if (filterKey != null) {
                return appendUnit("0");
            } else {
                return "Empty";
            }
        }

        String ret = MetricFormatter.formatMetric(storage.doubleValue() / storageKey.getAmountPerUnit());

        if (Character.isDigit(ret.charAt(ret.length() - 1))) {
            ret = appendUnit(ret);
        } else if (storageKey.getUnitSymbol() != null) {
            ret = ret + storageKey.getUnitSymbol();
        }

        return ret;
    }

    private String appendUnit(String s) {
        String unitSymbol = storageKey != null ? storageKey.getUnitSymbol() : null;
        if (unitSymbol == null || unitSymbol.isEmpty()) return s;
        return s + " " + unitSymbol;
    }

    private static class MetricFormatter {
        private static final String[] PREFIXES = {"y", "z", "a", "f", "p", "n", "µ", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q"};

        private static final double[] POWERS_OF_10 = {1e-24, 1e-21, 1e-18, 1e-15, 1e-12, 1e-9, 1e-6, 1e-3, 1.0, 1e3, 1e6, 1e9, 1e12, 1e15, 1e18, 1e21, 1e24, 1e27, 1e30};

        private static final int ZERO_INDEX = 8;
        private static final int MAX_INDEX = PREFIXES.length - 1;

        private static final ThreadLocal<CacheState> CACHE = ThreadLocal.withInitial(CacheState::new);

        private static String formatMetric(double value) {
            CacheState cache = CACHE.get();

            if (value == cache.lastValue) {
                return cache.lastResult;
            }

            String result = formatDirect(value);

            cache.lastValue = value;
            cache.lastResult = result;
            return result;
        }

        private static String formatDirect(double value) {
            // 1. Calculate engineering exponent bucket (index into lookup tables)
            int prefixIndex = (int) Math.floor(Math.log10(value) / 3.0) + ZERO_INDEX;

            // Clamp to available SI prefix array bounds
            if (prefixIndex < 0) prefixIndex = 0;
            else if (prefixIndex > MAX_INDEX) prefixIndex = MAX_INDEX;

            double scaled = value / POWERS_OF_10[prefixIndex];

            // 2. Handle rounding boundary edge cases (e.g. 999.6 -> 1.00 k)
            if (scaled >= 999.5 && prefixIndex < MAX_INDEX) {
                scaled /= 1000.0;
                prefixIndex++;
            }

            // 3. Fast char array construction (no String.format, no StringBuilder)
            char[] buf = new char[8];
            int len;

            if (scaled >= 99.5) {
                // Pattern: DDD (e.g., 100, 150, 999)
                long val = Math.round(scaled);
                buf[0] = (char) ('0' + (val / 100));
                buf[1] = (char) ('0' + ((val / 10) % 10));
                buf[2] = (char) ('0' + (val % 10));
                len = 3;
            } else if (scaled >= 9.95) {
                // Pattern: DD.D (e.g., 15.0, 99.5)
                long val = Math.round(scaled * 10.0);
                buf[0] = (char) ('0' + (val / 100));
                buf[1] = (char) ('0' + ((val / 10) % 10));
                buf[2] = '.';
                buf[3] = (char) ('0' + (val % 10));
                len = 4;
            } else {
                // Pattern: D.DD (e.g., 1.23, 1.00)
                long val = Math.round(scaled * 100.0);
                buf[0] = (char) ('0' + (val / 100));
                buf[1] = '.';
                buf[2] = (char) ('0' + ((val / 10) % 10));
                buf[3] = (char) ('0' + (val % 10));
                len = 4;
            }

            // 4. Append SI prefix character directly
            String prefix = PREFIXES[prefixIndex];
            if (!prefix.isEmpty()) {
                buf[len++] = ' ';
                buf[len++] = prefix.charAt(0);
            }

            return new String(buf, 0, len);
        }

        private static class CacheState {
            double lastValue = 0;
            String lastResult = "0";
        }
    }
}
