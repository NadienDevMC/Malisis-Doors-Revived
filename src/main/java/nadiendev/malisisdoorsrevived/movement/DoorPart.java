package nadiendev.malisisdoorsrevived.movement;

/**
 * The parts of a door that can be animated separately.
 */
public enum DoorPart
{
	/** Bottom half of a two blocks high door. */
	BOTTOM,
	/** Top half of a two blocks high door. */
	TOP,
	/** Whole door (trapdoors, OBJ doors). */
	DOOR,
	/** Left leaf of double doors (fence gates, big doors). */
	LEFT,
	/** Right leaf of double doors (fence gates, big doors). */
	RIGHT,
	/** Handle of hatches. */
	HANDLE
}
