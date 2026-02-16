# Block Loot - Ore Blocks Support Guide

## Overview

The `block_loot` task type **fully supports** tracking items obtained from ore blocks, including diamond ore and all other ores in Minecraft.

## How Ore Blocks Work with Block Loot

### Basic Concept

When you mine an ore block in Minecraft:
- **Ore Block** (e.g., `diamond_ore`) → **Resource Item** (e.g., `diamond`)
- The `block_loot` task type tracks the **resource items that drop**, not the ore blocks themselves

### Example: Diamond Ore

```yaml
objectives:
  diamonds:
    type: block_loot
    item: diamond  # Track the diamond ITEM, not diamond_ore
    amount: 10
```

**What happens:**
1. Player mines `diamond_ore` or `deepslate_diamond_ore`
2. Diamond items drop from the ore
3. Quest/milestone progress increases by the number of diamonds dropped
4. Works with both regular and deepslate variants automatically

---

## Supported Ore Types

The block_loot system works with **all Minecraft ore blocks**:

| Ore Block | Drops | Quest Item Name |
|-----------|-------|----------------|
| `coal_ore`, `deepslate_coal_ore` | Coal | `coal` |
| `iron_ore`, `deepslate_iron_ore` | Raw Iron | `raw_iron` |
| `copper_ore`, `deepslate_copper_ore` | Raw Copper | `raw_copper` |
| `gold_ore`, `deepslate_gold_ore` | Raw Gold | `raw_gold` |
| `diamond_ore`, `deepslate_diamond_ore` | Diamond | `diamond` |
| `emerald_ore`, `deepslate_emerald_ore` | Emerald | `emerald` |
| `lapis_ore`, `deepslate_lapis_ore` | Lapis Lazuli | `lapis_lazuli` |
| `redstone_ore`, `deepslate_redstone_ore` | Redstone | `redstone` |
| `nether_quartz_ore` | Nether Quartz | `quartz` |
| `nether_gold_ore` | Gold Nugget | `gold_nugget` |
| `ancient_debris` | Ancient Debris | `ancient_debris` * |

\* Ancient debris requires silk touch or drops itself

---

## Fortune Enchantment Support

✅ **Fully Supported**

The block_loot system correctly handles Fortune enchantments:

### Example: Mining with Fortune III

```yaml
objectives:
  diamonds:
    type: block_loot
    item: diamond
    amount: 64
```

**Behavior:**
- Mine 1 diamond ore with Fortune III
- Could drop 1-4 diamonds (random based on Fortune level)
- Quest progress increases by the actual number dropped (1-4)
- All diamonds are counted correctly

### How It Works

The plugin uses Bukkit's `BlockState.getDrops(tool)` method which:
1. Checks the tool's enchantments (including Fortune)
2. Calculates the correct number of drops
3. Returns the actual items that would drop
4. All drops are counted toward quest/milestone progress

---

## Silk Touch Behavior

⚠️ **Important: Different Behavior**

When mining ore blocks with Silk Touch:

### Without Silk Touch (Normal)
```yaml
objectives:
  diamonds:
    type: block_loot
    item: diamond  # ✅ Counts diamond items
    amount: 10
```
- Mining diamond_ore drops diamonds
- Quest progresses normally

### With Silk Touch (Special)
```yaml
objectives:
  diamond_ore_blocks:
    type: block_loot
    item: diamond_ore  # ✅ Counts ore BLOCKS
    amount: 10
```
- Mining diamond_ore drops diamond_ore block (with silk touch)
- Quest tracking diamond items will NOT progress
- Quest tracking diamond_ore blocks WILL progress

**Key Point:** Configure your quest based on what you want players to collect:
- Want diamonds? Use `item: diamond` (players must NOT use silk touch)
- Want ore blocks? Use `item: diamond_ore` (players MUST use silk touch)

---

## Configuration Examples

### Example 1: Basic Diamond Collection

```yaml
diamond_miner:
  type: block_loot
  objectives:
    diamonds:
      item: diamond
      amount: 64
  rewards:
    - type: command
      commands:
        - "give %player% diamond_pickaxe 1"
```

**Result:** Player needs to collect 64 diamonds from mining diamond ore (normal mining, no silk touch)

---

### Example 2: Mixed Ore Collection

```yaml
ore_collector:
  type: block_loot
  objectives:
    coal:
      item: coal
      amount: 128
    iron:
      item: raw_iron
      amount: 64
    gold:
      item: raw_gold
      amount: 32
    diamonds:
      item: diamond
      amount: 16
```

**Result:** Player must collect specified amounts of each resource from mining their respective ores

---

### Example 3: Silk Touch Ore Collection

