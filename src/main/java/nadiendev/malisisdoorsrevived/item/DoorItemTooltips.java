package nadiendev.malisisdoorsrevived.item;

import java.util.List;

import nadiendev.malisisdoorsrevived.door.DoorSettings;
import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/**
 * Tooltips for the doors edited in the Door Factory.
 *
 * @author Ordinastie
 */
public class DoorItemTooltips
{
	public static void appendSettings(ItemStack stack, List<Component> tooltip)
	{
		DoorSettings settings = stack.get(ModDataComponents.DOOR_SETTINGS.get());
		if (settings == null)
			return;
		tooltip.add(Component.translatable("malisisdoors.tooltip.movement", Component.translatable("malisisdoors.movement." + settings.movement()))
								.withStyle(ChatFormatting.DARK_AQUA));
		tooltip.add(Component.translatable("malisisdoors.tooltip.sound", Component.translatable("malisisdoors.sound." + settings.sound()))
								.withStyle(ChatFormatting.DARK_AQUA));
		tooltip.add(Component.translatable("malisisdoors.tooltip.opening_time", settings.openingTime()).withStyle(ChatFormatting.DARK_AQUA));
		if (settings.autoCloseTime() > 0)
			tooltip.add(Component.translatable("malisisdoors.tooltip.auto_close_time", settings.autoCloseTime())
									.withStyle(ChatFormatting.DARK_AQUA));
		tooltip.add(Component.translatable(settings.redstoneBehavior().getTranslationKey()).withStyle(ChatFormatting.DARK_AQUA));
		if (settings.proximityDetection())
			tooltip.add(Component.translatable("malisisdoors.tooltip.proximity").withStyle(ChatFormatting.DARK_AQUA));
		if (!settings.doubleDoor())
			tooltip.add(Component.translatable("malisisdoors.tooltip.single_door").withStyle(ChatFormatting.DARK_AQUA));
	}
}
