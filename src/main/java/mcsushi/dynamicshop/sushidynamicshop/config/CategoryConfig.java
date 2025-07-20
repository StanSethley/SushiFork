/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package mcsushi.dynamicshop.sushidynamicshop.config;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CategoryConfig {
    private static FileConfiguration config;
    private static File file;

    public static void setup(JavaPlugin plugin) {
        file = new File(plugin.getDataFolder(), "category.yml");
        if (!file.exists()) {
            plugin.saveResource("category.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return config;
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static boolean hasCategory(String id) {
        return config.contains("categories." + id);
    }

    public static void createCategory(String id) {
        config.createSection("categories." + id);
    }

    public static Set<String> getCategoryIds() {
        ConfigurationSection section = config.getConfigurationSection("categories");
        return section != null ? section.getKeys(false) : Collections.emptySet();
    }

    public static String getCategoryIdBySlot(int slot) {
        ConfigurationSection categories = config.getConfigurationSection("categories");
        if (categories == null) {
            return null;
        }
        for (String key : categories.getKeys(false)) {
            ConfigurationSection section = categories.getConfigurationSection(key);
            if (section == null || section.getInt("slot", -1) != slot) continue;
            return key;
        }
        return null;
    }

    public static String getShopIdBySlot(int slot) {
        ConfigurationSection categories = config.getConfigurationSection("categories");
        if (categories == null) {
            return null;
        }
        for (String key : categories.getKeys(false)) {
            ConfigurationSection section = categories.getConfigurationSection(key);
            if (section == null || section.getInt("slot", -1) != slot) continue;
            return section.getString("shopid", key);
        }
        return null;
    }

    public static String getPermissionBySlot(int slot) {
        ConfigurationSection categories = config.getConfigurationSection("categories");
        if (categories == null) {
            Bukkit.getLogger().warning("[Sushidynamicshop] Category not found in category.yml");
            return "";
        }
        boolean categoryFound = false;
        for (String key : categories.getKeys(false)) {
            ConfigurationSection section = categories.getConfigurationSection(key);
            if (section == null) continue;
            categoryFound = true;
            if (!section.contains("slot")) {
                Bukkit.getLogger().warning("[Sushidynamicshop] Category slot not found in \"" + key + "\"");
                continue;
            }
            int configSlot = section.getInt("slot", -1);
            if (configSlot != slot) continue;
            String permission = section.getString("permission", "");
            if (permission.isEmpty()) {
                return "";
            }
            return permission;
        }
        if (!categoryFound) {
            Bukkit.getLogger().warning("[Sushidynamicshop] No valid categories found in category.yml");
        }
        return "";
    }

    /* -------------------------------------
     *  Helper methods for OpenCommand
     * ------------------------------------- */

    /**
     * Get the permission node required for a given shop ID
     * Returns an empty string if no permission is defined
     */
    public static String getPermissionByShopId(String shopId) {
        ConfigurationSection categories = config.getConfigurationSection("categories");
        if (categories == null) {
            return "";
        }
        for (String key : categories.getKeys(false)) {
            ConfigurationSection section = categories.getConfigurationSection(key);
            if (section == null) continue;

            String cfgShopId = section.getString("shopid", key);
            if (cfgShopId != null && cfgShopId.equalsIgnoreCase(shopId)) {
                return section.getString("permission", "");
            }
        }
        return "";
    }

    /**
     * Retrieve a set containing every shop ID defined in the category config
     */
    public static Set<String> getAllShopIds() {
        Set<String> result = new HashSet<>();
        ConfigurationSection categories = config.getConfigurationSection("categories");
        if (categories == null) {
            return result;
        }
        for (String key : categories.getKeys(false)) {
            ConfigurationSection section = categories.getConfigurationSection(key);
            if (section == null) continue;

            String cfgShopId = section.getString("shopid", key);
            if (cfgShopId != null && !cfgShopId.isEmpty()) {
                result.add(cfgShopId);
            }
        }
        return result;
    }
}