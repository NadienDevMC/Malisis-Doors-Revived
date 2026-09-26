package nadiendev.malisisdoorsrevived.door;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The blocks used to build a custom door in the Door Factory.
 *
 * @param frame the block used for the frame of the door
 * @param top the block used for the panel of the top half
 * @param bottom the block used for the panel of the bottom half
 * @author Ordinastie
 */
public record DoorMaterials(BlockState frame, BlockState top, BlockState bottom)
{
	public static final DoorMaterials DEFAULT = new DoorMaterials(	Blocks.OAK_PLANKS.defaultBlockState(),
																	Blocks.GLASS.defaultBlockState(),
																	Blocks.GLASS.defaultBlockState());

	public static final Codec<DoorMaterials> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(	BlockState.CODEC.fieldOf("frame").forGetter(DoorMaterials::frame),
					BlockState.CODEC.fieldOf("top").forGetter(DoorMaterials::top),
					BlockState.CODEC.fieldOf("bottom").forGetter(DoorMaterials::bottom))
			.apply(instance, DoorMaterials::new));

	public static final StreamCodec<ByteBuf, DoorMaterials> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

	/**
	 * @return the highest light emission of the materials
	 */
	public int getLightEmission()
	{
		return Math.max(frame.getLightEmission(), Math.max(top.getLightEmission(), bottom.getLightEmission()));
	}

	public boolean hasTranslucency()
	{
		return !frame.canOcclude() || !top.canOcclude() || !bottom.canOcclude();
	}
}
