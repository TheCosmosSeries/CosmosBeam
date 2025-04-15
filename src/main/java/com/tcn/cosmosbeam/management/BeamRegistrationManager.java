package com.tcn.cosmosbeam.management;

import java.util.ArrayList;
import java.util.function.Supplier;

import com.tcn.cosmosbeam.CosmosBeam;
import com.tcn.cosmosbeam.client.container.ContainerBeam;
import com.tcn.cosmosbeam.client.renderer.RendererBeam;
import com.tcn.cosmosbeam.client.screen.ScreenBeam;
import com.tcn.cosmosbeam.core.block.BlockBeam;
import com.tcn.cosmosbeam.core.blockentity.BlockEntityBeam;
import com.tcn.cosmoslibrary.common.lib.ComponentColour;
import com.tcn.cosmoslibrary.common.lib.ComponentHelper;
import com.tcn.cosmoslibrary.runtime.common.CosmosRuntime;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = CosmosBeam.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class BeamRegistrationManager {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CosmosBeam.MOD_ID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CosmosBeam.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CosmosBeam.MOD_ID);
	public static final ArrayList<Supplier<? extends ItemLike>> TAB_ITEMS = new ArrayList<>();

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CosmosBeam.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, CosmosBeam.MOD_ID);
	
	public static final Supplier<CreativeModeTab> COSMOS_DECORATIONS_GROUP = TABS.register("cosmos_beam", 
		() -> CreativeModeTab.builder().title(ComponentHelper.style(ComponentColour.GREEN, "bold", "Cosmos Beam")).icon(() -> {
			return new ItemStack(BeamRegistrationManager.BLOCK_BEAM); 
		}).displayItems((params, output) -> TAB_ITEMS.forEach(itemLike -> output.accept(itemLike.get()))).build()
	); 
	
	public static final DeferredBlock<Block> BLOCK_BEAM = BLOCKS.register("block_beam", () -> new BlockBeam(BlockBehaviour.Properties.of().sound(SoundType.METAL).requiresCorrectToolForDrops().strength(4.0F, 16.0F).noOcclusion().lightLevel(light -> 15)));
	public static final DeferredItem<Item> ITEM_BEAM = addToTab(ITEMS.register("block_beam", () -> new BlockItem(BLOCK_BEAM.get(), new Item.Properties())));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BlockEntityBeam>> BLOCK_ENTITY_TYPE_BEAM = BLOCK_ENTITY_TYPES.register("blockentity_beam", () -> BlockEntityType.Builder.of(BlockEntityBeam::new, BLOCK_BEAM.get()).build(null));
	public static final DeferredHolder<MenuType<?>, MenuType<ContainerBeam>> MENU_TYPE_BEAM = MENU_TYPES.register("container_beam", () -> IMenuTypeExtension.create(ContainerBeam::new));
	
	public static void register(IEventBus bus) {
		ITEMS.register(bus);
		BLOCKS.register(bus);
		BLOCK_ENTITY_TYPES.register(bus);
		MENU_TYPES.register(bus);
		TABS.register(bus);
	}

	@SubscribeEvent
	public static void onBlockEntityRendererRegistry(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BLOCK_ENTITY_TYPE_BEAM.get(), RendererBeam::new);
		
		CosmosBeam.CONSOLE.startup("BlockEntityRenderer Registration complete.");
	}

	@SubscribeEvent
	public static void registerMenuScreensEvent(RegisterMenuScreensEvent event) {
		event.register(MENU_TYPE_BEAM.get(), ScreenBeam::new);
		
		CosmosBeam.CONSOLE.startup("Menu Screen registration complete...");
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	public static void onFMLClientSetup(FMLClientSetupEvent event) {
		CosmosRuntime.Client.setRenderLayers(RenderType.translucent(), BLOCK_BEAM.get());
	}

    private static <T extends Item> DeferredItem<T> addToTab(DeferredItem<T> itemLike) {
        TAB_ITEMS.add(itemLike);
        return itemLike;
    }
}