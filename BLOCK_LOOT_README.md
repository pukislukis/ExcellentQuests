# Block Loot Task Type - Complete Documentation

## Quick Answer (Jawaban Cepat)

**Pertanyaan:** "Apakah block_loot bisa digunakan untuk diamond dari diamond_ore/deepslate_diamond_ore?"

**Jawaban:** **YA!** ✅ Block_loot sudah mendukung penuh untuk diamond ore dan semua ore blocks lainnya!

**Question:** "Can block_loot be used for diamonds from diamond_ore/deepslate_diamond_ore?"

**Answer:** **YES!** ✅ Block_loot fully supports diamond ore and all other ore blocks!

---

## Quick Start

```yaml
# Example: Diamond Mining Quest
diamond_miner:
  type: block_loot
  objectives:
    diamonds:
      item: diamond  # ← Use the ITEM that drops, not diamond_ore
      amount: 64
```

**Result:** Players collect 64 diamonds by mining diamond_ore or deepslate_diamond_ore blocks.

---

## Documentation Files

### 1. [ORE_QUEST_EXAMPLES.md](ORE_QUEST_EXAMPLES.md) - **START HERE!**
Ready-to-use quest configurations:
- ✅ Diamond mining quest
- ✅ All ores collection quest
- ✅ Fortune miner quest (for Fortune III users)
- ✅ Nether ores quest
- ✅ Silk touch collector quest
- ✅ Progressive mining milestone
- ✅ Daily mining quest
- ✅ Common item names reference table

**Best for:** Server admins who want to quickly create ore mining quests.

---

### 2. [ORE_BLOCKS_GUIDE.md](ORE_BLOCKS_GUIDE.md) - **Technical Details**
Complete technical documentation:
- ✅ How ore blocks work with block_loot
- ✅ All supported ore types (with table)
- ✅ Fortune enchantment behavior
- ✅ Silk Touch behavior
- ✅ Deepslate ore variants
- ✅ Nether ores specifics
- ✅ Anti-abuse protection
- ✅ Debug mode usage
- ✅ Common issues & solutions

**Best for:** Understanding how the system works and troubleshooting.

---

### 3. [BLOCK_LOOT_FIXES.md](BLOCK_LOOT_FIXES.md) - **Bug Fixes**
Recent bug fixes applied:
- ✅ Quest progress over-counting bug (fixed)
- ✅ Ageable block spam abuse (fixed)
- ✅ Migration notes
- ✅ Testing recommendations

**Best for:** Understanding what bugs were fixed and why.

---

## What Works (Yang Berfungsi)

### ✅ All Vanilla Ores
- Coal Ore → Coal
- Iron Ore → Raw Iron
- Copper Ore → Raw Copper
- Gold Ore → Raw Gold
- **Diamond Ore → Diamond** ← **YOUR QUESTION!**
- Emerald Ore → Emerald
- Lapis Ore → Lapis Lazuli
- Redstone Ore → Redstone
- Nether Quartz Ore → Quartz
- Nether Gold Ore → Gold Nugget
- Ancient Debris → Ancient Debris

### ✅ Deepslate Variants
All deepslate ores work identically:
- `diamond_ore` → diamond
- `deepslate_diamond_ore` → diamond

**Both count toward the same quest objective!**

### ✅ Fortune Enchantment
Mining with Fortune gives more items:
- Fortune I: 0-2x multiplier
- Fortune II: 0-3x multiplier  
- Fortune III: 0-4x multiplier

**All drops count toward quest progress!**

### ✅ Silk Touch
With Silk Touch, ore blocks drop themselves:
- Mine with Silk Touch → Get ore BLOCK
- Configure quest with `item: diamond_ore` to track blocks
- Configure quest with `item: diamond` to track resource items

---

## Basic Configuration (Konfigurasi Dasar)

### For Resource Items (Untuk Item Resource)
```yaml
objectives:
  diamonds:
    item: diamond  # NOT diamond_ore
    amount: 64
```
Players mine diamond ore and collect diamond items (no silk touch).

### For Ore Blocks (Untuk Block Ore)
```yaml
objectives:
  ore_blocks:
    item: diamond_ore  # The ore block itself
    amount: 10
```
Players use silk touch to collect ore blocks.

---

## Anti-Abuse Settings (Pengaturan Anti-Abuse)

```yaml
# In config.yml
ANTI_ABUSE_COUNT_PLAYER_BLOCKS: false  # Recommended
```

**When `false` (Recommended):**
- ✅ Natural ores count
- ❌ Player-placed ores DON'T count
- Prevents farming by placing/breaking ores

