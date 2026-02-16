# Block Loot Bug Fixes

This document describes the fixes applied to resolve critical bugs in the `block_loot` task type.

## Issues Fixed

### 1. Quest Progress Over-Counting Bug

**Problem:**
- Quests using `block_loot` type were not properly checking current progress before adding new progress
- This allowed quest progress to exceed requirements
- Could potentially allow unlimited progress from a single event
- Milestones already had the correct implementation, but quests did not

**Root Cause:**
In `QuestManager.progressQuests()` (line 229), the code used:
```java
int count = Math.min(required, amount);
```

This only limited the count to the requirement, but didn't check if the player already had progress toward the objective.

**Fix Applied:**
Updated to match the milestone implementation:
```java
int current = questData.getCurrent(fullName);
if (current >= required) continue;

int count = Math.min(required - current, amount);
```

Now the code:
1. Gets the current progress
2. Skips if already completed
3. Calculates the proper count by subtracting current progress from requirement

**Impact:**
- Quest progress now works correctly and matches milestone behavior
- Players cannot exceed quest requirements
- Progress is properly capped at the required amount

---

### 2. Ageable Block Spam Abuse

**Problem:**
- Players could exploit the system by placing and immediately breaking crops (like sugar_cane, wheat, bamboo)
- The anti-abuse check completely bypassed Ageable blocks
- Players could spam place/break immature crops to farm infinite quest/milestone progress

**Example Exploit:**
1. Player has a quest to collect sugar_cane
2. Player places sugar_cane
3. Player immediately breaks it before it grows
4. Quest counts the drop
5. Repeat infinitely for instant quest completion

**Root Cause:**
In `BlockDropTaskListener.onTaskBlockDrop()` (line 94), the code used:
```java
if (!isAgeable && !Config.ANTI_ABUSE_COUNT_PLAYER_BLOCKS.get() && this.manager.isPlayerBlock(block)) {
    return; // Skip player-placed blocks
}
```

The condition `!isAgeable` meant that Ageable blocks were NEVER checked for player-placed status.

**Fix Applied:**
Added a growth check for Ageable blocks:
```java
// For Ageable blocks (crops, bamboo, etc.), only count if they're fully grown
if (isAgeable) {
    Ageable ageable = (Ageable) event.getBlockState().getBlockData();
    int age = ageable.getAge();
    int maxAge = ageable.getMaximumAge();
    
    // Only count fully grown Ageable blocks to prevent place-break spam abuse
    if (age < maxAge) {
        return; // Skip immature crops
    }
}
```

**How It Works:**
1. Checks if the block is Ageable (implements the Ageable interface)
2. Gets the current age and maximum age of the block
3. Only allows progress if the block is fully grown (age == maxAge)
4. Blocks that aren't fully grown are skipped, preventing spam abuse

**Impact:**
- Players can no longer exploit the system by spam placing/breaking crops
- Legitimate farming of mature crops still works normally
- Applies to all Ageable blocks: wheat, carrots, potatoes, beetroot, sugar_cane, bamboo, etc.

---

## Affected Block Types

The Ageable block check applies to all blocks that implement the `Ageable` interface, including:
- **Crops:** Wheat, Carrots, Potatoes, Beetroot, Nether Wart
- **Plants:** Sugar Cane, Bamboo, Sweet Berry Bush
- **Stems:** Pumpkin Stem, Melon Stem
- **Other:** Cocoa, Cave Vines

## Configuration

These fixes work in conjunction with the existing anti-abuse configuration:

```yaml
# Config option for non-Ageable blocks
ANTI_ABUSE_COUNT_PLAYER_BLOCKS: true/false
```

- When `false`: Player-placed non-Ageable blocks are NOT counted
- When `true`: All blocks (except immature Ageable blocks) are counted

**Note:** Ageable blocks now have their own anti-abuse mechanism (growth check) regardless of this config setting.

---

## Testing Recommendations

### Test Case 1: Normal Crop Farming
1. Plant wheat/carrots/potatoes
2. Wait for crops to fully grow
3. Harvest mature crops
4. **Expected:** Quest progress should increase ✅

### Test Case 2: Immature Crop Breaking
1. Plant sugar_cane or wheat
2. Immediately break before fully grown
3. **Expected:** Quest progress should NOT increase ✅

### Test Case 3: Spam Farming Prevention
1. Rapidly place and break sugar_cane
2. **Expected:** No quest progress ✅

### Test Case 4: Quest Progress Cap
1. Have a quest requiring 10 items
2. Complete 8 items
3. Get 5 items in one event
4. **Expected:** Progress should cap at 10, not 13 ✅

---

## Migration Notes

**Existing Players:**
- No data migration required
- Fixes apply immediately upon server restart
- Existing quest progress is unaffected

**Server Admins:**
- Review quest configurations for block_loot quests
- Consider enabling debug logging temporarily: `GENERAL_DEBUG_BLOCK_LOOT: true`
- Monitor for any unexpected behavior with crop-based quests

---

## Debug Logging

Enable detailed block_loot debugging in the config:

```yaml
GENERAL_DEBUG_BLOCK_LOOT: true
```

This will log:
- When Ageable blocks are harvested
- Age checks for crops
- Whether blocks are skipped due to anti-abuse
- Quest/milestone progression events

Example debug output:
```
[BlockLoot Debug] Ageable block age: 2/7
[BlockLoot Debug] Skipping non-fully-grown Ageable block (anti-abuse)
```

---

## Related Files Changed

1. **QuestManager.java** - Fixed quest progress calculation
2. **BlockDropTaskListener.java** - Added Ageable block age verification

## See Also

- [Anti-Abuse Configuration Guide](https://nightexpressdev.com/excellentquests/configuration/)
- [Quest Types Documentation](https://nightexpressdev.com/excellentquests/quests/)
- [Milestone Types Documentation](https://nightexpressdev.com/excellentquests/milestones/)
