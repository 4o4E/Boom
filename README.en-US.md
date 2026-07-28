# Boom

[English](README_EN.md)

> A management plugin based on BukkitAPI

Supports `Bukkit` and its fork cores such as `Spigot`, `Paper`, and `Purpur`.

Supports version `1.13.x` and above; `1.16.x`-`1.19.x` have been tested.

Complete language files are provided; PRs for other language files are welcome.

**Does NOT support version `1.12` and below, does NOT support `Sponge` core, does NOT support any server cores that add mod support such as `Mohist`, `Arclight`, or `CatServer`, and does NOT support the handling of MOD entities (you may use it, but please do not report issues here if you encounter them).**

[![Release](https://img.shields.io/github/v/release/4o4E/Boom?label=Release)](https://github.com/4o4E/Boom/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/4o4E/Boom/total?label=Download)](https://github.com/4o4E/Boom/releases)

> [!IMPORTANT]
> If TNT explosion prevention fails in higher versions, change `PRIMED_TNT` to `TNT`.
> 
> For TNT Minecarts: change `MINECART_TNT` to `TNT_MINECART`.

## Supported Features

All of the following configurations can be set individually by world or WorldGuard region (Settings under `global` are global, settings under `each.<world_name>` are for specific worlds, and settings under `region.<region_name>` are for regions. Priority: Region (`region`) -> World (`each`) -> Global (`global`)).

- Control entity explosions
- Prevent fire spread
- Prevent fire from burning blocks
- Protect farmland from being trampled by entities
- Prevent entity transformation (Villager, Witch, Zombie Villager, Drowned)
- Prevent Endermen from picking up blocks
- Force Armor Stands to align themselves when spawned (makes Armor Stands have both arms by default)
- Keep inventory on death
- Keep experience levels on death
- Prevent the use of beds
- Prevent the use of Respawn Anchors
- Prevent the use of commands (Command redirection bypasses this check)
- Command redirection: enter command `a` to execute command `b` (can trigger multiple commands) (supports console command redirection)
- Limit entity spawning (percentage)
- Prevent players from clicking entities/blocks

## Configuration File

[config.yml](src/main/resources/config.yml)

## Language File

[lang.yml](src/main/resources/lang.yml)

PRs for other language files are welcome.

## How to Configure

```yaml
# Configurations under global are global settings
global:
  explosion:
    CREEPER:
      enable: false
      cancel: false
  disable_fire_spread: false
  disable_fire_burn: false
  # Other configurations omitted...

# Configurations under each are for individual worlds
each:
  # !! During processing, the plugin will first look for the corresponding world config; if not found, it will use the global config !!
  # Only write configurations that differ from global; identical ones can be omitted.
  # example_world1 and example_world2 are used as world names here; change them to your actual world names in practice.
  # For example, world or world_nether etc.
  # World names are case-sensitive and cannot contain extra spaces.
  # If you don't know your world name, you can use the client (requires permission) to execute /bm world to see the current world name.
  example_world1:
    explosion:
      CREEPER:
        enable: true
        cancel: false
    disable_fire_spread: false
    disable_fire_burn: false
  example_world2:
    explosion:
      CREEPER:
        enable: true
        cancel: false
    disable_fire_spread: true
    disable_fire_burn: true
  # Other configurations omitted...

```

Based on the configuration above:

In world `example_world1`, Creeper explosions will not destroy blocks.

In world `example_world2`, fire will not spread or burn blocks.

In all other worlds, Creeper explosions will destroy blocks, and fire will spread and burn blocks.

## Commands

> The main plugin command is `boom`, with the alias `bm`. If it conflicts with other plugin commands, please use `boom`.

- `/bm reload` Reload the plugin
- `/bm debug` Toggle whether to receive debug messages
- `/bm world` View the current world name
- `/bm sun` Set the current world weather to clear for the next 10 minutes
- `/bm sun <world>` Set a specific world's weather to clear for the next 10 minutes
- `/bm sun <world> <duration>` Set a specific world's weather to clear for the specified duration
- `/bm rain` Set the current world weather to rain for the next 10 minutes
- `/bm rain <world>` Set a specific world's weather to rain for the next 10 minutes
- `/bm rain <world> <duration>` Set a specific world's weather to rain for the specified duration
- `/bm thunder` Set the current world weather to thunderstorms for the next 10 minutes
- `/bm thunder <world>` Set a specific world's weather to thunderstorms for the next 10 minutes
- `/bm thunder <world> <duration>` Set a specific world's weather to thunderstorms for the specified duration
- `/bm ls` Set the current world weather to clear for the next hour
- `/bm stick` Get a debug stick (used for modifying Armor Stands and Item Frames)

## Permissions

- `boom.admin` Allows use of plugin commands

- `boom.bypass.command` Allows bypassing command filtering

- `boom.weather` Allows use of all weather commands

  **Sub-permissions**

  - `boom.weather.sun` Allows switching weather to clear

  - `boom.weather.rain` Allows switching weather to rain

  - `boom.weather.thunder` Allows switching weather to thunderstorms

- `boom.stick` Allows obtaining and using the debug stick to modify Armor Stands/Item Frames

- `boom.bypass.*` Allows bypassing restrictions on clicking entities and blocks

  **Sub-permissions**

  - `boom.bypass.block` Allows bypassing restrictions on clicking blocks
  
  - `boom.bypass.entity` Allows bypassing restrictions on clicking entities

## Download

- [Latest Version](https://github.com/4o4E/Boom/releases/latest)

## Known Issues

- [ ] When using the Armor Stand debug stick, sneaking and clicking an Armor Stand to modify its hitbox sometimes triggers twice consecutively.

  Solution: Sneak-click a block close to the Armor Stand.

- [x] ~~Villager lightning protection prevents villagers from turning into zombie villagers (getting killed by zombies results in immediate death)~~ Fixed.

## Changelog

```log
2021.01.07 1.0.0 Plugin released
2021.01.07 1.0.1 Added reload command
2021.01.11 1.0.2 Added customizable particles and sound effects for prevention
2021.01.18 1.0.3 Bug fixes
2021.02.01 1.0.4 Added option to prevent bat spawning (mainly for my skyblock server XD)
2021.02.09 1.0.5 Added disabling of command usage and tab completion
2021.02.14 1.0.6 Removed separate bat spawn prevention; added spawn probability/disabling for all creatures
2021.02.21 1.1.0 Rewrote parts of the code, optimized performance, added farmland protection, fire spread toggle, command redirection/simplification
(Configuration files must be deleted when updating; new versions are completely incompatible with old ones)
2021.02.22 1.1.1 Added protection to prevent villagers from turning into witches via lightning
2021.02.23 1.1.2 Added item frame adjustment tool, allowing the use of a debug stick to toggle visibility/interaction
2021.02.25 1.2.0 Bug fixes, code optimization, adjusted default configuration
(Configuration files must be deleted when updating; new versions are completely incompatible with old ones)
2021.02.25 1.2.1 Added Armor Stand debug functionality, allowing the use of a debug stick to toggle visibility/interaction
2021.03.02 1.2.2 Bug fixes, added update check
2021.03.06 1.2.3 Bug fixes, added weather control
2021.03.07 1.2.4 Bug fixes, added console command execution when players use disabled commands
2021.03.28 1.2.5 Bug fixes, modified disabled command format, added fuzzy matching
2021.05.23 1.3.0 Bug fixes, added per-world disabled command configuration, optimized Armor Stand debug functionality, added bstats statistics
(Configuration files must be deleted when updating; new versions are completely incompatible with old ones)
2021.06.12 1.3.1 Removed legacy debug information
2021.06.12 1.3.2 Fixed issue where help information did not display
2021.11.07 1.3.3 Fixed incorrect content displayed by the help command
2022.06.23 2.0.0 Rewritten in Kotlin, fixed issue where beds could not be blocked in higher versions, added death drop settings, optimized processing flow, added debug logs
(Configuration files must be deleted when updating; new versions are completely incompatible with old ones)
2022.06.23 2.0.1 Modified unused hardcoded strings to use language files for easier localization
2022.07.01 2.0.2 Fixed incorrect debug information during entity explosions, fixed issue where specific world configurations were not loading correctly
2022.07.02 2.0.3 Removed remaining debug information
2022.07.02 2.0.4 Fixed entity limit configuration reading errors
2022.07.02 2.0.5 Added configuration options to prevent players from clicking entities/blocks
2022.07.07 2.0.6 Added bypass permissions for click restrictions, improved configuration comments, added handling for breaking item frames
2022.07.10 2.0.7 Fixed bug where clicking bedrock triggered the bed click check
2022.07.11 2.0.8 Added option to prevent players from taking damage
2022.07.25 2.0.9 Added WorldGuard soft dependency, added region configuration options, allowing per-region detection
(Configuration files must be deleted when updating; new versions are completely incompatible with old ones)
2022.09.28 2.0.10 Added settings to prevent damage to entities during explosions, optimized debug message handling
2024.01.19 2.11.0 Added handling for clicking air in prevent_click_block, fixed missing world name in world command
2025.01.21 2.12.0 Updated upstream dependencies
```

## bstats

[![bstats](https://bstats.org/signatures/bukkit/Boom.svg)](https://bstats.org/plugin/bukkit/Boom/11445)
