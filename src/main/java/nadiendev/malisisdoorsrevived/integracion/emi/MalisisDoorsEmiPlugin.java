package nadiendev.malisisdoorsrevived.integracion.emi;

import java.util.List;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import nadiendev.malisisdoorsrevived.recipe.RecipeViewerContent;

@EmiEntrypoint
public class MalisisDoorsEmiPlugin implements EmiPlugin
{
	@Override
	public void register(EmiRegistry registry)
	{
		for (RecipeViewerContent.Info info : RecipeViewerContent.infos())
			registry.addRecipe(new EmiInfoRecipe(List.of(EmiStack.of(info.item())), List.of(info.text()), info.id()));

		for (RecipeViewerContent.BigDoorExample example : RecipeViewerContent.bigDoorExamples(registry.getRecipeManager()))
			registry.addRecipe(new EmiCraftingRecipe(	example.inputs().stream().map(EmiIngredient::of).toList(),
														EmiStack.of(example.result()),
														example.id(),
														true));
	}
}
