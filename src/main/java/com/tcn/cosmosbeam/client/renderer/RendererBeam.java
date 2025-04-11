package com.tcn.cosmosbeam.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tcn.cosmosbeam.core.blockentity.BlockEntityBeam;
import com.tcn.cosmosbeam.management.BeamConfigManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RendererBeam implements BlockEntityRenderer<BlockEntityBeam> {
	private static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");
	private static final ResourceLocation GATEWAY_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_gateway_beam.png");
	
    public static final int MAX_RENDER_Y = 1024;
    
	@SuppressWarnings("unused")
	private BlockEntityRendererProvider.Context context;

	public RendererBeam(BlockEntityRendererProvider.Context contextIn) {
		this.context = contextIn;
	}	

	@Override
	public void render(BlockEntityBeam blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		BlockPos pos = player.blockPosition();
		BlockPos blockPos = blockEntity.getBlockPos();

		double distanceToPlayer = pos.distManhattan(blockPos);
		
		BlockEntityBeam.BeamType type = blockEntity.getBeamType();
		int buildHeight = blockEntity.getLevel().getMaxBuildHeight();
		
		float beamRad = type.beacon() ? 0.2F : 0.15F;
		float glowRad = type.beacon() ? 0.25F : 0.175F;
		
		ResourceLocation loc = type.beacon() ? BEAM_LOCATION : GATEWAY_BEAM_LOCATION;
		
		if (distanceToPlayer <= BeamConfigManager.getInstance().getBeamRenderDistance()) {
			poseStack.pushPose();
            BeaconRenderer.renderBeaconBeam(poseStack, bufferSource, loc, partialTick, 1F, blockEntity.getLevel().getGameTime(), -buildHeight, buildHeight * 2, blockEntity.getBeamColour().dec(), beamRad, glowRad);
            poseStack.popPose();
        }
	}

	@Override
    public boolean shouldRenderOffScreen(BlockEntityBeam blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return BeamConfigManager.getInstance().getBeamRenderDistance();
    }

    @Override
    public boolean shouldRender(BlockEntityBeam blockEntity, Vec3 cameraPos) {
        return Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(cameraPos.multiply(1.0, 0.0, 1.0), (double)this.getViewDistance());
    }

    @Override
    public AABB getRenderBoundingBox(BlockEntityBeam blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, MAX_RENDER_Y, pos.getZ() + 1.0);
    }
}