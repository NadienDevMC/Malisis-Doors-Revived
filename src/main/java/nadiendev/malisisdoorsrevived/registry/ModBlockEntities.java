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

public class ModBlockEntities
{
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(	Registries.BLOCK_ENTITY_TYPE,
																										MalisisDoors.MODID);

	public static final Supplier<BlockEntityType<DoorBlockEntity>> DOOR = BLOCK_ENTITIES.register(	"door",
																									() -> new BlockEntityType<>(DoorBlockEntity::new, blocks(ModBlocks.DOORS)));
	public static final Supplier<BlockEntityType<SaloonDoorBlockEntity>> SALOON_DOOR = BLOCK_ENTITIES.register(	"saloon_door",
																												() -> new BlockEntityType<>(SaloonDoorBlockEntity::new, ModBlocks.SALOON_DOOR.get()));
	public static final Supplier<BlockEntityType<CustomDoorBlockEntity>> CUSTOM_DOOR = BLOCK_ENTITIES.register(	"custom_door",
																												() -> new BlockEntityType<>(CustomDoorBlockEntity::new, ModBlocks.CUSTOM_DOOR.get()));
	public static final Supplier<BlockEntityType<TrapDoorBlockEntity>> TRAPDOOR = BLOCK_ENTITIES.register(	"trapdoor",
																											() -> new BlockEntityType<>(TrapDoorBlockEntity::new, blocks(ModBlocks.TRAPDOORS)));
	public static final Supplier<BlockEntityType<FenceGateBlockEntity>> FENCE_GATE = BLOCK_ENTITIES.register(	"fence_gate",
																												() -> new BlockEntityType<>(FenceGateBlockEntity::new, blocks(ModBlocks.FENCE_GATES)));
	public static final Supplier<BlockEntityType<GarageDoorBlockEntity>> GARAGE_DOOR = BLOCK_ENTITIES.register(	"garage_door",
																												() -> new BlockEntityType<>(GarageDoorBlockEntity::new, ModBlocks.GARAGE_DOOR.get()));
	public static final Supplier<BlockEntityType<MultiBlockDoorBlockEntity>> MULTIBLOCK_DOOR = BLOCK_ENTITIES.register(	"multiblock_door",
																														() -> new BlockEntityType<>(MultiBlockDoorBlockEntity::new, blocks(ModBlocks.MULTIBLOCK_DOORS)));
	public static final Supplier<BlockEntityType<ForcefieldBlockEntity>> FORCEFIELD = BLOCK_ENTITIES.register(	"forcefield",
																												() -> new BlockEntityType<>(ForcefieldBlockEntity::new, ModBlocks.FORCEFIELD.get()));
	public static final Supplier<BlockEntityType<DoorFactoryBlockEntity>> DOOR_FACTORY = BLOCK_ENTITIES.register(	"door_factory",
																													() -> new BlockEntityType<>(DoorFactoryBlockEntity::new, ModBlocks.DOOR_FACTORY.get()));

	private static Block[] blocks(List<? extends DeferredBlock<? extends Block>> list)
	{
		return list.stream().map(DeferredBlock::get).toArray(Block[]::new);
	}
}
