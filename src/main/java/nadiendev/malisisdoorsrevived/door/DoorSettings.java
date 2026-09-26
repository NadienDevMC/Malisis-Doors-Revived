package nadiendev.malisisdoorsrevived.door;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Serializable version of a {@link DoorDescriptor}, stored on the custom doors (item component and block entity).
 *
 * @author Ordinastie
 */
public record DoorSettings(String movement, String sound, int openingTime, int autoCloseTime, RedstoneBehavior redstoneBehavior, boolean doubleDoor, boolean proximityDetection)
{
	public static final DoorSettings DEFAULT = new DoorSettings("rotating_door", "vanilla_door", 6, 0, RedstoneBehavior.STANDARD, true, false);

	public static final Codec<DoorSettings> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(	Codec.STRING.optionalFieldOf("movement", DEFAULT.movement()).forGetter(DoorSettings::movement),
					Codec.STRING.optionalFieldOf("sound", DEFAULT.sound()).forGetter(DoorSettings::sound),
					Codec.INT.optionalFieldOf("opening_time", DEFAULT.openingTime()).forGetter(DoorSettings::openingTime),
					Codec.INT.optionalFieldOf("auto_close_time", 0).forGetter(DoorSettings::autoCloseTime),
					RedstoneBehavior.CODEC.optionalFieldOf("redstone_behavior", RedstoneBehavior.STANDARD).forGetter(DoorSettings::redstoneBehavior),
					Codec.BOOL.optionalFieldOf("double_door", true).forGetter(DoorSettings::doubleDoor),
					Codec.BOOL.optionalFieldOf("proximity_detection", false).forGetter(DoorSettings::proximityDetection))
			.apply(instance, DoorSettings::new));

	public static final StreamCodec<ByteBuf, DoorSettings> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
}
