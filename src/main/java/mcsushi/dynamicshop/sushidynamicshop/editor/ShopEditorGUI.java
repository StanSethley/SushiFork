/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package mcsushi.dynamicshop.sushidynamicshop.editor;

import java.util.Arrays;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorHolder;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopEditorGUI {
    public static void open(Player player, String shopId, String itemKey) {
        ConfigurationSection section = ShopConfig.getShopConfig(shopId).getConfigurationSection(itemKey);
        if (section == null) {
            player.sendMessage(String.valueOf(ChatColor.RED) + "Item not found in shop.");
            return;
        }
        Inventory gui = Bukkit.createInventory((InventoryHolder)new ShopEditorHolder(shopId, itemKey), (int)45, (String)(String.valueOf(ChatColor.LIGHT_PURPLE) + "Edit Shop Item: " + itemKey));
        ItemStack border = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }
        for (int i = 0; i < 45; ++i) {
            if (i > 8 && i < 36 && i % 9 != 0 && i % 9 != 8) continue;
            gui.setItem(i, border);
        }
        ShopEditorGUI.setButton(gui, 10, Material.ITEM_FRAME, "Slot", String.valueOf(section.getInt("slot", 0)));
        ShopEditorGUI.setButton(gui, 11, Material.EMERALD, "Base Price", String.valueOf(section.getDouble("base_price", 0.0)));
        ShopEditorGUI.setButton(gui, 12, Material.DIAMOND, "Current Price", String.valueOf(section.getDouble("current_price", 0.0)));
        ShopEditorGUI.setButton(gui, 13, Material.LIME_DYE, "Buy Multiplier", String.valueOf(section.getDouble("buy_multiplier", 1.0)));
        ShopEditorGUI.setButton(gui, 14, Material.RED_DYE, "Sell Multiplier", String.valueOf(section.getDouble("sell_multiplier", 0.7)));
        ShopEditorGUI.setButton(gui, 19, Material.BOOK, "Min Price", String.valueOf(section.getDouble("min_price", 0.0)));
        ShopEditorGUI.setButton(gui, 20, Material.BOOK, "Max Price", String.valueOf(section.getDouble("max_price", 0.0)));
        ShopEditorGUI.setButton(gui, 21, Material.SUNFLOWER, "Supply", String.valueOf(section.getDouble("supply", 0.0)));
        ShopEditorGUI.setButton(gui, 22, Material.NETHER_STAR, "Demand", String.valueOf(section.getDouble("demand", 0.0)));
        ShopEditorGUI.setButton(gui, 23, section.getBoolean("buy_enabled", true) ? Material.LIME_WOOL : Material.RED_WOOL, "Can Buy", section.getBoolean("buy_enabled", true) ? "ON" : "OFF");
        ShopEditorGUI.setButton(gui, 24, section.getBoolean("sell_enabled", true) ? Material.LIME_WOOL : Material.RED_WOOL, "Can Sell", section.getBoolean("sell_enabled", true) ? "ON" : "OFF");
        ShopEditorGUI.setButton(gui, 30, Material.CHEST, "Source", section.getString("source", "N/A"));
        ShopEditorGUI.setButton(gui, 31, Material.ITEM_FRAME, "Preview", "Click to preview");
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(String.valueOf(ChatColor.RED) + "Back");
            back.setItemMeta(backMeta);
        }
        gui.setItem(40, back);
        player.openInventory(gui);
    }

    private static void setButton(Inventory gui, int slot, Material mat, String label, String value) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(String.valueOf(ChatColor.YELLOW) + "Edit " + label);
            meta.setLore(Arrays.asList(String.valueOf(ChatColor.GRAY) + "Current:", String.valueOf(ChatColor.GRAY) + value));
            item.setItemMeta(meta);
        }
        gui.setItem(slot, item);
    }
}