```yaml
ore_block_collector:
  type: block_loot
  objectives:
    diamond_ores:
      item: diamond_ore
      amount: 10
    deepslate_diamond_ores:
      item: deepslate_diamond_ore
      amount: 10
```

**Result:** Player must use silk touch to collect actual ore blocks (not the resources)

---

### Example 4: Fortune-Boosted Mining

```yaml
fortune_miner:
  type: block_loot
  objectives:
    redstone:
      item: redstone
      amount: 500  # Easier with Fortune III
    lapis:
      item: lapis_lazuli
      amount: 200
```

**Result:** Players with Fortune enchantments will complete this quest faster (getting 4-5 redstone per ore instead of 1)

---

## Anti-Abuse Protection

### Player-Placed Ore Blocks

The plugin has built-in protection against ore block farming:

```yaml
# In config.yml
ANTI_ABUSE_COUNT_PLAYER_BLOCKS: false
```

**When set to `false`:**
- ✅ Natural ore blocks count toward progress
- ❌ Player-placed ore blocks do NOT count
- Prevents players from placing and breaking ore blocks repeatedly

**When set to `true`:**
- ✅ All ore blocks count (natural and player-placed)
- ⚠️ Players could potentially farm by placing/breaking ore blocks

**Recommendation:** Keep set to `false` for ore-based quests to prevent abuse.

---

## Nether Ores

All nether ores work the same way:

### Nether Gold Ore
```yaml
nether_gold:
  type: block_loot
  objectives:
    gold_nuggets:
      item: gold_nugget  # Drops nuggets, not raw gold
      amount: 100
```

### Nether Quartz Ore
```yaml
quartz_mining:
  type: block_loot
  objectives:
    quartz:
      item: quartz
      amount: 200
```

### Ancient Debris
```yaml
ancient_debris_collector:
  type: block_loot
  objectives:
    debris:
      item: ancient_debris
      amount: 16
```

**Note:** Ancient debris requires silk touch OR drops itself (same item as block)

---

## Deepslate Ore Variants

All deepslate ore variants work identically to their stone counterparts:

```yaml
deepslate_mining:
  type: block_loot
  objectives:
    # Both regular and deepslate ores count toward the same objective
    diamonds:
      item: diamond  # Works for BOTH diamond_ore AND deepslate_diamond_ore
      amount: 32
```

**Key Point:** You don't need separate objectives for regular vs deepslate ores - they both drop the same items!

---

## Debug Mode

To troubleshoot ore block tracking:

```yaml
# In config.yml
GENERAL_DEBUG_BLOCK_LOOT: true
```

**Debug Output Example:**
```
[BlockLoot Debug] BlockDropItemEvent triggered for player Steve, block: DIAMOND_ORE, isAgeable: false, items: 1
[BlockLoot Debug] Processing dropped item: DIAMOND x3 from player Steve
[BlockLoot Debug] TaskManager.progressQuests called for player Steve, taskType: block_loot, amount: 3
[BlockLoot Debug] Full name resolved: minecraft:diamond
```

---

## Common Issues & Solutions

### Issue 1: "My diamond ore quest isn't working!"

**Check:**
1. Item name is `diamond`, not `diamond_ore`
2. Player is NOT using silk touch
3. Anti-abuse setting allows natural ore blocks

**Solution:**
```yaml
objectives:
  diamonds:
    item: diamond  # NOT diamond_ore
    amount: 10
```

---

### Issue 2: "Progress not counting with Fortune III"

**This is actually working correctly!** Fortune just gives you MORE items per ore:
- Without Fortune: 1 diamond ore = 1 diamond = +1 progress
- With Fortune III: 1 diamond ore = 2-4 diamonds = +2 to +4 progress

**Expected behavior:** Fortune HELPS complete quests faster.

---

### Issue 3: "Silk Touch quest not working"

**Check:**
1. Item name is the ORE BLOCK name (e.g., `diamond_ore`)
2. Player IS using silk touch
3. Quest accepts block items, not just resources

**Solution:**
```yaml
objectives:
  ore_blocks:
    item: diamond_ore  # The ore block itself
    amount: 10
```

---

## Performance Considerations

✅ **No performance impact from ore tracking**

The block_loot system:
- Only processes blocks that actually drop items
- Uses efficient event listeners
- Doesn't require any special ore-to-item mapping
- Leverages Bukkit's built-in drop calculation

---

## See Also

- [BLOCK_LOOT_FIXES.md](BLOCK_LOOT_FIXES.md) - Recent bug fixes
- [Anti-Abuse Configuration](https://nightexpressdev.com/excellentquests/configuration/)
- [Task Types Guide](https://nightexpressdev.com/excellentquests/tasks/)
