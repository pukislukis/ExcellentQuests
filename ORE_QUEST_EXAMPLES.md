# Quick Start: Ore Mining Quests

This file provides ready-to-use examples for creating ore mining quests.

## Basic Diamond Mining Quest

```yaml
# File: quests/diamond_mining.yml
diamond_miner:
  name: "&6&lDiamond Miner"
  description:
    - "&7Mine 64 diamonds to complete this quest"
  type: block_loot
  objectives:
    diamonds:
      item: diamond
      amount: 64
  rewards:
    - type: command
      commands:
        - "give %player% diamond_pickaxe{Enchantments:[{id:fortune,lvl:3}]} 1"
```

## All Ores Collection Quest

```yaml
# File: quests/master_miner.yml
master_miner:
  name: "&e&lMaster Miner"
  description:
    - "&7Collect all types of ore resources"
  type: block_loot
  objectives:
    coal:
      item: coal
      amount: 128
    raw_iron:
      item: raw_iron
      amount: 64
    raw_copper:
      item: raw_copper
      amount: 64
    raw_gold:
      item: raw_gold
      amount: 32
    diamonds:
      item: diamond
      amount: 16
    emeralds:
      item: emerald
      amount: 8
    lapis:
      item: lapis_lazuli
      amount: 64
    redstone:
      item: redstone
      amount: 128
  rewards:
    - type: command
      commands:
        - "give %player% diamond 64"
        - "eco give %player% 10000"
```

## Fortune Miner Quest (Encourages Fortune Enchantment)

```yaml
# File: quests/fortune_miner.yml
fortune_miner:
  name: "&b&lFortune Miner"
  description:
    - "&7Collect large amounts of resources"
    - "&7Fortune III recommended!"
  type: block_loot
  objectives:
    redstone:
      item: redstone
      amount: 500  # Much easier with Fortune III
    lapis:
      item: lapis_lazuli
      amount: 250
    coal:
      item: coal
      amount: 256
  rewards:
    - type: command
      commands:
        - "give %player% netherite_pickaxe{Enchantments:[{id:fortune,lvl:3},{id:efficiency,lvl:5},{id:unbreaking,lvl:3}]} 1"
```

## Nether Ores Quest

```yaml
# File: quests/nether_mining.yml
nether_miner:
  name: "&c&lNether Miner"
  description:
    - "&7Mine resources in the Nether"
  type: block_loot
  objectives:
    quartz:
      item: quartz
      amount: 128
    gold_nuggets:
      item: gold_nugget
      amount: 64
    ancient_debris:
      item: ancient_debris
      amount: 4
  rewards:
    - type: command
      commands:
        - "give %player% netherite_ingot 1"
```

## Deepslate Mining Quest

```yaml
# File: quests/deep_miner.yml
deep_miner:
  name: "&8&lDeep Miner"
  description:
    - "&7Mine ores from deep underground"
    - "&7Both regular and deepslate ores count!"
  type: block_loot
  objectives:
    diamonds:
      item: diamond  # Counts BOTH diamond_ore AND deepslate_diamond_ore
      amount: 32
    raw_iron:
      item: raw_iron  # Counts BOTH iron_ore AND deepslate_iron_ore
      amount: 64
    coal:
      item: coal  # Counts BOTH coal_ore AND deepslate_coal_ore
      amount: 128
  rewards:
    - type: command
      commands:
        - "give %player% diamond 16"
```

## Silk Touch Collector Quest

```yaml
# File: quests/ore_collector.yml
ore_collector:
  name: "&d&lOre Collector"
  description:
    - "&7Collect ore blocks using Silk Touch"
    - "&cRequires Silk Touch enchantment!"
  type: block_loot
  objectives:
    diamond_ore:
      item: diamond_ore
      amount: 10
    deepslate_diamond_ore:
      item: deepslate_diamond_ore
      amount: 10
    emerald_ore:
      item: emerald_ore
      amount: 5
    ancient_debris:
      item: ancient_debris
      amount: 4
  rewards:
    - type: command
      commands:
        - "give %player% diamond_pickaxe{Enchantments:[{id:silk_touch,lvl:1},{id:efficiency,lvl:5},{id:unbreaking,lvl:3}]} 1"
```

## Progressive Mining Milestone

