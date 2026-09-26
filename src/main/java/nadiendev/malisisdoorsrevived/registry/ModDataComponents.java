package nadiendev.malisisdoorsrevived.registry;

import java.util.function.Supplier;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.door.DoorMaterials;
import nadiendev.malisisdoorsrevived.door.DoorSettings;
import nadiendev.malisisdoorsrevived.item.ForcefieldItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.mojang.serialization.Codec;

public class ModDataComponents
{
	public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE,
																									MalisisDoors.MODID);

	/** Behavior of a door edited in the Door Factory. */
	public static final Supplier<DataComponentType<DoorSettings>> DOOR_SETTINGS = COMPONENTS.register(	"door_settings",
																										() -> DataComponentType	.<DoorSettings> builder()
																																.persistent(DoorSettings.CODEC)
																																.networkSynchronized(DoorSettings.STREAM_CODEC)
																																.build());

	/** Blocks used to make a custom door. */
	public static final Supplier<DataComponentType<DoorMaterials>> DOOR_MATERIALS = COMPONENTS.register("door_materials",
																										() -> DataComponentType	.<DoorMaterials> builder()
																																.persistent(DoorMaterials.CODEC)
																																.networkSynchronized(DoorMaterials.STREAM_CODEC)
																																.build());

	/** Block used for the frame of the big doors. */
	public static final Supplier<DataComponentType<BlockState>> FRAME_STATE = COMPONENTS.register(	"frame_state",
																									() -> DataComponentType	.<BlockState> builder()
																															.persistent(BlockState.CODEC)
																															.networkSynchronized(ByteBufCodecs.idMapper(net.minecraft.world.level.block.Block.BLOCK_STATE_REGISTRY))
																															.build());

	/** Energy stored in the forcefield controller. */
	public static final Supplier<DataComponentType<Integer>> ENERGY = COMPONENTS.register(	"energy",
																							() -> DataComponentType	.<Integer> builder()
																													.persistent(Codec.INT)
																													.networkSynchronized(ByteBufCodecs.VAR_INT)
																													.build());

	/** First corner selected with the forcefield controller. */
	public static final Supplier<DataComponentType<ForcefieldItem.Selection>> FORCEFIELD_SELECTION = COMPONENTS.register("forcefield_selection",
																														() -> DataComponentType	.<ForcefieldItem.Selection> builder()
																																				.persistent(ForcefieldItem.Selection.CODEC)
																																				.networkSynchronized(ForcefieldItem.Selection.STREAM_CODEC)
																																				.build());
}
