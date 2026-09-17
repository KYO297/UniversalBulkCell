package com.KYO297.UniversalBulkCell.Components;

import com.KYO297.UniversalBulkCell.Cell.UInt128;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class UInt128Codecs {
    public static final Codec<UInt128> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.LONG.fieldOf("hi").forGetter(UInt128::getHi),
                    Codec.LONG.fieldOf("lo").forGetter(UInt128::getLo)
            ).apply(instance, UInt128::new));

    private static final StreamCodec<ByteBuf, Long> LONG = new StreamCodec<>() {
        public @NotNull Long decode(ByteBuf buf) {
            return buf.readLong();
        }

        public void encode(ByteBuf buf, @NotNull Long val) {
            buf.writeLong(val);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, UInt128> STREAM_CODEC = StreamCodec.composite(
            LONG, UInt128::getHi,
            LONG, UInt128::getLo,
            UInt128::new);
}