```yaml
# File: milestones/mining_progression.yml
mining_progression:
  name: "&6Mining Progression"
  description:
    - "&7Level up your mining skills"
  type: block_loot
  category: mining
  levels: 5
  objectives:
    diamonds:
      item: diamond
      amounts:
        level_1: 16
        level_2: 32
        level_3: 64
        level_4: 128
        level_5: 256
  rewards:
    level_1:
      - type: command
        commands:
          - "give %player% diamond 8"
    level_2:
      - type: command
        commands:
          - "give %player% diamond 16"
    level_3:
      - type: command
        commands:
          - "give %player% diamond_pickaxe{Enchantments:[{id:fortune,lvl:1}]} 1"
    level_4:
      - type: command
        commands:
          - "give %player% diamond_pickaxe{Enchantments:[{id:fortune,lvl:2}]} 1"
    level_5:
      - type: command
        commands:
          - "give %player% diamond_pickaxe{Enchantments:[{id:fortune,lvl:3},{id:efficiency,lvl:5},{id:unbreaking,lvl:3}]} 1"
```

## Iron Age Quest

```yaml
# File: quests/iron_age.yml
iron_age:
  name: "&7&lIron Age"
  description:
    - "&7Collect raw iron from mining"
  type: block_loot
  objectives:
    raw_iron:
      item: raw_iron
      amount: 128
  rewards:
    - type: command
      commands:
        - "give %player% iron_ingot 64"
        - "eco give %player% 500"
```

## Copper Rush Quest

```yaml
# File: quests/copper_rush.yml
copper_rush:
  name: "&6&lCopper Rush"
  description:
    - "&7Mine copper ore"
  type: block_loot
  objectives:
    raw_copper:
      item: raw_copper
      amount: 256
  rewards:
    - type: command
      commands:
        - "give %player% copper_ingot 128"
```

## Daily Mining Quest

```yaml
# File: quests/daily_mining.yml
daily_mining:
  name: "&e&lDaily Mining"
  description:
    - "&7Complete your daily mining quota"
  type: block_loot
  completion_time: 86400  # 24 hours
  objectives:
    ores:
      item: 
        - coal
        - raw_iron
        - raw_copper
        - diamond
      amount: 64
  rewards:
    - type: command
      commands:
        - "eco give %player% 1000"
```

---

## Configuration Tips

### For Regular Mining (No Silk Touch)
```yaml
objectives:
  resource_name:
    item: diamond  # Use the RESOURCE item
    amount: 64
```

### For Silk Touch Mining
```yaml
objectives:
  ore_blocks:
    item: diamond_ore  # Use the ORE BLOCK item
    amount: 10
```

### For Fortune-Friendly Quests
Set higher amounts to reward Fortune users:
```yaml
objectives:
  redstone:
    item: redstone
    amount: 500  # Easy with Fortune III, challenging without
```

### For Multiple Ore Types
Both regular and deepslate variants drop the same items:
```yaml
objectives:
  all_diamonds:
    item: diamond  # Counts from BOTH diamond_ore AND deepslate_diamond_ore
    amount: 32
```

---

## Anti-Abuse Settings

In your `config.yml`:

```yaml
# Prevent players from farming player-placed ore blocks
ANTI_ABUSE_COUNT_PLAYER_BLOCKS: false  # Recommended for ore quests

# Debug mode for troubleshooting
GENERAL_DEBUG_BLOCK_LOOT: false  # Set to true if you need to debug
```

---

## Common Item Names

| Ore Block | Drops Item | Item Name for Quest |
|-----------|-----------|---------------------|
| Coal Ore | Coal | `coal` |
| Iron Ore | Raw Iron | `raw_iron` |
| Copper Ore | Raw Copper | `raw_copper` |
| Gold Ore | Raw Gold | `raw_gold` |
| Diamond Ore | Diamond | `diamond` |
| Emerald Ore | Emerald | `emerald` |
| Lapis Ore | Lapis Lazuli | `lapis_lazuli` |
| Redstone Ore | Redstone | `redstone` |
| Nether Quartz Ore | Nether Quartz | `quartz` |
| Nether Gold Ore | Gold Nugget | `gold_nugget` |
| Ancient Debris | Ancient Debris | `ancient_debris` |

**Note:** Deepslate variants drop the same items as their regular counterparts.

---

## See Also

- [ORE_BLOCKS_GUIDE.md](ORE_BLOCKS_GUIDE.md) - Complete guide
- [BLOCK_LOOT_FIXES.md](BLOCK_LOOT_FIXES.md) - Recent bug fixes
- [ExcellentQuests Documentation](https://nightexpressdev.com/excellentquests/)
