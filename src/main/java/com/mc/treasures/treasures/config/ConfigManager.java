package com.mc.treasures.treasures.config;

import com.mc.treasures.treasures.Treasures;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ConfigManager {

    private final Treasures plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    public ConfigManager(Treasures plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        // 确保资源文件存在
        plugin.saveDefaultConfig();
        
        // 加载主配置
        config = plugin.getConfig();
        
        // 创建消息文件
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        plugin.getLogger().info("配置文件已加载！");
    }

    public void saveConfig() {
        config.save(new File(plugin.getDataFolder(), "config.yml"));
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        // 重新加载消息文件
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
        
        plugin.getLogger().info("配置已重新加载！");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public String getMessage(String path) {
        String message = messages.getString(path, "&c消息未找到: " + path);
        return colorize(message);
    }

    public String getMessage(String path, String... replacements) {
        String message = getMessage(path);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return message;
    }

    // 世界配置
    public List<String> getEnabledWorlds() {
        return config.getStringList("resource-worlds.enabled-worlds");
    }

    public List<String> getDisabledWorlds() {
        return config.getStringList("resource-worlds.disabled-worlds");
    }

    public boolean isWorldEnabled(String worldName) {
        // 检查是否在禁用列表中
        if (getDisabledWorlds().contains(worldName)) {
            return false;
        }
        // 检查是否在启用列表中（如果启用列表为空，则所有世界都启用）
        List<String> enabledWorlds = getEnabledWorlds();
        return enabledWorlds.isEmpty() || enabledWorlds.contains(worldName);
    }

    // 战利品表注入配置
    public boolean isLootInjectionEnabled() {
        return config.getBoolean("loot-injection.enabled", true);
    }

    public boolean isStructureEnabled(String structure) {
        return config.getBoolean("loot-injection.structures." + structure + ".enabled", true);
    }

    public int getStructureWeight(String structure) {
        return config.getInt("loot-injection.structures." + structure + ".weight", 30);
    }

    public int getStructureMinPieces(String structure) {
        return config.getInt("loot-injection.structures." + structure + ".min-pieces", 1);
    }

    public int getStructureMaxPieces(String structure) {
        return config.getInt("loot-injection.structures." + structure + ".max-pieces", 2);
    }

    // 稀有度权重配置
    public int getRarityWeight(String rarity) {
        return config.getInt("loot-tables.default." + rarity.toLowerCase(), 10);
    }

    // 物品效果配置
    public int getHeartShardMaxStacks() {
        return config.getInt("item-effects.heart-shard.max-stacks", 5);
    }

    public int getSoulShardMaxStacks() {
        return config.getInt("item-effects.soul-shard.max-stacks", 3);
    }

    public int getCompassUpdateInterval() {
        return config.getInt("item-effects.compass.update-interval", 20);
    }

    public int getCompassRange() {
        return config.getInt("item-effects.compass.range", 100);
    }

    public int getInvisibilityDuration() {
        return config.getInt("item-effects.invisibility-chalk.duration", 600);
    }

    public int getInvisibilityCooldown() {
        return config.getInt("item-effects.invisibility-chalk.cooldown", 6000);
    }

    // 工具方法：颜色代码转换
    private String colorize(String message) {
        if (message == null) {
            return "";
        }
        return message.replace("&", "§");
    }
}
