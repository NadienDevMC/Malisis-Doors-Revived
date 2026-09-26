package nadiendev.malisisdoorsrevived.datagen.providers;

import java.util.List;

import java.util.stream.Stream;

import com.mojang.math.Quadrant;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.animated.AnimatedCopies;
import nadiendev.malisisdoorsrevived.block.PlayerSensorBlock;
import nadiendev.malisisdoorsrevived.client.item.ForcefieldStateProperty;
import nadiendev.malisisdoorsrevived.client.item.MalisisItemSpecialRenderer;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import nadiendev.malisisdoorsrevived.registry.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;

import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplate;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import net.neoforged.neoforge.registries.DeferredBlock;

public class MDModelProvider extends ModelProvider
{
	private static final TextureSlot BOTTOM = TextureSlot.create("bottom");
	private static final TextureSlot TOP = TextureSlot.create("top");
	private static final TextureSlot SIDE = TextureSlot.create("side");

	private BlockModelGenerators blocks;
	private ItemModelGenerators items;

	public MDModelProvider(PackOutput output)
	{
		super(output, MalisisDoors.MODID);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels)
	{
		this.blocks = blockModels;
		this.items = itemModels;

		animatedCopies();
		texturedDoor(ModBlocks.WOOD_SLIDING_DOOR, "block/sliding_door_wood");
		texturedDoor(ModBlocks.IRON_SLIDING_DOOR, "block/sliding_door_iron");
		texturedDoor(ModBlocks.JAIL_DOOR, "block/jail_door");
		texturedDoor(ModBlocks.LABORATORY_DOOR, "block/laboratory_door");
		texturedDoor(ModBlocks.FACTORY_DOOR, "block/factory_door");
		texturedDoor(ModBlocks.SHOJI_DOOR, "block/shoji_door");
		for (DyeColor color : DyeColor.values())
			texturedDoor(ModBlocks.CURTAINS.get(color), "block/curtains/curtain_" + color.getSerializedName());

		particleOnly(ModBlocks.VERTICAL_HATCH.get(), MalisisDoors.id("block/vertical_hatch"));
		particleOnly(ModBlocks.SALOON_DOOR.get(), MalisisDoors.id("block/saloon_door"));
		particleOnly(ModBlocks.CUSTOM_DOOR.get(), Identifier.withDefaultNamespace("block/oak_planks"));
		particleOnly(ModBlocks.CARRIAGE_DOOR.get(), MalisisDoors.id("block/carriage_door"));
		particleOnly(ModBlocks.MEDIEVAL_DOOR.get(), MalisisDoors.id("block/medieval_door"));
		for (DeferredBlock<? extends Block> door : ModBlocks.DOORS_3X3)
			particleOnly(door.get(), MalisisDoors.id("block/" + door.getId().getPath()));
		particleOnly(ModBlocks.RUSTY_HATCH.get(), MalisisDoors.id("block/rusty_hatch"));
		particleOnly(ModBlocks.HITECH_DOOR.get(), MalisisDoors.id("block/hitechdoor"));
		particleOnly(ModBlocks.FORCEFIELD.get(), MalisisDoors.id("block/forcefield"));

		slidingTrapDoor();
		camoFenceGate();
		garageDoor();
		rustyLadder();
		doorFactory();
		playerSensor();

		flatItems();
		specialItems();
		forcefieldController();
	}

	@Override
	protected Stream<? extends net.minecraft.core.Holder<Block>> getKnownBlocks()
	{
		return super.getKnownBlocks().filter(holder -> !AnimatedCopies.isAnimatedCopy(holder.value()));
	}

	private static Material material(Identifier id)
	{
		return new Material(id);
	}

	private static Identifier modelId(Block block)
	{
		return BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/");
	}

