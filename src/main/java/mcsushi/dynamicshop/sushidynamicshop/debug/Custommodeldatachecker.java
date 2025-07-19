/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package mcsushi.dynamicshop.sushidynamicshop.debug;

import java.util.Collections;
import java.util.List;
import mcsushi.dynamicshop.sushidynamicshop.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Custommodeldatachecker
implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "You are not holding any item.");
            return true;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasCustomModelData()) {
            int data = meta.getCustomModelData();
            player.sendMessage(String.valueOf(ChatColor.GREEN) + "CustomModelData: " + String.valueOf(ChatColor.YELLOW) + data);
        } else {
            player.sendMessage(String.valueOf(ChatColor.RED) + "This item does NOT have CustomModelData.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}

