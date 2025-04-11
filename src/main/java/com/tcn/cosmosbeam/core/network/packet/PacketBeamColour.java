package com.tcn.cosmosbeam.core.network.packet;

import com.tcn.cosmosbeam.CosmosBeam;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacketBeamColour(BlockPos pos, boolean reverse) implements CustomPacketPayload, PacketBeam {

	public static final CustomPacketPayload.Type<PacketBeamColour> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CosmosBeam.MOD_ID, "colour"));

	public static final StreamCodec<ByteBuf, PacketBeamColour> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC,
		PacketBeamColour::pos,
		ByteBufCodecs.BOOL,
		PacketBeamColour::reverse,
		PacketBeamColour::new
	);
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

}
