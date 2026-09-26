package nadiendev.malisisdoorsrevived.datagen.providers;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import nadiendev.malisisdoorsrevived.registry.ModItems;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MDItemModelProvider extends ItemModelProvider
{
	public MDItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, MalisisDoors.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels()
	{
		generated(ModBlocks.WOOD_SLIDING_DOOR);
		generated(ModBlocks.IRON_SLIDING_DOOR);
		generated(ModBlocks.JAIL_DOOR);
		generated(ModBlocks.LABORATORY_DOOR);
		generated(ModBlocks.FACTORY_DOOR);
		generated(ModBlocks.SHOJI_DOOR);
		for (DyeColor color : DyeColor.values())
			generated(ModBlocks.CURTAINS.get(color));
		generated(ModBlocks.SALOON_DOOR);
		generated(ModBlocks.CARRIAGE_DOOR);
		generated(ModBlocks.MEDIEVAL_DOOR);
		for (DeferredBlock<? extends Block> door : ModBlocks.DOORS_3X3)
			generated(door);
		generated(ModBlocks.RUSTY_HATCH);

		builtinEntity(ModBlocks.VERTICAL_HATCH, modLoc("block/vertical_hatch"));
		builtinEntity(ModItems.CUSTOM_DOOR, mcLoc("block/oak_planks"));
		builtinEntity(ModItems.HITECH_DOOR, modLoc("block/hitechdoor"));
		builtinEntity(ModItems.RUSTY_HANDLE, modLoc("block/rusty_hatch_handle"));

		blockParent(ModBlocks.CAMO_FENCE_GATE, "camo_fence_gate");
		blockParent(ModBlocks.GARAGE_DOOR, "garage_door");
		blockParent(ModBlocks.SLIDING_TRAPDOOR, "sliding_trapdoor_bottom");
		blockParent(ModBlocks.RUSTY_LADDER, "rusty_ladder");
		blockParent(ModBlocks.DOOR_FACTORY, "door_factory");

		forcefieldController();
		playerSensor();
	}

	private String name(ItemLike item)
	{
		return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
	}

	private void generated(ItemLike item)
	{
		String name = name(item);
		withExistingParent(name, mcLoc("item/generated")).texture("layer0", modLoc("item/" + name));
	}

	private void builtinEntity(ItemLike item, ResourceLocation particle)
	{
		getBuilder(name(item))	.parent(new ModelFile.UncheckedModelFile("builtin/entity"))
								.guiLight(BlockModel.GuiLight.SIDE)
								.texture("particle", particle)
								.transforms()
								.transform(ItemDisplayContext.GUI)
								.rotation(30, 225, 0)
								.translation(0, 0, 0)
								.scale(0.625F)
								.end()
								.transform(ItemDisplayContext.GROUND)
								.rotation(0, 0, 0)
								.translation(0, 3, 0)
								.scale(0.25F)
								.end()
								.transform(ItemDisplayContext.HEAD)
								.rotation(0, 180, 0)
								.translation(0, 0, 0)
								.scale(1)
								.end()
								.transform(ItemDisplayContext.FIXED)
								.rotation(0, 180, 0)
								.translation(0, 0, 0)
								.scale(0.5F)
								.end()
								.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
								.rotation(75, 315, 0)
								.translation(0, 2.5F, 0)
								.scale(0.375F)
								.end()
								.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
								.rotation(0, 315, 0)
								.translation(0, 0, 0)
								.scale(0.4F)
								.end()
								.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
								.rotation(0, 315, 0)
								.translation(0, 0, 0)
								.scale(0.4F)
								.end()
								.end();
	}

	private void blockParent(ItemLike item, String model)
	{
		withExistingParent(name(item), modLoc("block/" + model));
	}

	private void forcefieldController()
	{
		String name = name(ModItems.FORCEFIELD_CONTROLLER);
		ItemModelBuilder builder = withExistingParent(name, mcLoc("item/handheld")).texture("layer0", modLoc("item/" + name));
		String[] states = { "yellow", "red", "green" };
		for (int i = 0; i < states.length; i++)
		{
			String variant = name + "_" + states[i];
			ModelFile model = withExistingParent(variant, mcLoc("item/handheld")).texture("layer0", modLoc("item/" + variant));
			builder.override().predicate(MalisisDoors.id("state"), (i + 1) * 0.25F).model(model).end();
		}
	}

	private void playerSensor()
	{
		ResourceLocation texture = modLoc("block/player_sensor");
		withExistingParent(name(ModItems.PLAYER_SENSOR), mcLoc("block/block"))	.texture("particle", texture)
																				.texture("texture", texture)
																				.element()
																				.from(2, 6, 6)
																				.to(14, 10, 10)
																				.allFaces((direction, face) -> face.texture("#texture"))
																				.end();
	}
}
