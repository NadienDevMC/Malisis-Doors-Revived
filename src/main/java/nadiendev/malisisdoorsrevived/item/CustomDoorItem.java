package nadiendev.malisisdoorsrevived.item;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.List;

import nadiendev.malisisdoorsrevived.door.DoorMaterials;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/**
 * Item of the custom doors made in the Door Factory.
 *
 * @author Ordinastie
 */
public class CustomDoorItem extends DoubleHighBlockItem
{
	public CustomDoorItem(Block block, Properties properties)
	{
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag)
	{
		DoorMaterials materials = stack.getOrDefault(ModDataComponents.DOOR_MATERIALS.get(), DoorMaterials.DEFAULT);
		tooltip.accept(Component.translatable("item.malisisdoors.custom_door.frame", materials.frame().getBlock().getName())
								.withStyle(ChatFormatting.GRAY));
		tooltip.accept(Component.translatable("item.malisisdoors.custom_door.top", materials.top().getBlock().getName()).withStyle(ChatFormatting.GRAY));
		tooltip.accept(Component.translatable("item.malisisdoors.custom_door.bottom", materials.bottom().getBlock().getName())
								.withStyle(ChatFormatting.GRAY));
		DoorItemTooltips.appendSettings(stack, tooltip);
	}
}
