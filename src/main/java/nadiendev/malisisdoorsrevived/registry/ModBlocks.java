package nadiendev.malisisdoorsrevived.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.block.BigDoorBlock;
import nadiendev.malisisdoorsrevived.block.CustomDoorBlock;
import nadiendev.malisisdoorsrevived.block.Door3x3Block;
import nadiendev.malisisdoorsrevived.block.DoorFactoryBlock;
import nadiendev.malisisdoorsrevived.block.ForcefieldBlock;
import nadiendev.malisisdoorsrevived.block.GarageDoorBlock;
import nadiendev.malisisdoorsrevived.block.HitechDoorBlock;
import nadiendev.malisisdoorsrevived.block.MalisisDoorBlock;
import nadiendev.malisisdoorsrevived.block.MalisisFenceGateBlock;
import nadiendev.malisisdoorsrevived.block.MalisisTrapDoorBlock;
import nadiendev.malisisdoorsrevived.block.MultiBlockDoorBlock;
import nadiendev.malisisdoorsrevived.block.PlayerSensorBlock;
import nadiendev.malisisdoorsrevived.block.RustyHatchBlock;
import nadiendev.malisisdoorsrevived.block.SaloonDoorBlock;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.DoorDescriptor;
import nadiendev.malisisdoorsrevived.door.DoorRegistry;
import nadiendev.malisisdoorsrevived.door.RedstoneBehavior;
import nadiendev.malisisdoorsrevived.sound.DoorSound;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * All the blocks of MalisisDoors.
 *
 * @author Ordinastie
 */
