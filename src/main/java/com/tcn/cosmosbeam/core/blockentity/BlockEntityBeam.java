package com.tcn.cosmosbeam.core.blockentity;

import com.tcn.cosmosbeam.client.container.ContainerBeam;
import com.tcn.cosmosbeam.management.BeamRegistrationManager;
import com.tcn.cosmoslibrary.common.enums.EnumUIHelp;
import com.tcn.cosmoslibrary.common.enums.EnumUILock;
import com.tcn.cosmoslibrary.common.enums.EnumUIMode;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockInteract;
import com.tcn.cosmoslibrary.common.interfaces.block.IBlockNotifier;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUILockable;
import com.tcn.cosmoslibrary.common.interfaces.blockentity.IBEUIMode;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.registry.gson.object.ObjectPlayerInformation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BlockEntityBeam extends BlockEntity implements IBlockNotifier, IBlockInteract, IBEUIMode, IBEUILockable, MenuProvider, Nameable {
	
	public enum BeamType {
		BEACON (0),
		GATEWAY (1);
		
		public int id;
		
		BeamType(int id) {
			this.id = id;
		}
		
		public static BeamType fromId(int id) {
			return id == 0 ? BEACON : GATEWAY;
		}
		
		public int getId() {
			return this.id;
		}
		
		public boolean beacon() {
			return this == BEACON;
		}
		
		public boolean gateway() {
			return this == GATEWAY;
		}
		
		public MutableComponent getColouredName() {
			return this.beacon() ? ComponentHelper.style(ComponentColour.CYAN, "bold", "cosmosbeam.beamtype.beacon") : ComponentHelper.style(ComponentColour.END, "bold", "cosmosbeam.beamtype.gateway");
		}
	}
	
	
	private ObjectPlayerInformation owner;
	
	public ComponentColour beamColour = ComponentColour.WHITE;
	public BeamType beamType = BeamType.BEACON;
	
	private EnumUIMode uiMode = EnumUIMode.DARK;
	private EnumUILock uiLock = EnumUILock.PRIVATE;
	
	public BlockEntityBeam(BlockPos pos, BlockState blockState) {
		super(BeamRegistrationManager.BLOCK_ENTITY_TYPE_BEAM.get(), pos, blockState);
	}

	public void sendUpdates(boolean update) {
		if (this.getLevel() != null) {
			this.setChanged();
			BlockState state = this.getBlockState();
			
			this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
			
			if (update) {
				if (!this.getLevel().isClientSide()) {
					this.getLevel().setBlockAndUpdate(this.getBlockPos(), state.updateShape(Direction.DOWN, state, level, worldPosition, worldPosition));
				}
			}
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.saveAdditional(compound, provider);
		
		compound.putInt("beam_colour", this.beamColour.getIndex());
		compound.putInt("beamType", this.beamType.getId());
		
		compound.putInt("ui_mode", this.uiMode.getIndex());
		compound.putInt("ui_lock", this.uiLock.getIndex());
		
		if (this.owner != null) {
			this.owner.writeToNBT(compound, "owner");
		}
	}

	@Override
	public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
		super.loadAdditional(compound, provider);
		
		this.beamColour = ComponentColour.fromIndex(compound.getInt("beam_colour"));
		this.beamType = BeamType.fromId(compound.getInt("beamType"));
		
		this.uiMode = EnumUIMode.getStateFromIndex(compound.getInt("ui_mode"));
		this.uiLock = EnumUILock.getStateFromIndex(compound.getInt("ui_lock"));
		
		if (compound.contains("owner")) {
			this.owner = ObjectPlayerInformation.readFromNBT(compound, "owner");
		}
	}

	//Set the data once it has been received. [NBT > TE]
	@Override
	public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
		super.handleUpdateTag(tag, provider);
	}
	
	//Retrieve the data to be stored. [TE > NBT]
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		this.saveAdditional(tag, provider);
		return tag;
	}
	
	//Actually sends the data to the server. [NBT > SER]
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	//Method is called once packet has been received by the client. [SER > CLT]
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
		super.onDataPacket(net, pkt, provider);
		CompoundTag tag_ = pkt.getTag();
		
		this.handleUpdateTag(tag_, provider);
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stackIn, BlockState state, Level levelIn, BlockPos posIn, Player playerIn, InteractionHand hand, BlockHitResult hit) {
		if (stackIn.getItem() instanceof BlockItem) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		} else if (!this.getLevel().isClientSide() && playerIn instanceof ServerPlayer serverPlayer) {
			if (!this.getUILock().equals(EnumUILock.PRIVATE) || (this.getUILock().equals(EnumUILock.PRIVATE) && this.checkIfOwner(playerIn))) {
				serverPlayer.openMenu(this, (buf) -> buf.writeBlockPos(posIn));
			}
	        return ItemInteractionResult.sidedSuccess(this.getLevel().isClientSide());
		}
		return ItemInteractionResult.sidedSuccess(this.getLevel().isClientSide());
	}

	@Override
	public void setPlacedBy(Level levelIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) { 
		if (!levelIn.isClientSide()) {
			if (placer instanceof Player) {
				Player player = (Player) placer;
				this.setOwner(player);
			}
		}
	}

	@Override
	public Component getDisplayName() {
		return ComponentHelper.title("cosmosbeam.gui.beam");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventoryIn, Player player) {
		return new ContainerBeam(containerId, playerInventoryIn, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()), this.getBlockPos());
	}

	@Override
	public Component getName() {
		return ComponentHelper.title("cosmosbeam.gui.beam");
	}
	
	@Override
	public InteractionResult useWithoutItem(BlockState state, Level levelIn, BlockPos posIn, Player playerIn, BlockHitResult hit) {
		return null;
	}

	@Override
	public void attack(BlockState state, Level worldIn, BlockPos pos, Player player) { }

	@Override
	public BlockState playerWillDestroy(Level levelIn, BlockPos pos, BlockState state, Player player) {
		return null;
	}

	@Override
	public void neighborChanged(BlockState state, Level levelIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) { }

	@Override
	public void onPlace(BlockState state, Level levelIn, BlockPos pos, BlockState oldState, boolean isMoving) { }

	public ComponentColour getBeamColour() {
		return this.beamColour;
	}
	
	public void setBeamColour(ComponentColour beamColourIn) {
		this.beamColour = beamColourIn;
	}
	
	public void updateBeamColour(boolean reverse) {
		if (reverse) {
			ComponentColour colour = this.beamColour.getNextVanillaColourReverse(false);
			this.setBeamColour(colour);
		} else {
			this.setBeamColour(ComponentColour.getNextVanillaColour(false, this.beamColour));
		}
	}
	
	@Override
	public EnumUIMode getUIMode() {
		return this.uiMode;
	}

	@Override
	public void setUIMode(EnumUIMode modeIn) {
		this.uiMode = modeIn;
	}

	@Override
	public void cycleUIMode() {
		this.uiMode = EnumUIMode.getNextStateFromState(this.uiMode);
	}

	@Override
	public EnumUILock getUILock() {
		return this.uiLock;
	}

	@Override
	public void setUILock(EnumUILock modeIn) {
		this.uiLock = modeIn;
	}

	@Override
	public void cycleUILock() {
		this.uiLock = EnumUILock.getNextStateFromState(this.uiLock);
	}

	@Override
	public void setOwner(Player playerIn) {
		this.owner = new ObjectPlayerInformation(playerIn);
	}

	@Override
	public boolean checkIfOwner(Player playerIn) {
		if (this.owner != null) {
			return this.owner.getPlayerUUID().equals(playerIn.getUUID());
		}
		return true;
	}

	@Override
	public boolean canPlayerAccess(Player playerIn) {
		if (this.getUILock().equals(EnumUILock.PUBLIC)) {
			return true;
		}
		return this.checkIfOwner(playerIn);
	}
	
	@Override
	public EnumUIHelp getUIHelp() {
		return EnumUIHelp.HIDDEN;
	}

	@Override
	public void setUIHelp(EnumUIHelp modeIn) { }

	@Override
	public void cycleUIHelp() { }

	public BeamType getBeamType() {
		return this.beamType;
	}
	
	public void setBeamType(BeamType type) {
		this.beamType = type;
	}
}