package com.mc.treasures.treasures.effects;

import com.mc.treasures.treasures.Treasures;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * 物品效果管理器
 */
public class ItemEffectManager {

    private final Treasures plugin;
    
    // 玩家当前效果映射
    private final Map<UUID, Set<ItemEffects>> activeEffects;
    
    // 玩家航点位置
    private final Map<UUID, Location> waypoints;
    
    // 隐身冷却
    private final Map<UUID, Long> invisibilityCooldowns;
    
    // 玩家临时心数量
    private final Map<UUID, Integer> extraHearts;
    
    public ItemEffectManager(Treasures plugin) {
        this.plugin = plugin;
        this.activeEffects = new HashMap<>();
        this.waypoints = new HashMap<>();
        this.invisibilityCooldowns = new HashMap<>();
        this.extraHearts = new HashMap<>();
    }
    
    /**
     * 注册所有效果
     */
    public void registerEffects() {
        plugin.logInfo("已注册 " + ItemEffects.values().length + " 种物品效果");
    }
    
    /**
     * 应用物品效果
     */
    public void applyEffect(Player player, String itemId) {
        ItemEffects effect = ItemEffects.fromItemId(itemId);
        if (effect == null) {
            return;
        }
        
        switch (effect) {
            case XP_BOOST -> applyXpBoost(player);
            case NIGHT_VISION -> applyNightVision(player);
            case INVISIBILITY -> applyInvisibility(player);
            case WATER_BREATHING -> applyWaterBreathing(player);
            case GLOW_EFFECT -> applyGlowEffect(player);
            case WAYPOINT -> {} // 需要右键设置航点
            case TEMPORARY_LIGHT -> {} // 需要右键放置光源
            case CHEST_PROTECTION -> {} // 需要右键储物箱
            case COMPASS_TRACKING -> {} // 指南针自动追踪
            case ORE_DETECTION -> applyOreDetection(player);
            case STRUCTURE_FINDER -> {} // 指向结构
            case EXTRA_HEART -> applyExtraHeart(player);
            case MUSIC_PLAYER -> {} // 放置播放音乐
            case SPIRIT_FOLLOWER -> {} // 召唤幽灵
            case TELEPORT -> {} // 投掷传送
        }
        
        // 添加到活跃效果列表
        addActiveEffect(player, effect);
    }
    
    /**
     * 移除物品效果
     */
    public void removeEffect(Player player, ItemEffects effect) {
        switch (effect) {
            case XP_BOOST -> removeXpBoost(player);
            case NIGHT_VISION -> removeNightVision(player);
            case INVISIBILITY -> removeInvisibility(player);
            case WATER_BREATHING -> removeWaterBreathing(player);
            case GLOW_EFFECT -> removeGlowEffect(player);
            case ORE_DETECTION -> removeOreDetection(player);
            case EXTRA_HEART -> removeExtraHeart(player);
            default -> {}
        }
        
        // 从活跃效果列表移除
        removeActiveEffect(player, effect);
    }
    
