package com.tcn.cosmosbeam;

import com.tcn.cosmosbeam.management.BeamConfigManager;
import com.tcn.cosmosbeam.management.BeamRegistrationManager;
import com.tcn.cosmoslibrary.runtime.common.CosmosConsoleManager;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(CosmosBeam.MOD_ID)
public class CosmosBeam {

	//This must NEVER EVER CHANGE!
	public static final String MOD_ID = "cosmosbeam";
		
	public static CosmosConsoleManager CONSOLE = new CosmosConsoleManager(CosmosBeam.MOD_ID, true, true);
	
	public CosmosBeam(ModContainer container, IEventBus bus) {
		container.registerConfig(ModConfig.Type.COMMON, BeamConfigManager.SPEC, "cosmos-beam-common.toml");
		
		BeamRegistrationManager.register(bus);
		
		bus.addListener(this::onFMLCommonSetup);
		bus.addListener(this::onFMLClientSetup);
	}
	
	public void onFMLCommonSetup(final FMLCommonSetupEvent event) {
		CONSOLE = new CosmosConsoleManager(CosmosBeam.MOD_ID, BeamConfigManager.getInstance().getDebugMessage(), BeamConfigManager.getInstance().getInfoMessage());
	
		CONSOLE.startup("CosmosBeam Common Setup complete.");
	}
	
	public void onFMLClientSetup(final FMLClientSetupEvent event) {
		BeamRegistrationManager.onFMLClientSetup(event);
		
		CONSOLE.startup("CosmosBeam Client Setup complete.");
	}
}