	private void simple(Block block, Identifier model)
	{
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, BlockModelGenerators.plainVariant(model)));
	}

	private void animatedCopies()
	{
		List<? extends DeferredBlock<? extends Block>> list = Stream.of(ModBlocks.DOORS, ModBlocks.TRAPDOORS, ModBlocks.FENCE_GATES).flatMap(List::stream).toList();
		for (DeferredBlock<? extends Block> deferred : list)
		{
			Block block = deferred.get();
			if (AnimatedCopies.isAnimatedCopy(block) || !(block instanceof IDoorBlock door) || !(door.getAppearance() instanceof DoorAppearance.Copy copy))
				continue;
			Identifier vanilla = BuiltInRegistries.BLOCK.getKey(copy.block().get());
			String suffix = block instanceof DoorBlock ? "_bottom_left" : block instanceof TrapDoorBlock ? "_bottom" : "";
			simple(block, vanilla.withPath(path -> "block/" + path.replaceFirst("^waxed_", "") + suffix));
		}
	}

	private static ExtendedModelTemplate doorTemplate(boolean top, boolean flipped, float west, float east)
	{
		TextureSlot main = top ? TOP : BOTTOM;
		Direction end = top ? Direction.UP : Direction.DOWN;
		return ExtendedModelTemplateBuilder	.builder()
											.requiredTextureSlot(BOTTOM)
											.requiredTextureSlot(TOP)
											.requiredTextureSlot(SIDE)
											.requiredTextureSlot(TextureSlot.PARTICLE)
											.ambientOcclusion(false)
											.element(element -> element	.from(0, 0, 0)
																		.to(16, 16, 3)
																		.face(Direction.NORTH, face -> face.uvs(flipped ? 16 : 0, 0, flipped ? 0 : 16, 16).texture(main))
																		.face(Direction.SOUTH, face -> face.uvs(flipped ? 0 : 16, 0, flipped ? 16 : 0, 16).texture(main))
																		.face(Direction.WEST, face -> face.uvs(west, 0, west + 3, 16).texture(SIDE))
																		.face(Direction.EAST, face -> face.uvs(east, 0, east + 3, 16).texture(SIDE))
																		.face(end, face -> face.uvs(0, 0, 3, 16).rotation(Quadrant.R90).texture(SIDE)))
											.build();
	}

	private void texturedDoor(DeferredBlock<? extends Block> door, String texture)
	{
		String name = door.getId().getPath();
		TextureMapping mapping = new TextureMapping()	.put(BOTTOM, material(MalisisDoors.id(texture + "_lower")))
														.put(TOP, material(MalisisDoors.id(texture + "_upper")))
														.put(SIDE, material(MalisisDoors.id(texture + "_side")));
		MultiVariant[][] models = new MultiVariant[2][2];
		for (DoubleBlockHalf half : DoubleBlockHalf.values())
		{
			for (DoorHingeSide hinge : DoorHingeSide.values())
			{
				boolean top = half == DoubleBlockHalf.UPPER;
				boolean right = hinge == DoorHingeSide.RIGHT;
				String part = (top ? "top" : "bottom") + "_" + (right ? "right" : "left");
				float west = top ? (right ? 6 : 3) : (right ? 12 : 9);
				float east = top ? (right ? 3 : 6) : (right ? 9 : 12);
				TextureMapping textures = mapping.copyAndUpdate(TextureSlot.PARTICLE, mapping.get(top ? TOP : BOTTOM));
				Identifier model = doorTemplate(top, right, west, east).create(MalisisDoors.id("block/" + name + "_" + part), textures, blocks.modelOutput);
				models[half.ordinal()][hinge.ordinal()] = BlockModelGenerators.plainVariant(model);
			}
		}
		blocks.blockStateOutput.accept(MultiVariantGenerator	.dispatch(door.get())
																.with(PropertyDispatch	.initial(DoorBlock.HALF, DoorBlock.HINGE)
																						.generate((half, hinge) -> models[half.ordinal()][hinge.ordinal()])));
	}

	private void particleOnly(Block block, Identifier particle)
	{
		Identifier model = ExtendedModelTemplateBuilder	.builder()
														.requiredTextureSlot(TextureSlot.PARTICLE)
														.build()
														.create(modelId(block), new TextureMapping().put(TextureSlot.PARTICLE, material(particle)), blocks.modelOutput);
		simple(block, model);
	}

	private void slidingTrapDoor()
	{
		Identifier bottom = slidingTrapDoorModel("sliding_trapdoor_bottom", 0, 1.5F, 14.5F, 16);
		Identifier top = slidingTrapDoorModel("sliding_trapdoor_top", 14.5F, 16, 0, 1.5F);
		blocks.blockStateOutput.accept(MultiVariantGenerator	.dispatch(ModBlocks.SLIDING_TRAPDOOR.get())
																.with(PropertyDispatch	.initial(TrapDoorBlock.HALF)
																						.select(Half.BOTTOM, BlockModelGenerators.plainVariant(bottom))
																						.select(Half.TOP, BlockModelGenerators.plainVariant(top))));
	}

	private Identifier slidingTrapDoorModel(String name, float y1, float y2, float v1, float v2)
	{
		Material texture = material(MalisisDoors.id("block/sliding_trapdoor"));
		return ExtendedModelTemplateBuilder	.builder()
											.parent(Identifier.withDefaultNamespace("block/thin_block"))
											.requiredTextureSlot(TextureSlot.TEXTURE)
											.requiredTextureSlot(TextureSlot.PARTICLE)
											.element(element -> element	.from(0, y1, 0)
																		.to(16, y2, 16)
																		.allFaces((direction, face) -> face	.uvs(0, direction.getAxis() == Direction.Axis.Y ? 0 : v1, 16, direction.getAxis() == Direction.Axis.Y ? 16 : v2)
																											.texture(TextureSlot.TEXTURE)))
											.build()
											.create(MalisisDoors.id("block/" + name), new TextureMapping().put(TextureSlot.TEXTURE, texture).put(TextureSlot.PARTICLE, texture), blocks.modelOutput);
	}

	private void camoFenceGate()
	{
		Block gate = ModBlocks.CAMO_FENCE_GATE.get();
		TextureMapping textures = TextureMapping.defaultTexture(material(MalisisDoors.id("block/camo_fencegate")));
		Identifier closed = ModelTemplates.FENCE_GATE_CLOSED.create(gate, textures, blocks.modelOutput);
		Identifier wall = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(gate, textures, blocks.modelOutput);
		blocks.blockStateOutput.accept(MultiVariantGenerator	.dispatch(gate)
																.with(PropertyDispatch	.initial(FenceGateBlock.IN_WALL)
																						.select(false, BlockModelGenerators.plainVariant(closed))
																						.select(true, BlockModelGenerators.plainVariant(wall))));
	}

	private void garageDoor()
	{
		Block block = ModBlocks.GARAGE_DOOR.get();
		Identifier model = ExtendedModelTemplateBuilder	.builder()
														.parent(Identifier.withDefaultNamespace("block/block"))
														.requiredTextureSlot(TextureSlot.PARTICLE)
														.requiredTextureSlot(SIDE)
														.requiredTextureSlot(TextureSlot.FRONT)
														.element(element -> element	.from(0, 0, 6.5F)
																					.to(16, 16, 9.5F)
																					.face(Direction.DOWN, face -> face.uvs(0, 6.5F, 16, 9.5F).texture(SIDE))
																					.face(Direction.UP, face -> face.uvs(0, 6.5F, 16, 9.5F).texture(SIDE))
																					.face(Direction.NORTH, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.FRONT))
																					.face(Direction.SOUTH, face -> face.uvs(0, 0, 16, 16).texture(TextureSlot.FRONT))
																					.face(Direction.WEST, face -> face.uvs(6.5F, 0, 9.5F, 16).texture(SIDE))
																					.face(Direction.EAST, face -> face.uvs(6.5F, 0, 9.5F, 16).texture(SIDE)))
														.build()
														.create(modelId(block),
																new TextureMapping()	.put(TextureSlot.PARTICLE, material(MalisisDoors.id("block/garage_door")))
																						.put(SIDE, material(MalisisDoors.id("block/garage_door")))
																						.put(TextureSlot.FRONT, material(MalisisDoors.id("block/garage_door_top"))),
																blocks.modelOutput);
		simple(block, model);
	}

	private void rustyLadder()
	{
		Block block = ModBlocks.RUSTY_LADDER.get();
		ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder	.builder()
																			.requiredTextureSlot(TextureSlot.TEXTURE)
																			.requiredTextureSlot(TextureSlot.PARTICLE)
																			.ambientOcclusion(false);
		for (float x : new float[] { 1, 13 })
			builder.element(element -> element	.from(x, 0, 14)
												.to(x + 2, 16, 16)
												.allFaces((direction, face) -> face.uvs(x, 0, x + 2, 16).texture(TextureSlot.TEXTURE)));
		for (float y : new float[] { 2, 6, 10, 14 })
		{
			float v = 15 - y;
			builder.element(element -> {
				element.from(3, y, 14.5F).to(13, y + 1, 15.5F);
				for (Direction direction : new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN })
					element.face(direction, face -> face.uvs(3, v, 13, v + 1).texture(TextureSlot.TEXTURE));
			});
		}
		Material texture = material(MalisisDoors.id("block/rusty_hatch_handle"));
		Identifier model = builder.build().create(modelId(block), new TextureMapping().put(TextureSlot.TEXTURE, texture).put(TextureSlot.PARTICLE, texture), blocks.modelOutput);
		horizontal(block, LadderBlock.FACING, model);
	}

	private void horizontal(Block block, EnumProperty<Direction> facing, Identifier model)
	{
		MultiVariant variant = BlockModelGenerators.plainVariant(model);
		blocks.blockStateOutput.accept(MultiVariantGenerator	.dispatch(block)
																.with(PropertyDispatch	.initial(facing)
																						.select(Direction.NORTH, variant)
																						.select(Direction.EAST, variant.with(BlockModelGenerators.Y_ROT_90))
																						.select(Direction.SOUTH, variant.with(BlockModelGenerators.Y_ROT_180))
																						.select(Direction.WEST, variant.with(BlockModelGenerators.Y_ROT_270))));
	}

	private void doorFactory()
	{
		Block block = ModBlocks.DOOR_FACTORY.get();
		Identifier model = ModelTemplates.CUBE_ORIENTABLE.create(	block,
																	new TextureMapping()	.put(TextureSlot.FRONT, material(MalisisDoors.id("block/door_factory")))
																							.put(TextureSlot.SIDE, material(MalisisDoors.id("block/door_factory_side")))
																							.put(TextureSlot.TOP, material(MalisisDoors.id("block/door_factory_side"))),
																	blocks.modelOutput);
		horizontal(block, HorizontalDirectionalBlock.FACING, model);
	}

	private void playerSensor()
	{
		Block block = ModBlocks.PLAYER_SENSOR.get();
		Material texture = material(MalisisDoors.id("block/player_sensor"));
		TextureMapping textures = new TextureMapping().put(TextureSlot.TEXTURE, texture).put(TextureSlot.PARTICLE, texture);
		Identifier wall = sensorModel(2, 2, 14, 14, 4, 16, Direction.SOUTH).create(modelId(block), textures, blocks.modelOutput);
		Identifier floor = sensorModel(6, 0, 6, 10, 1, 10, Direction.DOWN).create(modelId(block).withSuffix("_floor"), textures, blocks.modelOutput);
		MultiVariant wallVariant = BlockModelGenerators.plainVariant(wall);
		MultiVariant floorVariant = BlockModelGenerators.plainVariant(floor);
		blocks.blockStateOutput.accept(MultiVariantGenerator	.dispatch(block)
																.with(PropertyDispatch	.initial(PlayerSensorBlock.FACING)
																						.select(Direction.NORTH, wallVariant)
																						.select(Direction.EAST, wallVariant.with(BlockModelGenerators.Y_ROT_90))
																						.select(Direction.SOUTH, wallVariant.with(BlockModelGenerators.Y_ROT_180))
																						.select(Direction.WEST, wallVariant.with(BlockModelGenerators.Y_ROT_270))
																						.select(Direction.UP, floorVariant)
																						.select(Direction.DOWN, floorVariant.with(BlockModelGenerators.X_ROT_180))));
		items.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(sensorModel(2, 6, 6, 14, 10, 10, null).create(	MalisisDoors.id("item/player_sensor"),
																																textures,
																																items.modelOutput)));
	}

	private static ExtendedModelTemplate sensorModel(float x1, float y1, float z1, float x2, float y2, float z2, Direction cull)
	{
		return ExtendedModelTemplateBuilder	.builder()
											.parent(Identifier.withDefaultNamespace("block/block"))
											.requiredTextureSlot(TextureSlot.TEXTURE)
											.requiredTextureSlot(TextureSlot.PARTICLE)
											.element(element -> element	.from(x1, y1, z1)
																		.to(x2, y2, z2)
																		.allFaces((direction, face) -> {
																			face.texture(TextureSlot.TEXTURE);
																			if (direction == cull)
																				face.cullface(cull);
																		}))
											.build();
	}

	private void flatItems()
	{
		Stream.of(ModBlocks.WOOD_SLIDING_DOOR, ModBlocks.IRON_SLIDING_DOOR, ModBlocks.JAIL_DOOR, ModBlocks.LABORATORY_DOOR, ModBlocks.FACTORY_DOOR, ModBlocks.SHOJI_DOOR)
				.forEach(door -> items.generateFlatItem(door.get().asItem(), ModelTemplates.FLAT_ITEM));
		for (DyeColor color : DyeColor.values())
			items.generateFlatItem(ModBlocks.CURTAINS.get(color).get().asItem(), ModelTemplates.FLAT_ITEM);
		items.generateFlatItem(ModBlocks.SALOON_DOOR.get().asItem(), ModelTemplates.FLAT_ITEM);
		items.generateFlatItem(ModItems.CARRIAGE_DOOR.get(), ModelTemplates.FLAT_ITEM);
		items.generateFlatItem(ModItems.MEDIEVAL_DOOR.get(), ModelTemplates.FLAT_ITEM);
		for (DeferredBlock<? extends Block> door : ModBlocks.DOORS_3X3)
			items.generateFlatItem(door.get().asItem(), ModelTemplates.FLAT_ITEM);
		items.generateFlatItem(ModItems.RUSTY_HATCH.get(), ModelTemplates.FLAT_ITEM);

		blockItem(ModItems.CAMO_FENCE_GATE.get(), MalisisDoors.id("block/camo_fence_gate"));
		blockItem(ModItems.GARAGE_DOOR.get(), MalisisDoors.id("block/garage_door"));
		blockItem(ModItems.SLIDING_TRAPDOOR.get(), MalisisDoors.id("block/sliding_trapdoor_bottom"));
		blockItem(ModItems.RUSTY_LADDER.get(), MalisisDoors.id("block/rusty_ladder"));
		blockItem(ModItems.DOOR_FACTORY.get(), MalisisDoors.id("block/door_factory"));
	}

	private void blockItem(Item item, Identifier model)
	{
		items.itemModelOutput.accept(item, ItemModelUtils.plainModel(model));
	}

	private void specialItems()
	{
		special(ModItems.CUSTOM_DOOR.get(), Identifier.withDefaultNamespace("block/oak_planks"));
		special(ModBlocks.VERTICAL_HATCH.get().asItem(), MalisisDoors.id("block/vertical_hatch"));
		special(ModItems.HITECH_DOOR.get(), MalisisDoors.id("block/hitechdoor"));
		special(ModItems.RUSTY_HANDLE.get(), MalisisDoors.id("block/rusty_hatch_handle"));
	}

	private void special(ItemLike item, Identifier particle)
	{
		Identifier id = BuiltInRegistries.ITEM.getKey(item.asItem()).withPrefix("item/");
		Identifier base = ExtendedModelTemplateBuilder	.builder()
														.requiredTextureSlot(TextureSlot.PARTICLE)
														.guiLight(UnbakedModel.GuiLight.SIDE)
														.transform(ItemDisplayContext.GUI, t -> t.rotation(30, 225, 0).scale(0.625F))
														.transform(ItemDisplayContext.GROUND, t -> t.translation(0, 3, 0).scale(0.25F))
														.transform(ItemDisplayContext.HEAD, t -> t.rotation(0, 180, 0))
														.transform(ItemDisplayContext.FIXED, t -> t.rotation(0, 180, 0).scale(0.5F))
														.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, t -> t.rotation(75, 315, 0).translation(0, 2.5F, 0).scale(0.375F))
														.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, t -> t.rotation(0, 315, 0).scale(0.4F))
														.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, t -> t.rotation(0, 315, 0).scale(0.4F))
														.build()
														.create(id, new TextureMapping().put(TextureSlot.PARTICLE, material(particle)), items.modelOutput);
		items.itemModelOutput.accept(item.asItem(), ItemModelUtils.specialModel(base, new MalisisItemSpecialRenderer.Unbaked()));
	}

	private void forcefieldController()
	{
		Item item = ModItems.FORCEFIELD_CONTROLLER.get();
		Identifier base = items.createFlatItemModel(item, ModelTemplates.FLAT_HANDHELD_ITEM);
		Identifier yellow = items.createFlatItemModel(item, "_yellow", ModelTemplates.FLAT_HANDHELD_ITEM);
		Identifier red = items.createFlatItemModel(item, "_red", ModelTemplates.FLAT_HANDHELD_ITEM);
		Identifier green = items.createFlatItemModel(item, "_green", ModelTemplates.FLAT_HANDHELD_ITEM);
		items.itemModelOutput.accept(item, ItemModelUtils.rangeSelect(	new ForcefieldStateProperty(),
																		ItemModelUtils.plainModel(base),
																		ItemModelUtils.override(ItemModelUtils.plainModel(yellow), 0.25F),
																		ItemModelUtils.override(ItemModelUtils.plainModel(red), 0.5F),
																		ItemModelUtils.override(ItemModelUtils.plainModel(green), 0.75F)));
	}
}
