package nadiendev.malisisdoorsrevived.recipe;

import java.util.List;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import nadiendev.malisisdoorsrevived.registry.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

public class RecipeViewerContent
{
	public record Info(ItemLike item, Component text)
	{
		public ResourceLocation id()
		{
			return MalisisDoors.id("/info/" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath());
		}
	}

	public record BigDoorExample(ResourceLocation id, Ingredient door, Ingredient frame, ItemStack result)
	{
		public NonNullList<Ingredient> inputs()
		{
			NonNullList<Ingredient> inputs = NonNullList.create();
			inputs.add(door);
			for (int i = 0; i < BigDoorRecipe.FRAME_COUNT; i++)
				inputs.add(frame);
			return inputs;
		}
	}

	public static List<Info> infos()
	{
		return List.of(	info(ModItems.DOOR_FACTORY, "door_factory"),
						info(ModItems.CUSTOM_DOOR, "custom_door"),
						info(ModItems.PLAYER_SENSOR, "player_sensor"),
						info(ModItems.FORCEFIELD_CONTROLLER, "forcefield_controller"),
						info(ModItems.GARAGE_DOOR, "garage_door"),
						info(ModItems.CARRIAGE_DOOR, "big_door"),
						info(ModItems.MEDIEVAL_DOOR, "big_door"),
						info(ModItems.RUSTY_HATCH, "rusty_hatch"),
						info(ModItems.CAMO_FENCE_GATE, "camo_fence_gate"),
						info(ModBlocks.SALOON_DOOR.get(), "saloon_door"));
	}

	private static Info info(ItemLike item, String key)
	{
		return new Info(item, Component.translatable("malisisdoors.info." + key));
	}

	public static List<BigDoorExample> bigDoorExamples(RecipeManager recipes)
	{
		return recipes	.getAllRecipesFor(RecipeType.CRAFTING)
						.stream()
						.filter(holder -> holder.value() instanceof BigDoorRecipe)
						.map(holder -> {
							BigDoorRecipe recipe = (BigDoorRecipe) holder.value();
							return new BigDoorExample(	MalisisDoors.id("/" + holder.id().getPath()),
														recipe.getDoor(),
														Ingredient.of(ItemTags.PLANKS),
														recipe.getResult());
						})
						.toList();
	}
}
