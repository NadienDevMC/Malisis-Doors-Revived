package nadiendev.malisisdoorsrevived.item;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/**
 * Item of the MalisisDoors doors : shows the settings applied in the Door Factory.
 *
 * @author Ordinastie
 */
public class EditableDoorItem extends DoubleHighBlockItem
{
	public EditableDoorItem(Block block, Properties properties)
	{
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
	{
		DoorItemTooltips.appendSettings(stack, tooltip);
	}
}
