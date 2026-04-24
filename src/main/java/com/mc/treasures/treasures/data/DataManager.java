package com.mc.treasures.treasures.data;

import com.mc.treasures.treasures.Treasures;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 数据管理器 - 管理玩家数据持久化
 */
public class DataManager {

    private final Treasures plugin;
    private final Map<UUID, PlayerData> playerDataCache;
    private File playerDataFolder;
    
    public DataManager(Treasures plugin) {
        this.plugin = plugin;
        this.playerDataCache = new HashMap<>();
        
        // 创建玩家数据文件夹
        playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }
    
    /**
     * 加载玩家数据
     */
    public PlayerData loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        
        // 检查缓存
        if (playerDataCache.containsKey(uuid)) {
            return playerDataCache.get(uuid);
        }
        
        // 加载文件
        File file = new File(playerDataFolder, uuid.toString() + ".yml");
        PlayerData data = new PlayerData(uuid);
        
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            
            // 读取数据
            data.loadFromConfig(config.getValues(false));
        }
        
        // 添加到缓存
        playerDataCache.put(uuid, data);
        
        plugin.logInfo("已加载玩家 " + player.getName() + " 的数据");
        
        return data;
    }
    
    /**
     * 保存玩家数据
     */
    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = playerDataCache.get(uuid);
        
        if (data == null) {
            return;
        }
        
        savePlayerData(data);
    }
    
    /**
     * 保存指定玩家数据
     */
    public void savePlayerData(PlayerData data) {
        File file = new File(playerDataFolder, data.getUuid().toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        // 保存数据
        Map<String, Object> values = data.saveToConfig();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }
        
        try {
            config.save(file);
            plugin.logInfo("已保存玩家数据: " + data.getUuid());
        } catch (IOException e) {
            plugin.logSevere("保存玩家数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 保存所有玩家数据
     */
    public void saveAllPlayerData() {
        for (PlayerData data : playerDataCache.values()) {
            savePlayerData(data);
        }
        plugin.logInfo("已保存所有玩家数据");
    }
    
    /**
     * 获取玩家数据
     */
    public PlayerData getPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        
        if (!playerDataCache.containsKey(uuid)) {
            return loadPlayerData(player);
        }
        
        return playerDataCache.get(uuid);
    }
    
    /**
     * 获取或创建玩家数据
     */
    public PlayerData getOrCreatePlayerData(UUID uuid) {
        return playerDataCache.computeIfAbsent(uuid, id -> new PlayerData(id));
    }
    
    /**
     * 移除玩家数据缓存
     */
    public void removePlayerDataCache(UUID uuid) {
        playerDataCache.remove(uuid);
    }
    
    /**
     * 清理所有缓存
     */
    public void clearCache() {
        playerDataCache.clear();
    }
}
