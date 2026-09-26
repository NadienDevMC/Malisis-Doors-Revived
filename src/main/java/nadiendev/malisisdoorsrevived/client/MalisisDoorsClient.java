package nadiendev.malisisdoorsrevived.client;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.animated.AnimatedCopiesClient;
import nadiendev.malisisdoorsrevived.client.item.ForcefieldStateProperty;
import nadiendev.malisisdoorsrevived.client.item.MalisisItemSpecialRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.DoorRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.FenceGateRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.ForcefieldRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.GarageDoorRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.MultiBlockDoorRenderer;
import nadiendev.malisisdoorsrevived.client.renderer.TrapDoorRenderer;
import nadiendev.malisisdoorsrevived.client.screen.DoorFactoryScreen;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
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
		modEventBus.addListener(MalisisDoorsClient::registerSpecialRenderers);
		modEventBus.addListener(MalisisDoorsClient::registerItemProperties);
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

	private static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event)
	{
		event.register(MalisisDoors.id("item"), MalisisItemSpecialRenderer.Unbaked.CODEC);
	}

	private static void registerItemProperties(RegisterRangeSelectItemModelPropertyEvent event)
	{
		event.register(MalisisDoors.id("forcefield_state"), ForcefieldStateProperty.CODEC);
	}
}
