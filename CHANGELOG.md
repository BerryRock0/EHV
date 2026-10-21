## [Ether Hack v2.7](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v2.7)

- Added the `Increase the reach radius` feature, it allows opening and looting containers (drawers, crates, safes, cabinets) and taking items from any distance, without walking to them. The radius is unlimited
- The overrides of the reach radius are re-installed after the game reloads its lua files, so they are not lost when a world is loaded
- The relevance check of the server is patched, so the contents of a container are sent to the player even when he is far away from it
- Added the `Performance` panel with settings for improving the game performance (removing the frame rate limit, lighting, water, puddles and fog quality, reflections, skybox and animation interpolation)
- Added the `Restore the original settings` button to the `Performance` panel

## [Ether Hack v1.1](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v1.1) - 2023-07-09

- Fixed API and GUI initialization
- Fixed feature `Set Max Default Skills`, now all available skills are set to the maximum level
- Removed `ghostmode` because its implementation duplicates `invisible`
- The `Set Max Default Skills` button has been moved to the `Player Editor` panel
- Added the ability to reload the Lua GUI with the `Home` key
- Added the `Unlimited Carry` feature
- Added the `MultiHit Zombie` feature
- Added the `Unlimited Endurance` feature
- Added the `Disable Fatigue` feature
- Added the `Disable Hunger` feature
- Added the `Disable Thirst` feature
- Added the `Disable Character Needs` feature
- added a button to open the admin panel
- Added the ability to load additional lua dependencies via the `EtherRequire` method
- Added Credits when installing the cheat
- Added logo display at game launch

## [Ether Hack v1.0](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/EtherHack-1.0) - 2023-07-05

- The logic of the version report has been changed
- Changed the system of injections into game files
- UI rendering logic changed - now via LUA
- Integrated support for custom Lua methods from Java
- Added new cheat functions
- Added the ability to use developer mode in multiplayer
