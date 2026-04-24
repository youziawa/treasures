package com.mc.treasures.treasures.data;

import org.bukkit.Location;

import java.util.*;

/**
 * 玩家数据类
 */
public class PlayerData {

    private final UUID uuid;
    private int chestsOpened;
    private final List<String> discoveredItems;
    private final Map<String, Long> cooldowns;
    private Location lastLocation;
    
    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.chestsOpened = 0;
        this.discoveredItems = new ArrayList<>();
        this.cooldowns = new HashMap<>();
    }
    
    /**
     * 增加开启的宝箱数量
     */
    public void incrementChestsOpened() {
        chestsOpened++;
    }
    
    /**
     * 添加发现的物品
     */
    public void addDiscoveredItem(String itemId) {
        if (!discoveredItems.contains(itemId)) {
            discoveredItems.add(itemId);
        }
    }
    
    /**
     * 检查冷却时间
     */
    public boolean isOnCooldown(String itemId) {
        if (!cooldowns.containsKey(itemId)) {
            return false;
        }
        
        long cooldownEnd = cooldowns.get(itemId);
        return System.currentTimeMillis() < cooldownEnd;
    }
    
    /**
     * 设置冷却时间（秒）
     */
    public void setCooldown(String itemId, int seconds) {
        long cooldownEnd = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.put(itemId, cooldownEnd);
    }
    
    /**
     * 获取冷却剩余时间（秒）
     */
    public int getCooldownRemaining(String itemId) {
        if (!cooldowns.containsKey(itemId)) {
            return 0;
        }
        
        long cooldownEnd = cooldowns.get(itemId);
        long remaining = cooldownEnd - System.currentTimeMillis();
        
        if (remaining <= 0) {
            cooldowns.remove(itemId);
            return 0;
        }
        
        return (int) (remaining / 1000);
    }
    
    // Getter方法
    public UUID getUuid() { return uuid; }
    public int getChestsOpened() { return chestsOpened; }
    public List<String> getDiscoveredItems() { return new ArrayList<>(discoveredItems); }
    public int getDiscoveredItemsCount() { return discoveredItems.size(); }
    public Location getLastLocation() { return lastLocation; }
    
    // Setter方法
    public void setLastLocation(Location location) { this.lastLocation = location; }
    
    /**
     * 从配置加载数据
     */
    public void loadFromConfig(Map<String, Object> data) {
        if (data.containsKey("chestsOpened")) {
            chestsOpened = (int) data.get("chestsOpened");
        }
        
        if (data.containsKey("discoveredItems")) {
            discoveredItems.clear();
            discoveredItems.addAll((List<String>) data.get("discoveredItems"));
        }
        
        if (data.containsKey("lastLocation")) {
            // 从字符串加载位置
            String locationStr = (String) data.get("lastLocation");
            if (locationStr != null && !locationStr.isEmpty()) {
                // 简化处理，实际应该解析字符串
                lastLocation = null;
            }
        }
    }
    
    /**
     * 保存数据到配置
     */
    public Map<String, Object> saveToConfig() {
        Map<String, Object> data = new HashMap<>();
        data.put("chestsOpened", chestsOpened);
        data.put("discoveredItems", new ArrayList<>(discoveredItems));
        
        if (lastLocation != null) {
            data.put("lastLocation", lastLocation.getWorld().getName() + "," + 
                    lastLocation.getX() + "," + 
                    lastLocation.getY() + "," + 
                    lastLocation.getZ());
        }
        
        return data;
    }
}