**When `true`:**
- ✅ All ores count (natural and player-placed)
- ⚠️ Players could farm by placing/breaking ores

---

## Fortune Behavior Example

**Quest Configuration:**
```yaml
diamonds:
  item: diamond
  amount: 64
```

**Player Actions:**
1. Mines 1 diamond ore without Fortune → Gets 1 diamond → Progress: 1/64
2. Mines 1 diamond ore with Fortune III → Gets 3 diamonds → Progress: 4/64
3. Mines 1 diamond ore with Fortune III → Gets 4 diamonds → Progress: 8/64

**Result:** Fortune helps complete quests faster! This is intentional and correct.

---

## Common Mistakes (Kesalahan Umum)

### ❌ Wrong: Using ore block name
```yaml
objectives:
  diamonds:
    item: diamond_ore  # WRONG - this is the ORE BLOCK
    amount: 64
```
**Problem:** Only counts if player uses silk touch (gets ore blocks, not diamonds).

### ✅ Correct: Using resource item name
```yaml
objectives:
  diamonds:
    item: diamond  # CORRECT - this is the RESOURCE ITEM
    amount: 64
```
**Result:** Counts diamonds from mining ore (normal behavior).

---

## Debug Mode (Mode Debug)

Enable detailed logging:

```yaml
# In config.yml
GENERAL_DEBUG_BLOCK_LOOT: true
```

**Example Output:**
```
[BlockLoot Debug] BlockDropItemEvent triggered for player Steve, block: DIAMOND_ORE, isAgeable: false, items: 1
[BlockLoot Debug] Processing dropped item: DIAMOND x3 from player Steve
[BlockLoot Debug] Full name resolved: minecraft:diamond
```

---

## FAQ (Pertanyaan Umum)

### Q: Apakah diamond ore berfungsi? / Does diamond ore work?
**A: YA! / YES!** ✅ Sudah berfungsi dengan sempurna / Works perfectly!

### Q: Bagaimana dengan deepslate diamond ore? / What about deepslate diamond ore?
**A:** Sama saja! / Same! Both `diamond_ore` and `deepslate_diamond_ore` drop `diamond` items, so one quest objective tracks both.

### Q: Apakah Fortune III dihitung? / Is Fortune III counted?
**A: YA! / YES!** ✅ Semua drops dihitung / All drops are counted. Fortune III makes quests complete faster.

### Q: Bagaimana dengan Silk Touch? / What about Silk Touch?
**A:** Silk Touch drops the ore BLOCK, not the resource. Configure quest accordingly:
- For diamonds: `item: diamond` (no silk touch)
- For ore blocks: `item: diamond_ore` (with silk touch)

### Q: Apakah ada bug? / Are there bugs?
**A:** Sudah diperbaiki! / Already fixed! See [BLOCK_LOOT_FIXES.md](BLOCK_LOOT_FIXES.md) for details.

---

## Example Quests (Contoh Quest)

See [ORE_QUEST_EXAMPLES.md](ORE_QUEST_EXAMPLES.md) for:
- Basic diamond mining
- All ores collection
- Fortune miner (high requirements)
- Nether ores
- Silk touch ore blocks
- Progressive mining milestone
- Daily mining quest
- And more!

---

## Need Help? (Butuh Bantuan?)

1. **Check examples:** [ORE_QUEST_EXAMPLES.md](ORE_QUEST_EXAMPLES.md)
2. **Read guide:** [ORE_BLOCKS_GUIDE.md](ORE_BLOCKS_GUIDE.md)
3. **Enable debug mode:** Set `GENERAL_DEBUG_BLOCK_LOOT: true` in config
4. **Check console logs:** Look for `[BlockLoot Debug]` messages

---

## Summary (Ringkasan)

✅ **Diamond ore fully supported** (Diamond ore sepenuhnya didukung)
✅ **All ores work** (Semua ore berfungsi)
✅ **Fortune counted correctly** (Fortune dihitung dengan benar)
✅ **Deepslate variants work** (Varian deepslate berfungsi)
✅ **Anti-abuse protection** (Perlindungan anti-abuse)
✅ **No code changes needed** (Tidak perlu perubahan kode)

**The system already works perfectly for diamond ore and all other ores!**
**Sistem sudah berfungsi sempurna untuk diamond ore dan semua ore lainnya!**

---

## See Also

- [ExcellentQuests Documentation](https://nightexpressdev.com/excellentquests/)
- [SpigotMC Resource Page](https://spigotmc.org/resources/107283/)
- [Discord Support](https://discord.gg/EwNFGsnGaW)
