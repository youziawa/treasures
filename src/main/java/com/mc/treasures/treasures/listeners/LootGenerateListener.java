package com.mc.treasures.treasures.listeners;

import com.mc.treasures.treasures.Treasures;
import com.mc.treasures.treasures.loot.LootTableConfig;
import com.mc.treasures.treasures.loot.LootTableInjector;
import com.mc.treasures.treasures.special.SpecialItem;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * 战利品生成监听器
 * 监听战利品生成事件并注入特殊物品
 */
public class LootGenerateListener implements Listener {

    private final Treasures plugin;
    private final LootTableConfig lootTableConfig;
    
    public LootGenerateListener(Treasures plugin) {
        this.plugin = plugin;
        this.lootTableConfig = new LootTableConfig(plugin);
    }
    
    /**
     * 监听战利品生成事件
     * 这是Paper API提供的LootGenerateEvent事件
     */
    @EventHandler(priority = org.bukkit.event.EventPriority.HIGH)
    public void onLootGenerate(LootGenerateEvent event) {
        // 检查世界是否启用
        World world = event.getWorld();
        if (!plugin.getConfigManager().isWorldEnabled(world.getName())) {
            return;
        }
        
        // 获取战利品表路径
        String lootTablePath = event.getLootTable().getKey().asString();
        
        // 匹配结构类型
        String structure = matchStructure(lootTablePath);
        if (structure == null) {
            return;
        }
        
        // 检查结构是否启用
        if (!lootTableConfig.isStructureEnabled(structure)) {
            return;
        }
        
        // 生成特殊物品
        List<ItemStack> specialItems = generateSpecialItems(structure);
        
        // 添加到战利品
        for (ItemStack item : specialItems) {
            event.getLoot().add(item);
            
            // 向最近的玩家发送消息
            sendItemFoundMessage(world, item);
        }
        
        plugin.logInfo("已向战利品表 " + lootTablePath + " 添加 " + specialItems.size() + " 个特殊物品");
    }
    
    /**
     * 生成特殊物品列表
     */
    private List<ItemStack> generateSpecialItems(String structure) {
        return plugin.getLootTableInjector().generateSpecialItems(structure);
    }
    
    /**
     * 匹配战利品表路径到结构类型
     */
    private String matchStructure(String lootTablePath) {
        // 移除 "minecraft:" 前缀
        String path = lootTablePath.replace("minecraft:", "");
        
        // 匹配结构类型
        if (path.contains("village")) {
            return "village";
        } else if (path.contains("desert_pyramid")) {
            return "desert_temple";
        } else if (path.contains("jungle_temple")) {
            return "jungle_temple";
        } else if (path.contains("shipwreck")) {
            return "shipwreck";
        } else if (path.contains("abandoned_mineshaft")) {
            return "abandoned_mineshaft";
        } else if (path.contains("stronghold")) {
            return "stronghold";
        } else if (path.contains("witch_hut")) {
            return "witch_hut";
        } else if (path.contains("igloo")) {
            return "igloo";
        } else if (path.contains("monument")) {
            return "ocean_monument";
        } else if (path.contains("bastion")) {
            return "bastion_remnant";
        } else if (path.contains("ancient_city")) {
            return "ancient_city";
        }
        
        return null;
    }
    
    /**
     * 发送物品发现消息
     */
    private void sendItemFoundMessage(World world, ItemStack item) {
        // 查找最近的玩家
        Player nearestPlayer = findNearestPlayer(world);
        
        if (nearestPlayer != null) {
            String itemName = item.getItemMeta().getDisplayName();
            String message = plugin.getConfigManager().getMessage("item-found", "{item}", itemName);
            nearestPlayer.sendMessage(message);
        }
    }
    
    /**
     * 查找最近开启战利品的玩家
     */
    private Player findNearestPlayer(World world) {
        Player[] players = world.getPlayers().toArray(new Player[0]);
        if (players.length > 0) {
            return players[0];
        }
        return null;
    }
}
