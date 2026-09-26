package nadiendev.malisisdoorsrevived.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import nadiendev.malisisdoorsrevived.registry.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BigDoorRecipe extends CustomRecipe
{
	public static final int FRAME_COUNT = 5;

	public static final MapCodec<BigDoorRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
			.group(	CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(BigDoorRecipe::category),
					Ingredient.CODEC.fieldOf("door").forGetter(BigDoorRecipe::getDoor),
					ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result))
			.apply(instance, BigDoorRecipe::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BigDoorRecipe> STREAM_CODEC = StreamCodec.composite(	CraftingBookCategory.STREAM_CODEC,
																													BigDoorRecipe::category,
																													Ingredient.CONTENTS_STREAM_CODEC,
																													BigDoorRecipe::getDoor,
																													ItemStackTemplate.STREAM_CODEC,
																													recipe -> recipe.result,
																													BigDoorRecipe::new);

	public static final RecipeSerializer<BigDoorRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

	private final CraftingBookCategory category;
	private final Ingredient door;
	private final ItemStackTemplate result;

	public BigDoorRecipe(CraftingBookCategory category, Ingredient door, ItemStackTemplate result)
	{
		this.category = category;
		this.door = door;
		this.result = result;
	}

	public Ingredient getDoor()
	{
		return door;
	}

	public ItemStack getResult()
	{
		return result.create();
	}

	@Override
	public CraftingBookCategory category()
	{
		return category;
	}

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
	public ItemStack assemble(CraftingInput input)
	{
		ItemStack frame = getFrame(input);
		if (frame.isEmpty())
			return ItemStack.EMPTY;
		ItemStack stack = result.create();
		stack.set(ModDataComponents.FRAME_STATE.get(), ((BlockItem) frame.getItem()).getBlock().defaultBlockState());
		return stack;
	}

	@Override
	public RecipeSerializer<BigDoorRecipe> getSerializer()
	{
		return ModRecipes.BIG_DOOR.get();
	}
}
