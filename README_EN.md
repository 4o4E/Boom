# Boom

> Bukkit APi-based management plugin

Support `Bukkit` branch such as `Bukkit`/`Spigot`/`Paper`/`Purpur`

Support `1.13.x`-`1.19.x`, `1.16.x`-`1.19.x` tested

Provide complete language files, welcome pr other language files

## 1.12 and below are not supported, `Sponge` core is not supported, Does not support `Mohist`/`Arclight`/`CatServer` etc. any server core with mod support added, handling of MOD entities is not supported(You can use it, but please don't report any problems here)

[![Release](https://img.shields.io/github/v/release/4o4E/Boom?label=Release)](https://github.com/4o4E/Boom/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/4o4E/Boom/total?label=Download)](https://github.com/4o4E/Boom/releases)

## support function

All of the following configurations can be configured individually by world(Under `global` are the global settings, Under `each.<world name>` are the settings for individual worlds)
- control entity explosion
- prevent fire from spreading
- prevent fire from destroying blocks
- protect farmland from being trampled by entities
- prevent entity transformation (villager, witch, zombie villager, drowned)
- prevent enderman from lifting blocks
- straighten itself when making armorstand spawn(made armorstand have two hands by default)
- keep items on death
- preserve experiences on death
- prevent the use of the bed
- prevent the use of respawn anchor
- prevent players from using commands(The function of command transform will skip this detection)
- command transform enter command `a` to use command `b`(can trigger multiple commands)(supports the transfer of console commands)
- restrict entity generation(percentage)

## configuration

[config.yml](src/main/resources/config.yml)

## lang

[lang.yml](src/main/resources/lang.yml)

pr other language files are welcome

## how to configure

```yaml
# The configuration under global is the global configuration
global:
  explosion:
    CREEPER:
      enable: false
      cancel: false
  disable_fire_spread: false
  disable_fire_burn: false
  # other configuration is omitted here...

# The configuration under each is the configuration of a separate world
each:
  # !! In actual processing, the configuration of the corresponding world will be found first, if not found, the configuration in global will be used !!
  # Here you only need to write a configuration different from global, the same can be omitted
  # Here example_world1 and example_world2 are used as world names, and they are changed to the names of their own worlds in actual use.
  # such as world or world_nether, etc.
  # World names are case-sensitive, no extra spaces
  # If you don't know the name of your world, you can use the client (requires permission) to execute bm world to view the name of the world you are currently in.
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
  # other configuration is omitted here...

```

According to the above configuration

In the world `example_world1`, creeper explosions do not break blocks

In the world `example_world2`, fire does not spread and burn blocks

In all other worlds, creeper explosions break blocks, fire spreads and burns blocks

## command

> The main command of the plugin is `boom`, including the alias `bm`, if it conflicts with other plugin commands, please use `boom`
-`/bm reload` Reload plugin
-`/bm debug` Toggles acceptance of debug messages
-`/bm world` View current world name
-`/bm sun` Toggle current world weather to sunny for the next 10 minutes
-`/bm sun <world>` Toggles specifying that the world weather will be sunny for the next 10 minutes
-`/bm sun <world> <duration>` Toggles the specified world weather to be sunny for the next specified period of time
-`/bm rain` Toggle current world weather to rain for the next 10 minutes
-`/bm rain <world>` Toggles specifying world weather to be rain for the next 10 minutes
-`/bm rain <world> <duration>` Toggle the specified world weather to rain for the next specified duration
-`/bm thunder` Toggle current world weather to thunderstorms for the next 10 minutes
-`/bm thunder <world>` Toggles specifying world weather to be thunderstorms for the next 10 minutes
-`/bm thunder <world> <duration>` Toggles the specified world weather to be thunderstorms for the next specified duration
-`/bm ls` Toggle current world weather to sunny for the next hour
-`/bm stick` Get the debug stick(used to modify armor stands and display frames)

## permission

- `boom.admin` Allow use of plugin directives

- `boom.bypass.command` Allow skipping instruction filtering

- `boom.weather` All weather commands are allowed

  **child permission node**

  - `boom.weather.sun` Allow to switch the weather to sunny

  - `boom.weather.rain` Allow to switch weather to rain

  - `boom.weather.thunder` Allow switching weather to thunderstorm

- `boom.stick` Allows getting the debug stick, modifying armor stand and item frame with the debug stick

## download

- [latest version](https://github.com/4o4E/Boom/releases/latest)

## known issues

- [ ] Armor Stand Debug Stick Sneak-clicking on an armorstand to modify its hitbox sometimes triggers twice in a row

  Solution: Sneak and click on the block close to the armorstand

- [x] ~~Lightning protection for villagers will prevent villagers from becoming zombie villagers(killed by zombies is directly dead)~~ already fixed

## update record

See [CHANGELOG.md](CHANGELOG.md) for the complete version history and upgrade notes (Chinese).

## bstats

![bstats](https://bstats.org/signatures/bukkit/Boom.svg)