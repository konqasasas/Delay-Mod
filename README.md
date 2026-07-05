# Delay Mod

Client-side Minecraft mod that displays grounded delay ticks on the HUD.

Download builds from the CurseForge files page:

https://www.curseforge.com/minecraft/mc-mods/delay-mod/files/all?page=1&pageSize=20&showAlphaFiles=hide

This repository contains two maintained source trees:

- `forge-1.12.2/` - Forge version for Minecraft 1.12.2
- `fabric-1.21.11/` - Fabric version for Minecraft 1.21.11

## Features

- Shows the current grounded delay as HUD text, for example `Delay: 0`
- Movable HUD position editor with `/delay gui`
- Customizable label/value text and Minecraft text colors
- Client-side configuration file

## Commands

- `/delay gui`
- `/delay text label <text>`
- `/delay text value <template-with-{n}>`
- `/delay color label <color16>`
- `/delay color value <color16>`

Examples:

```text
/delay text label Delay:
/delay text value {n} ticks
/delay color label gold
/delay color value white
```

## Build

Build each version from its own directory.

Forge 1.12.2 requires a Java 8 toolchain:

```powershell
cd forge-1.12.2
.\gradlew.bat build --no-daemon
```

Fabric 1.21.11 requires Java 21 or newer:

```powershell
cd fabric-1.21.11
.\gradlew.bat build
```

Generated jars are written under each version's `build/libs/` directory.
