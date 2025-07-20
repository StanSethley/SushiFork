package mcsushi.dynamicshop.sushidynamicshop.command;

import mcsushi.dynamicshop.sushidynamicshop.config.CategoryConfig;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopGUI;
import mcsushi.dynamicshop.sushidynamicshop.util.TranslationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class OpenCommand implements SubCommand {

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sushishop.open.self") && !sender.isOp()) {
            sender.sendMessage(TranslationUtil.get("nopermission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /sshop open <shopId> [player]");
            return true;
        }

        String shopId = args[1];

        if (!isShopInCategoryConfig(shopId)) {
            sender.sendMessage(ChatColor.RED + "That shop is not part of the category GUI.");
            return true;
        }

        Player target;
        if (args.length >= 3) {
            if (!sender.hasPermission("sushishop.open.others") && !sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You do not have permission to open shops for other players.");
                return true;
            }
            target = Bukkit.getPlayerExact(args[2]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + args[2]);
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Console must specify a player.");
            return true;
        }

        String requiredPerm = CategoryConfig.getPermissionByShopId(shopId);
        if (!requiredPerm.isEmpty() && !target.hasPermission(requiredPerm)) {
            sender.sendMessage(TranslationUtil.get("no_shop_permission"));
            return true;
        }

        ShopGUI.open(target, shopId);

        if (!sender.equals(target)) {
            sender.sendMessage(ChatColor.GREEN + "Opened shop '" + shopId + "' for " + target.getName() + ".");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("sushishop.open.self") && !sender.isOp()) {
            return Collections.emptyList();
        }

        if (args.length == 2) {
            Set<String> shopIds = CategoryConfig.getAllShopIds();
            return shopIds.stream().filter(id -> id.toLowerCase().startsWith(args[1].toLowerCase())).toList();
        }

        if (args.length == 3) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return Collections.emptyList();
    }

    private boolean isShopInCategoryConfig(String shopId) {
        var cats = CategoryConfig.get().getConfigurationSection("categories");
        if (cats == null) return false;

        for (String key : cats.getKeys(false)) {
            String configuredShop = cats.getConfigurationSection(key).getString("shopid", key);
            if (shopId.equalsIgnoreCase(configuredShop)) return true;
        }
        return false;
    }
}
