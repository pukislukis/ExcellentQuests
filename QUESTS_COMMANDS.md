# Daily Quests Commands

## Overview
Commands for managing the Daily Quests system in ExcellentQuests.

## Available Commands

### Player Commands

#### `/quests`
- **Permission:** `excellentquests.command.quests`
- **Description:** Opens the Daily Quests GUI for the player
- **Usage:** `/quests`

---

### Admin Commands

#### `/quests refresh <player>`
- **Permission:** `excellentquests.command.quests.refresh`
- **Description:** Refresh a player's daily quests
- **Arguments:**
  - `<player>` - Target player name
- **Usage:** `/quests refresh Steve`
- **Notes:**
  - Clears all existing quests (both active and inactive)
  - Generates new random quests for the player
  - Player receives a notification about new quests

#### `/quests reroll <player>`
- **Permission:** `excellentquests.command.quests.reroll`
- **Description:** Reroll a player's daily quests
- **Arguments:**
  - `<player>` - Target player name
- **Usage:** `/quests reroll Steve`
- **Notes:**
  - Cancels all active quests by setting them to inactive
  - Clears all existing quest data
  - Generates new random quests for the player
  - Player receives a notification about new quests
  - **Key Difference from Refresh:** Active quests are explicitly cancelled before generating new ones

---

## Permission Hierarchy

All Daily Quests commands inherit from the parent permission:
```
excellentquests.command.quests
├── excellentquests.command.quests.refresh
└── excellentquests.command.quests.reroll
```

Grant `excellentquests.command.*` for all command permissions.

---

## Command Aliases

Daily Quests commands support configurable aliases defined in the config.
Default alias: `/quests`

---

## Differences Between Refresh and Reroll

### `/quests refresh <player>`
- Simply resets the quest timer and generates new quests
- Designed for general quest refresh functionality
- Does not explicitly cancel active quests

### `/quests reroll <player>`
- **Cancels all active quests** by setting them to inactive
- Ensures accepted quests are properly terminated
- Then clears all quest data and generates new quests
- Best used when you want to give players a fresh start

---

## Examples

### Reroll Quests for a Player
```
/quests reroll Steve
```
This will:
1. Cancel all of Steve's active quests
2. Clear all quest data
3. Generate new random quests
4. Send Steve a notification

### Refresh Quests for Multiple Players
```
/quests refresh Steve
/quests refresh Alex
/quests refresh Notch
```

---

## Tips

1. **Reroll vs Refresh:** Use `reroll` when you want to ensure all accepted quests are cancelled before generating new ones
2. **Quest Testing:** Use `/quests reroll` on yourself to test different quest configurations
3. **Player Support:** Use `reroll` to help players who are stuck with difficult quests
4. **Backup Data:** Always backup player data before making bulk changes

---

## See Also

- [PlaceholderAPI Integration](PLACEHOLDERAPI.md) - Use quest data in other plugins
- [Battle Pass Commands](BATTLEPASS_COMMANDS.md) - Battle Pass command documentation
- [ExcellentQuests Wiki](https://nightexpressdev.com/excellentquests/) - Full plugin documentation
