# Battle Pass Commands

## Overview
Commands for managing the Battle Pass system in ExcellentQuests.

## Available Commands

### Player Commands

#### `/battlepass` or `/bp`
- **Permission:** `excellentquests.command.battlepass`
- **Description:** Opens the Battle Pass GUI for the player
- **Usage:** `/battlepass`

---

### Admin Commands

#### `/battlepass start <name> <duration>`
- **Permission:** `excellentquests.command.battlepass.start`
- **Description:** Start a new Battle Pass season
- **Arguments:**
  - `<name>` - Name of the new season
  - `<duration>` - Duration in days (1-365)
- **Usage:** `/battlepass start "Summer 2026" 60`
- **Example:** `/bp start "Winter Season" 90`

#### `/battlepass cancel`
- **Permission:** `excellentquests.command.battlepass.cancel`
- **Description:** Cancel the current or scheduled season
- **Usage:** `/battlepass cancel`

---

### Level Management Commands

#### `/battlepass addlevel <amount> [player]`
- **Permission:** `excellentquests.command.battlepass.addlevel`
- **Description:** Add levels to a player's Battle Pass
- **Arguments:**
  - `<amount>` - Number of levels to add (1-max level)
  - `[player]` - Target player (optional, defaults to command sender)
- **Usage:** 
  - `/battlepass addlevel 5` - Add 5 levels to yourself
  - `/bp addlevel 10 Steve` - Add 10 levels to Steve

#### `/battlepass removelevel <amount> [player]`
- **Permission:** `excellentquests.command.battlepass.removelevel`
- **Description:** Remove levels from a player's Battle Pass
- **Arguments:**
  - `<amount>` - Number of levels to remove (1-max level)
  - `[player]` - Target player (optional, defaults to command sender)
- **Usage:** `/bp removelevel 3 Alex` - Remove 3 levels from Alex

#### `/battlepass setlevel <amount> [player]`
- **Permission:** `excellentquests.command.battlepass.setlevel`
- **Description:** Set a player's Battle Pass level
- **Arguments:**
  - `<amount>` - Level to set (0-max level)
  - `[player]` - Target player (optional, defaults to command sender)
- **Usage:** `/bp setlevel 25 Steve` - Set Steve's level to 25

---

### XP Management Commands

#### `/battlepass addxp <amount> [player]`
- **Permission:** `excellentquests.command.battlepass.addxp`
- **Description:** Add XP to a player's Battle Pass
- **Arguments:**
  - `<amount>` - Amount of XP to add (minimum 1)
  - `[player]` - Target player (optional, defaults to command sender)
- **Usage:** `/bp addxp 500 Steve` - Add 500 XP to Steve

#### `/battlepass removexp <amount> [player]`
- **Permission:** `excellentquests.command.battlepass.removexp`
- **Description:** Remove XP from a player's Battle Pass
- **Arguments:**
  - `<amount>` - Amount of XP to remove (minimum 1)
  - `[player]` - Target player (optional, defaults to command sender)
- **Usage:** `/bp removexp 200 Alex` - Remove 200 XP from Alex

#### `/battlepass setxp <amount> [player]`
- **Permission:** `excellentquests.command.battlepass.setxp`
- **Description:** Set a player's Battle Pass XP
- **Arguments:**
  - `<amount>` - XP amount to set (minimum 0)
  - `[player]` - Target player (optional, defaults to command sender)
- **Usage:** `/bp setxp 1000 Steve` - Set Steve's XP to 1000

---

### Premium Status Command

#### `/battlepass setpremium <true|false> [player]`
- **Permission:** `excellentquests.command.battlepass.setpremium`
- **Description:** Set a player's Battle Pass premium status
- **Arguments:**
  - `<true|false>` - Premium status (true for Premium, false for Free)
  - `[player]` - Target player (optional, defaults to command sender)
- **Usage:** 
  - `/bp setpremium true` - Set your own Battle Pass to Premium
  - `/bp setpremium false Steve` - Set Steve's Battle Pass to Free
  - `/battlepass setpremium true Alex` - Set Alex's Battle Pass to Premium
- **Notes:**
  - Premium players can claim rewards from both Free and Premium tracks
  - Free players can only claim rewards from the Free track
  - This command is useful for granting premium access via commands instead of permissions

---

## Permission Hierarchy

All Battle Pass commands inherit from the parent permission:
```
excellentquests.command.battlepass
├── excellentquests.command.battlepass.start
├── excellentquests.command.battlepass.cancel
├── excellentquests.command.battlepass.addlevel
├── excellentquests.command.battlepass.removelevel
├── excellentquests.command.battlepass.setlevel
├── excellentquests.command.battlepass.addxp
├── excellentquests.command.battlepass.removexp
├── excellentquests.command.battlepass.setxp
└── excellentquests.command.battlepass.setpremium
```

Grant `excellentquests.command.*` for all command permissions.

---

## Command Aliases

Battle Pass commands support the following aliases:
- `/battlepass` or `/bp`

Example: Both `/battlepass setpremium true` and `/bp setpremium true` work the same.

---

## Error Messages

### No Active Season
If no Battle Pass season is running or scheduled, commands will return:
```
There is no active or scheduled season.
```

To start a season, use: `/battlepass start <name> <duration>`

### Invalid Player
If the specified player is not found:
```
Player not found.
```

### Invalid Arguments
If command arguments are invalid, the command usage will be displayed.

---

## Examples

### Grant Premium to Multiple Players
```
/bp setpremium true Steve
/bp setpremium true Alex
/bp setpremium true Notch
```

### Level Up Rewards Testing
```
/bp setlevel 0 TestPlayer
/bp addlevel 1 TestPlayer
# Check rewards
/bp addlevel 1 TestPlayer
# Check next level rewards
```

### Quick Premium Toggle
```
# Grant premium
/bp setpremium true %player%

# Remove premium
/bp setpremium false %player%
```

### XP Management
```
# Give 1000 XP to a player
/bp addxp 1000 Steve

# Set exact XP amount
/bp setxp 500 Steve

# Remove XP
/bp removexp 250 Steve
```

---

## Integration with Other Systems

### Premium Access via Shop Plugins
Use the `setpremium` command in shop plugins like **DeluxeMenus**, **ChestCommands**, or **CommandShop**:

**Example (DeluxeMenus):**
```yaml
items:
  premium_pass:
    material: GOLD_INGOT
    name: '&6&lPremium Battle Pass'
    lore:
      - '&7Unlock premium rewards!'
      - '&7Price: $1000'
    click_commands:
      - '[console] eco take %player_name% 1000'
      - '[console] bp setpremium true %player_name%'
      - '[message] &aYou purchased Premium Battle Pass!'
```

### Premium Access via Permission Groups
While you can use permissions for premium access (`excellentquests.battlepass.premium`), the `setpremium` command allows for more flexible management, such as:
- Temporary premium access
- Premium access per season
- Premium as a purchasable item
- Premium as a reward

---

## Tips

1. **Season Management:** Always cancel the previous season before starting a new one
2. **Premium Testing:** Use `/bp setpremium true` on yourself to test premium rewards
3. **Level Testing:** Use `/bp setlevel` to quickly test rewards at different levels
4. **XP Rewards:** Combine with quest rewards to give players XP for completing tasks
5. **Backup Data:** Always backup player data before making bulk changes

---

## See Also

- [PlaceholderAPI Integration](PLACEHOLDERAPI.md) - Use Battle Pass data in other plugins
- [ExcellentQuests Wiki](https://nightexpressdev.com/excellentquests/) - Full plugin documentation
