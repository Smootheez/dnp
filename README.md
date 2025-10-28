# Damage Number Particles

**Damage Number Particles** is a lightweight Minecraft mod that displays floating number particles whenever an entity takes **damage** or receives **healing**.
This feature provides real-time visual feedback, helping players easily track combat effectiveness or healing output during gameplay.

## Requirements

This mod requires the following dependencies to work properly:

### For versions **below `0.0.5+1.21.9`**

| Modrinth                                                                    | Curseforge                                                                                      |
|-----------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| [Smootheez Config Lib (scl)](https://modrinth.com/mod/smootheez-config-lib) | [Smootheez Config Lib (scl)](https://www.curseforge.com/minecraft/mc-mods/smootheez-config-lib) |

### For versions **`0.0.5+1.21.9` and above**

| Modrinth                                                | Curseforge                                                                  |
|---------------------------------------------------------|-----------------------------------------------------------------------------|
| [Smoothiez API](https://modrinth.com/mod/smoothiez-api) | [Smoothiez API](https://www.curseforge.com/minecraft/mc-mods/smoothiez-api) |
| [Fabric API](https://modrinth.com/mod/fabric-api)       | [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)       |


## Preview

![Damage Number Particles Preview](https://raw.githubusercontent.com/Smootheez/dnp/refs/heads/dev/assets/gif/damage_particle_preview.gif)

Experience floating numbers that dynamically appear above entities as they take damage or receive healing — all rendered smoothly in real time.

## Configuration

Damage Number Particles can be fully customized to match your preferences.
You can modify settings either **in-game** (via Mod Menu) or **manually** through the config file.

### Available Options

* Toggle particle visibility
* Adjust display radius and minimum damage threshold
* Manage an **entity blacklist** to prevent certain mobs from displaying particles

To blacklist an entity, add its identifier in the following format:

```yaml
id:entity_identifier
```

**Example:**

```yaml
minecraft:allay
```

## Download

Get the latest version from your preferred platform:

| Modrinth                                                    | Curseforge                                                                      |
|-------------------------------------------------------------|---------------------------------------------------------------------------------|
| **[Damage Number Particles](https://modrinth.com/mod/dnp)** | **[Damage Number Particles](https://www.curseforge.com/minecraft/mc-mods/dnp)** |

## Reporting Issues

Found a bug or want to suggest a new feature?
Please open an issue on GitHub: [**GitHub Issues**](https://github.com/Smootheez/dnp/issues)

## Support My Work

If you enjoy this mod and want to support future development, consider donating:

[![ko-fi](https://raw.githubusercontent.com/Smootheez/Smootheez/7b16ed55570e49b9320e9cade5e572b271e9f1fe/assets/donation-kofi.svg)](https://ko-fi.com/smootheez)
[![paypal](https://raw.githubusercontent.com/Smootheez/Smootheez/7b16ed55570e49b9320e9cade5e572b271e9f1fe/assets/donation-paypal.svg)](https://paypal.me/smootheez)
