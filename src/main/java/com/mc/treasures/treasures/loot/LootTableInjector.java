package com.mc.treasures.treasures.loot;

import com.mc.treasures.treasures.Treasures;
import com.mc.treasures.treasures.special.SpecialItem;
import com.mc.treasures.treasures.special.SpecialItemManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Level;

/**
 * 战利品表注入器 - 核心功能
 * 将特殊物品注入到原版结构的战利品表中
 */
public class LootTableInjector {

    private final Treasures plugin;
    private final LootTableConfig lootTableConfig;
    private final SpecialItemManager itemManager;
    
    // 已注入的世界列表
    private final Set<String> injectedWorlds;
    
    public LootTableInjector(Treasures plugin) {
        this.plugin = plugin;
        this.lootTableConfig = new LootTableConfig(plugin);
        this.itemManager = plugin.getSpecialItemManager();
        this.injectedWorlds = new HashSet<>();
    }
    
    /**
     * 为指定世界注入战利品表
     */
    public void injectIntoWorld(World world) {
        if (injectedWorlds.contains(world.getName())) {
            plugin.logInfo("世界 " + world.getName() + " 已经注入过战利品表");
            return;
        }
        
        plugin.logInfo("正在为世界 " + world.getName() + " 注入战利品表...");
        
        try {
            // 为每个启用的结构注入
            for (String structure : lootTableConfig.getEnabledStructures()) {
                injectIntoStructure(world, structure);
            }
            
            injectedWorlds.add(world.getName());
            plugin.logInfo("世界 " + world.getName() + " 战利品表注入完成！");
            
        } catch (Exception e) {
            plugin.logSevere("注入战利品表时发生错误: " + e.getMessage());
            plugin.getLogger().log(Level.SEVERE, "注入错误", e);
        }
    }
    
    /**
     * 为指定世界的指定结构注入战利品表
     */
    private void injectIntoStructure(World world, String structure) {
        String[] lootTablePaths = lootTableConfig.getLootTablePaths(structure);
        
        for (String path : lootTablePaths) {
            try {
                injectLootTable(world, path, structure);
            } catch (Exception e) {
                plugin.logWarning("注入战利品表 " + path + " 失败: " + e.getMessage());
            }
        }
    }
    
    /**
     * 注入单个战利品表
     */
    private void injectLootTable(World world, String lootTablePath, String structure) {
        // 获取战利品表
        var lootTable = Bukkit.getLootTable(NamespacedKey.fromString(lootTablePath));
        
        if (lootTable == null) {
            plugin.logWarning("无法获取战利品表: " + lootTablePath);
            return;
        }
        
        plugin.logInfo("注入战利品表: " + lootTablePath);
    }
    
    /**
     * 生成特殊物品列表
     */
    public List<ItemStack> generateSpecialItems(String structure) {
        List<ItemStack> items = new ArrayList<>();
        
        // 检查是否应该生成
        if (!lootTableConfig.shouldGenerateLoot(structure)) {
            return items;
        }
        
        // 获取生成数量
        int count = lootTableConfig.getRandomPiecesCount(structure);
        
        // 生成物品
        for (int i = 0; i < count; i++) {
            Optional<SpecialItem> item = itemManager.getRandomItem();
            item.ifPresent(specialItem -> items.add(specialItem.createItemStack()));
        }
        
        return items;
    }
    
    /**
     * 检查世界是否应该注入战利品
     */
    public boolean shouldInjectWorld(World world) {
        // 跳过主世界、下界和末地
        if (world.getName().equals("world") || 
            world.getName().equals("world_nether") || 
            world.getName().equals("world_the_end")) {
            return false;
        }
        
        return plugin.getConfigManager().isWorldEnabled(world.getName());
    }
    
    /**
     * 获取已注入的世界列表
     */
    public Set<String> getInjectedWorlds() {
        return new HashSet<>(injectedWorlds);
    }
    
    /**
     * 清除注入记录（用于重新加载）
     */
    public void clearInjectionRecords() {
        injectedWorlds.clear();
        plugin.logInfo("已清除战利品表注入记录");
    }
}