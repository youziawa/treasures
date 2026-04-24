package com.mc.treasures.treasures.commands;

import com.mc.treasures.treasures.Treasures;
import com.mc.treasures.treasures.data.PlayerData;
import com.mc.treasures.treasures.special.SpecialItem;
import com.mc.treasures.treasures.special.SpecialItemManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treasures 插件命令处理
 */
public class TreasuresCommand implements CommandExecutor, TabCompleter {

    private final Treasures plugin;
    private final SpecialItemManager itemManager;
    
    public TreasuresCommand(Treasures plugin) {
        this.plugin = plugin;
        this.itemManager = plugin.getSpecialItemManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help" -> sendHelp(sender);
            case "list" -> sendItemList(sender);
            case "info" -> handleInfo(sender, args);
            case "give" -> handleGive(sender, args);
            case "reload" -> handleReload(sender);
            case "stats" -> handleStats(sender, args);
            default -> sender.sendMessage(plugin.getConfigManager().getMessage("unknown-command"));
        }
        
        return true;
    }
    
    /**
     * 发送帮助信息
     */
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6========== Treasures 帮助 ==========");
        sender.sendMessage("§e/treasures help §7- §f显示帮助信息");
        sender.sendMessage("§e/treasures list §7- §f列出所有特殊物品");
        sender.sendMessage("§e/treasures info <物品ID> §7- §f查看物品详细信息");
        sender.sendMessage("§e/treasures stats [玩家] §7- §f查看统计数据");
        
        if (sender.hasPermission("treasures.admin")) {
            sender.sendMessage("§e/treasures give <物品ID> [玩家] §7- §f给予物品");
            sender.sendMessage("§e/treasures reload §7- §f重新加载配置");
        }
        
        sender.sendMessage("§6===================================");
    }
    
    /**
     * 发送物品列表
     */
    private void sendItemList(CommandSender sender) {
        sender.sendMessage("§6========== 特殊物品列表 ==========");
        
        for (SpecialItem.Rarity rarity : SpecialItem.Rarity.values()) {
            List<SpecialItem> items = itemManager.getItemsByRarity(rarity);
            if (!items.isEmpty()) {
                sender.sendMessage("§e" + rarity.getColor() + "§l" + rarity.getDisplayName() + "§r §e物品:");
                
                for (SpecialItem item : items) {
                    sender.sendMessage("  §f- §e" + item.getId() + " §7" + item.getName());
                }
            }
        }
        
        sender.sendMessage("§6===================================");
    }
    
    /**
     * 处理 info 命令
     */
    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c用法: /treasures info <物品ID>");
            return;
        }
        
        String itemId = args[1];
        SpecialItem item = itemManager.getItemById(itemId);
        
        if (item == null) {
            sender.sendMessage("§c物品不存在: " + itemId);
            return;
        }
        
        sender.sendMessage("§6========== " + item.getName() + " ==========");
        sender.sendMessage("§eID: §f" + item.getId());
        sender.sendMessage("§e名称: " + item.getRarity().getColor() + item.getName());
        sender.sendMessage("§e稀有度: " + item.getRarity().getColor() + item.getRarity().getDisplayName());
        sender.sendMessage("§e描述: §f" + item.getDescription());
        sender.sendMessage("§e材质: §f" + item.getMaterial().name());
        sender.sendMessage("§e最大堆叠: §f" + item.getMaxStack());
        if (item.getCooldown() > 0) {
            sender.sendMessage("§e冷却时间: §f" + item.getCooldown() + "秒");
        }
        sender.sendMessage("§6===================================");
    }
    
    /**
     * 处理 give 命令
     */
    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("treasures.give")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§c用法: /treasures give <物品ID> [玩家]");
            return;
        }
        
        String itemId = args[1];
        
        // 检查物品是否存在
        if (!itemManager.itemExists(itemId)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("give-failed", "{item}", itemId));
            return;
        }
        
        // 确定目标玩家
        Player targetPlayer;
        if (args.length >= 3) {
            targetPlayer = Bukkit.getPlayer(args[2]);
            if (targetPlayer == null) {
                sender.sendMessage("§c玩家不在线: " + args[2]);
                return;
            }
        } else if (sender instanceof Player) {
            targetPlayer = (Player) sender;
        } else {
            sender.sendMessage("§c请指定玩家名称");
            return;
        }
        
        // 创建并给予物品
        ItemStack item = itemManager.createItemStack(itemId);
        if (item != null) {
            targetPlayer.getInventory().addItem(item);
            sender.sendMessage(plugin.getConfigManager().getMessage("give-success",
                "{player}", targetPlayer.getName(), "{item}", item.getItemMeta().getDisplayName()));
            targetPlayer.sendMessage(plugin.getConfigManager().getMessage("item-received",
                "{item}", item.getItemMeta().getDisplayName()));
        }
    }
    
    /**
     * 处理 reload 命令
     */
    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("treasures.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
            return;
        }
        
        try {
            plugin.reload();
            sender.sendMessage(plugin.getConfigManager().getMessage("reload-success"));
        } catch (Exception e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("reload-failed"));
            plugin.logSevere("重新加载配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 处理 stats 命令
     */
    private void handleStats(CommandSender sender, String[] args) {
        Player targetPlayer;
        
        if (args.length >= 2) {
            targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage("§c玩家不在线: " + args[1]);
                return;
            }
        } else if (sender instanceof Player) {
            targetPlayer = (Player) sender;
        } else {
            sender.sendMessage("§c请指定玩家名称");
            return;
        }
        
        PlayerData data = plugin.getDataManager().getPlayerData(targetPlayer);
        
        sender.sendMessage("§6========== " + targetPlayer.getName() + " 的统计 ==========");
        sender.sendMessage("§e开启的宝箱: §f" + data.getChestsOpened());
        sender.sendMessage("§e发现的物品: §f" + data.getDiscoveredItemsCount());
        sender.sendMessage("§6=========================================");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // 第一个参数
            List<String> subCommands = new ArrayList<>(Arrays.asList("help", "list", "info", "stats"));
            
            if (sender.hasPermission("treasures.give")) {
                subCommands.add("give");
            }
            
            if (sender.hasPermission("treasures.admin")) {
                subCommands.add("reload");
            }
            
            String input = args[0].toLowerCase();
            completions = subCommands.stream()
                .filter(cmd -> cmd.startsWith(input))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("info") || subCommand.equals("give")) {
                // 物品ID补全
                String input = args[1].toLowerCase();
                completions = itemManager.getAllItemIds().stream()
                    .filter(id -> id.startsWith(input))
                    .collect(Collectors.toList());
            } else if (subCommand.equals("stats")) {
                // 玩家补全
                String input = args[1].toLowerCase();
                completions = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                // 玩家补全
                String input = args[2].toLowerCase();
                completions = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .collect(Collectors.toList());
            }
        }
        
        return completions;
    }
}
