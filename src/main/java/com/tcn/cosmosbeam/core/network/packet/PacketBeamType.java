package com.tcn.cosmosbeam.core.network.packet;

import com.tcn.cosmosbeam.CosmosBeam;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketBeamType(BlockPos pos) implements CustomPacketPayload, PacketBeam {

	public static final CustomPacketPayload.Type<PacketBeamType> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CosmosBeam.MOD_ID, "type"));

	public static final StreamCodec<ByteBuf, PacketBeamType> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketBeamType::pos,
		PacketBeamType::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