    /**
     * 每tick检查效果状态
     */
    public void onTick() {
        // 检查并移除过期的隐身效果
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            
            // 检查活跃效果并刷新
            Set<ItemEffects> effects = activeEffects.get(uuid);
            if (effects != null) {
                for (ItemEffects effect : effects) {
                    if (effect.isPassive()) {
                        refreshEffect(player, effect);
                    }
                }
            }
        }
    }
    
    // 刷新效果（保持效果持续）
    private void refreshEffect(Player player, ItemEffects effect) {
        switch (effect) {
            case NIGHT_VISION -> {
                if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
                }
            }
            case GLOW_EFFECT -> {
                // 在玩家脚下生成发光粒子
                player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 0.1, 0), 1, 0, 0, 0, 0);
            }
            default -> {}
        }
    }
    
    // 应用经验增益效果
    private void applyXpBoost(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, Integer.MAX_VALUE, 0, false, false));
    }
    
    private void removeXpBoost(Player player) {
        player.removePotionEffect(PotionEffectType.LUCK);
    }
    
    // 应用夜视效果
    private void applyNightVision(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
    }
    
    private void removeNightVision(Player player) {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    }
    
    // 应用隐身效果
    private void applyInvisibility(Player player) {
        int duration = plugin.getConfigManager().getInvisibilityDuration();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, 0, false, false));
    }
    
    private void removeInvisibility(Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }
    
    // 检查隐身冷却
    public boolean isOnInvisibilityCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        if (!invisibilityCooldowns.containsKey(uuid)) {
            return false;
        }
        
        long cooldownEnd = invisibilityCooldowns.get(uuid);
        if (System.currentTimeMillis() < cooldownEnd) {
            return true;
        }
        
        // 冷却结束，移除记录
        invisibilityCooldowns.remove(uuid);
        return false;
    }
    
    public void setInvisibilityCooldown(Player player) {
        int cooldown = plugin.getConfigManager().getInvisibilityCooldown();
        long cooldownEnd = System.currentTimeMillis() + (cooldown * 50L); // 转换为毫秒
        invisibilityCooldowns.put(player.getUniqueId(), cooldownEnd);
    }
    
    // 应用水下呼吸效果
    private void applyWaterBreathing(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 600, 0, false, false));
    }
    
    private void removeWaterBreathing(Player player) {
        player.removePotionEffect(PotionEffectType.WATER_BREATHING);
    }
    
    // 应用发光效果
    private void applyGlowEffect(Player player) {
        // 发光效果通过粒子实现，这里不需要potion effect
    }
    
    private void removeGlowEffect(Player player) {
        // 粒子效果会在tick中自动停止
    }
    
    // 应用矿石探测效果
    private void applyOreDetection(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, Integer.MAX_VALUE, 0, false, false));
    }
    
    private void removeOreDetection(Player player) {
        player.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
    }
    
    // 应用临时心效果
    private void applyExtraHeart(Player player) {
        UUID uuid = player.getUniqueId();
        int currentHearts = extraHearts.getOrDefault(uuid, 0);
        int maxHearts = plugin.getConfigManager().getHeartShardMaxStacks();
        
        if (currentHearts < maxHearts) {
            int newHearts = currentHearts + 1;
            extraHearts.put(uuid, newHearts);
            player.setMaxHealth(player.getMaxHealth() + 2);
            player.setHealth(player.getHealth() + 2);
        }
    }
    
    private void removeExtraHeart(Player player) {
        UUID uuid = player.getUniqueId();
        int hearts = extraHearts.getOrDefault(uuid, 0);
        
        if (hearts > 0) {
            player.setMaxHealth(player.getMaxHealth() - (hearts * 2));
            extraHearts.remove(uuid);
        }
    }
    
    // 航点相关方法
    public void setWaypoint(Player player, Location location) {
        waypoints.put(player.getUniqueId(), location);
    }
    
    public Location getWaypoint(Player player) {
        return waypoints.get(player.getUniqueId());
    }
    
    public boolean hasWaypoint(Player player) {
        return waypoints.containsKey(player.getUniqueId());
    }
    
    // 效果列表管理
    private void addActiveEffect(Player player, ItemEffects effect) {
        UUID uuid = player.getUniqueId();
        activeEffects.computeIfAbsent(uuid, k -> new HashSet<>()).add(effect);
    }
    
    private void removeActiveEffect(Player player, ItemEffects effect) {
        UUID uuid = player.getUniqueId();
        Set<ItemEffects> effects = activeEffects.get(uuid);
        if (effects != null) {
            effects.remove(effect);
        }
    }
    
    public Set<ItemEffects> getActiveEffects(Player player) {
        return activeEffects.getOrDefault(player.getUniqueId(), Collections.emptySet());
    }
    
    // 玩家离开时清理数据
    public void cleanupPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        
        // 移除所有活跃效果
        Set<ItemEffects> effects = activeEffects.remove(uuid);
        if (effects != null) {
            for (ItemEffects effect : effects) {
                removeEffectDirectly(player, effect);
            }
        }
        
        // 清理航点
        waypoints.remove(uuid);
        
        // 清理临时心
        extraHearts.remove(uuid);
    }
    
    private void removeEffectDirectly(Player player, ItemEffects effect) {
        switch (effect) {
            case NIGHT_VISION -> player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            case XP_BOOST -> player.removePotionEffect(PotionEffectType.LUCK);
            case WATER_BREATHING -> player.removePotionEffect(PotionEffectType.WATER_BREATHING);
            case INVISIBILITY -> player.removePotionEffect(PotionEffectType.INVISIBILITY);
            default -> {}
        }
    }
}
