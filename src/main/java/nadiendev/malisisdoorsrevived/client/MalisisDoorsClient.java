package nadiendev.malisisdoorsrevived.client;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.client.renderer.DoorRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.FenceGateRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.ForcefieldRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.GarageDoorRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.MultiBlockDoorRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.TrapDoorRenderer;
import nadiendev.malisisdoorsrevived.client.screen.DoorFactoryScreen;
import nadiendev.malisisdoorsrevived.animated.AnimatedCopiesClient;
import nadiendev.malisisdoorsrevived.item.ForcefieldItem;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import nadiendev.malisisdoorsrevived.registry.ModItems;
import nadiendev.malisisdoorsrevived.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Client entry point of MalisisDoors.
 *
 * @author Ordinastie
 */
@Mod(value = MalisisDoors.MODID, dist = Dist.CLIENT)
public class MalisisDoorsClient
{
	public MalisisDoorsClient(IEventBus modEventBus, ModContainer container)
	{
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		modEventBus.addListener(MalisisDoorsClient::registerRenderers);
		modEventBus.addListener(MalisisDoorsClient::registerScreens);
		modEventBus.addListener(MalisisDoorsClient::registerClientExtensions);
		modEventBus.addListener(MalisisDoorsClient::clientSetup);
		AnimatedCopiesClient.init(modEventBus);
	}

	private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
		event.registerBlockEntityRenderer(ModBlockEntities.DOOR.get(), DoorRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.SALOON_DOOR.get(), DoorRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.CUSTOM_DOOR.get(), DoorRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.TRAPDOOR.get(), TrapDoorRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.FENCE_GATE.get(), FenceGateRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.GARAGE_DOOR.get(), GarageDoorRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.MULTIBLOCK_DOOR.get(), MultiBlockDoorRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntities.FORCEFIELD.get(), ForcefieldRenderer::new);
	}

	private static void registerScreens(RegisterMenuScreensEvent event)
	{
		event.register(ModMenus.DOOR_FACTORY.get(), DoorFactoryScreen::new);
	}

	private static void registerClientExtensions(RegisterClientExtensionsEvent event)
	{
		event.registerItem(new IClientItemExtensions()
		{
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer()
			{
				return MalisisItemRenderer.get();
			}
		}, ModItems.CUSTOM_DOOR.get(), ModBlocks.VERTICAL_HATCH.get().asItem(), ModItems.HITECH_DOOR.get(), ModItems.RUSTY_HANDLE.get());
	}

	private static void clientSetup(FMLClientSetupEvent event)
	{
		event.enqueueWork(() -> ItemProperties.register(ModItems.FORCEFIELD_CONTROLLER.get(),
														MalisisDoors.id("state"),
														(stack, level, entity, seed) -> getForcefieldState(stack)));
	}

	/**
	 * State of the forcefield controller, used by the item model : 0 idle, 0.25 first corner selected, 0.5 invalid, 0.75 valid.
	 */
	private static float getForcefieldState(ItemStack stack)
	{
		ForcefieldItem.Selection selection = ForcefieldItem.getSelection(stack);
		if (selection == null)
			return 0;
		Minecraft mc = Minecraft.getInstance();
		if (!(mc.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK)
			return 0.25F;
		BlockPos pos = hit.getBlockPos().relative(hit.getDirection());
		boolean creative = mc.player != null && mc.player.isCreative();
		return ForcefieldItem.isValid(stack, selection.pos(), pos, creative) ? 0.75F : 0.5F;
	}
}
