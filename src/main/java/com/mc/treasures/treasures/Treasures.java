package com.mc.treasures.treasures;

import com.mc.treasures.treasures.commands.TreasuresCommand;
import com.mc.treasures.treasures.config.ConfigManager;
import com.mc.treasures.treasures.data.DataManager;
import com.mc.treasures.treasures.effects.ItemEffectManager;
import com.mc.treasures.treasures.listeners.ItemUseListener;
import com.mc.treasures.treasures.listeners.LootGenerateListener;
import com.mc.treasures.treasures.listeners.PlayerListener;
import com.mc.treasures.treasures.loot.LootTableInjector;
import com.mc.treasures.treasures.special.SpecialItemManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Treasures extends JavaPlugin {

    private static Treasures instance;
    
    private ConfigManager configManager;
    private SpecialItemManager specialItemManager;
    private ItemEffectManager itemEffectManager;
    private LootTableInjector lootTableInjector;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        // 单例实例
        instance = this;
        
        // 禁用Paper的WanderingTrader修复（如果需要）
        // getServer().getPluginManager().registerEvents(new WanderingTraderFix(), this);
        
        // 初始化配置管理器
        configManager = new ConfigManager(this);
        configManager.loadConfig();
        
        // 初始化数据管理器
        dataManager = new DataManager(this);
        
        // 初始化特殊物品管理器
        specialItemManager = new SpecialItemManager(this);
        specialItemManager.registerItems();
        
        // 初始化物品效果管理器
        itemEffectManager = new ItemEffectManager(this);
        itemEffectManager.registerEffects();
        
        // 初始化战利品表注入器
        lootTableInjector = new LootTableInjector(this);
        
        // 注册命令
        getCommand("treasures").setExecutor(new TreasuresCommand(this));
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new LootGenerateListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemUseListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        
        // 在所有启用世界的战利品表中注入特殊物品
        injectLootTables();
        
        // 启动物品效果定时器
        startEffectTimer();
        
        getLogger().info("==================================");
        getLogger().info("Treasures 插件已成功启用!");
        getLogger().info("版本: " + getDescription().getVersion());
        getLogger().info("作者: " + getDescription().getAuthors());
        getLogger().info("==================================");
    }

    @Override
    public void onDisable() {
        // 保存所有玩家数据
        if (dataManager != null) {
            dataManager.saveAllPlayerData();
        }
        
        // 保存配置
        if (configManager != null) {
            configManager.saveConfig();
        }
        
        getLogger().info("Treasures 插件已禁用!");
    }

    /**
     * 在所有启用世界的战利品表中注入特殊物品
     */
    private void injectLootTables() {
        if (!configManager.isLootInjectionEnabled()) {
            getLogger().info("战利品表注入已禁用");
            return;
        }
        
        for (World world : Bukkit.getWorlds()) {
            if (configManager.isWorldEnabled(world.getName())) {
                lootTableInjector.injectIntoWorld(world);
                getLogger().info("已在世界 '" + world.getName() + "' 注入战利品表");
            }
        }
    }

    /**
     * 启动物品效果定时器（每秒检查一次）
     */
    private void startEffectTimer() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (itemEffectManager != null) {
                itemEffectManager.onTick();
            }
        }, 20L, 20L); // 每秒执行一次
    }

    // 单例访问方法
    public static Treasures getInstance() {
        return instance;
    }

    // 管理器访问方法
    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SpecialItemManager getSpecialItemManager() {
        return specialItemManager;
    }

    public ItemEffectManager getItemEffectManager() {
        return itemEffectManager;
    }

    public LootTableInjector getLootTableInjector() {
        return lootTableInjector;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    /**
     * 记录日志信息
     */
    public void logInfo(String message) {
        getLogger().info(message);
    }

    /**
     * 记录警告信息
     */
    public void logWarning(String message) {
        getLogger().warning(message);
    }

    /**
     * 记录严重错误信息
     */
    public void logSevere(String message) {
        getLogger().severe(message);
    }

    /**
     * 重新加载插件（用于 /treasures reload 命令）
     */
    public void reload() {
        // 重新加载配置
        configManager.reloadConfig();
        
        // 重新注入战利品表
        injectLootTables();
        
        // 重新注册物品
        specialItemManager.registerItems();
        
        // 重新注册效果
        itemEffectManager.registerEffects();
    }
}
