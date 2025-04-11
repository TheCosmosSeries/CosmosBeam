package com.tcn.cosmosbeam;

import com.tcn.cosmosbeam.client.screen.BeamConfigScreen;
import com.tcn.cosmoslibrary.runtime.common.CosmosRuntime;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = CosmosBeam.MOD_ID, dist = Dist.CLIENT)
public class CosmosBeamClient {

	public CosmosBeamClient(ModContainer container) {
		CosmosRuntime.Client.regiserConfigScreen(container, BeamConfigScreen::new);
	}
}
