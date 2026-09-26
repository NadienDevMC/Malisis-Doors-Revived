package nadiendev.malisisdoorsrevived.integracion.jei;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.recipe.RecipeViewerContent;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

@JeiPlugin
public class MalisisDoorsJeiPlugin implements IModPlugin
{
	@Override
	public Identifier getPluginUid()
	{
		return MalisisDoors.id("jei");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		for (RecipeViewerContent.Info info : RecipeViewerContent.infos())
			registration.addIngredientInfo(info.item(), info.text());

		List<RecipeHolder<CraftingRecipe>> recipes = RecipeViewerContent	.bigDoorExamples()
																			.stream()
																			.map(example -> new RecipeHolder<CraftingRecipe>(	ResourceKey.create(Registries.RECIPE, example.id()),
																																new ShapelessRecipe(new Recipe.CommonInfo(false),
																																					new CraftingRecipe.CraftingBookInfo(CraftingBookCategory.REDSTONE, ""),
																																					new ItemStackTemplate(example.result().getItem()),
																																					example.inputs())))
																			.toList();
		registration.addRecipes(RecipeTypes.CRAFTING, recipes);
	}
}
