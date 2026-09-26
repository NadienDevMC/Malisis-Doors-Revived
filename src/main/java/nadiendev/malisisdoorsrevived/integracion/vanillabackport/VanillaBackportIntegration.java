package nadiendev.malisisdoorsrevived.integracion.vanillabackport;

import nadiendev.malisisdoorsrevived.animated.DoorIntegration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class VanillaBackportIntegration extends DoorIntegration
{
	public VanillaBackportIntegration()
	{
		super("vanillabackport", "Vanilla Backport");
	}

	@Override
	public boolean animates(ResourceLocation id, Block block)
	{
		return id.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE) && id.getPath().startsWith("pale_oak_") && isStandard(block);
	}
}
