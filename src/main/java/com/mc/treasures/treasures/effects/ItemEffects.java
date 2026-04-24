package com.mc.treasures.treasures.effects;

import org.bukkit.entity.Player;

/**
 * 物品效果类型定义
 */
public enum ItemEffects {
    
    // 普通物品效果
    COMPASS_TRACKING("指南针追踪", true),
    TEMPORARY_LIGHT("临时光源", false),
    CHEST_PROTECTION("储物箱保护", false),
    
    // 稀有物品效果
    XP_BOOST("经验增益", true),
    ORE_DETECTION("矿石探测", true),
    WAYPOINT("航点传送", false),
    
    // 珍贵物品效果
    GLOW_EFFECT("发光效果", true),
    TELEPORT("传送", false),
    WATER_BREATHING("水下呼吸", false),
    
    // 史诗物品效果
    NIGHT_VISION("夜视", true),
    INVISIBILITY("隐身", false),
    STRUCTURE_FINDER("结构查找", true),
    
    // 传说物品效果
    EXTRA_HEART("临时心", false),
    MUSIC_PLAYER("音乐播放", false),
    SPIRIT_FOLLOWER("幽灵跟随", false);
    
    private final String displayName;
    private final boolean isPassive; // 是否是被动效果
    
    ItemEffects(String displayName, boolean isPassive) {
        this.displayName = displayName;
        this.isPassive = isPassive;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isPassive() {
        return isPassive;
    }
    
    /**
     * 根据物品ID获取效果类型
     */
    public static ItemEffects fromItemId(String itemId) {
        switch (itemId) {
            case "explorer_compass":
                return COMPASS_TRACKING;
            case "portable_torch":
                return TEMPORARY_LIGHT;
            case "chest_lock":
                return CHEST_PROTECTION;
            case "xp_shard":
                return XP_BOOST;
            case "ore_detector":
                return ORE_DETECTION;
            case "waypoint_book":
                return WAYPOINT;
            case "glowstone_shard":
                return GLOW_EFFECT;
            case "teleport_pearl":
                return TELEPORT;
            case "underwater_seal":
                return WATER_BREATHING;
            case "night_vision_goggles":
                return NIGHT_VISION;
            case "invisibility_chalk":
                return INVISIBILITY;
            case "ancient_compass":
                return STRUCTURE_FINDER;
            case "heart_fragment":
                return EXTRA_HEART;
            case "music_box":
                return MUSIC_PLAYER;
            case "soul_shard":
                return SPIRIT_FOLLOWER;
            default:
                return null;
        }
    }
}
