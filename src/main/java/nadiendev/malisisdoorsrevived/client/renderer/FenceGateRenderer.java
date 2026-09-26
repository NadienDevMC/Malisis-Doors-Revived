package nadiendev.malisisdoorsrevived.client.renderer;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.malisis.core.client.render.RenderUtils;
import nadiendev.malisisdoorsrevived.block.MalisisFenceGateBlock;
import nadiendev.malisisdoorsrevived.blockentity.FenceGateBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.movement.DoorPart;
import nadiendev.malisisdoorsrevived.registry.ModBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Renderer of the animated fence gates.<br>
 * The quads of the fence gate model are split in three groups : the posts (static), the left leaf and the right leaf (animated).<br>
 * The camouflage fence gate takes the texture of the blocks on both sides when they're identical.
 *
 * @author Ordinastie
 */
public class FenceGateRenderer implements BlockEntityRenderer<FenceGateBlockEntity>
{
	private static final Direction[] DIRECTIONS_AND_NULL = { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST,
			Direction.EAST, null };
	private static final float POST = 2 / 16F + 0.001F;

	public FenceGateRenderer(BlockEntityRendererProvider.Context context)
	{}

	@Override
	public void render(FenceGateBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
	{
		BlockState state = be.getBlockState();
		if (!(state.getBlock() instanceof MalisisFenceGateBlock gate))
			return;

		Block modelBlock = gate.getAppearance() instanceof DoorAppearance.Copy copy ? copy.block().get() : gate;
		BlockState modelState = modelBlock	.defaultBlockState()
											.setValue(FenceGateBlock.FACING, Direction.SOUTH)
											.setValue(FenceGateBlock.IN_WALL, state.getValue(FenceGateBlock.IN_WALL));
		BakedModel model = RenderUtils.getModel(modelState);

		Level level = be.getLevel();
		BlockPos pos = be.getBlockPos();
		BlockState camo = gate == ModBlocks.CAMO_FENCE_GATE.get() ? getCamoState(be) : null;
		TextureAtlasSprite camoSprite = camo != null ? RenderUtils.getSprite(camo, Direction.SOUTH) : null;
		int camoColor = camo != null ? RenderUtils.getTint(camo, level, pos.relative(be.getDirection().getClockWise())) : 0xFFFFFFFF;

		for (Part part : Part.values())
		{
			DoorRenderHelper.begin(poseStack, be, be.getDirection(), part.doorPart, partialTick);
			DoorRenderHelper.toCorner(poseStack);
			PoseStack.Pose pose = poseStack.last();

			RandomSource random = RandomSource.create();
			for (RenderType renderType : model.getRenderTypes(modelState, RandomSource.create(42), ModelData.EMPTY))
			{
				VertexConsumer consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(renderType, false));
				for (Direction side : DIRECTIONS_AND_NULL)
				{
					random.setSeed(42L);
					List<BakedQuad> quads = model.getQuads(modelState, side, random, ModelData.EMPTY, renderType);
					for (BakedQuad quad : quads)
					{
						if (getPart(quad) != part)
							continue;
						if (camoSprite != null)
						{
							float r = (camoColor >> 16 & 255) / 255F, g = (camoColor >> 8 & 255) / 255F, b = (camoColor & 255) / 255F;
							consumer.putBulkData(pose, RenderUtils.retexture(quad, camoSprite), r, g, b, 1, packedLight, packedOverlay, false);
						}
						else
							RenderUtils.putQuad(pose, consumer, quad, modelState, level, pos, packedLight, packedOverlay, 1);
					}
				}
			}
			DoorRenderHelper.end(poseStack);
		}
	}

	private enum Part
	{
		POSTS(null),
		LEFT(DoorPart.LEFT),
		RIGHT(DoorPart.RIGHT);

		private final DoorPart doorPart;

		Part(@Nullable DoorPart doorPart)
		{
			this.doorPart = doorPart;
		}
	}

	private static Part getPart(BakedQuad quad)
	{
		float[] range = RenderUtils.getRange(quad, Direction.Axis.X);
		if (range[1] <= POST || range[0] >= 1 - POST)
			return Part.POSTS;
		return RenderUtils.getCenter(quad)[0] < 0.5F ? Part.LEFT : Part.RIGHT;
	}

	/**
	 * Gets the state the camouflage fence gate copies : the blocks on both sides of the gate (or of the double gate) must be identical.
	 */
	@Nullable
	private static BlockState getCamoState(FenceGateBlockEntity be)
	{
		Level level = be.getLevel();
		if (level == null)
			return null;
		Direction side = be.getDirection().getClockWise();
		BlockState state1 = getSideState(be, side);
		BlockState state2 = getSideState(be, side.getOpposite());
		if (state1 == null || state2 == null || state1 != state2 || state1.isAir() || state1.getBlock() instanceof FenceGateBlock)
			return null;
		if (!state1.isSolidRender(level, be.getBlockPos().relative(side)))
			return null;
		return state1;
	}

	@Nullable
	private static BlockState getSideState(FenceGateBlockEntity be, Direction side)
	{
		Level level = be.getLevel();
		BlockPos pos = be.getBlockPos().relative(side);
		if (level.getBlockEntity(pos) instanceof FenceGateBlockEntity other && be.isMatchingDoubleDoor(other))
			pos = pos.relative(side);
		return level.getBlockState(pos);
	}

	@Override
	public net.minecraft.world.phys.AABB getRenderBoundingBox(FenceGateBlockEntity be)
	{
		return new net.minecraft.world.phys.AABB(be.getBlockPos()).inflate(1);
	}
}
