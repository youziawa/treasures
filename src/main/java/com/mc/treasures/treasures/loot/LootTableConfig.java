package com.mc.treasures.treasures.loot;

import com.mc.treasures.treasures.Treasures;
import com.mc.treasures.treasures.config.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

/**
 * 战利品表配置管理
 */
public class LootTableConfig {

    private final Treasures plugin;
    private final ConfigManager config;
    
    // 结构名称到战利品表路径的映射
    private static final Map<String, String[]> STRUCTURE_LOOT_TABLES = new HashMap<>();
    
    static {
        // 村庄结构
        STRUCTURE_LOOT_TABLES.put("village", new String[]{
            "minecraft:chests/village/village_weaponsmith",
            "minecraft:chests/village/village_toolsmith",
            "minecraft:chests/village/village_armorer",
            "minecraft:chests/village/village_cartographer",
            "minecraft:chests/village/village_cleric",
            "minecraft:chests/village/village_fletcher",
            "minecraft:chests/village/village_shepherd"
        });
        
        // 沙漠神殿
        STRUCTURE_LOOT_TABLES.put("desert_temple", new String[]{
            "minecraft:chests/desert_pyramid"
        });
        
        // 丛林神庙
        STRUCTURE_LOOT_TABLES.put("jungle_temple", new String[]{
            "minecraft:chests/jungle_temple"
        });
        
        // 沉船
        STRUCTURE_LOOT_TABLES.put("shipwreck", new String[]{
            "minecraft:chests/shipwreck_supply",
            "minecraft:chests/shipwreck_treasure"
        });
        
        // 废弃矿井
        STRUCTURE_LOOT_TABLES.put("abandoned_mineshaft", new String[]{
            "minecraft:chests/abandoned_mineshaft"
        });
        
        // 要塞
        STRUCTURE_LOOT_TABLES.put("stronghold", new String[]{
            "minecraft:chests/stronghold_corridor",
            "minecraft:chests/stronghold_library",
            "minecraft:chests/stronghold_storage"
        });
        
        // 女巫小屋
        STRUCTURE_LOOT_TABLES.put("witch_hut", new String[]{
            "minecraft:chests/witch_hut"
        });
        
        // 雪屋
        STRUCTURE_LOOT_TABLES.put("igloo", new String[]{
            "minecraft:chests/igloo_chest"
        });
        
        // 海洋神殿
        STRUCTURE_LOOT_TABLES.put("ocean_monument", new String[]{
            "minecraft:chests/monument"
        });
        
        // 堡垒遗迹
        STRUCTURE_LOOT_TABLES.put("bastion_remnant", new String[]{
            "minecraft:chests/bastion_bridge",
            "minecraft:chests/bastion_other",
            "minecraft:chests/bastion_treasure",
            "minecraft:chests/bastion_wall"
        });
        
        // 远古城市
        STRUCTURE_LOOT_TABLES.put("ancient_city", new String[]{
            "minecraft:chests/ancient_city"
        });
    }
    
    public LootTableConfig(Treasures plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }
    
    /**
     * 检查结构是否启用
     */
    public boolean isStructureEnabled(String structure) {
        return config.isStructureEnabled(structure);
    }
    
    /**
     * 获取结构的权重
     */
    public int getStructureWeight(String structure) {
        return config.getStructureWeight(structure);
    }
    
    /**
     * 获取结构生成物品的最小数量
     */
    public int getMinPieces(String structure) {
        return config.getStructureMinPieces(structure);
    }
    
    /**
     * 获取结构生成物品的最大数量
     */
    public int getMaxPieces(String structure) {
        return config.getStructureMaxPieces(structure);
    }
    
    /**
     * 获取结构的战利品表路径列表
     */
    public String[] getLootTablePaths(String structure) {
        return STRUCTURE_LOOT_TABLES.getOrDefault(structure, new String[]{});
    }
    
    /**
     * 获取所有启用的结构名称
     */
    public java.util.List<String> getEnabledStructures() {
        java.util.List<String> enabled = new java.util.ArrayList<>();
        for (String structure : STRUCTURE_LOOT_TABLES.keySet()) {
            if (isStructureEnabled(structure)) {
                enabled.add(structure);
            }
        }
        return enabled;
    }
    
    /**
     * 检查是否应该根据随机权重生成物品
     */
    public boolean shouldGenerateLoot(String structure) {
        if (!isStructureEnabled(structure)) {
            return false;
        }
        
        int weight = getStructureWeight(structure);
        int random = (int) (Math.random() * 100);
        
        return random < weight;
    }
    
    /**
     * 获取应该生成的物品数量
     */
    public int getRandomPiecesCount(String structure) {
        int min = getMinPieces(structure);
        int max = getMaxPieces(structure);
        
        return min + (int) (Math.random() * (max - min + 1));
    }
}
