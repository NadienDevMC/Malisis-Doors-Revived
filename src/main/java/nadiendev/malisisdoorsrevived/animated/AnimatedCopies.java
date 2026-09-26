package nadiendev.malisisdoorsrevived.animated;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.integracion.ad_astra.AdAstraIntegration;
import nadiendev.malisisdoorsrevived.integracion.allthemodium.AllthemodiumIntegration;
import nadiendev.malisisdoorsrevived.integracion.ars_nouveau.ArsNouveauIntegration;
import nadiendev.malisisdoorsrevived.integracion.chipped.ChippedIntegration;
import nadiendev.malisisdoorsrevived.integracion.create.CreateIntegration;
import nadiendev.malisisdoorsrevived.integracion.enchanted.EnchantedIntegration;
import nadiendev.malisisdoorsrevived.integracion.enderio.EnderIOIntegration;
import nadiendev.malisisdoorsrevived.integracion.farmersdelight.FarmersDelightIntegration;
import nadiendev.malisisdoorsrevived.integracion.malum.MalumIntegration;
import nadiendev.malisisdoorsrevived.integracion.manyideas_doors.ManyIdeasDoorsIntegration;
import nadiendev.malisisdoorsrevived.integracion.mcwdoors.MacawsDoorsIntegration;
import nadiendev.malisisdoorsrevived.integracion.mcwfences.MacawsFencesIntegration;
import nadiendev.malisisdoorsrevived.integracion.mcwtrpdoors.MacawsTrapdoorsIntegration;
import nadiendev.malisisdoorsrevived.integracion.mekanismadditions.MekanismAdditionsIntegration;
import nadiendev.malisisdoorsrevived.integracion.occultism.OccultismIntegration;
import nadiendev.malisisdoorsrevived.integracion.twilightforest.TwilightForestIntegration;
import nadiendev.malisisdoorsrevived.integracion.vanillabackport.VanillaBackportIntegration;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.RegisterEvent;

public class AnimatedCopies
{
	public record Copy(ResourceLocation id, Block original, Block animated, DoorIntegration integration)
	{}

	public static final List<DoorIntegration> INTEGRATIONS = List.of(	new MacawsDoorsIntegration(),
																		new MacawsTrapdoorsIntegration(),
																		new MacawsFencesIntegration(),
																		new ManyIdeasDoorsIntegration(),
																		new VanillaBackportIntegration(),
																		new CreateIntegration(),
																		new AdAstraIntegration(),
																		new TwilightForestIntegration(),
																		new MalumIntegration(),
																		new MekanismAdditionsIntegration(),
																		new AllthemodiumIntegration(),
																		new EnderIOIntegration(),
																		new OccultismIntegration(),
																		new EnchantedIntegration(),
																		new ArsNouveauIntegration(),
																		new FarmersDelightIntegration(),
																		new ChippedIntegration());

	private static final List<Copy> COPIES = new ArrayList<>();
	private static final Map<Block, Copy> BY_ORIGINAL = new IdentityHashMap<>();
	private static final Map<Block, Copy> BY_ANIMATED = new IdentityHashMap<>();

	public static void register(RegisterEvent event)
	{
		if (!event.getRegistryKey().equals(Registries.BLOCK))
			return;

		List<DoorIntegration> loaded = INTEGRATIONS.stream().filter(integration -> ModList.get().isLoaded(integration.modId())).toList();
		if (loaded.isEmpty())
			return;

		Map<ResourceLocation, DoorIntegration> candidates = new LinkedHashMap<>();
		for (ResourceLocation id : BuiltInRegistries.BLOCK.keySet())
		{
			Block block = BuiltInRegistries.BLOCK.get(id);
			for (DoorIntegration integration : loaded)
			{
				if (integration.animates(id, block))
				{
					candidates.put(id, integration);
					break;
				}
			}
		}

		candidates.forEach((originalId, integration) -> {
			Block original = BuiltInRegistries.BLOCK.get(originalId);
			if (BY_ORIGINAL.containsKey(original) || ModBlocks.hasAnimatedCopy(original))
				return;

			ResourceLocation id = MalisisDoors.id("animated/" + originalId.getNamespace() + "/" + originalId.getPath());
			Block animated = ModBlocks.createAnimatedCopy(original);
			event.register(Registries.BLOCK, id, () -> animated);
			ModBlocks.addAnimatedCopy(original, DeferredBlock.createBlock(id), animated);

			Copy copy = new Copy(id, original, animated, integration);
			COPIES.add(copy);
			BY_ORIGINAL.put(original, copy);
			BY_ANIMATED.put(animated, copy);
		});

		for (DoorIntegration integration : loaded)
		{
			long count = COPIES.stream().filter(copy -> copy.integration() == integration).count();
			MalisisDoors.LOGGER.info("{}: {} animated doors, trapdoors and fence gates", integration.name(), count);
		}
	}

	public static List<Copy> getCopies()
	{
		return Collections.unmodifiableList(COPIES);
	}

	@Nullable
	public static DoorIntegration getIntegration(Block original)
	{
		Copy copy = BY_ORIGINAL.get(original);
		return copy != null ? copy.integration() : null;
	}

	public static boolean isAnimatedCopy(Block animated)
	{
		return BY_ANIMATED.containsKey(animated);
	}
}
