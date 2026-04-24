package com.mc.treasures.treasures.special;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class SpecialItem {
    
    private final String id;
    private final String name;
    private final String description;
    private final Material material;
    private final Rarity rarity;
    private final int maxStack;
    private final int cooldown; // 秒
    private final Map<Enchantment, Integer> enchantments;
    private final List<String> lore;
    
    // 构造方法
    public SpecialItem(String id, String name, String description, Material material, 
                      Rarity rarity) {
        this(id, name, description, material, rarity, 1, 0, new HashMap<>(), new ArrayList<>());
    }
    
    public SpecialItem(String id, String name, String description, Material material,
                      Rarity rarity, int maxStack, int cooldown,
                      Map<Enchantment, Integer> enchantments, List<String> lore) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.material = material;
        this.rarity = rarity;
        this.maxStack = maxStack;
        this.cooldown = cooldown;
        this.enchantments = enchantments;
        this.lore = lore;
    }
    
    /**
     * 创建物品实例
     */
    public ItemStack createItemStack() {
        return createItemStack(1);
    }
    
    /**
     * 创建指定数量的物品实例
     */
    public ItemStack createItemStack(int amount) {
        ItemStack item = new ItemStack(material, Math.min(amount, maxStack));
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // 设置物品名称（带颜色代码）
            meta.setDisplayName(rarity.getColor() + name);
            
            // 设置lore
            List<String> itemLore = new ArrayList<>();
            itemLore.add(rarity.getColor() + "✦ " + rarity.getDisplayName() + " 物品 ✦");
            itemLore.add("§7" + description);
            
            if (cooldown > 0) {
                itemLore.add("§e冷却时间: §f" + formatCooldown(cooldown));
            }
            
            itemLore.addAll(lore);
            itemLore.add("");
            itemLore.add("§8§o[Treasures 特殊物品]");
            
            meta.setLore(itemLore);
            
            // 添加持久化数据标签
            NamespacedKey idKey = new NamespacedKey("treasures", "item_id");
            NamespacedKey rarityKey = new NamespacedKey("treasures", "rarity");
            NamespacedKey cooldownKey = new NamespacedKey("treasures", "cooldown");
            
            meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, id);
            meta.getPersistentDataContainer().set(rarityKey, PersistentDataType.STRING, rarity.name());
            meta.getPersistentDataContainer().set(cooldownKey, PersistentDataType.INTEGER, cooldown);
            
            // 应用附魔
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                meta.addEnchant(entry.getKey(), entry.getValue(), true);
            }
            
            // 设置堆叠大小上限
            meta.setMaxStackSize(maxStack);
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    /**
     * 从ItemStack提取物品ID
     */
    public static Optional<String> getSpecialItemId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }
        
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("treasures", "item_id");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        if (container.has(key, PersistentDataType.STRING)) {
            return Optional.of(container.get(key, PersistentDataType.STRING));
        }
        
        return Optional.empty();
    }
    
    /**
     * 从ItemStack提取稀有度
     */
    public static Optional<Rarity> getRarity(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return Optional.empty();
        }
        
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey("treasures", "rarity");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        if (container.has(key, PersistentDataType.STRING)) {
            String rarityStr = container.get(key, PersistentDataType.STRING);
            try {
                return Optional.of(Rarity.valueOf(rarityStr));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * 格式化冷却时间显示
     */
    private String formatCooldown(int seconds) {
        if (seconds >= 3600) {
            int hours = seconds / 3600;
            int mins = (seconds % 3600) / 60;
            return hours + "小时" + mins + "分钟";
        } else if (seconds >= 60) {
            int mins = seconds / 60;
            int secs = seconds % 60;
            return mins + "分钟" + secs + "秒";
        } else {
            return seconds + "秒";
        }
    }
    
    // Getter 方法
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Material getMaterial() { return material; }
    public Rarity getRarity() { return rarity; }
    public int getMaxStack() { return maxStack; }
    public int getCooldown() { return cooldown; }
    public Map<Enchantment, Integer> getEnchantments() { return enchantments; }
    public List<String> getLore() { return lore; }
    
    /**
     * 稀有度枚举
     */
    public enum Rarity {
        COMMON("普通", "§f", 0.40),
        UNCOMMON("稀有", "§a", 0.25),
        RARE("珍贵", "§b", 0.20),
        EPIC("史诗", "§d", 0.10),
        LEGENDARY("传说", "§6", 0.05);
        
        private final String displayName;
        private final String color;
        private final double dropChance;
        
        Rarity(String displayName, String color, double dropChance) {
            this.displayName = displayName;
            this.color = color;
            this.dropChance = dropChance;
        }
        
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
        public double getDropChance() { return dropChance; }
    }
}
