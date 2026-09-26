package nadiendev.malisisdoorsrevived.animated;

import java.lang.reflect.Method;
import java.util.Set;

import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;

public abstract class DoorIntegration
{
	private static final Set<String> BEHAVIOR_METHODS = Set.of(	"useWithoutItem",
																"useItemOn",
																"getShape",
																"getCollisionShape",
																"neighborChanged",
																"updateShape",
																"setOpen",
																"tick",
																"randomTick",
																"isRandomlyTicking",
																"entityInside",
																"createBlockStateDefinition");

	private final String modId;
	private final String name;

	protected DoorIntegration(String modId, String name)
	{
		this.modId = modId;
		this.name = name;
	}

	public String modId()
	{
		return modId;
	}

	public String name()
	{
		return name;
	}

	public boolean animates(ResourceLocation id, Block block)
	{
		return id.getNamespace().equals(modId) && isStandard(block);
	}

	protected static boolean isStandard(Block block)
	{
		Class<?> base = baseClass(block);
		if (base == null || !isCopyable(block))
			return false;
		for (Class<?> c = block.getClass(); c != base; c = c.getSuperclass())
		{
			for (Method method : c.getDeclaredMethods())
			{
				if (BEHAVIOR_METHODS.contains(method.getName()))
					return false;
			}
		}
		return true;
	}

	protected static boolean isCopyable(Block block)
	{
		if (baseClass(block) == null || block instanceof EntityBlock || block instanceof IDoorBlock)
			return false;
		return !(block instanceof DoorBlock) || block.getStateDefinition().getProperties().contains(DoorBlock.HALF);
	}

	private static Class<?> baseClass(Block block)
	{
		if (block instanceof DoorBlock)
			return DoorBlock.class;
		if (block instanceof TrapDoorBlock)
			return TrapDoorBlock.class;
		if (block instanceof FenceGateBlock)
			return FenceGateBlock.class;
		return null;
	}
}
