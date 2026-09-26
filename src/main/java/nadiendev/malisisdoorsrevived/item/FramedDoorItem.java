package nadiendev.malisisdoorsrevived.item;

import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import java.util.List;

import nadiendev.malisisdoorsrevived.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Item of the big doors, displays the block used for the frame.
 *
 * @author Ordinastie
 */
public class FramedDoorItem extends BlockItem
{
	public FramedDoorItem(Block block, Properties properties)
	{
		super(block, properties);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag)
	{
		BlockState frame = stack.get(ModDataComponents.FRAME_STATE.get());
		if (frame != null)
			tooltip.accept(Component.translatable("item.malisisdoors.big_door.frame", frame.getBlock().getName()).withStyle(ChatFormatting.GRAY));
	}
}
