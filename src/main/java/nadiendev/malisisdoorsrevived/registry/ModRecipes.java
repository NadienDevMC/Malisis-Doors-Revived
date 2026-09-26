package nadiendev.malisisdoorsrevived.registry;

import java.util.function.Supplier;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.recipe.BigDoorRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes
{
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MalisisDoors.MODID);

	public static final Supplier<RecipeSerializer<BigDoorRecipe>> BIG_DOOR = SERIALIZERS.register("big_door", BigDoorRecipe.Serializer::new);
}
