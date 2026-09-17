package com.KYO297.UniversalBulkCell.Event;

import com.KYO297.UniversalBulkCell.Cell.BulkCellItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent.InteractionKeyMappingTriggered;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.KYO297.UniversalBulkCell.UniversalBulkCell.MODID;
import static net.neoforged.api.distmarker.Dist.CLIENT;

@EventBusSubscriber(modid = MODID, value = CLIENT)
public class AttackEventListener {
    @SubscribeEvent
    public static void onLeftClick(InteractionKeyMappingTriggered event) {
        if (event == null || !event.isAttack()) return;

        Player player = Minecraft.getInstance().player;
        if (player == null || !player.isShiftKeyDown()) return;

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof BulkCellItem)) return;

        event.setCanceled(true);
        event.setSwingHand(false);
        PacketDistributor.sendToServer(new ClearFilterPayload());
    }
}
