package mcsushi.dynamicshop.sushidynamicshop.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mcsushi.dynamicshop.sushidynamicshop.config.CategoryConfig;
import mcsushi.dynamicshop.sushidynamicshop.editor.holder.CategoryEditorHolder;
import mcsushi.dynamicshop.sushidynamicshop.gui.CategoryMenuHolder;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopHolder;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopEditorHolder;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.InventoryHolder;

public class GuiSlotHolder {

    public static Set<Integer> getReservedSlotsFor(InventoryHolder holder) {
        if (holder instanceof CategoryMenuHolder) {
            return getCategoryMenuReservedSlots();
        }
        if (holder instanceof CategoryEditorHolder) {
            return getCategoryEditorReservedSlots();
        }
        if (holder instanceof ShopHolder) {
            return getShopReservedSlots();
        }
        if (holder instanceof ShopEditorHolder) {
            return getShopEditorReservedSlots();
        }
        return new HashSet<>();
    }

    public static int findAvailableSlot(InventoryHolder holder) {
        HashSet<Integer> used = new HashSet<>(getReservedSlotsFor(holder));

        if ((holder instanceof CategoryMenuHolder || holder instanceof CategoryEditorHolder)) {
            ConfigurationSection categories = CategoryConfig.get().getConfigurationSection("categories");
            if (categories != null) {
                for (String key : categories.getKeys(false)) {
                    ConfigurationSection section = categories.getConfigurationSection(key);
                    if (section == null) continue;
                    int slot = section.getInt("slot", -1);
                    if (slot < 0 || slot >= 54) continue;
                    used.add(slot);
                }
            }
        }

        if (holder instanceof ShopHolder || holder instanceof ShopEditorHolder) {
            for (String shopId : ShopConfig.getShopConfigMap().keySet()) {
                List<String> itemKeys = ShopConfig.getShopItems(shopId);
                for (String key : itemKeys) {
                    int slot = ShopConfig.getSlot(shopId, key);
                    if (slot < 0 || slot >= 54) continue;
                    used.add(slot);
                }
            }
        }

        for (int i = 0; i < 54; i++) {
            if (!used.contains(i)) {
                Bukkit.getLogger().info("[DEBUG] Available slot found: " + i + " for GUI: " + holder.getClass().getSimpleName());
                return i;
            }
        }

        Bukkit.getLogger().warning("[DEBUG] No available slot found for GUI: " + holder.getClass().getSimpleName());
        return -1;
    }

    public static Set<Integer> getCategoryMenuReservedSlots() {
        return new HashSet<>(Arrays.asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17, 18, 26,
                27, 35, 36, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53
        ));
    }

    public static Set<Integer> getCategoryEditorReservedSlots() {
        return new HashSet<>(Arrays.asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17, 18, 22, 26
        ));
    }

    public static Set<Integer> getShopReservedSlots() {
        return getCategoryMenuReservedSlots();
    }

    public static Set<Integer> getShopEditorReservedSlots() {
        return new HashSet<>(Arrays.asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 17, 18, 26, 27,
                35, 36, 37, 38, 39, 40, 41, 42, 43, 44
        ));
    }
}
