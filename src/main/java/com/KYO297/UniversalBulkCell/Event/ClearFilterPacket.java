package com.KYO297.UniversalBulkCell.Event;

import appeng.core.definitions.AEItems;
import com.KYO297.UniversalBulkCell.Cell.BulkCellInventory;
import com.KYO297.UniversalBulkCell.Cell.BulkCellItem;
import com.KYO297.UniversalBulkCell.UniversalBulkCell;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class ClearFilterPacket {
    public static void encode(ClearFilterPacket msg, FriendlyByteBuf buf) {
    }

    public static ClearFilterPacket decode(FriendlyByteBuf buf) {
        return new ClearFilterPacket();
    }

    public static void handle(ClearFilterPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof BulkCellItem) {
                BulkCellInventory inv = (BulkCellInventory) BulkCellItem.getHandler().getCellInventory(stack, null);
                if (inv != null && inv.getStorageKey() == null) {
                    var playerInv = player.getInventory();
                    if (inv.isVoidCardInstalled()) {
                        playerInv.placeItemBackInInventory(new ItemStack(AEItems.VOID_CARD));
                    }
                    player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(UniversalBulkCell.CELL.get()));
//                    player.containerMenu.broadcastChanges();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}