public class ModBlocks
{
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MalisisDoors.MODID);

	/** Doors using the default {@link nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity}. */
	public static final List<DeferredBlock<? extends MalisisDoorBlock>> DOORS = new ArrayList<>();
	/** Doors with their own item (as opposed to the animated vanilla doors). */
	public static final List<DeferredBlock<? extends Block>> ITEM_DOORS = new ArrayList<>();
	public static final List<DeferredBlock<MalisisTrapDoorBlock>> TRAPDOORS = new ArrayList<>();
	public static final List<DeferredBlock<MalisisFenceGateBlock>> FENCE_GATES = new ArrayList<>();
	public static final List<DeferredBlock<? extends MultiBlockDoorBlock>> MULTIBLOCK_DOORS = new ArrayList<>();
	public static final Map<DyeColor, DeferredBlock<MalisisDoorBlock>> CURTAINS = new java.util.EnumMap<>(DyeColor.class);
	public static final List<DeferredBlock<Door3x3Block>> DOORS_3X3 = new ArrayList<>();

	/** Vanilla blocks and their animated version (filled once the blocks are registered). */
	private static final List<VanillaCopy> VANILLA_COPIES = new ArrayList<>();
	private static Map<Block, Block> vanillaToAnimated;

	private record VanillaCopy(Supplier<Block> vanilla, DeferredBlock<? extends Block> animated)
	{}

	//#region Animated vanilla doors, trapdoors and fence gates
	static
	{
		vanillaDoor("oak", () -> Blocks.OAK_DOOR, BlockSetType.OAK);
		vanillaDoor("spruce", () -> Blocks.SPRUCE_DOOR, BlockSetType.SPRUCE);
		vanillaDoor("birch", () -> Blocks.BIRCH_DOOR, BlockSetType.BIRCH);
		vanillaDoor("jungle", () -> Blocks.JUNGLE_DOOR, BlockSetType.JUNGLE);
		vanillaDoor("acacia", () -> Blocks.ACACIA_DOOR, BlockSetType.ACACIA);
		vanillaDoor("dark_oak", () -> Blocks.DARK_OAK_DOOR, BlockSetType.DARK_OAK);
		vanillaDoor("mangrove", () -> Blocks.MANGROVE_DOOR, BlockSetType.MANGROVE);
		vanillaDoor("cherry", () -> Blocks.CHERRY_DOOR, BlockSetType.CHERRY);
		vanillaDoor("pale_oak", () -> Blocks.PALE_OAK_DOOR, BlockSetType.PALE_OAK);
		vanillaDoor("bamboo", () -> Blocks.BAMBOO_DOOR, BlockSetType.BAMBOO);
		vanillaDoor("crimson", () -> Blocks.CRIMSON_DOOR, BlockSetType.CRIMSON);
		vanillaDoor("warped", () -> Blocks.WARPED_DOOR, BlockSetType.WARPED);
		vanillaDoor("iron", () -> Blocks.IRON_DOOR, BlockSetType.IRON);
		vanillaDoor("copper", () -> Blocks.COPPER_DOOR, BlockSetType.COPPER);
		vanillaDoor("exposed_copper", () -> Blocks.EXPOSED_COPPER_DOOR, BlockSetType.COPPER);
		vanillaDoor("weathered_copper", () -> Blocks.WEATHERED_COPPER_DOOR, BlockSetType.COPPER);
		vanillaDoor("oxidized_copper", () -> Blocks.OXIDIZED_COPPER_DOOR, BlockSetType.COPPER);
		vanillaDoor("waxed_copper", () -> Blocks.WAXED_COPPER_DOOR, BlockSetType.COPPER);
		vanillaDoor("waxed_exposed_copper", () -> Blocks.WAXED_EXPOSED_COPPER_DOOR, BlockSetType.COPPER);
		vanillaDoor("waxed_weathered_copper", () -> Blocks.WAXED_WEATHERED_COPPER_DOOR, BlockSetType.COPPER);
		vanillaDoor("waxed_oxidized_copper", () -> Blocks.WAXED_OXIDIZED_COPPER_DOOR, BlockSetType.COPPER);

		vanillaTrapDoor("oak", () -> Blocks.OAK_TRAPDOOR, BlockSetType.OAK);
		vanillaTrapDoor("spruce", () -> Blocks.SPRUCE_TRAPDOOR, BlockSetType.SPRUCE);
		vanillaTrapDoor("birch", () -> Blocks.BIRCH_TRAPDOOR, BlockSetType.BIRCH);
		vanillaTrapDoor("jungle", () -> Blocks.JUNGLE_TRAPDOOR, BlockSetType.JUNGLE);
		vanillaTrapDoor("acacia", () -> Blocks.ACACIA_TRAPDOOR, BlockSetType.ACACIA);
		vanillaTrapDoor("dark_oak", () -> Blocks.DARK_OAK_TRAPDOOR, BlockSetType.DARK_OAK);
		vanillaTrapDoor("mangrove", () -> Blocks.MANGROVE_TRAPDOOR, BlockSetType.MANGROVE);
		vanillaTrapDoor("cherry", () -> Blocks.CHERRY_TRAPDOOR, BlockSetType.CHERRY);
		vanillaTrapDoor("pale_oak", () -> Blocks.PALE_OAK_TRAPDOOR, BlockSetType.PALE_OAK);
		vanillaTrapDoor("bamboo", () -> Blocks.BAMBOO_TRAPDOOR, BlockSetType.BAMBOO);
		vanillaTrapDoor("crimson", () -> Blocks.CRIMSON_TRAPDOOR, BlockSetType.CRIMSON);
		vanillaTrapDoor("warped", () -> Blocks.WARPED_TRAPDOOR, BlockSetType.WARPED);
		vanillaTrapDoor("iron", () -> Blocks.IRON_TRAPDOOR, BlockSetType.IRON);
		vanillaTrapDoor("copper", () -> Blocks.COPPER_TRAPDOOR, BlockSetType.COPPER);
		vanillaTrapDoor("exposed_copper", () -> Blocks.EXPOSED_COPPER_TRAPDOOR, BlockSetType.COPPER);
		vanillaTrapDoor("weathered_copper", () -> Blocks.WEATHERED_COPPER_TRAPDOOR, BlockSetType.COPPER);
		vanillaTrapDoor("oxidized_copper", () -> Blocks.OXIDIZED_COPPER_TRAPDOOR, BlockSetType.COPPER);
		vanillaTrapDoor("waxed_copper", () -> Blocks.WAXED_COPPER_TRAPDOOR, BlockSetType.COPPER);
		vanillaTrapDoor("waxed_exposed_copper", () -> Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR, BlockSetType.COPPER);
		vanillaTrapDoor("waxed_weathered_copper", () -> Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR, BlockSetType.COPPER);
		vanillaTrapDoor("waxed_oxidized_copper", () -> Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR, BlockSetType.COPPER);

		vanillaFenceGate("oak", () -> Blocks.OAK_FENCE_GATE, WoodType.OAK);
		vanillaFenceGate("spruce", () -> Blocks.SPRUCE_FENCE_GATE, WoodType.SPRUCE);
		vanillaFenceGate("birch", () -> Blocks.BIRCH_FENCE_GATE, WoodType.BIRCH);
		vanillaFenceGate("jungle", () -> Blocks.JUNGLE_FENCE_GATE, WoodType.JUNGLE);
		vanillaFenceGate("acacia", () -> Blocks.ACACIA_FENCE_GATE, WoodType.ACACIA);
		vanillaFenceGate("dark_oak", () -> Blocks.DARK_OAK_FENCE_GATE, WoodType.DARK_OAK);
		vanillaFenceGate("mangrove", () -> Blocks.MANGROVE_FENCE_GATE, WoodType.MANGROVE);
		vanillaFenceGate("cherry", () -> Blocks.CHERRY_FENCE_GATE, WoodType.CHERRY);
		vanillaFenceGate("pale_oak", () -> Blocks.PALE_OAK_FENCE_GATE, WoodType.PALE_OAK);
		vanillaFenceGate("bamboo", () -> Blocks.BAMBOO_FENCE_GATE, WoodType.BAMBOO);
		vanillaFenceGate("crimson", () -> Blocks.CRIMSON_FENCE_GATE, WoodType.CRIMSON);
		vanillaFenceGate("warped", () -> Blocks.WARPED_FENCE_GATE, WoodType.WARPED);
	}

	private enum Kind
	{
		DOOR,
		TRAPDOOR,
		FENCE_GATE
	}

	/**
	 * Copies the properties of the vanilla block (the loot table is not copied : the animated blocks have their own).
	 */
	private static BlockBehaviour.Properties copyProperties(Block vanilla, Kind kind)
	{
		BlockBehaviour.Properties properties = BlockBehaviour.Properties	.of()
																		.mapColor(vanilla.defaultMapColor())
																		.strength(vanilla.defaultDestroyTime(), vanilla.getExplosionResistance())
																		.noOcclusion()
																		.overrideDescription(vanilla.getDescriptionId());
		if (vanilla.defaultBlockState().requiresCorrectToolForDrops())
			properties.requiresCorrectToolForDrops();
		if (vanilla.defaultBlockState().ignitedByLava())
			properties.ignitedByLava();
		if (vanilla.defaultBlockState().isRandomlyTicking())
			properties.randomTicks();
		switch (kind)
		{
			case DOOR -> properties.pushReaction(PushReaction.DESTROY);
			//like vanilla : mobs can't spawn on trapdoors
			case TRAPDOOR -> properties.isValidSpawn(Blocks::never);
			//like vanilla : fence gates block fluids
			case FENCE_GATE -> properties.forceSolidOn();
		}
		return properties;
	}

	private static void vanillaDoor(String name, Supplier<Block> vanilla, BlockSetType type)
	{
		DeferredBlock<MalisisDoorBlock> block = BLOCKS.register("animated_" + name + "_door", id -> animatedDoor(vanilla, type, withId(id, copyProperties(vanilla.get(), Kind.DOOR))));
		DOORS.add(block);
		VANILLA_COPIES.add(new VanillaCopy(vanilla, block));
	}

	private static void vanillaTrapDoor(String name, Supplier<Block> vanilla, BlockSetType type)
	{
		DeferredBlock<MalisisTrapDoorBlock> block = BLOCKS.register("animated_" + name + "_trapdoor",
																	id -> animatedTrapDoor(vanilla, type, withId(id, copyProperties(vanilla.get(), Kind.TRAPDOOR))));
		TRAPDOORS.add(block);
		VANILLA_COPIES.add(new VanillaCopy(vanilla, block));
	}

	private static void vanillaFenceGate(String name, Supplier<Block> vanilla, WoodType type)
	{
		DeferredBlock<MalisisFenceGateBlock> block = BLOCKS.register("animated_" + name + "_fence_gate",
																	id -> animatedFenceGate(vanilla, type, withId(id, copyProperties(vanilla.get(), Kind.FENCE_GATE))));
		FENCE_GATES.add(block);
		VANILLA_COPIES.add(new VanillaCopy(vanilla, block));
	}

	private static MalisisDoorBlock animatedDoor(Supplier<Block> vanilla, BlockSetType type, BlockBehaviour.Properties properties)
	{
		DoorDescriptor desc = new DoorDescriptor()	.setMovement(DoorRegistry.ROTATING_DOOR)
													.setSound(DoorSound.door(type))
													.setOpeningTime(6)
													.setRedstoneBehavior(type.canOpenByHand() ? RedstoneBehavior.STANDARD : RedstoneBehavior.REDSTONE_ONLY);
		return new MalisisDoorBlock(type, properties, desc, new DoorAppearance.Copy(vanilla), () -> vanilla.get().asItem());
	}

	private static MalisisTrapDoorBlock animatedTrapDoor(Supplier<Block> vanilla, BlockSetType type, BlockBehaviour.Properties properties)
	{
		DoorDescriptor desc = new DoorDescriptor()	.setMovement(DoorRegistry.TRAP_DOOR)
													.setSound(DoorSound.trapDoor(type))
													.setOpeningTime(6)
													.setDoubleDoor(false)
													.setRedstoneBehavior(type.canOpenByHand() ? RedstoneBehavior.STANDARD : RedstoneBehavior.REDSTONE_ONLY);
		return new MalisisTrapDoorBlock(type, properties, desc, new DoorAppearance.Copy(vanilla), () -> vanilla.get().asItem());
	}

	private static MalisisFenceGateBlock animatedFenceGate(Supplier<Block> vanilla, WoodType type, BlockBehaviour.Properties properties)
	{
		DoorDescriptor desc = new DoorDescriptor()	.setMovement(DoorRegistry.FENCE_GATE)
													.setSound(DoorSound.fenceGate(type))
													.setOpeningTime(6);
		return new MalisisFenceGateBlock(type, properties, desc, new DoorAppearance.Copy(vanilla), () -> vanilla.get().asItem());
	}

	public static Block createAnimatedCopy(Identifier id, Block original)
	{
		Supplier<Block> supplier = () -> original;
		return switch (original)
		{
			case DoorBlock door -> animatedDoor(supplier, door.type(), withId(id, copyProperties(original, Kind.DOOR).noLootTable()));
			case TrapDoorBlock trapDoor -> animatedTrapDoor(supplier,
															ObfuscationReflectionHelper.getPrivateValue(TrapDoorBlock.class, trapDoor, "type"),
															withId(id, copyProperties(original, Kind.TRAPDOOR).noLootTable()));
			case FenceGateBlock gate -> animatedFenceGate(	supplier,
															ObfuscationReflectionHelper.getPrivateValue(FenceGateBlock.class, gate, "type"),
															withId(id, copyProperties(original, Kind.FENCE_GATE).noLootTable()));
			default -> throw new IllegalArgumentException(original + " is not a door, a trapdoor or a fence gate");
		};
	}

	@SuppressWarnings("unchecked")
	public static void addAnimatedCopy(Block original, DeferredBlock<? extends Block> animated, Block block)
	{
		switch (block)
		{
			case MalisisDoorBlock door -> DOORS.add((DeferredBlock<MalisisDoorBlock>) animated);
			case MalisisTrapDoorBlock trapDoor -> TRAPDOORS.add((DeferredBlock<MalisisTrapDoorBlock>) animated);
			case MalisisFenceGateBlock gate -> FENCE_GATES.add((DeferredBlock<MalisisFenceGateBlock>) animated);
			default -> throw new IllegalArgumentException(block + " is not an animated door, trapdoor or fence gate");
		}
		VANILLA_COPIES.add(new VanillaCopy(() -> original, animated));
	}

	public static boolean hasAnimatedCopy(Block original)
	{
		return VANILLA_COPIES.stream().anyMatch(copy -> copy.vanilla().get() == original);
	}

	/**
	 * Gets the animated version of a vanilla door, trapdoor or fence gate.
	 *
	 * @return the animated block, or null if there is none
	 */
	public static Block getAnimated(Block vanilla)
	{
		if (vanillaToAnimated == null)
		{
			Map<Block, Block> map = new IdentityHashMap<>();
			for (VanillaCopy copy : VANILLA_COPIES)
				map.put(copy.vanilla().get(), copy.animated().get());
			vanillaToAnimated = Collections.unmodifiableMap(map);
		}
		return vanillaToAnimated.get(vanilla);
	}
	//#end Animated vanilla doors, trapdoors and fence gates

	//#region MalisisDoors doors
	private static BlockBehaviour.Properties withId(Identifier id, BlockBehaviour.Properties properties)
	{
		return properties.setId(ResourceKey.create(Registries.BLOCK, id));
	}

	private static BlockBehaviour.Properties wood(float hardness)
	{
		return BlockBehaviour.Properties.of()
										.mapColor(MapColor.WOOD)
										.instrument(NoteBlockInstrument.BASS)
										.strength(hardness)
										.sound(SoundType.WOOD)
										.noOcclusion()
										.ignitedByLava()
										.pushReaction(PushReaction.DESTROY);
	}

	private static BlockBehaviour.Properties metal(float hardness)
	{
		return BlockBehaviour.Properties.of()
										.mapColor(MapColor.METAL)
										.strength(hardness)
										.requiresCorrectToolForDrops()
										.noOcclusion()
										.pushReaction(PushReaction.DESTROY);
	}

	private static DeferredBlock<MalisisDoorBlock> door(String name, BlockSetType type, BlockBehaviour.Properties properties, DoorDescriptor desc, DoorAppearance appearance)
	{
		DeferredBlock<MalisisDoorBlock> block = BLOCKS.register(name, id -> new MalisisDoorBlock(type, withId(id, properties), desc, appearance));
		DOORS.add(block);
		ITEM_DOORS.add(block);
		return block;
	}

	public static final DeferredBlock<MalisisDoorBlock> WOOD_SLIDING_DOOR = door(	"wood_sliding_door",
																					BlockSetType.OAK,
																					wood(2.0F),
																					new DoorDescriptor()	.setMovement(DoorRegistry.SLIDING_DOOR)
																											.setSound(DoorRegistry.GLASS_DOOR_SOUND),
																					DoorAppearance.SELF);
	public static final DeferredBlock<MalisisDoorBlock> IRON_SLIDING_DOOR = door(	"iron_sliding_door",
																					BlockSetType.IRON,
																					metal(3.0F).sound(SoundType.METAL),
																					new DoorDescriptor()	.setMovement(DoorRegistry.SLIDING_DOOR)
																											.setSound(DoorRegistry.GLASS_DOOR_SOUND)
																											.setRedstoneBehavior(RedstoneBehavior.REDSTONE_ONLY),
																					DoorAppearance.SELF);
	public static final DeferredBlock<MalisisDoorBlock> JAIL_DOOR = door(	"jail_door",
																			BlockSetType.IRON,
																			metal(5.0F).sound(SoundType.METAL),
																			new DoorDescriptor()	.setMovement(DoorRegistry.SLIDING_DOOR)
																									.setSound(DoorRegistry.JAIL_DOOR_SOUND)
																									.setOpeningTime(12)
																									.setRedstoneBehavior(RedstoneBehavior.REDSTONE_ONLY),
																			DoorAppearance.SELF);
	public static final DeferredBlock<MalisisDoorBlock> LABORATORY_DOOR = door(	"laboratory_door",
																				BlockSetType.IRON,
																				metal(4.0F).sound(SoundType.METAL),
																				new DoorDescriptor()	.setMovement(DoorRegistry.SLIDING_UP_DOOR)
																										.setSound(DoorRegistry.PNEUMATIC_SOUND)
																										.setOpeningTime(12)
																										.setDoubleDoor(false),
																				DoorAppearance.SELF);
	public static final DeferredBlock<MalisisDoorBlock> FACTORY_DOOR = door("factory_door",
																			BlockSetType.IRON,
																			metal(4.0F).sound(SoundType.METAL),
																			new DoorDescriptor()	.setMovement(DoorRegistry.SLIDING_SPLIT_DOOR)
																									.setSound(DoorRegistry.PNEUMATIC_SOUND)
																									.setOpeningTime(12),
																			DoorAppearance.SELF);
	public static final DeferredBlock<MalisisDoorBlock> SHOJI_DOOR = door(	"shoji_door",
																			BlockSetType.OAK,
																			wood(1.0F).sound(SoundType.WOOL),
																			new DoorDescriptor()	.setMovement(DoorRegistry.SLIDING_DOOR)
																									.setSound(DoorRegistry.SHOJI_DOOR_SOUND)
																									.setOpeningTime(20),
																			DoorAppearance.SELF);

	static
	{
		for (DyeColor color : DyeColor.values())
		{
			BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
																			.mapColor(color.getMapColor())
																			.strength(2.0F)
																			.sound(SoundType.WOOL)
																			.noOcclusion()
																			.ignitedByLava()
																			.pushReaction(PushReaction.DESTROY);
			CURTAINS.put(color, door("curtain_" + color.getSerializedName(),
										BlockSetType.OAK,
										properties,
										new DoorDescriptor().setMovement(DoorRegistry.CURTAIN).setSound(DoorRegistry.SILENT_SOUND),
										DoorAppearance.SELF));
		}
	}

	public static final DeferredBlock<MalisisDoorBlock> VERTICAL_HATCH = door(	"vertical_hatch",
																				BlockSetType.IRON,
																				metal(3.5F).sound(SoundType.METAL),
																				new DoorDescriptor()	.setMovement(DoorRegistry.VERTICAL_HATCH)
																										.setSound(DoorRegistry.RUSTY_HATCH_SOUND)
																										.setOpeningTime(60)
																										.setDoubleDoor(false),
																				new DoorAppearance.Obj(	MalisisDoors.id("models/vertical_hatch.obj"),
																										MalisisDoors.id("block/vertical_hatch")));

	public static final DeferredBlock<SaloonDoorBlock> SALOON_DOOR = registerItemDoor("saloon_door",
																						id -> new SaloonDoorBlock(BlockSetType.OAK, withId(id, wood(2.5F)),
																													new DoorDescriptor().setMovement(DoorRegistry.SALOON_DOOR)
																																		.setSound(DoorRegistry.SILENT_SOUND)
																																		.setOpeningTime(40),
																													new DoorAppearance.Obj(	MalisisDoors.id("models/saloon_door.obj"),
																																			MalisisDoors.id("block/saloon_door"))));

	public static final DeferredBlock<CustomDoorBlock> CUSTOM_DOOR = registerItemDoor("custom_door",
																						id -> new CustomDoorBlock(BlockSetType.OAK, withId(id, wood(2.0F).mapColor(MapColor.STONE)),
																													new DoorDescriptor()));

	private static <T extends Block> DeferredBlock<T> registerItemDoor(String name, Function<Identifier, T> factory)
	{
		DeferredBlock<T> block = BLOCKS.register(name, factory);
		ITEM_DOORS.add(block);
		return block;
	}
	//#end MalisisDoors doors

	//#region Trapdoors and fence gates
	public static final DeferredBlock<MalisisTrapDoorBlock> SLIDING_TRAPDOOR = BLOCKS.register(	"sliding_trapdoor",
																								id -> new MalisisTrapDoorBlock(BlockSetType.IRON, withId(id, metal(4.0F).sound(SoundType.METAL).pushReaction(PushReaction.NORMAL).isValidSpawn(Blocks::never)),
																																new DoorDescriptor().setMovement(DoorRegistry.SLIDING_TRAPDOOR)
																																					.setSound(DoorRegistry.PNEUMATIC_SOUND)
																																					.setOpeningTime(12)
																																					.setDoubleDoor(false),
																																DoorAppearance.SELF,
																																null));

	public static final DeferredBlock<MalisisFenceGateBlock> CAMO_FENCE_GATE = BLOCKS.register(	"camo_fence_gate",
																								id -> new MalisisFenceGateBlock(WoodType.OAK, withId(id, wood(2.0F).strength(2.0F, 3.0F).forceSolidOn().pushReaction(PushReaction.NORMAL)),
																																new DoorDescriptor().setMovement(DoorRegistry.FENCE_GATE)
																																					.setSound(DoorRegistry.FENCE_GATE_SOUND),
																																DoorAppearance.SELF,
																																null));

	static
	{
		TRAPDOORS.add(SLIDING_TRAPDOOR);
		FENCE_GATES.add(CAMO_FENCE_GATE);
	}
	//#end Trapdoors and fence gates

	//#region Other doors
	public static final DeferredBlock<GarageDoorBlock> GARAGE_DOOR = BLOCKS.register("garage_door", id -> new GarageDoorBlock(withId(id, wood(2.0F))));

	public static final DeferredBlock<BigDoorBlock> CARRIAGE_DOOR = BLOCKS.register("carriage_door",
																					id -> new BigDoorBlock(withId(id, wood(5.0F).strength(5.0F, 10.0F)),
																											new DoorAppearance.Obj(	MalisisDoors.id("models/big_door.obj"),
																																	MalisisDoors.id("block/carriage_door"))));
	public static final DeferredBlock<BigDoorBlock> MEDIEVAL_DOOR = BLOCKS.register("medieval_door",
																					id -> new BigDoorBlock(withId(id, wood(5.0F).strength(5.0F, 10.0F)),
																											new DoorAppearance.Obj(	MalisisDoors.id("models/big_door.obj"),
																																	MalisisDoors.id("block/medieval_door"))));

	public static final DeferredBlock<Door3x3Block> BIG_DOOR_OAK_3X3 = door3x3("oak", false);
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_SPRUCE_3X3 = door3x3("spruce", false);
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_BIRCH_3X3 = door3x3("birch", false);
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_JUNGLE_3X3 = door3x3("jungle", false);
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_ACACIA_3X3 = door3x3("acacia", false);
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_DARK_OAK_3X3 = door3x3("dark_oak", false);
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_MANGROVE_3X3 = door3x3("mangrove", wood(3.0F).strength(3.0F, 10.0F).mapColor(MapColor.COLOR_RED));
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_CHERRY_3X3 = door3x3("cherry", wood(3.0F).strength(3.0F, 10.0F).mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.CHERRY_WOOD));
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_BAMBOO_3X3 = door3x3("bamboo", wood(3.0F).strength(3.0F, 10.0F).mapColor(MapColor.COLOR_YELLOW).sound(SoundType.BAMBOO_WOOD));
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_PALE_OAK_3X3 = door3x3("pale_oak", wood(3.0F).strength(3.0F, 10.0F).mapColor(MapColor.QUARTZ));
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_CRIMSON_3X3 = door3x3("crimson", netherWood(MapColor.CRIMSON_STEM));
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_WARPED_3X3 = door3x3("warped", netherWood(MapColor.WARPED_STEM));
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_IRON_3X3 = door3x3("iron", true);
	public static final DeferredBlock<Door3x3Block> BIG_DOOR_RUSTY_3X3 = door3x3("rusty", true);

	private static BlockBehaviour.Properties netherWood(MapColor color)
	{
		return BlockBehaviour.Properties.of()
										.mapColor(color)
										.instrument(NoteBlockInstrument.BASS)
										.strength(3.0F, 10.0F)
										.sound(SoundType.NETHER_WOOD)
										.noOcclusion()
										.pushReaction(PushReaction.DESTROY);
	}

	private static DeferredBlock<Door3x3Block> door3x3(String type, boolean metal)
	{
		return door3x3(type, metal ? metal(5.0F).strength(5.0F, 200.0F).sound(SoundType.METAL) : wood(3.0F).strength(3.0F, 10.0F), metal);
	}

	private static DeferredBlock<Door3x3Block> door3x3(String type, BlockBehaviour.Properties properties)
	{
		return door3x3(type, properties, false);
	}

	private static DeferredBlock<Door3x3Block> door3x3(String type, BlockBehaviour.Properties properties, boolean metal)
	{
		String name = "big_door_" + type + "_3x3";
		DeferredBlock<Door3x3Block> block = BLOCKS.register(name,
															id -> new Door3x3Block(withId(id, properties),
																					metal,
																					new DoorAppearance.Textured(MalisisDoors.id("block/" + name))));
		DOORS_3X3.add(block);
		return block;
	}

	public static final DeferredBlock<RustyHatchBlock> RUSTY_HATCH = BLOCKS.register(	"rusty_hatch",
																						id -> new RustyHatchBlock(withId(id, metal(3.0F).strength(3.0F, 10000.0F).sound(SoundType.METAL)),
																													new DoorAppearance.Obj(	MalisisDoors.id("models/rusty_hatch.obj"),
																																			MalisisDoors.id("block/rusty_hatch"))));

	public static final DeferredBlock<LadderBlock> RUSTY_LADDER = BLOCKS.register(	"rusty_ladder",
																						id -> new LadderBlock(withId(id, BlockBehaviour.Properties.of()
																																			.mapColor(MapColor.METAL)
																																			.strength(2.0F)
																																			.sound(SoundType.LADDER)
																																			.noOcclusion()
																																			.pushReaction(PushReaction.DESTROY))));

	public static final DeferredBlock<HitechDoorBlock> HITECH_DOOR = BLOCKS.register(	"hitech_door",
																						id -> new HitechDoorBlock(withId(id, metal(4.0F).sound(SoundType.METAL)),
																													new DoorAppearance.Obj(	MalisisDoors.id("models/hitechdoor.obj"),
																																			MalisisDoors.id("block/hitechdoor"))));

	public static final DeferredBlock<ForcefieldBlock> FORCEFIELD = BLOCKS.register("forcefield",
																					id -> new ForcefieldBlock(withId(id, BlockBehaviour.Properties	.of()
																																		.mapColor(MapColor.NONE)
																																		.strength(-1.0F, 3600000.0F)
																																		.noLootTable()
																																		.sound(SoundType.ANVIL)
																																		.lightLevel(s -> 7)
																																		.pushReaction(PushReaction.BLOCK))));

	public static final DeferredBlock<DoorFactoryBlock> DOOR_FACTORY = BLOCKS.register(	"door_factory",
																						id -> new DoorFactoryBlock(withId(id, BlockBehaviour.Properties.of()
																																			.mapColor(MapColor.METAL)
																																			.strength(3.5F)
																																			.requiresCorrectToolForDrops()
																																			.sound(SoundType.METAL))));

	static
	{
		MULTIBLOCK_DOORS.add(CARRIAGE_DOOR);
		MULTIBLOCK_DOORS.add(MEDIEVAL_DOOR);
		MULTIBLOCK_DOORS.addAll(DOORS_3X3);
		MULTIBLOCK_DOORS.add(RUSTY_HATCH);
		MULTIBLOCK_DOORS.add(HITECH_DOOR);
	}
	//#end Other doors

	public static final DeferredBlock<PlayerSensorBlock> PLAYER_SENSOR = BLOCKS.register(	"player_sensor",
																							id -> new PlayerSensorBlock(withId(id, BlockBehaviour.Properties	.of()
																																				.mapColor(MapColor.METAL)
																																				.noCollision()
																																				.strength(0.5F)
																																				.sound(SoundType.METAL)
																																				.pushReaction(PushReaction.DESTROY))));
}
