package nadiendev.malisisdoorsrevived.client.renderer;

import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import nadiendev.malisiscorerevived.client.render.SimpleBlockEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import nadiendev.malisisdoorsrevived.MalisisDoors;
import nadiendev.malisisdoorsrevived.blockentity.ForcefieldBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;

/**
 * Renderer of the forcefields : an animated, glowing plane.
 *
 * @author Ordinastie
 */
public class ForcefieldRenderer extends SimpleBlockEntityRenderer<ForcefieldBlockEntity>
{
	private static final int FRAMES = 50;
	private static final Identifier[] TEXTURES = new Identifier[FRAMES];
	static
	{
		for (int i = 0; i < FRAMES; i++)
			TEXTURES[i] = MalisisDoors.id(String.format("textures/forcefield/forcefield%02d.png", i));
	}

	/** Size (in blocks) covered by one texture. */
	private static final float TEXTURE_SIZE = 3;
	private static final int COLOR = 0xFF6A6A6A;

	public ForcefieldRenderer(BlockEntityRendererProvider.Context context)
	{}

	@Override
	protected void render(ForcefieldBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
	{
		AABB area = be.getArea();
		if (area == null || be.isOpened() || be.getLevel() == null)
			return;

		int frame = (int) (be.getLevel().getGameTime() % FRAMES);
		VertexConsumer consumer = buffer.getBuffer(RenderTypes.eyes(TEXTURES[frame]));
		PoseStack.Pose pose = poseStack.last();

		float x0 = (float) area.minX, y0 = (float) area.minY, z0 = (float) area.minZ;
		float x1 = (float) area.maxX, y1 = (float) area.maxY, z1 = (float) area.maxZ;
		switch (be.getAxis())
		{
			case Y -> {
				float y = y0 + 0.5F;
				float u = (x1 - x0) / TEXTURE_SIZE, v = (z1 - z0) / TEXTURE_SIZE;
				quad(pose, consumer, x0, y, z0, x0, y, z1, x1, y, z1, x1, y, z0, u, v, 0, 1, 0);
			}
			case X -> {
				float x = x0 + 0.5F;
				float u = (z1 - z0) / TEXTURE_SIZE, v = (y1 - y0) / TEXTURE_SIZE;
				quad(pose, consumer, x, y1, z0, x, y0, z0, x, y0, z1, x, y1, z1, u, v, 1, 0, 0);
			}
			case Z -> {
				float z = z0 + 0.5F;
				float u = (x1 - x0) / TEXTURE_SIZE, v = (y1 - y0) / TEXTURE_SIZE;
				quad(pose, consumer, x0, y1, z, x0, y0, z, x1, y0, z, x1, y1, z, u, v, 0, 0, 1);
			}
		}
	}

	/**
	 * Draws a double sided quad.
	 */
	private void quad(PoseStack.Pose pose, VertexConsumer consumer, float xa, float ya, float za, float xb, float yb, float zb, float xc, float yc, float zc, float xd, float yd, float zd, float u, float v, float nx, float ny, float nz)
	{
		vertex(pose, consumer, xa, ya, za, 0, 0, nx, ny, nz);
		vertex(pose, consumer, xb, yb, zb, 0, v, nx, ny, nz);
		vertex(pose, consumer, xc, yc, zc, u, v, nx, ny, nz);
		vertex(pose, consumer, xd, yd, zd, u, 0, nx, ny, nz);

		vertex(pose, consumer, xd, yd, zd, u, 0, -nx, -ny, -nz);
		vertex(pose, consumer, xc, yc, zc, u, v, -nx, -ny, -nz);
		vertex(pose, consumer, xb, yb, zb, 0, v, -nx, -ny, -nz);
		vertex(pose, consumer, xa, ya, za, 0, 0, -nx, -ny, -nz);
	}

	private void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, float u, float v, float nx, float ny, float nz)
	{
		consumer.addVertex(pose, x, y, z)
				.setColor(COLOR)
				.setUv(u, v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(LightCoordsUtil.FULL_BRIGHT)
				.setNormal(pose, nx, ny, nz);
	}

	@Override
	public boolean shouldRenderOffScreen()
	{
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(ForcefieldBlockEntity be)
	{
		return be.getRenderBounds();
	}
}
