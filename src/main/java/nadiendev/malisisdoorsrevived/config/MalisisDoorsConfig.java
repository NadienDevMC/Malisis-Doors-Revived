package nadiendev.malisisdoorsrevived.config;

import java.util.LinkedHashMap;
import java.util.Map;

import nadiendev.malisisdoorsrevived.animated.AnimatedCopies;
import nadiendev.malisisdoorsrevived.animated.DoorIntegration;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Configuration of MalisisDoors. The options are per world and synced to the clients.
 *
 * @author Ordinastie
 */
public class MalisisDoorsConfig
{
	public static final ModConfigSpec SERVER_SPEC;

	public static final ModConfigSpec.BooleanValue ANIMATE_VANILLA_DOORS;
	public static final ModConfigSpec.BooleanValue ANIMATE_VANILLA_TRAPDOORS;
	public static final ModConfigSpec.BooleanValue ANIMATE_VANILLA_FENCE_GATES;
	public static final Map<String, ModConfigSpec.BooleanValue> INTEGRATIONS = new LinkedHashMap<>();

	static
	{
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		builder.comment("Vanilla doors, trapdoors and fence gates are replaced by their animated version when placed or used.").push("vanilla");
		ANIMATE_VANILLA_DOORS = builder	.comment("Animate the vanilla doors.")
										.translation("malisisdoors.configuration.animateVanillaDoors")
										.define("animateVanillaDoors", true);
		ANIMATE_VANILLA_TRAPDOORS = builder	.comment("Animate the vanilla trapdoors.")
											.translation("malisisdoors.configuration.animateVanillaTrapdoors")
											.define("animateVanillaTrapdoors", true);
		ANIMATE_VANILLA_FENCE_GATES = builder	.comment("Animate the vanilla fence gates.")
												.translation("malisisdoors.configuration.animateVanillaFenceGates")
												.define("animateVanillaFenceGates", true);
		builder.pop();
		builder.comment("Doors, trapdoors and fence gates of other mods are replaced by their animated version when placed or used.").push("integrations");
		for (DoorIntegration integration : AnimatedCopies.INTEGRATIONS)
			INTEGRATIONS.put(integration.modId(),
								builder	.comment("Animate the doors, trapdoors and fence gates of " + integration.name() + ".")
										.translation("malisisdoors.configuration." + integration.modId())
										.define(integration.modId(), true));
		builder.pop();
		SERVER_SPEC = builder.build();
	}
}
