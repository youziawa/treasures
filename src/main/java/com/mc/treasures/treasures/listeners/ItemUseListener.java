package com.mc.treasures.treasures.listeners;

import com.mc.treasures.treasures.Treasures;
import com.mc.treasures.treasures.effects.ItemEffectManager;
import com.mc.treasures.treasures.effects.ItemEffects;
import com.mc.treasures.treasures.special.SpecialItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * 物品使用监听器
 */
public class ItemUseListener implements Listener {

    private final Treasures plugin;
    private final ItemEffectManager effectManager;
    
    public ItemUseListener(Treasures plugin) {
        this.plugin = plugin;
        this.effectManager = plugin.getItemEffectManager();
    }
    
    /**
     * 监听玩家交互事件
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 检查是否点击了方块
        if (event.getClickedBlock() == null) {
            return;
        }
        
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        
        // 检查是否是特殊物品
        Optional<String> itemIdOpt = SpecialItem.getSpecialItemId(item);
        if (itemIdOpt.isEmpty()) {
            return;
        }
        
        String itemId = itemIdOpt.get();
        
        // 检查动作
        Action action = event.getAction();
        
        // 右键使用物品
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            handleRightClick(event.getPlayer(), item, itemId, event.getClickedBlock());
        }
        // 左键点击方块（航点传送）
        else if (action == Action.LEFT_CLICK_BLOCK) {
            handleLeftClick(event.getPlayer(), item, itemId);
        }
    }
    
    /**
     * 处理右键使用物品
     */
    private void handleRightClick(Player player, ItemStack item, String itemId, Block clickedBlock) {
        switch (itemId) {
            case "waypoint_book" -> {
                // 设置航点
                Location location = player.getLocation();
                effectManager.setWaypoint(player, location);
                player.sendMessage(plugin.getConfigManager().getMessage("effect-applied", 
                    "{item}", "航点已设置在当前位置"));
            }
            
            case "chest_lock" -> {
                // 上锁储物箱
                if (clickedBlock != null && isContainer(clickedBlock.getType())) {
                    player.sendMessage(plugin.getConfigManager().getMessage("effect-applied",
                        "{item}", "储物箱已上锁"));
                    // 减少物品数量
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().remove(item);
                    }
                }
            }
            
            default -> {
                // 其他物品应用效果
                ItemEffects effect = ItemEffects.fromItemId(itemId);
                if (effect != null) {
                    effectManager.applyEffect(player, itemId);
                    player.sendMessage(plugin.getConfigManager().getMessage("effect-applied",
                        "{item}", item.getItemMeta().getDisplayName()));
                }
            }
        }
    }
    
    /**
     * 处理左键点击物品
     */
    private void handleLeftClick(Player player, ItemStack item, String itemId) {
        if (itemId.equals("waypoint_book")) {
            // 传送到航点
            if (effectManager.hasWaypoint(player)) {
                Location waypoint = effectManager.getWaypoint(player);
                player.teleport(waypoint);
                player.sendMessage(plugin.getConfigManager().getMessage("effect-applied",
                    "{item}", "已传送到航点"));
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("effect-cooldown",
                    "{time}", "0"));
            }
        }
    }
    
    /**
     * 监听物品切换事件（用于刷新显示效果）
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeldChanged(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        
        // 检查新物品是否是特殊物品
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (newItem != null) {
            Optional<String> itemIdOpt = SpecialItem.getSpecialItemId(newItem);
            itemIdOpt.ifPresent(itemId -> {
                ItemEffects effect = ItemEffects.fromItemId(itemId);
                if (effect != null && effect.isPassive()) {
                    effectManager.applyEffect(player, itemId);
                }
            });
        }
    }
    
    /**
     * 监听方块放置事件（便携火把、音乐盒）
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        
        // 检查是否是特殊物品
        Optional<String> itemIdOpt = SpecialItem.getSpecialItemId(item);
        if (itemIdOpt.isEmpty()) {
            return;
        }
        
        String itemId = itemIdOpt.get();
        
        switch (itemId) {
            case "portable_torch" -> {
                // 放置临时光源
                // 延迟5分钟后删除
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    Block block = event.getBlockPlaced();
                    if (block.getType() == Material.TORCH) {
                        block.setType(Material.AIR);
                        event.getPlayer().sendMessage(plugin.getConfigManager().getMessage("effect-expired"));
                    }
                }, 6000L); // 5分钟 = 6000 ticks
            }
            
            case "music_box" -> {
                // 放置音乐盒
                event.getPlayer().sendMessage(plugin.getConfigManager().getMessage("effect-applied",
                    "{item}", "音乐盒正在播放音乐"));
            }
        }
    }
    
    /**
     * 检查方块是否是容器
     */
    private boolean isContainer(Material material) {
        return material == Material.CHEST || 
               material == Material.TRAPPED_CHEST ||
               material == Material.BARREL ||
               material == Material.SHULKER_BOX;
    }
}
