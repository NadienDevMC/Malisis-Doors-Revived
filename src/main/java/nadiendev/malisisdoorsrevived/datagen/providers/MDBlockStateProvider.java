package nadiendev.malisisdoorsrevived.datagen.providers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.animated.AnimatedCopies;
import nadiendev.malisisdoorsrevived.block.PlayerSensorBlock;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class MDBlockStateProvider extends BlockStateProvider
{
	public MDBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, MalisisDoors.MODID, existingFileHelper);
	}

	@Override
	protected void registerStatesAndModels()
	{
		animatedCopies();
		doorTemplates();
		texturedDoor(ModBlocks.WOOD_SLIDING_DOOR, "block/sliding_door_wood");
		texturedDoor(ModBlocks.IRON_SLIDING_DOOR, "block/sliding_door_iron");
		texturedDoor(ModBlocks.JAIL_DOOR, "block/jail_door");
		texturedDoor(ModBlocks.LABORATORY_DOOR, "block/laboratory_door");
		texturedDoor(ModBlocks.FACTORY_DOOR, "block/factory_door");
		texturedDoor(ModBlocks.SHOJI_DOOR, "block/shoji_door");
		for (DyeColor color : DyeColor.values())
			texturedDoor(ModBlocks.CURTAINS.get(color), "block/curtains/curtain_" + color.getSerializedName());

		particleOnly(ModBlocks.VERTICAL_HATCH, modLoc("block/vertical_hatch"));
		particleOnly(ModBlocks.SALOON_DOOR, modLoc("block/saloon_door"));
		particleOnly(ModBlocks.CUSTOM_DOOR, mcLoc("block/oak_planks"));
		particleOnly(ModBlocks.CARRIAGE_DOOR, modLoc("block/carriage_door"));
		particleOnly(ModBlocks.MEDIEVAL_DOOR, modLoc("block/medieval_door"));
		for (DeferredBlock<? extends Block> door : ModBlocks.DOORS_3X3)
			particleOnly(door, modLoc("block/" + door.getId().getPath()));
		particleOnly(ModBlocks.RUSTY_HATCH, modLoc("block/rusty_hatch"));
		particleOnly(ModBlocks.HITECH_DOOR, modLoc("block/hitechdoor"));
		particleOnly(ModBlocks.FORCEFIELD, modLoc("block/forcefield"));

		slidingTrapDoor();
		camoFenceGate();
		garageDoor();
		rustyLadder();
		doorFactory();
		playerSensor();
	}

	private void animatedCopies()
	{
		List<? extends DeferredBlock<? extends Block>> blocks = Stream.of(ModBlocks.DOORS, ModBlocks.TRAPDOORS, ModBlocks.FENCE_GATES).flatMap(List::stream).toList();
		for (DeferredBlock<? extends Block> deferred : blocks)
		{
			Block block = deferred.get();
			if (AnimatedCopies.isAnimatedCopy(block) || !(block instanceof IDoorBlock door) || !(door.getAppearance() instanceof DoorAppearance.Copy copy))
				continue;
			ResourceLocation vanilla = BuiltInRegistries.BLOCK.getKey(copy.block().get());
			String suffix = block instanceof DoorBlock ? "_bottom_left" : block instanceof TrapDoorBlock ? "_bottom" : "";
			simpleBlock(block, new ModelFile.UncheckedModelFile(vanilla.withPrefix("block/").withSuffix(suffix)));
		}
	}

	private void doorTemplates()
	{
		doorTemplate("bottom_left", "bottom", Direction.DOWN, false, 9, 12);
		doorTemplate("bottom_right", "bottom", Direction.DOWN, true, 12, 9);
		doorTemplate("top_left", "top", Direction.UP, false, 3, 6);
		doorTemplate("top_right", "top", Direction.UP, true, 6, 3);
	}

	private void doorTemplate(String name, String texture, Direction end, boolean flipped, float west, float east)
	{
		String main = "#" + texture;
		models()	.getBuilder("template_door_" + name)
					.ao(false)
					.renderType("cutout")
					.texture("particle", main)
					.element()
					.from(0, 0, 0)
					.to(16, 16, 3)
					.face(Direction.NORTH)
					.uvs(flipped ? 16 : 0, 0, flipped ? 0 : 16, 16)
					.texture(main)
					.end()
					.face(Direction.SOUTH)
					.uvs(flipped ? 0 : 16, 0, flipped ? 16 : 0, 16)
					.texture(main)
					.end()
					.face(Direction.WEST)
					.uvs(west, 0, west + 3, 16)
					.texture("#side")
					.end()
					.face(Direction.EAST)
					.uvs(east, 0, east + 3, 16)
					.texture("#side")
					.end()
					.face(end)
					.uvs(0, 0, 3, 16)
					.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90)
					.texture("#side")
					.end()
					.end();
	}

	private void texturedDoor(DeferredBlock<? extends Block> door, String texture)
	{
		String name = door.getId().getPath();
		VariantBlockStateBuilder builder = getVariantBuilder(door.get());
		for (DoubleBlockHalf half : DoubleBlockHalf.values())
		{
			for (DoorHingeSide hinge : DoorHingeSide.values())
			{
				String part = (half == DoubleBlockHalf.LOWER ? "bottom" : "top") + "_" + (hinge == DoorHingeSide.LEFT ? "left" : "right");
				ModelFile model = models()	.withExistingParent(name + "_" + part, modLoc("block/template_door_" + part))
											.texture("bottom", modLoc(texture + "_lower"))
											.texture("top", modLoc(texture + "_upper"))
											.texture("side", modLoc(texture + "_side"));
				builder.partialState().with(DoorBlock.HALF, half).with(DoorBlock.HINGE, hinge).modelForState().modelFile(model).addModel();
			}
		}
	}

	private void particleOnly(Supplier<? extends Block> block, ResourceLocation particle)
	{
		Block b = block.get();
		simpleBlock(b, models().getBuilder(BuiltInRegistries.BLOCK.getKey(b).getPath()).texture("particle", particle));
	}

	private void slidingTrapDoor()
	{
		ModelFile bottom = slidingTrapDoorModel("sliding_trapdoor_bottom", 0, 1.5F, 14.5F, 16);
		ModelFile top = slidingTrapDoorModel("sliding_trapdoor_top", 14.5F, 16, 0, 1.5F);
		VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.SLIDING_TRAPDOOR.get());
		builder.partialState().with(TrapDoorBlock.HALF, Half.BOTTOM).modelForState().modelFile(bottom).addModel();
		builder.partialState().with(TrapDoorBlock.HALF, Half.TOP).modelForState().modelFile(top).addModel();
	}

	private ModelFile slidingTrapDoorModel(String name, float y1, float y2, float v1, float v2)
	{
		ResourceLocation texture = modLoc("block/sliding_trapdoor");
		BlockModelBuilder builder = models()	.withExistingParent(name, mcLoc("block/thin_block"))
												.renderType("cutout")
												.texture("particle", texture)
												.texture("texture", texture);
		builder	.element()
				.from(0, y1, 0)
				.to(16, y2, 16)
				.allFaces((direction, face) -> face.uvs(0, direction.getAxis() == Direction.Axis.Y ? 0 : v1, 16, direction.getAxis() == Direction.Axis.Y ? 16 : v2)
													.texture("#texture"))
				.end();
		return builder;
	}

	private void camoFenceGate()
	{
		ResourceLocation texture = modLoc("block/camo_fencegate");
		ModelFile gate = models().withExistingParent("camo_fence_gate", mcLoc("block/template_fence_gate")).texture("texture", texture);
		ModelFile wall = models().withExistingParent("camo_fence_gate_wall", mcLoc("block/template_fence_gate_wall")).texture("texture", texture);
		VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.CAMO_FENCE_GATE.get());
		builder.partialState().with(FenceGateBlock.IN_WALL, false).modelForState().modelFile(gate).addModel();
		builder.partialState().with(FenceGateBlock.IN_WALL, true).modelForState().modelFile(wall).addModel();
	}

	private void garageDoor()
	{
		BlockModelBuilder model = models()	.withExistingParent("garage_door", mcLoc("block/block"))
											.texture("particle", modLoc("block/garage_door"))
											.texture("side", modLoc("block/garage_door"))
											.texture("front", modLoc("block/garage_door_top"));
		model	.element()
				.from(0, 0, 6.5F)
				.to(16, 16, 9.5F)
				.face(Direction.DOWN)
				.uvs(0, 6.5F, 16, 9.5F)
				.texture("#side")
				.end()
				.face(Direction.UP)
				.uvs(0, 6.5F, 16, 9.5F)
				.texture("#side")
				.end()
				.face(Direction.NORTH)
				.uvs(0, 0, 16, 16)
				.texture("#front")
				.end()
				.face(Direction.SOUTH)
				.uvs(0, 0, 16, 16)
				.texture("#front")
				.end()
				.face(Direction.WEST)
				.uvs(6.5F, 0, 9.5F, 16)
				.texture("#side")
				.end()
				.face(Direction.EAST)
				.uvs(6.5F, 0, 9.5F, 16)
				.texture("#side")
				.end()
				.end();
		simpleBlock(ModBlocks.GARAGE_DOOR.get(), model);
	}

	private void rustyLadder()
	{
		ResourceLocation texture = modLoc("block/rusty_hatch_handle");
		BlockModelBuilder model = models()	.getBuilder("rusty_ladder")
											.ao(false)
											.renderType("cutout")
											.texture("particle", texture)
											.texture("texture", texture);
		for (float x : new float[] { 1, 13 })
			model	.element()
					.from(x, 0, 14)
					.to(x + 2, 16, 16)
					.allFaces((direction, face) -> face.uvs(x, 0, x + 2, 16).texture("#texture"))
					.end();
		for (float y : new float[] { 2, 6, 10, 14 })
		{
			float v = 15 - y;
			model	.element()
					.from(3, y, 14.5F)
					.to(13, y + 1, 15.5F)
					.face(Direction.NORTH)
					.uvs(3, v, 13, v + 1)
					.texture("#texture")
					.end()
					.face(Direction.SOUTH)
					.uvs(3, v, 13, v + 1)
					.texture("#texture")
					.end()
					.face(Direction.UP)
					.uvs(3, v, 13, v + 1)
					.texture("#texture")
					.end()
					.face(Direction.DOWN)
					.uvs(3, v, 13, v + 1)
					.texture("#texture")
					.end()
					.end();
		}
		horizontal(ModBlocks.RUSTY_LADDER.get(), LadderBlock.FACING, model);
	}

	private void horizontal(Block block, DirectionProperty facing, ModelFile model)
	{
		VariantBlockStateBuilder builder = getVariantBuilder(block);
		for (Direction direction : Direction.Plane.HORIZONTAL)
			builder	.partialState()
					.with(facing, direction)
					.modelForState()
					.modelFile(model)
					.rotationY(((int) direction.toYRot() + 180) % 360)
					.addModel();
	}

	private void doorFactory()
	{
		ModelFile model = models()	.withExistingParent("door_factory", mcLoc("block/orientable"))
									.texture("front", modLoc("block/door_factory"))
									.texture("side", modLoc("block/door_factory_side"))
									.texture("top", modLoc("block/door_factory_side"));
		horizontal(ModBlocks.DOOR_FACTORY.get(), HorizontalDirectionalBlock.FACING, model);
	}

	private void playerSensor()
	{
		ResourceLocation texture = modLoc("block/player_sensor");
		BlockModelBuilder wall = models()	.withExistingParent("player_sensor", mcLoc("block/block"))
											.texture("particle", texture)
											.texture("texture", texture);
		wall	.element()
				.from(2, 2, 14)
				.to(14, 4, 16)
				.allFaces((direction, face) -> {
					face.texture("#texture");
					if (direction == Direction.SOUTH)
						face.cullface(Direction.SOUTH);
				})
				.end();
		BlockModelBuilder floor = models()	.withExistingParent("player_sensor_floor", mcLoc("block/block"))
											.texture("particle", texture)
											.texture("texture", texture);
		floor	.element()
				.from(6, 0, 6)
				.to(10, 1, 10)
				.allFaces((direction, face) -> {
					face.texture("#texture");
					if (direction == Direction.DOWN)
						face.cullface(Direction.DOWN);
				})
				.end();
		VariantBlockStateBuilder builder = getVariantBuilder(ModBlocks.PLAYER_SENSOR.get());
		for (Direction facing : Direction.values())
		{
			ConfiguredModel.Builder<VariantBlockStateBuilder> model = builder.partialState().with(PlayerSensorBlock.FACING, facing).modelForState();
			switch (facing)
			{
				case UP -> model.modelFile(floor).addModel();
				case DOWN -> model.modelFile(floor).rotationX(180).addModel();
				default -> model.modelFile(wall).rotationY(((int) facing.toYRot() + 180) % 360).addModel();
			}
		}
	}
}
