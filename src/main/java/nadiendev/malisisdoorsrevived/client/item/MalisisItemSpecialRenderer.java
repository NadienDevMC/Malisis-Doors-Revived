package nadiendev.malisisdoorsrevived.client.item;

import java.util.function.Consumer;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;

import nadiendev.malisiscorerevived.client.render.RecordingBufferSource;
import nadiendev.malisisdoorsrevived.client.MalisisItemRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.world.item.ItemStack;

public class MalisisItemSpecialRenderer implements SpecialModelRenderer<ItemStack>
{
	@Override
	public void submit(@Nullable ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean hasFoil, int outlineColor)
	{
		if (stack == null)
			return;
		RecordingBufferSource buffer = new RecordingBufferSource();
		MalisisItemRenderer.render(stack, poseStack, buffer, light, overlay);
		buffer.submit(collector);
	}

	@Override
	public void getExtents(Consumer<Vector3fc> output)
	{
		for (int x = 0; x <= 1; x++)
			for (int y = 0; y <= 1; y++)
				for (int z = 0; z <= 1; z++)
					output.accept(new Vector3f(x, y, z));
	}

	@Override
	public ItemStack extractArgument(ItemStack stack)
	{
		return stack.copy();
	}

	public record Unbaked() implements SpecialModelRenderer.Unbaked<ItemStack>
	{
		public static final MapCodec<Unbaked> CODEC = MapCodec.unit(new Unbaked());

		@Override
		public SpecialModelRenderer<ItemStack> bake(SpecialModelRenderer.BakingContext context)
		{
			return new MalisisItemSpecialRenderer();
		}

		@Override
		public MapCodec<Unbaked> type()
		{
			return CODEC;
		}
	}
}
