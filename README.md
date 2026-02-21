# Delay Mod (Forge 1.12.2 / Fabric 1.21.11)
ディレイ (地面との接地時間)をHUDに表示するクライアントmodです。

## Features
- HUDに次のように表示されます： `Delay: n`  (`n` = ディレイのtick単位での長さ)
- HUDをコマンドで操作できます
## コマンド
- `/delay gui`
- `/delay text label <text>`
- `/delay text value <template-with-{n}>`
- `/delay color label <color16>`
- `/delay color value <color16>`

例:
- `/delay text label Delay:`
- `/delay text value {n} ticks`
- `/delay color label gold`
- `/delay color value white`
