package mcsushi.dynamicshop.sushidynamicshop.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import mcsushi.dynamicshop.sushidynamicshop.config.CategoryConfig;
import mcsushi.dynamicshop.sushidynamicshop.gui.CategoryMenuHolder;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopGUI;
import mcsushi.dynamicshop.sushidynamicshop.util.TranslationUtil;
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

public class CategoryMenuGUI {
    public static void open(Player player) {
        HashSet<Integer> usedSlots = new HashSet<>();
        String title = ChatColor.translateAlternateColorCodes('&', CategoryConfig.get().getString("name", "&fDefault Shop"));

        int slotSize = CategoryConfig.get().getInt("slot", 54);
        if (slotSize != 27 && slotSize != 36 && slotSize != 45 && slotSize != 54) {
            slotSize = 54;
        }

        Inventory gui = Bukkit.createInventory(new CategoryMenuHolder(), slotSize, title);

        String decoMaterial = CategoryConfig.get().getString("deco", "PINK_STAINED_GLASS_PANE");
        Material decoMat = Material.matchMaterial(decoMaterial);
        if (decoMat == null) {
            decoMat = Material.PINK_STAINED_GLASS_PANE;
        }

        ItemStack deco = new ItemStack(decoMat);
        ItemMeta decoMeta = deco.getItemMeta();
        if (decoMeta != null) {
            decoMeta.setDisplayName(" ");
            deco.setItemMeta(decoMeta);
        }

        int[] decoSlots;
        switch (slotSize) {
            case 27 -> decoSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
            case 36 -> decoSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
            case 45 -> decoSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
            case 54 -> decoSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
            default -> decoSlots = new int[]{};
        }

        for (int slot : decoSlots) {
            gui.setItem(slot, deco);
        }

        ConfigurationSection categories = CategoryConfig.get().getConfigurationSection("categories");
        if (categories != null) {
            for (String key : categories.getKeys(false)) {
                ConfigurationSection section = categories.getConfigurationSection(key);
                if (section == null) continue;

                Material mat = Material.matchMaterial(section.getString("material", "STONE"));
                int slot = section.getInt("slot", -1);

                if (mat == null || slot < 0 || slot >= gui.getSize()) continue;

                if (!usedSlots.add(slot)) {
                    Bukkit.getLogger().warning("[Sushidynamicshop] Duplicate slot in category: " + key + " at slot " + slot);
                    continue;
                }

                ItemStack item = VanillaItemFactory.createIconFromSection(section);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', section.getString("name", key)));

                    List<String> rawLore = section.getStringList("lore");
                    ArrayList<String> coloredLore = new ArrayList<>();
                    for (String line : rawLore) {
                        coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
                    }
                    meta.setLore(coloredLore);
                    item.setItemMeta(meta);
                }
                gui.setItem(slot, item);
            }
        }

        int closeSlot;
        switch (slotSize) {
            case 27 -> closeSlot = 22;
            case 36 -> closeSlot = 31;
            case 45 -> closeSlot = 40;
            case 54 -> closeSlot = 49;
            default -> closeSlot = slotSize - 5;
        }

        if (closeSlot >= 0 && closeSlot < slotSize) {
            ItemStack close = new ItemStack(Material.BARRIER);
            ItemMeta closeMeta = close.getItemMeta();
            if (closeMeta != null) {
                closeMeta.setDisplayName(TranslationUtil.get("close"));
                close.setItemMeta(closeMeta);
            }
            gui.setItem(closeSlot, close);
        } else {
            Bukkit.getLogger().warning("[Sushidynamicshop] Invalid closeSlot: " + closeSlot + " for size: " + slotSize);
        }

        player.openInventory(gui);
    }

    public static void handleClick(Player player, int slot) {
        if (slot == 49) {
            player.closeInventory();
            return;
        }

        String shopId = CategoryConfig.getShopIdBySlot(slot);
        if (shopId == null || shopId.isEmpty()) {
            return;
        }

        String permission = CategoryConfig.getPermissionBySlot(slot);
        if (!permission.isEmpty() && !player.hasPermission(permission)) {
            player.sendMessage(TranslationUtil.get("no_shop_permission"));
            return;
        }

        ShopGUI.open(player, shopId);
    }
}
