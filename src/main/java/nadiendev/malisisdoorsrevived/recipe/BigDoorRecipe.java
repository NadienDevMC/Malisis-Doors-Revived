package nadiendev.malisisdoorsrevived.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import nadiendev.malisisdoorsrevived.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Recipe for the big doors : a door and at least five identical blocks used for the frame of the door.
 *
 * @author Ordinastie
 */
public class BigDoorRecipe extends CustomRecipe
{
	public static final int FRAME_COUNT = 5;

	private final Ingredient door;
	private final ItemStack result;

	public BigDoorRecipe(CraftingBookCategory category, Ingredient door, ItemStack result)
	{
		super(category);
		this.door = door;
		this.result = result;
	}

	public Ingredient getDoor()
	{
		return door;
	}

	public ItemStack getResult()
	{
		return result;
	}

	/**
	 * Gets the stack used for the frame.
	 *
	 * @return the frame stack, or an empty stack if the input doesn't match
	 */
	private ItemStack getFrame(CraftingInput input)
	{
		boolean doorFound = false;
		ItemStack frame = ItemStack.EMPTY;
		int frameCount = 0;
		for (int i = 0; i < input.size(); i++)
		{
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty())
				continue;
			if (door.test(stack))
			{
				if (doorFound)
					return ItemStack.EMPTY;
				doorFound = true;
				continue;
			}
			if (!(stack.getItem() instanceof BlockItem) || (!frame.isEmpty() && !ItemStack.isSameItem(frame, stack)))
				return ItemStack.EMPTY;
			frame = stack;
			frameCount++;
		}
		return doorFound && frameCount >= FRAME_COUNT ? frame : ItemStack.EMPTY;
	}

	@Override
	public boolean matches(CraftingInput input, Level level)
	{
		return !getFrame(input).isEmpty();
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries)
	{
		ItemStack frame = getFrame(input);
		if (frame.isEmpty())
			return ItemStack.EMPTY;
		ItemStack stack = result.copy();
		stack.set(ModDataComponents.FRAME_STATE.get(), ((BlockItem) frame.getItem()).getBlock().defaultBlockState());
		return stack;
	}

	@Override
	public ItemStack getResultItem(HolderLookup.Provider registries)
	{
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height)
	{
		return width * height > FRAME_COUNT;
	}

	@Override
	public RecipeSerializer<?> getSerializer()
	{
		return ModRecipes.BIG_DOOR.get();
	}

	public static class Serializer implements RecipeSerializer<BigDoorRecipe>
	{
		private static final MapCodec<BigDoorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
				.group(	CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CraftingRecipe::category),
						Ingredient.CODEC_NONEMPTY.fieldOf("door").forGetter(r -> r.door),
						ItemStack.STRICT_CODEC.fieldOf("result").forGetter(r -> r.result))
				.apply(instance, BigDoorRecipe::new));

		private static final StreamCodec<RegistryFriendlyByteBuf, BigDoorRecipe> STREAM_CODEC = StreamCodec.composite(	CraftingBookCategory.STREAM_CODEC,
																														CraftingRecipe::category,
																														Ingredient.CONTENTS_STREAM_CODEC,
																														r -> r.door,
																														ItemStack.STREAM_CODEC,
																														r -> r.result,
																														BigDoorRecipe::new);

		@Override
		public MapCodec<BigDoorRecipe> codec()
		{
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, BigDoorRecipe> streamCodec()
		{
			return STREAM_CODEC;
		}
	}
}
