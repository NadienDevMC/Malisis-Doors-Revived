package nadiendev.malisisdoorsrevived.integracion.manyideas_doors;

import nadiendev.malisisdoorsrevived.animated.DoorIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class ManyIdeasDoorsIntegration extends DoorIntegration
{
	public ManyIdeasDoorsIntegration()
	{
		super("manyideas_doors", "ManyIdeas Doors");
	}

	@Override
	public boolean animates(ResourceLocation id, Block block)
	{
		return id.getNamespace().equals(modId()) && isCopyable(block);
	}
}
