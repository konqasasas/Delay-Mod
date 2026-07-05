# Delay Mod (Forge 1.12.2)

Client-side mod that displays grounded delay ticks as a HUD text.

## Features

- Shows `Delay: n` on HUD (`n` = grounded tick counter)
- Counter logic:
  - `isAir = !prevOnGround || !nowOnGround`
  - `isGround = !isAir`
  - Reset to `0` when ground starts (`!prevOnGround && nowOnGround`)
  - Increment only while `isGround`
- HUD editor via command
  - Drag to move
  - Clamp to screen edge (0px margin)
  - Arrow keys move by 1px
- Customizable label/value text and colors (Minecraft 16 colors)
- Config saved to `config/delaymod.cfg`

## Commands

- `/delay gui`
- `/delay text label <text>`
- `/delay text value <template-with-{n}>`
- `/delay color label <color16>`
- `/delay color value <color16>`

Examples:

- `/delay text label Delay:`
- `/delay text value {n} ticks`
- `/delay color label gold`
- `/delay color value white`

## Build

Forge 1.12.2 toolchain requires **Java 8** for Gradle/ForgeGradle execution.

PowerShell example:

```powershell
$env:JAVA_HOME='C:\Program Files\Eclipse Adoptium\jdk-8.0.472.8-hotspot'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat build --no-daemon
```

Output jar:

- `build/libs/delaymod-1.12.2-1.0.0.jar`
