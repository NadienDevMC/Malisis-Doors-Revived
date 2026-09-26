package nadiendev.malisisdoorsrevived.integracion.create;

import nadiendev.malisisdoorsrevived.animated.DoorIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;

public class CreateIntegration extends DoorIntegration
{
	public CreateIntegration()
	{
		super("create", "Create");
	}

	@Override
	public boolean animates(ResourceLocation id, Block block)
	{
		return id.getNamespace().equals(modId()) && block instanceof TrapDoorBlock && isCopyable(block);
	}
}
