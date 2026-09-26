package nadiendev.malisisdoorsrevived.client.renderer;

import nadiendev.malisiscorerevived.client.render.SimpleBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;

import nadiendev.malisiscorerevived.animation.Transformable;
import nadiendev.malisiscorerevived.client.render.RenderUtils;
import nadiendev.malisisdoorsrevived.blockentity.DoorBlockEntity;
import nadiendev.malisisdoorsrevived.door.DoorAppearance;
import nadiendev.malisisdoorsrevived.door.IDoorBlock;
import nadiendev.malisisdoorsrevived.movement.DoorPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renderer of the animated trapdoors.
 *
 * @author Ordinastie
 */
public class TrapDoorRenderer extends SimpleBlockEntityRenderer<DoorBlockEntity>
{
	public TrapDoorRenderer(BlockEntityRendererProvider.Context context)
	{}

	@Override
	protected void render(DoorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
	{
		BlockState state = be.getBlockState();
		if (!(state.getBlock() instanceof IDoorBlock door) || !be.getMovement().shouldRender(be, DoorPart.DOOR))
			return;

		Block modelBlock = door.getAppearance() instanceof DoorAppearance.Copy copy ? copy.block().get() : state.getBlock();
		BlockState modelState = modelBlock	.defaultBlockState()
											.setValue(TrapDoorBlock.FACING, Direction.SOUTH)
											.setValue(TrapDoorBlock.HALF, state.getValue(TrapDoorBlock.HALF));

		Transformable t = DoorRenderHelper.begin(poseStack, be, be.getDirection(), DoorPart.DOOR, partialTick);
		DoorRenderHelper.toCorner(poseStack);
		RenderUtils.renderBlockState(poseStack, buffer, modelState, be.getLevel(), be.getBlockPos(), packedLight, packedOverlay, t.getAlphaF());
		DoorRenderHelper.end(poseStack);
	}
}
