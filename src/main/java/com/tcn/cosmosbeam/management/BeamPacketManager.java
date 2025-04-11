package com.tcn.cosmosbeam.management;

import com.tcn.cosmosbeam.CosmosBeam;
import com.tcn.cosmosbeam.core.network.BeamPacketHandler;
import com.tcn.cosmosbeam.core.network.packet.PacketBeamColour;
import com.tcn.cosmosbeam.core.network.packet.PacketBeamType;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CosmosBeam.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class BeamPacketManager {
	
	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
	    final PayloadRegistrar registrar = event.registrar("1");
	    registrar.playToServer(PacketBeamColour.TYPE, PacketBeamColour.STREAM_CODEC, BeamPacketHandler::handleDataOnNetwork);
	    registrar.playToServer(PacketBeamType.TYPE, PacketBeamType.STREAM_CODEC, BeamPacketHandler::handleDataOnNetwork);
	}
}