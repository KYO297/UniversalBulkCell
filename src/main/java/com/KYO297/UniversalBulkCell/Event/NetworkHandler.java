package com.KYO297.UniversalBulkCell.Event;

import com.KYO297.UniversalBulkCell.UniversalBulkCell;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(UniversalBulkCell.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        INSTANCE.registerMessage(0, ClearFilterPacket.class,
                ClearFilterPacket::encode, ClearFilterPacket::decode, ClearFilterPacket::handle);
    }
}
