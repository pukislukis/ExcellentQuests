# PlaceholderAPI Integration for ExcellentQuests

## Overview
ExcellentQuests now supports PlaceholderAPI, allowing you to use plugin placeholders in other plugins and configurations that support PlaceholderAPI.

## Identifier
The main placeholder identifier is: `%excellentquests_<placeholder>%`

## Available Placeholders

### Battle Pass Placeholders
Display information about a player's Battle Pass progress in the current season.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%excellentquests_battle_pass_type%` | Player's Battle Pass type (Free or Premium) | `Premium` |
| `%excellentquests_battle_pass_level%` | Current Battle Pass level | `15` |
| `%excellentquests_battle_pass_max_level%` | Maximum Battle Pass level | `50` |
| `%excellentquests_battle_pass_xp%` | Current Battle Pass XP | `1,250` |
| `%excellentquests_battle_pass_xp_max%` | Total XP required for next level | `2,000` |
| `%excellentquests_battle_pass_xp_to_up%` | XP needed to level up | `750` |
| `%excellentquests_battle_pass_xp_to_down%` | XP that can be lost before leveling down | `1,250` |

**Alternative formats** (also supported):
- `%excellentquests_battlepass_level%` (without underscore)
- `%excellentquests_battle_pass_maxlevel%` (without underscore in sub-property)

### Season Placeholders
Display information about the current Battle Pass season.

| Placeholder | Description | Example Output |
|-------------|-------------|----------------|
| `%excellentquests_season_name%` | Current season name | `Summer 2026` |
| `%excellentquests_season_start_date%` | Season start date | `2026-01-01 00:00:00` |
| `%excellentquests_season_end_date%` | Season end date | `2026-03-31 23:59:59` |
| `%excellentquests_season_expire_date%` | Season expiration date | `2026-04-30 23:59:59` |
| `%excellentquests_season_duration%` | How long the season has been running | `45 days 12 hours` |
| `%excellentquests_season_timeleft%` | Time remaining until season ends | `15 days 6 hours` |

**Alternative formats**:
- `%excellentquests_season_startdate%` (without underscore)
- `%excellentquests_season_time_left%` (with underscore)

### Milestone Category Placeholders
Display information about milestone categories.

**Format:** `%excellentquests_milestone_category_<category_id>_<property>%`

| Property | Description | Example |
|----------|-------------|---------|
| `id` | Category unique identifier | `combat` |
| `name` | Category display name | `Combat Challenges` |
| `description` | Category description | `Complete combat-related milestones` |

**Examples:**
- `%excellentquests_milestone_category_combat_name%` → `Combat Challenges`
- `%excellentquests_milestone_category_mining_id%` → `mining`
- `%excellentquests_milestone_category_farming_description%` → `Farm crops and breed animals`

### Milestone Placeholders
Display information about specific milestones for the player.

**Format:** `%excellentquests_milestone_<milestone_id>_<property>%`

| Property | Description | Example |
|----------|-------------|---------|
| `id` | Milestone unique identifier | `kill_zombies` |
| `name` | Milestone display name | `Zombie Hunter` |
| `description` | Milestone description | `Kill zombies to progress` |
| `level` | Player's current level in this milestone | `3` |
| `completed` | Whether player has completed all levels | `true` or `false` |

**Examples:**
- `%excellentquests_milestone_kill_zombies_name%` → `Zombie Hunter`
- `%excellentquests_milestone_kill_zombies_level%` → `3`
- `%excellentquests_milestone_mine_diamonds_completed%` → `false`

### Quest Placeholders
Display information about specific quests for the player.

**Format:** `%excellentquests_quest_<quest_id>_<property>%`

| Property | Description | Example |
|----------|-------------|---------|
| `id` | Quest unique identifier | `daily_mining` |
| `name` | Quest display name | `Daily Mining` |
| `description` | Quest description | `Mine 100 stone blocks` |
| `active` | Whether the quest is currently active for the player | `true` or `false` |
| `completed` | Whether the player has completed the quest | `true` or `false` |

**Alternative property names:**
- `isactive` instead of `active`
- `iscompleted` instead of `completed`

**Examples:**
- `%excellentquests_quest_daily_mining_name%` → `Daily Mining`
- `%excellentquests_quest_daily_mining_active%` → `true`
- `%excellentquests_quest_kill_mobs_completed%` → `false`

## Usage Examples

### In Chat Plugins (e.g., ChatControl, DeluxeChat)
```yaml
format: "&7[&6Level %excellentquests_battle_pass_level%&7] &f%player_name%: %message%"
```

### In Scoreboard Plugins (e.g., FeatherBoard, AnimatedScoreboard)
```yaml
lines:
  - "&6&lBattle Pass"
  - "&eLevel: &f%excellentquests_battle_pass_level%/%excellentquests_battle_pass_max_level%"
  - "&eXP: &f%excellentquests_battle_pass_xp%/%excellentquests_battle_pass_xp_max%"
  - "&eType: &f%excellentquests_battle_pass_type%"
  - ""
  - "&6&lSeason: &f%excellentquests_season_name%"
  - "&eTime Left: &f%excellentquests_season_timeleft%"
```

### In Tab Plugins (e.g., TAB, NametagEdit)
```yaml
tablist-prefix: "&7[BP %excellentquests_battle_pass_level%] "
```

### In Custom Items/GUIs
```yaml
lore:
  - "&7Battle Pass Progress"
  - "&eLevel: &f%excellentquests_battle_pass_level%"
  - "&eXP to Next: &f%excellentquests_battle_pass_xp_to_up%"
```

## Technical Details

### Implementation
- **Class:** `PlaceholderAPIHook.java`
- **Package:** `su.nightexpress.quests.hook`
- **Expansion Identifier:** `excellentquests`
- **Auto-registration:** Automatically registers when PlaceholderAPI is detected

### Requirements
- PlaceholderAPI plugin installed on the server
- ExcellentQuests version 4.0.6 or higher

### Features
- **Null-safe:** Returns empty string when data is not available
- **Feature-aware:** Returns empty string when a feature (Battle Pass, Milestones, Quests) is disabled
- **Format support:** Supports multiple naming formats for convenience
- **Real-time:** All placeholders update in real-time based on player data

### Performance
- **Caching:** Uses the plugin's existing user data cache
- **Efficient lookups:** Direct map lookups for O(1) performance
- **Minimal overhead:** No additional database queries

## Troubleshooting

### Placeholder shows as raw text
1. Ensure PlaceholderAPI is installed: `/plugins`
2. Check if the expansion is registered: `/papi list`
3. Look for "excellentquests" in the expansions list
4. Verify the placeholder format is correct

### Placeholder returns empty
1. Check if the feature is enabled in config (Battle Pass, Milestones, Quests)
2. Verify the ID exists (for milestone_category, milestone, quest placeholders)
3. Ensure the player has data for that feature
4. Check server logs for any errors

### Need to reload
After installing PlaceholderAPI or updating ExcellentQuests:
```
/papi reload
/equests reload
```

## Support
For issues or questions:
- Check the [ExcellentQuests wiki](https://nightexpressdev.com/excellentquests/)
- Join the [Discord server](https://discord.gg/EwNFGsnGaW)
- Open an issue on the GitHub repository
