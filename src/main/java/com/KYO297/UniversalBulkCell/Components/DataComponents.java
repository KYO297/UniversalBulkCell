package com.KYO297.UniversalBulkCell.Components;

import appeng.api.stacks.AEKey;
import com.KYO297.UniversalBulkCell.Cell.UInt128;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.KYO297.UniversalBulkCell.UniversalBulkCell.MODID;

public abstract class DataComponents {
    public static final DeferredRegister<DataComponentType<?>> DR =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);

    public static final DataComponentType<AEKey> CELL_ITEM =
            register("cell_item", AEKey.CODEC, AEKey.STREAM_CODEC);

    public static final DataComponentType<UInt128> CELL_CONTENTS =
            register("cell_contents", UInt128Codecs.CODEC, UInt128Codecs.STREAM_CODEC);

    private static <T> DataComponentType<T> register(String name,
                                                     Codec<T> codec,
                                                     StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {

        DataComponentType<T> componentType = DataComponentType.<T>builder()
                .persistent(codec)
                .networkSynchronized(streamCodec)
                .build();

        DR.register(name, () -> componentType);
        return componentType;
    }
}
