package com.tcn.cosmosbeam.core.network;

import com.tcn.cosmosbeam.CosmosBeam;
import com.tcn.cosmosbeam.core.blockentity.BlockEntityBeam;
import com.tcn.cosmosbeam.core.blockentity.BlockEntityBeam.BeamType;
import com.tcn.cosmosbeam.core.network.packet.PacketBeam;
import com.tcn.cosmosbeam.core.network.packet.PacketBeamColour;
import com.tcn.cosmosbeam.core.network.packet.PacketBeamType;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BeamPacketHandler {

	public static void handleDataOnNetwork(final PacketBeam data, final IPayloadContext context) {
		if (data instanceof PacketBeamColour packet) {
			context.enqueueWork(() -> {
				ServerLevel world = (ServerLevel) context.player().level();
				BlockEntity tile = world.getBlockEntity(packet.pos());
				
				if (tile instanceof BlockEntityBeam entity) {
					entity.updateBeamColour(packet.reverse());
					entity.sendUpdates(true);
				} else {
					CosmosBeam.CONSOLE.debugWarn("[Packet Delivery Failure] <beamcolour> Block Entity not equal to expected.");
				}			
			});
		}
		
		if (data instanceof PacketBeamType packet) {
			context.enqueueWork(() -> {
				ServerLevel world = (ServerLevel) context.player().level();
				BlockEntity tile = world.getBlockEntity(packet.pos());
				
				if (tile instanceof BlockEntityBeam entity) {
					entity.setBeamType(entity.getBeamType().beacon() ? BeamType.GATEWAY : BeamType.BEACON);
					entity.sendUpdates(true);
				} else {
					CosmosBeam.CONSOLE.debugWarn("[Packet Delivery Failure] <beamtype> Block Entity not equal to expected.");
				}			
			});
		}
	}
}