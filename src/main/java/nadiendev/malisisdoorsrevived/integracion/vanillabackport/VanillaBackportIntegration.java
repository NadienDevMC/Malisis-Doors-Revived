package nadiendev.malisisdoorsrevived.integracion.vanillabackport;

import nadiendev.malisisdoorsrevived.animated.DoorIntegration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

public class VanillaBackportIntegration extends DoorIntegration
{
	public VanillaBackportIntegration()
	{
		super("vanillabackport", "Vanilla Backport");
	}

	@Override
	public boolean animates(Identifier id, Block block)
	{
		return id.getNamespace().equals(Identifier.DEFAULT_NAMESPACE) && id.getPath().startsWith("pale_oak_") && isStandard(block);
	}
}
