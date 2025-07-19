/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 */
package mcsushi.dynamicshop.sushidynamicshop.command;

import java.util.Collections;
import java.util.List;
import mcsushi.dynamicshop.sushidynamicshop.command.SubCommand;
import mcsushi.dynamicshop.sushidynamicshop.config.CategoryConfig;
import mcsushi.dynamicshop.sushidynamicshop.editor.CategoryEditorGUI;
import mcsushi.dynamicshop.sushidynamicshop.gui.CategoryMenuHolder;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopGUI;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.util.GuiSlotHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class AddCommand
implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player)sender;
        if (!player.isOp()) {
            player.sendMessage("You must be OP to use this command.");
            return true;
        }
        if (args.length != 3) {
            player.sendMessage("Usage: /sushishop add <category|shop> <id>");
            return true;
        }
        String type = args[1].toLowerCase();
        String id = args[2];
        if (type.equals("category")) {
            if (CategoryConfig.hasCategory(id)) {
                player.sendMessage("This category ID already exists.");
                return true;
            }
            int slot = GuiSlotHolder.findAvailableSlot(new CategoryMenuHolder());
            CategoryConfig.createCategory(id);
            CategoryConfig.get().set("categories." + id + ".slot", (Object)slot);
            CategoryConfig.save();
            CategoryEditorGUI.open(player, id);
            player.sendMessage("Category '" + id + "' created at slot " + slot + " and opened for editing.");
            return true;
        }
        if (type.equals("shop")) {
            if (ShopConfig.hasShop(id)) {
                player.sendMessage("This shop ID already exists.");
                return true;
            }
            YamlConfiguration config = new YamlConfiguration();
            config.set("inventory.name", (Object)"&fNew Shop");
            config.set("inventory.slot", (Object)54);
            config.set("inventory.shopcurrency", (Object)"VAULT");
            ShopConfig.createShop(id, config);
            ConfigurationSection vanillaSection = config.createSection("VANILLA_STONE");
            vanillaSection.set("source", (Object)"STONE");
            vanillaSection.set("slot", (Object)10);
            vanillaSection.set("base_price", (Object)10.0);
            vanillaSection.set("current_price", (Object)10.0);
            vanillaSection.set("buy_multiplier", (Object)1.0);
            vanillaSection.set("sell_multiplier", (Object)0.7);
            vanillaSection.set("min_price", (Object)1.0);
            vanillaSection.set("max_price", (Object)100.0);
            vanillaSection.set("price_change_rate", (Object)0.5);
            vanillaSection.set("supply", (Object)0);
            vanillaSection.set("demand", (Object)0);
            vanillaSection.set("buy_enabled", (Object)true);
            vanillaSection.set("sell_enabled", (Object)true);
            ShopConfig.addItemToShop(id, "VANILLA_STONE", vanillaSection);
            ConfigurationSection mmoSection = config.createSection("MMOITEM_CUTLASS");
            mmoSection.set("source", (Object)"mmoitem:sword:cutlass");
            mmoSection.set("slot", (Object)11);
            mmoSection.set("base_price", (Object)50.0);
            mmoSection.set("current_price", (Object)50.0);
            mmoSection.set("buy_multiplier", (Object)1.0);
            mmoSection.set("sell_multiplier", (Object)0.7);
            mmoSection.set("min_price", (Object)10.0);
            mmoSection.set("max_price", (Object)200.0);
            mmoSection.set("price_change_rate", (Object)1.0);
            mmoSection.set("supply", (Object)0);
            mmoSection.set("demand", (Object)0);
            mmoSection.set("buy_enabled", (Object)true);
            mmoSection.set("sell_enabled", (Object)true);
            ShopConfig.addItemToShop(id, "MMOITEM_CUTLASS", mmoSection);
            player.sendMessage("Shop '" + id + "' created successfully.");
            ShopGUI.open(player, id);
            return true;
        }
        player.sendMessage("Usage: /sushishop add <category|shop> <id>");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return List.of("category", "shop");
        }
        if (args.length == 3 && (args[1].equalsIgnoreCase("category") || args[1].equalsIgnoreCase("shop"))) {
            return List.of("<id>");
        }
        return Collections.emptyList();
    }
}

