package nadiendev.malisisdoorsrevived.door;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

/**
 * How a door reacts to redstone and to players.
 *
 * @author Ordinastie
 */
public enum RedstoneBehavior implements StringRepresentable
{
	/** Opened by hand and by redstone. */
	STANDARD("standard"),
	/** Only opened by redstone. */
	REDSTONE_ONLY("redstone_only"),
	/** Opened by hand, but locked while powered. */
	REDSTONE_LOCK("redstone_lock"),
	/** Only opened by hand. */
	HAND_ONLY("hand_only");

	public static final Codec<RedstoneBehavior> CODEC = StringRepresentable.fromEnum(RedstoneBehavior::values);

	private final String name;

	RedstoneBehavior(String name)
	{
		this.name = name;
	}

	@Override
	public String getSerializedName()
	{
		return name;
	}

	public String getTranslationKey()
	{
		return "malisisdoors.redstone_behavior." + name;
	}

	public RedstoneBehavior next()
	{
		RedstoneBehavior[] values = values();
		return values[(ordinal() + 1) % values.length];
	}
}
