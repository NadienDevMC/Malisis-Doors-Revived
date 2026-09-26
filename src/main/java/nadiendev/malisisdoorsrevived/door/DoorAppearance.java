package nadiendev.malisisdoorsrevived.door;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Describes how a door is rendered by its block entity renderer.
 *
 * @author Ordinastie
 */
public sealed interface DoorAppearance
{
	/** The door renders the models of its own block states. */
	DoorAppearance SELF = new Self();

	@Nullable
	static BlockState originalState(BlockState state)
	{
		if (state.getBlock() instanceof IDoorBlock door && door.getAppearance() instanceof Copy copy)
			return copy.copyState(state);
		return null;
	}

	/**
	 * The door renders the models of its own block states.
	 */
	record Self() implements DoorAppearance
	{}

	/**
	 * The door renders the models of the states of another (vanilla) block.
	 */
	record Copy(Supplier<? extends Block> block) implements DoorAppearance
	{
		public BlockState copyState(BlockState state)
		{
			BlockState copied = block.get().defaultBlockState();
			for (Property<?> property : state.getProperties())
			{
				if (copied.hasProperty(property))
					copied = copyValue(state, copied, property);
			}
			return copied;
		}

		private static <T extends Comparable<T>> BlockState copyValue(BlockState from, BlockState to, Property<T> property)
		{
			return to.setValue(property, from.getValue(property));
		}
	}

	/**
	 * The door renders an OBJ model.
	 *
	 * @param model the location of the model (for example "malisisdoors:models/saloon_door.obj")
	 * @param texture the location of the texture in the block atlas (for example "malisisdoors:block/saloon_door")
	 */
	record Obj(ResourceLocation model, ResourceLocation texture) implements DoorAppearance
	{}

	/**
	 * The door renders boxes textured with a single texture.
	 *
	 * @param texture the location of the texture in the block atlas
	 */
	record Textured(ResourceLocation texture) implements DoorAppearance
	{}

	/**
	 * The door renders boxes textured with the materials stored in its block entity (custom doors).
	 */
	record Custom() implements DoorAppearance
	{}
}
