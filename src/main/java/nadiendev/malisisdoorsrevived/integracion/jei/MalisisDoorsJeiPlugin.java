package nadiendev.malisisdoorsrevived.integracion.jei;

import java.util.List;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.recipe.RecipeViewerContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

@JeiPlugin
public class MalisisDoorsJeiPlugin implements IModPlugin
{
	@Override
	public ResourceLocation getPluginUid()
	{
		return MalisisDoors.id("jei");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration)
	{
		for (RecipeViewerContent.Info info : RecipeViewerContent.infos())
			registration.addIngredientInfo(info.item(), info.text());

		ClientLevel level = Minecraft.getInstance().level;
		if (level == null)
			return;
		List<RecipeHolder<CraftingRecipe>> recipes = RecipeViewerContent	.bigDoorExamples(level.getRecipeManager())
																			.stream()
																			.map(example -> new RecipeHolder<CraftingRecipe>(	example.id(),
																																new ShapelessRecipe("",
																																					CraftingBookCategory.REDSTONE,
																																					example.result(),
																																					example.inputs())))
																			.toList();
		registration.addRecipes(RecipeTypes.CRAFTING, recipes);
	}
}
