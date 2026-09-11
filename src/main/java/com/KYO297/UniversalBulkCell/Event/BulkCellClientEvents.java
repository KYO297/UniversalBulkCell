package com.KYO297.UniversalBulkCell.Event;

import com.KYO297.UniversalBulkCell.Cell.BulkCellInventory;
import com.KYO297.UniversalBulkCell.Cell.BulkCellItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.KYO297.UniversalBulkCell.UniversalBulkCell.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BulkCellClientEvents {
    @SubscribeEvent
    public static void onAttackKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) return;

        Player player = Minecraft.getInstance().player;
        if (player == null || !player.isCrouching()) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof BulkCellItem)) return;

        BulkCellInventory inv = (BulkCellInventory) BulkCellItem.getHandler().getCellInventory(stack, null);
        if (inv == null ) {
            return;
        }
        if (inv.getStorageKey() != null){
            player.displayClientMessage(Component.translatable("message.universalbulkcell.not_empty"),true);
        }


        event.setCanceled(true);
        event.setSwingHand(true);

        NetworkHandler.INSTANCE.sendToServer(new ClearFilterPacket());
    }
}