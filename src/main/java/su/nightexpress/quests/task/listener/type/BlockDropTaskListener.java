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

import java.util.Collection;
import java.util.UUID;

public class BlockDropTaskListener extends TaskListener<ItemStack, AdapterFamily<ItemStack>> {

    private static final String BLOCK_LOOT_PROCESSED_KEY = "excellent_quests_processed";
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
        // Check if this is an Ageable block (crops, etc.)
        // Use getBlockState() to get the block data BEFORE it was broken (event.getBlock() returns AIR after breaking)
        boolean isAgeable = event.getBlockState().getBlockData() instanceof Ageable;
        
        if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
            this.plugin.info("[BlockLoot Debug] BlockDropItemEvent triggered for player " + player.getName() + ", block: " + event.getBlockState().getType() + ", isAgeable: " + isAgeable + ", items: " + event.getItems().size());
        }
        
        // For Ageable blocks, only count if they're fully grown to prevent spam farming exploit
        if (isAgeable) {
            Ageable ageable = (Ageable) event.getBlockState().getBlockData();
            int age = ageable.getAge();
            int maxAge = ageable.getMaximumAge();
            
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] Ageable block age: " + age + "/" + maxAge);
            }
            
            // Only count fully grown crops to prevent place-break spam abuse
            if (age < maxAge) {
                if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                    this.plugin.info("[BlockLoot Debug] Skipping non-fully-grown Ageable block (anti-abuse)");
                }
                return;
            }
        }
        // Apply player-placed block check for non-Ageable blocks
        else if (!Config.ANTI_ABUSE_COUNT_PLAYER_BLOCKS.get() && this.manager.isPlayerBlock(block)) {
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] Skipping player-placed block (anti-abuse enabled)");
            }
            return;
        }

        // Process dropped items immediately for quest progression
        UUID playerUUID = player.getUniqueId();
        
        // For Ageable blocks (crops), if no items in the event, calculate what should have dropped
        if (isAgeable && event.getItems().isEmpty()) {
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] Ageable block with no items in event, calculating expected drops");
            }
            
            // Get the drops that would have been produced
            // Check both hands to find the tool used (prefer main hand if both have items)
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();
            
            // Select the tool to use for drop calculation (default to air if both hands are empty)
            ItemStack tool;
            if (mainHand != null && !mainHand.getType().isAir()) {
                tool = mainHand;
            } else if (offHand != null && !offHand.getType().isAir()) {
                tool = offHand;
            } else {
                tool = new ItemStack(Material.AIR);
            }
            
            // Use the original block state (before breaking) to get accurate drops
            // This method accounts for fortune, silk touch, and other enchantments
            Collection<ItemStack> drops = event.getBlockState().getDrops(tool);
            drops.forEach(itemStack -> {
                if (itemStack == null || itemStack.getType().isAir() || itemStack.getAmount() <= 0) {
                    if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                        this.plugin.info("[BlockLoot Debug] Skipping invalid calculated drop (null, air, or zero amount)");
                    }
                    return;
                }
                
                if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                    this.plugin.info("[BlockLoot Debug] Processing calculated drop: " + itemStack.getType() + " x" + itemStack.getAmount());
                }
                this.progressQuests(player, itemStack, itemStack.getAmount());
            });
        } else {
            // Normal processing for items in the event
            event.getItems().forEach(item -> {
                ItemStack itemStack = item.getItemStack();
                if (itemStack == null || itemStack.getType().isAir() || itemStack.getAmount() <= 0) {
                    if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                        this.plugin.info("[BlockLoot Debug] Skipping invalid item (null, air, or zero amount)");
                    }
                    return;
                }
                
                // Progress quests immediately for the dropped items
                if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                    this.plugin.info("[BlockLoot Debug] Processing dropped item: " + itemStack.getType() + " x" + itemStack.getAmount() + " from player " + player.getName());
                }
                this.progressQuests(player, itemStack, itemStack.getAmount());
                
                // Mark item as processed so pickup event doesn't double-count
                item.getPersistentDataContainer().set(this.blockLootPlayerKey, PersistentDataType.STRING, BLOCK_LOOT_PROCESSED_KEY);
            });
        }
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
        
        // Check if this item was already processed in BlockDropItemEvent
        String processed = itemEntity.getPersistentDataContainer().get(this.blockLootPlayerKey, PersistentDataType.STRING);
        if (BLOCK_LOOT_PROCESSED_KEY.equals(processed)) {
            // Item was already counted in BlockDropItemEvent, skip
            if (Config.GENERAL_DEBUG_BLOCK_LOOT.get()) {
                this.plugin.info("[BlockLoot Debug] EntityPickupItemEvent: Item already processed in BlockDropItemEvent");
            }
            return;
        }
        
        // Items without the processed marker are not from block breaks tracked by this plugin
        // (e.g., items dropped by players, mob drops, or from other sources)
        // We don't count these for block_loot progression
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
