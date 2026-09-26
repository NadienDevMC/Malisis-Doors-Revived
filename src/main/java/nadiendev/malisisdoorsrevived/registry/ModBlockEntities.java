package nadiendev.malisisdoorsrevived.registry;

import java.util.List;
import java.util.function.Supplier;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.blockentity.CustomDoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.DoorFactoryBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.FenceGateBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.ForcefieldBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.GarageDoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.MultiBlockDoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.SaloonDoorBlockEntity;
import nadiendev.malisisdoorsrevived.blockentity.TrapDoorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("DataFlowIssue")
public class ModBlockEntities
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(	Registries.BLOCK_ENTITY_TYPE,
																										MalisisDoors.MODID);

	public static final Supplier<BlockEntityType<DoorBlockEntity>> DOOR = BLOCK_ENTITIES.register(	"door",
																									() -> BlockEntityType.Builder	.of(DoorBlockEntity::new,
																																		blocks(ModBlocks.DOORS))
																																	.build(null));
	public static final Supplier<BlockEntityType<SaloonDoorBlockEntity>> SALOON_DOOR = BLOCK_ENTITIES.register(	"saloon_door",
																												() -> BlockEntityType.Builder	.of(SaloonDoorBlockEntity::new,
																																					ModBlocks.SALOON_DOOR.get())
																																				.build(null));
	public static final Supplier<BlockEntityType<CustomDoorBlockEntity>> CUSTOM_DOOR = BLOCK_ENTITIES.register(	"custom_door",
																												() -> BlockEntityType.Builder	.of(CustomDoorBlockEntity::new,
																																					ModBlocks.CUSTOM_DOOR.get())
																																				.build(null));
	public static final Supplier<BlockEntityType<TrapDoorBlockEntity>> TRAPDOOR = BLOCK_ENTITIES.register(	"trapdoor",
																											() -> BlockEntityType.Builder	.of(TrapDoorBlockEntity::new,
																																				blocks(ModBlocks.TRAPDOORS))
																																			.build(null));
	public static final Supplier<BlockEntityType<FenceGateBlockEntity>> FENCE_GATE = BLOCK_ENTITIES.register(	"fence_gate",
																												() -> BlockEntityType.Builder	.of(FenceGateBlockEntity::new,
																																					blocks(ModBlocks.FENCE_GATES))
																																				.build(null));
	public static final Supplier<BlockEntityType<GarageDoorBlockEntity>> GARAGE_DOOR = BLOCK_ENTITIES.register(	"garage_door",
																												() -> BlockEntityType.Builder	.of(GarageDoorBlockEntity::new,
																																					ModBlocks.GARAGE_DOOR.get())
																																				.build(null));
	public static final Supplier<BlockEntityType<MultiBlockDoorBlockEntity>> MULTIBLOCK_DOOR = BLOCK_ENTITIES.register(	"multiblock_door",
																														() -> BlockEntityType.Builder	.of(MultiBlockDoorBlockEntity::new,
																																							blocks(ModBlocks.MULTIBLOCK_DOORS))
																																						.build(null));
	public static final Supplier<BlockEntityType<ForcefieldBlockEntity>> FORCEFIELD = BLOCK_ENTITIES.register(	"forcefield",
																												() -> BlockEntityType.Builder	.of(ForcefieldBlockEntity::new,
																																					ModBlocks.FORCEFIELD.get())
																																				.build(null));
	public static final Supplier<BlockEntityType<DoorFactoryBlockEntity>> DOOR_FACTORY = BLOCK_ENTITIES.register(	"door_factory",
																													() -> BlockEntityType.Builder	.of(DoorFactoryBlockEntity::new,
																																						ModBlocks.DOOR_FACTORY.get())
																																					.build(null));

	private static Block[] blocks(List<? extends DeferredBlock<? extends Block>> list)
	{
		return list.stream().map(DeferredBlock::get).toArray(Block[]::new);
	}
}
