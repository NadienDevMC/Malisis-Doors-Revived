package nadiendev.malisisdoorsrevived.integracion.roughlyenoughitems;

import java.util.List;
import java.util.Optional;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomDisplay;
import nadiendev.malisisdoorsrevived.recipe.RecipeViewerContent;

@REIPluginClient
public class MalisisDoorsReiPlugin implements REIClientPlugin
{
	@Override
	public void registerDisplays(DisplayRegistry registry)
	{
		for (RecipeViewerContent.Info info : RecipeViewerContent.infos())
			registry.add(DefaultInformationDisplay.createFromEntries(EntryIngredients.of(info.item()), info.item().asItem().getDescription()).line(info.text()));

		for (RecipeViewerContent.BigDoorExample example : RecipeViewerContent.bigDoorExamples(registry.getRecipeManager()))
			registry.add(DefaultCustomDisplay.simple(	example.inputs().stream().map(EntryIngredients::ofIngredient).toList(),
														List.of(EntryIngredients.ofItemStacks(List.of(example.result()))),
														Optional.of(example.id())));
	}
}
