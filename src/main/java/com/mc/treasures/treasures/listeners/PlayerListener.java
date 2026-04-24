package com.mc.treasures.treasures.listeners;

import com.mc.treasures.treasures.Treasures;
import com.mc.treasures.treasures.data.DataManager;
import com.mc.treasures.treasures.effects.ItemEffectManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * 玩家事件监听器
 */
public class PlayerListener implements Listener {

    private final Treasures plugin;
    private final DataManager dataManager;
    private final ItemEffectManager effectManager;
    
    public PlayerListener(Treasures plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
        this.effectManager = plugin.getItemEffectManager();
    }
    
    /**
     * 玩家加入事件
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // 加载玩家数据
        dataManager.loadPlayerData(player);
        
        // 发送欢迎消息
        String prefix = plugin.getConfigManager().getMessage("prefix");
        player.sendMessage(prefix + "§a欢迎回来！探索原版结构获取特殊物品吧！");
    }
    
    /**
     * 玩家离开事件
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // 保存玩家数据
        dataManager.savePlayerData(player);
        
        // 清理效果数据
        effectManager.cleanupPlayer(player);
        
        // 移除数据缓存
        dataManager.removePlayerDataCache(player.getUniqueId());
    }
}
