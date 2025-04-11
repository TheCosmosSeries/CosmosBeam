package com.tcn.cosmosbeam;

import net.minecraft.resources.ResourceLocation;

public class CosmosBeamReference {

	public static final String PRE = CosmosBeam.MOD_ID + ":";

	public static final String RESOURCE = PRE + "textures/";
	public static final String GUI = RESOURCE + "gui/";

	public static final String BLOCKS = PRE + "blocks/";
	public static final String ITEMS = RESOURCE + "items/";

	public static final ResourceLocation[] BEAM = new ResourceLocation[] { ResourceLocation.parse(GUI + "beam/background.png"), ResourceLocation.parse(GUI + "beam/background_dark.png") };
	public static final ResourceLocation[] BEAM_OVERLAY = new ResourceLocation[] { ResourceLocation.parse(GUI + "beam/overlay.png"), ResourceLocation.parse(GUI + "beam/overlay_dark.png") };
	
}
