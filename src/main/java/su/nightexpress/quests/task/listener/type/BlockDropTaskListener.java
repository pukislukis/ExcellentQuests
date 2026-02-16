package su.nightexpress.quests.task.listener.type;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.quests.QuestsPlugin;
import su.nightexpress.quests.config.Config;
import su.nightexpress.quests.task.adapter.AdapterFamily;
import su.nightexpress.quests.task.TaskManager;
import su.nightexpress.quests.task.TaskType;
import su.nightexpress.quests.task.listener.TaskListener;

import java.util.UUID;

public class BlockDropTaskListener extends TaskListener<ItemStack, AdapterFamily<ItemStack>> {

    private final NamespacedKey blockLootPlayerKey;

    public BlockDropTaskListener(@NotNull QuestsPlugin plugin, @NotNull TaskManager manager, @NotNull TaskType<ItemStack, AdapterFamily<ItemStack>> taskType) {
        super(plugin, manager, taskType);
        this.blockLootPlayerKey = new NamespacedKey(plugin, "block_loot_player");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskBlockHarvest(PlayerHarvestBlockEvent event) {
        Player player = event.getPlayer();
        if (!this.manager.canDoTasks(player)) {
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] PlayerHarvestBlockEvent: Player " + player.getName() + " cannot do tasks");
            }
            return;
        }

        if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
            this.plugin.info("[BlockLoot Debug] PlayerHarvestBlockEvent triggered for player " + player.getName() + ", items harvested: " + event.getItemsHarvested().size());
        }
        
        event.getItemsHarvested().forEach(itemStack -> {
            if (itemStack == null || itemStack.getType().isAir() || itemStack.getAmount() <= 0) {
                if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                    this.plugin.info("[BlockLoot Debug] Skipping invalid item (null, air, or zero amount)");
                }
                return;
            }
            
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] Processing harvested item: " + itemStack.getType() + " x" + itemStack.getAmount());
            }
            this.progressQuests(player, itemStack, itemStack.getAmount());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTaskBlockDrop(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        if (!this.manager.canDoTasks(player)) {
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] BlockDropItemEvent: Player " + player.getName() + " cannot do tasks");
            }
            return;
        }
        if (event.getBlockState() instanceof Container) {
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] BlockDropItemEvent: Skipping container block");
            }
            return; // Do not handle container's drops.
        }

        Block block = event.getBlock();
        // Skip anti-abuse check for Ageable blocks (crops, etc.) since they are meant to be planted and harvested by players
        boolean isAgeable = block.getBlockData() instanceof Ageable;
        if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
            this.plugin.info("[BlockLoot Debug] BlockDropItemEvent triggered for player " + player.getName() + ", block: " + block.getType() + ", isAgeable: " + isAgeable + ", items: " + event.getItems().size());
        }
        
        if (!isAgeable && !Config.ANTI_ABUSE_COUNT_PLAYER_BLOCKS.get() && this.manager.isPlayerBlock(block)) {
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] Skipping player-placed block (anti-abuse enabled)");
            }
            return;
        }

        // Mark dropped items with player UUID so we can track them when picked up
        UUID playerUUID = player.getUniqueId();
        event.getItems().forEach(item -> {
            ItemStack itemStack = item.getItemStack();
            if (itemStack == null || itemStack.getType().isAir() || itemStack.getAmount() <= 0) {
                if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                    this.plugin.info("[BlockLoot Debug] Skipping invalid item (null, air, or zero amount)");
                }
                return;
            }
            
            // Mark the Item entity with the player's UUID who broke the block
            item.getPersistentDataContainer().set(this.blockLootPlayerKey, PersistentDataType.STRING, playerUUID.toString());
            
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] Marked dropped item: " + itemStack.getType() + " x" + itemStack.getAmount() + " from player " + player.getName());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTaskItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!this.manager.canDoTasks(player)) {
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] EntityPickupItemEvent: Player " + player.getName() + " cannot do tasks");
            }
            return;
        }

        Item itemEntity = event.getItem();
        
        // Check if this item was marked as block loot
        String markedPlayerUUID = itemEntity.getPersistentDataContainer().get(this.blockLootPlayerKey, PersistentDataType.STRING);
        if (markedPlayerUUID == null) {
            // Item was not from block loot, ignore
            return;
        }
        
        // Check if the player picking up is the one who broke the block
        UUID markedUUID;
        try {
            markedUUID = UUID.fromString(markedPlayerUUID);
        } catch (IllegalArgumentException e) {
            // Invalid UUID stored, ignore
            return;
        }
        
        if (!player.getUniqueId().equals(markedUUID)) {
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] EntityPickupItemEvent: Player " + player.getName() + " picked up item but didn't break the block");
            }
            return;
        }
        
        ItemStack itemStack = itemEntity.getItemStack();
        if (itemStack == null || itemStack.getType().isAir() || itemStack.getAmount() <= 0) {
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] EntityPickupItemEvent: Invalid item stack");
            }
            return;
        }
        
        if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
            this.plugin.info("[BlockLoot Debug] EntityPickupItemEvent: Player " + player.getName() + " picked up block loot: " + itemStack.getType() + " x" + itemStack.getAmount());
        }
        this.progressQuests(player, itemStack, itemStack.getAmount());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTaskHoneyCollect(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        if (event.useInteractedBlock() == Event.Result.DENY) return;

        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.BEEHIVE) return;

        EquipmentSlot slot = event.getHand();
        if (slot == null) return;

        Player player = event.getPlayer();
        if (!this.manager.canDoTasks(player)) return;

        ItemStack itemStack = player.getInventory().getItem(slot);
        if (itemStack == null || itemStack.getType() != Material.GLASS_BOTTLE) return;

        this.plugin.runTask(task -> {
            ItemStack honey = player.getInventory().getItem(slot);
            if (honey == null || honey.getType() != Material.HONEY_BOTTLE) return;

            //this.giveXP(player, (job, table) -> table.getBlockResourceXP(honey));
        });
    }
}
