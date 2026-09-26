# MalisisDoors Revived

A port of [MalisisDoors](https://github.com/Ordinastie/MalisisDoors) (originally by Ordinastie, MIT license) to **Minecraft 1.21.1 / NeoForge 21.1.240**.

Requires [MalisisCore Revived](https://github.com/Modern-Mods/Malisis-Core-Revived).

## Features

* **Animated vanilla doors, trapdoors and fence gates**: every wood type (including mangrove, cherry, bamboo, crimson and warped)
  and iron. Vanilla blocks are swapped for their animated version when a player places or uses them, so it also works for doors
  generated in villages. The animated blocks drop the vanilla items. Each kind can be disabled in the server config
  (`malisisdoors-server.toml`).
* **New doors**, each with its own animation and sound:
  wooden and iron glass (sliding) doors, jail door, laboratory door (slides up), factory door (splits up and down), shoji door,
  16 curtains, saloon doors (swing when you walk through them), reinforced door (vertical hatch with a spinning wheel) and
  the hi-tech door.
* **Sliding trapdoor** and **camo fence gate** (takes the texture of the blocks on both sides when they are identical).
* **Garage doors**: stack panels, power them with redstone and they roll into the ceiling. Garage doors next to each other open together.
* **Big doors**: carriage and medieval doors (4x5, crafted with a door and 5 identical blocks used for the frame) and large 3x3 double doors (every vanilla wood, iron and rusty iron).
* **Rusty hatch** (2x2 hatch for floors and ceilings, opened with a wheel) and **rusty ladder**.
* **Forcefields**: select two corners with the forcefield controller to create a forcefield plane. Use the controller on it to
  toggle it, sneak-use to destroy it. The controller uses energy that regenerates over time.
* **Door Factory**: create custom doors from any three blocks (frame, top panel, bottom panel) and choose their movement (18 movements:
  rotating, sliding, vault, vortex, vanishing...), sound, opening time, auto-closing delay, redstone behavior, double door and player
  detection. In modify mode, the settings are applied to existing MalisisDoors doors.
* **Player sensor**: emits a redstone signal while a player is near it. Place it on the wall above a door to open the door
  when a player comes, or on a ceiling or a floor to detect the players below or above it.
* Doors placed between fences, walls, iron bars or glass panes are centered in the block.
* Villagers can use the wooden doors, wind charges open them and redstone works like vanilla (or as configured).

## Integrations

* **Doors from other mods**: the doors, trapdoors and fence gates of these mods are replaced by an animated version, like the vanilla
  ones. Each mod can be disabled in the server config (`[integrations]` section).
  Macaw's Doors, Macaw's Trapdoors, Macaw's Fences and Walls, ManyIdeas Doors, Vanilla Backport (pale oak), Create (trapdoors),
  Ad Astra, The Twilight Forest, Malum, Mekanism Additions, Allthemodium, Ender IO, Occultism, Enchanted, Ars Nouveau,
  Farmer's Delight and Chipped.
  Blocks with their own block entity or behavior (sliding, folding, weathering, multiblock doors) keep their own animation.
* **JEI, REI and EMI**: information pages for the special blocks and examples of the big door recipe.
* **Jade and WTHIT**: the animated doors are shown as the door they replace.

## Differences with the 1.12 version

* The vanilla doors are not replaced in the registry anymore (not possible on modern versions): animated copies are swapped in instead.
* Vanilla now has all the wood trapdoors, so the MalisisDoors wood trapdoors are the animated vanilla ones.
* The rusty hatch only occupies its 2x2 hatch: build the shaft below or above it yourself with rusty ladders.
* The Door Factory GUI is a vanilla style screen (settings are buttons instead of the MalisisCore GUI widgets). The digicode was not ported.

## Building

Requirements: JDK 21. Check out MalisisCore Revived next to this repository (or set `malisiscore_path` in `gradle.properties`):

```
git clone https://github.com/Modern-Mods/Malisis-Core-Revived.git
git clone https://github.com/Modern-Mods/Malisis-Doors-Revived.git
cd Malisis-Doors-Revived
./gradlew build
./gradlew runClient
```

Without a local checkout, the dependency is resolved from `mavenLocal()`, the `repo` folder of MalisisCore Revived, or a jar in `libs/`.

The blockstates, models and translations are generated: run `./gradlew runData` after changing the providers in the `datagen` package.
The output goes to `src/generated/resources`.

The mods loaded by `runClient` are chosen in `gradle.properties`: `dev_recipe_viewers` (`jei`, `rei`, `emi`), `dev_tooltip_mods`
(`jade`, `wthit`) and `dev_door_mods` (`true` or `false`).

## License

MIT. MalisisDoors was created by Ordinastie; this port is maintained by Modern Mods. The textures, models and sounds come from the original mod.
