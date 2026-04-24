package com.mc.treasures.treasures.special;

import com.mc.treasures.treasures.Treasures;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SpecialItemManager {

    private final Treasures plugin;
    private final Map<String, SpecialItem> items;
    private final Random random;
    
    public SpecialItemManager(Treasures plugin) {
        this.plugin = plugin;
        this.items = new HashMap<>();
        this.random = new Random();
    }
    
    /**
     * 注册所有预设的特殊物品
     */
    public void registerItems() {
        items.clear();
        
        // ==================== 普通物品 ====================
        
        // 探险家指南针 - 指向最近的宝箱位置
        registerItem(new SpecialItem(
            "explorer_compass",
            "探险家指南针",
            "指向最近的宝箱位置",
            Material.COMPASS,
            SpecialItem.Rarity.COMMON
        ));
        
        // 便携火把 - 右键放置发光方块
        registerItem(new SpecialItem(
            "portable_torch",
            "便携火把",
            "右键放置发光方块，5分钟后消失",
            Material.TORCH,
            SpecialItem.Rarity.COMMON,
            16, 5,
            new HashMap<>(),
            Arrays.asList("§e放置后提供光源", "§75分钟后自动消失")
        ));
        
        // 储物箱锁 - 上锁储物箱
        registerItem(new SpecialItem(
            "chest_lock",
            "储物箱锁",
            "右键点击储物箱上锁，他人无法打开",
            Material.IRON_BARS,
            SpecialItem.Rarity.COMMON,
            8, 10,
            new HashMap<>(),
            Arrays.asList("§e将物品放在储物箱上", "§e即可保护储物箱")
        ));
        
        // ==================== 稀有物品 ====================
        
        // 经验碎片 - 增加经验获取
        registerItem(new SpecialItem(
            "xp_shard",
            "经验碎片",
            "携带时增加10%经验获取",
            Material.PRISMARINE_SHARD,
            SpecialItem.Rarity.UNCOMMON,
            3, 0,
            new HashMap<>(),
            Arrays.asList("§e增加经验值获取量")
        ));
        
        // 矿石探测器 - 显示附近矿石
        registerItem(new SpecialItem(
            "ore_detector",
            "矿石探测器",
            "显示附近地下矿石位置",
            Material.CLOCK,
            SpecialItem.Rarity.UNCOMMON,
            1, 30,
            new HashMap<>(),
            Arrays.asList("§e显示附近的矿石", "§e通过粒子效果指示方向")
        ));
        
        // 航点标记书 - 记录和传送
        registerItem(new SpecialItem(
            "waypoint_book",
            "航点标记书",
            "右键设置航点，左键传送至航点",
            Material.BOOK,
            SpecialItem.Rarity.UNCOMMON,
            1, 60,
            new HashMap<>(),
            Arrays.asList("§e记录当前位置为航点", "§e左键点击传送到航点")
        ));
        
        // ==================== 珍贵物品 ====================
        
        // 荧石碎片 - 脚下发光
        registerItem(new SpecialItem(
            "glowstone_shard",
            "荧石碎片",
            "佩戴时脚下发光，适合探索",
            Material.GLOWSTONE_DUST,
            SpecialItem.Rarity.RARE,
            2, 0,
            new HashMap<>(),
            Arrays.asList("§e脚下产生发光效果", "§e照亮周围环境")
        ));
        
        // 传送珍珠 - 投掷传送
        registerItem(new SpecialItem(
            "teleport_pearl",
            "传送珍珠",
            "投掷后传送到目标位置",
            Material.ENDER_PEARL,
            SpecialItem.Rarity.RARE,
            3, 0,
            new HashMap<>(),
            Arrays.asList("§e像末影珍珠一样使用", "§e传送到目标位置")
        ));
        
        // 水下封印符 - 水下呼吸
        registerItem(new SpecialItem(
            "underwater_seal",
            "水下封印符",
            "允许在水下呼吸额外30秒",
            Material.LILY_PAD,
            SpecialItem.Rarity.RARE,
            2, 0,
            new HashMap<>(),
            Arrays.asList("§e给予水下呼吸效果", "§e持续30秒")
        ));
        
        // ==================== 史诗物品 ====================
        
        // 夜视镜 - 永久夜视
        registerItem(new SpecialItem(
            "night_vision_goggles",
            "夜视镜",
            "永久夜视效果（需保持背包栏位）",
            Material.GHAST_TEAR,
            SpecialItem.Rarity.EPIC,
            1, 0,
            new HashMap<>(),
            Arrays.asList("§e给予永久夜视效果", "§e需保持在背包中")
        ));
        
        // 隐身粉笔 - 隐身效果
        registerItem(new SpecialItem(
            "invisibility_chalk",
            "隐身粉笔",
            "30秒隐身效果，5分钟冷却",
            Material.BONE,
            SpecialItem.Rarity.EPIC,
            3, 300,
            new HashMap<>(),
            Arrays.asList("§e给予30秒隐身效果", "§e有5分钟冷却时间")
        ));
        
        // 远古罗盘 - 指向结构
        registerItem(new SpecialItem(
            "ancient_compass",
            "远古罗盘",
            "显示最近村庄或遗迹位置",
            Material.COMPASS,
            SpecialItem.Rarity.EPIC,
            1, 0,
            new HashMap<>(),
            Arrays.asList("§e指向最近的结构", "§e村庄、沙漠神殿等")
        ));
        
        // ==================== 传说物品 ====================
        
        // 生命碎片 - 增加临时心
        registerItem(new SpecialItem(
            "heart_fragment",
            "生命碎片",
            "收集5个增加一颗临时心",
            Material.NETHER_STAR,
            SpecialItem.Rarity.LEGENDARY,
            5, 0,
            new HashMap<>(),
            Arrays.asList("§e收集5个增加临时心", "§c可增加最大生命值")
        ));
        
        // 音乐盒 - 播放音乐
        registerItem(new SpecialItem(
            "music_box",
            "音乐盒",
            "放置后播放音乐，营造探索氛围",
            Material.JUKEBOX,
            SpecialItem.Rarity.LEGENDARY,
            1, 0,
            new HashMap<>(),
            Arrays.asList("§e放置后播放音乐", "§e营造探索氛围")
        ));
        
        // 灵魂碎片 - 召唤幽灵
        registerItem(new SpecialItem(
            "soul_shard",
            "灵魂碎片",
            "收集3个可召唤友善幽灵跟随",
            Material.SOUL_LANTERN,
            SpecialItem.Rarity.LEGENDARY,
            3, 0,
            new HashMap<>(),
            Arrays.asList("§e收集3个召唤幽灵", "§e幽灵会跟随玩家")
        ));
        
        plugin.getLogger().info("已注册 " + items.size() + " 个特殊物品！");
    }
    
    /**
     * 注册单个特殊物品
     */
    public void registerItem(SpecialItem item) {
        items.put(item.getId(), item);
    }
    
    /**
     * 根据ID获取物品
     */
    public SpecialItem getItemById(String id) {
        return items.get(id);
    }
    
    /**
     * 创建指定ID的物品实例
     */
    public ItemStack createItemStack(String id) {
        return createItemStack(id, 1);
    }
    
    /**
     * 创建指定ID和数量的物品实例
     */
    public ItemStack createItemStack(String id, int amount) {
        SpecialItem item = getItemById(id);
        if (item != null) {
            return item.createItemStack(amount);
        }
        return null;
    }
    
    /**
     * 根据稀有度权重随机获取物品
     */
    public Optional<SpecialItem> getRandomItem() {
        return getRandomItemByRarity();
    }
    
    /**
     * 根据稀有度权重随机获取物品
     */
    public Optional<SpecialItem> getRandomItemByRarity() {
        // 计算总权重
        double totalWeight = 0;
        for (SpecialItem.Rarity rarity : SpecialItem.Rarity.values()) {
            totalWeight += rarity.getDropChance();
        }
        
        // 随机选择
        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;
        
        for (SpecialItem.Rarity rarity : SpecialItem.Rarity.values()) {
            currentWeight += rarity.getDropChance();
            if (randomValue <= currentWeight) {
                // 获取该稀有度的所有物品
                List<SpecialItem> rarityItems = getItemsByRarity(rarity);
                if (!rarityItems.isEmpty()) {
                    return Optional.of(rarityItems.get(random.nextInt(rarityItems.size())));
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * 获取特定稀有度的所有物品
     */
    public List<SpecialItem> getItemsByRarity(SpecialItem.Rarity rarity) {
        List<SpecialItem> rarityItems = new ArrayList<>();
        for (SpecialItem item : items.values()) {
            if (item.getRarity() == rarity) {
                rarityItems.add(item);
            }
        }
        return rarityItems;
    }
    
    /**
     * 获取所有特殊物品
     */
    public Collection<SpecialItem> getAllItems() {
        return items.values();
    }
    
    /**
     * 获取所有物品ID列表
     */
    public Set<String> getAllItemIds() {
        return items.keySet();
    }
    
    /**
     * 检查物品是否存在
     */
    public boolean itemExists(String id) {
        return items.containsKey(id);
    }
}
