package com.tcn.cosmosbeam.client.container;

import com.tcn.cosmosbeam.management.BeamRegistrationManager;
import com.tcn.cosmoslibrary.client.container.CosmosContainerMenuBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class ContainerBeam extends CosmosContainerMenuBlockEntity {
	
	Inventory playerInventory;

	public ContainerBeam(int indexIn, Inventory playerInventoryIn, FriendlyByteBuf extraData) {
		this(indexIn, playerInventoryIn, ContainerLevelAccess.NULL, extraData.readBlockPos());
	}

	public ContainerBeam(int indexIn, Inventory playerInventoryIn, ContainerLevelAccess accessIn, BlockPos posIn) {
		super(BeamRegistrationManager.MENU_TYPE_BEAM.get(), indexIn, playerInventoryIn, accessIn, posIn);
		
		this.playerInventory = playerInventoryIn;

		/** @Inventory */
		for (int k = 0; k < 3; ++k) {
			for (int i1 = 0; i1 < 9; ++i1) {
				this.addSlot(new Slot(playerInventoryIn, i1 + k * 9 + 9, 8 + i1 * 18, 44 + k * 18));
			}
		}

		/** @Actionbar */
		for (int l = 0; l < 9; ++l) {
			this.addSlot(new Slot(playerInventoryIn, l, 8 + l * 18, 101));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int indexIn) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = (Slot) this.slots.get(indexIn);
		boolean flag = slot.mayPickup(playerIn);
		
		if (flag && slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			
			if (indexIn >= this.slots.size() && indexIn < this.slots.size()) {
				if (!this.moveItemStackTo(itemstack1, 0, this.slots.size(), false)) {
					return ItemStack.EMPTY;
				}
			} else if (indexIn >= this.slots.size() - 9 && indexIn < this.slots.size()) {
				if (!this.moveItemStackTo(itemstack1, 0, this.slots.size() - 9, false)) {
					return ItemStack.EMPTY;
				}
			} else if (indexIn >= 0 && indexIn < this.slots.size() - 9) {
				if (!this.moveItemStackTo(itemstack1, this.slots.size() - 9, this.slots.size(), false)) {
					return ItemStack.EMPTY;
				}
			}
			
			slot.onTake(playerIn, itemstack1);
			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			return itemstack;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(this.access, playerIn, BeamRegistrationManager.BLOCK_BEAM.get());
	}

}
