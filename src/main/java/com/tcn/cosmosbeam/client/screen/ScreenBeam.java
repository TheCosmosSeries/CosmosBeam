package com.tcn.cosmosbeam.client.screen;

import java.util.Arrays;

import com.tcn.cosmosbeam.CosmosBeamReference;
import com.tcn.cosmosbeam.client.container.ContainerBeam;
import com.tcn.cosmosbeam.core.blockentity.BlockEntityBeam;
import com.tcn.cosmosbeam.core.network.packet.PacketBeamColour;
import com.tcn.cosmosbeam.core.network.packet.PacketBeamType;
import com.tcn.cosmoslibrary.client.ui.CosmosUISystem;
import com.tcn.cosmoslibrary.client.ui.screen.CosmosScreenBlockEntityUI;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosButtonWithType.TYPE;
import com.tcn.cosmoslibrary.client.ui.screen.widget.CosmosColourButton;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class ScreenBeam extends CosmosScreenBlockEntityUI<ContainerBeam> {

	private CosmosColourButton colourButton; private int[] indexC = new int[] { 66, 13, 20 };
	private CosmosButtonWithType typeButton; private int[] indexT = new int[] { 90, 13, 20 };

	public ScreenBeam(ContainerBeam containerIn, Inventory playerInventoryIn, Component titleIn) {
		super(containerIn, playerInventoryIn, titleIn);

		this.setImageDims(176, 125);

		this.setLight(CosmosBeamReference.BEAM[0]);
		this.setDark(CosmosBeamReference.BEAM[1]);

		this.setUIModeButtonIndex(159, 5);
		this.setUILockButtonIndex(159, 19);
		
		this.setInventoryLabelDims(8, 33);
	}

	@Override
	protected void init() {
		super.init();
	}
	
	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
	}

	@Override
	protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(graphics, partialTicks, mouseX, mouseY);

		if (this.getBlockEntity() instanceof BlockEntityBeam blockEntity) {
			CosmosUISystem.Render.renderStaticElementWithUIMode(graphics, this.getScreenCoords(), 0, 0, 0, 0, this.imageWidth, this.imageHeight, new float[] { 1.0F, 1.0F, 1.0F, 1.0F }, blockEntity, CosmosBeamReference.BEAM_OVERLAY);
		}
	}
	
	@Override
	public void renderStandardHoverEffect(GuiGraphics graphics, Style style, int mouseX, int mouseY) {
		if (this.getBlockEntity() instanceof BlockEntityBeam blockEntity) {
			if (this.colourButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmoslibrary.gui.colour.info"), 
					(MutableComponent) ComponentHelper.style(ComponentColour.GRAY, "cosmoslibrary.gui.colour.value").append(blockEntity.getBeamColour().getColouredName())
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
			else if (this.typeButton.isMouseOver(mouseX, mouseY)) {
				MutableComponent[] comp = new MutableComponent[] { ComponentHelper.style(ComponentColour.WHITE, "cosmosbeam.gui.type.info"), 
					(MutableComponent) ComponentHelper.style(ComponentColour.GRAY, "cosmosbeam.gui.type.value").append(blockEntity.getBeamType().getColouredName())
				};
				
				graphics.renderComponentTooltip(this.font, Arrays.asList(comp), mouseX, mouseY);
			}
		}
		super.renderStandardHoverEffect(graphics, style, mouseX, mouseY);
	}
	
	@Override
	protected void addButtons() {  //36 + 37
		super.addButtons();
		if (this.getBlockEntity() instanceof BlockEntityBeam blockEntity) {
			this.colourButton = this.addRenderableWidget(new CosmosColourButton(blockEntity.getBeamColour(), this.getScreenCoords()[0] + indexC[0], this.getScreenCoords()[1] + indexC[1], indexC[2], indexC[2], true, true, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.colourButton, isLeftClick); }));
			this.typeButton = this.addRenderableWidget(new CosmosButtonWithType(TYPE.GENERAL, this.getScreenCoords()[0] + indexT[0], this.getScreenCoords()[1] + indexT[1], true, true, blockEntity.getBeamType().beacon() ? 36 : 37, ComponentHelper.empty(), (button, isLeftClick) -> { this.clickButton(this.typeButton, isLeftClick); }));
		}
	}
	
	@Override
	public void clickButton(Button button, boolean isLeftClick) {
		super.clickButton(button, isLeftClick);
		
		if (this.getBlockEntity() instanceof BlockEntityBeam) {
			if (isLeftClick) {
				if (button.equals(this.colourButton)) {
					PacketDistributor.sendToServer(new PacketBeamColour(this.menu.getBlockPos(), false));
				} else if (button.equals(this.typeButton)) {
					PacketDistributor.sendToServer(new PacketBeamType(this.menu.getBlockPos()));
				}
			} else {	
				if (button.equals(this.colourButton)) {
					PacketDistributor.sendToServer(new PacketBeamColour(this.menu.getBlockPos(), true));
				}
			}
		} 
	}
	
}