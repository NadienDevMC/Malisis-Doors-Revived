package nadiendev.malisisdoorsrevived;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import nadiendev.malisisdoorsrevived.config.MalisisDoorsConfig;
import nadiendev.malisisdoorsrevived.door.DoorRegistry;
import nadiendev.malisisdoorsrevived.event.VanillaDoorReplacer;
import nadiendev.malisisdoorsrevived.animated.AnimatedCopies;
import nadiendev.malisisdoorsrevived.animated.AnimatedCopiesPack;
import nadiendev.malisisdoorsrevived.registry.ModBlockEntities;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import nadiendev.malisisdoorsrevived.registry.ModCreativeTabs;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import nadiendev.malisisdoorsrevived.registry.ModItems;
import nadiendev.malisisdoorsrevived.registry.ModMenus;
import nadiendev.malisisdoorsrevived.registry.ModRecipes;
import nadiendev.malisisdoorsrevived.registry.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;

/**
 * MalisisDoors : animated doors, trapdoors and fence gates, and a lot of new doors.
 *
 * @author Ordinastie
 */
@Mod(MalisisDoors.MODID)
public class MalisisDoors
{
	public static final String MODID = "malisisdoors";
	public static final Logger LOGGER = LogUtils.getLogger();

	public MalisisDoors(IEventBus modEventBus, ModContainer modContainer)
	{
		ModSounds.SOUNDS.register(modEventBus);
		ModDataComponents.COMPONENTS.register(modEventBus);
		ModBlocks.BLOCKS.register(modEventBus);
		ModItems.ITEMS.register(modEventBus);
		ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		ModMenus.MENUS.register(modEventBus);
		ModRecipes.SERIALIZERS.register(modEventBus);
		ModCreativeTabs.TABS.register(modEventBus);
		modEventBus.addListener(EventPriority.LOWEST, AnimatedCopies::register);
		modEventBus.addListener(AnimatedCopiesPack::addPack);

		//make sure the movements and sounds are registered before anything uses them
		LOGGER.debug("{} door movements registered", DoorRegistry.listMovements().size());

		modContainer.registerConfig(ModConfig.Type.SERVER, MalisisDoorsConfig.SERVER_SPEC);

		NeoForge.EVENT_BUS.addListener(VanillaDoorReplacer::onBlockPlaced);
		NeoForge.EVENT_BUS.addListener(VanillaDoorReplacer::onRightClickBlock);
	}

	public static ResourceLocation id(String path)
	{
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}
}
