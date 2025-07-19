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

import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import mcsushi.dynamicshop.sushidynamicshop.config.CategoryConfig;
import mcsushi.dynamicshop.sushidynamicshop.editor.holder.CategoryEditorHolder;
import mcsushi.dynamicshop.sushidynamicshop.util.VanillaItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CategoryEditorGUI {
    public static void open(Player player, String categoryId) {
        int i;
        ConfigurationSection section = CategoryConfig.get().getConfigurationSection("categories." + categoryId);
        if (section == null) {
            section = CategoryConfig.get().createSection("categories." + categoryId);
        }
        HashSet<Integer> reserved = new HashSet<Integer>(Arrays.asList(new Integer[0]));
        if (!section.contains("name")) {
            section.set("name", (Object) categoryId);
        }
        if (!section.contains("material")) {
            section.set("material", (Object) "STONE");
        }
        if (!section.contains("slot")) {
            HashSet<Integer> usedSlots = new HashSet<Integer>();
            ConfigurationSection all = CategoryConfig.get().getConfigurationSection("categories");
            if (all != null) {
                for (String key : all.getKeys(false)) {
                    ConfigurationSection cat = all.getConfigurationSection(key);
                    if (cat == null || !cat.contains("slot")) continue;
                    usedSlots.add(cat.getInt("slot"));
                }
            }
            int freeSlot = -1;
            for (i = 0; i < 54; ++i) {
                if (usedSlots.contains(i) || reserved.contains(i)) continue;
                freeSlot = i;
                break;
            }
            section.set("slot", (Object) (freeSlot == -1 ? 10 : freeSlot));
            Bukkit.getLogger().info("[DEBUG] Slot assigned to category '" + categoryId + "': " + (freeSlot == -1 ? 10 : freeSlot));
        }
        if (!section.contains("shopid")) {
            section.set("shopid", (Object) categoryId);
        }
        if (!section.contains("permission")) {
            section.set("permission", (Object) "");
        }
        if (!section.contains("lore")) {
            section.set("lore", new ArrayList());
        }
        if (!section.contains("CustomModelData")) {
            section.set("CustomModelData", null);
        }
        CategoryConfig.save();
        Inventory gui = Bukkit.createInventory((InventoryHolder) new CategoryEditorHolder(categoryId), (int) 27, (String) (String.valueOf(ChatColor.LIGHT_PURPLE) + "Edit Category: " + categoryId));
        ItemStack border = new ItemStack(Material.PINK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }
        for (i = 0; i < gui.getSize(); ++i) {
            gui.setItem(i, border);
        }
        gui.setItem(10, VanillaItemFactory.createIconFromSection(section));
        CategoryEditorGUI.setButton(gui, 11, Material.ITEM_FRAME, "slot", String.valueOf(section.getInt("slot")));
        CategoryEditorGUI.setButton(gui, 12, Material.CHEST, "shopid", section.getString("shopid"));
        CategoryEditorGUI.setButton(gui, 13, Material.NAME_TAG, "name", section.getString("name"));
        CategoryEditorGUI.setButton(gui, 14, Material.BOOK, "lore", String.join((CharSequence) "\n", section.getStringList("lore")));
        CategoryEditorGUI.setButton(gui, 15, Material.STRUCTURE_VOID, "permission", section.getString("permission"));
        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(String.valueOf(ChatColor.RED) + "Back");
            back.setItemMeta(backMeta);
        }
        gui.setItem(22, back);
        ItemMeta iconMeta = gui.getItem(10).getItemMeta();
        if (iconMeta != null) {
            iconMeta.setDisplayName(String.valueOf(ChatColor.YELLOW) + "Edit Icon");
            gui.getItem(10).setItemMeta(iconMeta);
        }
        player.openInventory(gui);
    }

    private static void setButton(Inventory gui, int slot, Material material, String label, String value) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "Edit " + label);
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Current: " + value);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        gui.setItem(slot, item);
    }
}
