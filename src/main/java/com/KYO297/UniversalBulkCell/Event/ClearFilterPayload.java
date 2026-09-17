package com.KYO297.UniversalBulkCell.Event;

import appeng.api.storage.cells.CellState;
import appeng.core.definitions.AEItems;
import com.KYO297.UniversalBulkCell.Cell.BulkCellItem;
import com.KYO297.UniversalBulkCell.UniversalBulkCell;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.KYO297.UniversalBulkCell.UniversalBulkCell.MODID;

@SuppressWarnings("unused")
public record ClearFilterPayload() implements CustomPacketPayload {
    public static final Type<ClearFilterPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "clear_filter"));

    public static final StreamCodec<ByteBuf, ClearFilterPayload> STREAM_CODEC =
            StreamCodec.unit(new ClearFilterPayload());

    public static void handle(ClearFilterPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            ItemStack held = player.getMainHandItem();
            if (!(held.getItem() instanceof BulkCellItem)) return;

            var inv = BulkCellItem.getInventory(held);
            if (inv == null) return;

            if (inv.getStatus() != CellState.EMPTY) {
                player.displayClientMessage(Component.translatable("message.universalbulkcell.not_empty"), true);
                return;
            }

            if (inv.isVoidCardInstalled()) {
                player.getInventory().placeItemBackInInventory(new ItemStack(AEItems.VOID_CARD));
            }

            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(UniversalBulkCell.CELL.get()));
        });
    }

    @Override
    public @NotNull Type<ClearFilterPayload> type() {
        return TYPE;
    }
}