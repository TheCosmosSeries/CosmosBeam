package com.tcn.cosmosbeam.management;

import org.apache.commons.lang3.tuple.Pair;

import com.tcn.cosmosbeam.CosmosBeam;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class BeamConfigManager {

	public static final BeamConfigManager CONFIG;
	public static final ModConfigSpec SPEC;
	
	static {
		{
			final Pair<BeamConfigManager, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(BeamConfigManager::new);
			CONFIG = specPair.getLeft();
			SPEC = specPair.getRight();
		}
	}
	
	public static void save() {
		SPEC.save();
	}

	private final IntValue render_distance;
	
	private final BooleanValue info_message;
	private final BooleanValue debug_message;
	
	BeamConfigManager(final ModConfigSpec.Builder builder) {
		builder.push("visual");
		{
			render_distance = builder.comment("How far away beams will still render.").defineInRange("render_distance", 64, 32, 512);
		}
		builder.pop();
		
		builder.push("console_messages");
		{
			info_message = builder.comment("Whether this mod will print [INFO] messages to the console/log").define("info", true);
			debug_message = builder.comment("Whether this mod will print [DEBUG] messages to the console/log").define("debug", false);
		}
		builder.pop();
	}
	
	public static BeamConfigManager getInstance() {
		return CONFIG;
	}

	
	/** -Messages- */
	public boolean getInfoMessage() {
		return info_message.get();
	}
	
	public void setInfoMessage(boolean value) {
		this.info_message.set(value);
		CosmosBeam.CONSOLE.updateInfoEnabled(value);
	}
	
	public boolean getDebugMessage() {
		return debug_message.get();
	}
	
	public void setDebugMessage(boolean value) {
		this.debug_message.set(value);
		CosmosBeam.CONSOLE.updateDebugEnabled(value);
	}
	
	/** -Visual- */
	public int getBeamRenderDistance() {
		return this.render_distance.get();
	}
	
	public void setBeamRenderDistance(int value) {
		this.render_distance.set(value);
	}
	
}