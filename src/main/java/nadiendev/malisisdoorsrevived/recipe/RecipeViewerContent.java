package nadiendev.malisisdoorsrevived.recipe;

import java.util.List;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import nadiendev.malisisdoorsrevived.registry.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class RecipeViewerContent
{
	public record Info(ItemLike item, Component text)
	{
		public Identifier id()
		{
			return MalisisDoors.id("/info/" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath());
		}
	}

	public record BigDoorExample(Identifier id, Ingredient door, Ingredient frame, ItemStack result)
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

	public static List<BigDoorExample> bigDoorExamples()
	{
		return List.of(bigDoorExample("carriage_door", Items.OAK_DOOR, ModItems.CARRIAGE_DOOR.get()), bigDoorExample("medieval_door", Items.SPRUCE_DOOR, ModItems.MEDIEVAL_DOOR.get()));
	}

	private static BigDoorExample bigDoorExample(String name, ItemLike door, ItemLike result)
	{
		return new BigDoorExample(MalisisDoors.id("/" + name), Ingredient.of(door), Ingredient.of(BuiltInRegistries.ITEM.getOrThrow(ItemTags.PLANKS)), new ItemStack(result));
	}
}
