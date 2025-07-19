/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.plugin.java.JavaPlugin
 */
package mcsushi.dynamicshop.sushidynamicshop.shop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mcsushi.dynamicshop.sushidynamicshop.pricehandler.PriceHandler;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopCurrency;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopConfig {
    private static final Map<String, FileConfiguration> shopMap = new HashMap<String, FileConfiguration>();

    public static void clearCache() {
        shopMap.clear();
    }

    public static void ensureDefaultShop(JavaPlugin plugin) {
        File defaultShop;
        File folder = new File(plugin.getDataFolder(), "shop");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!(defaultShop = new File(folder, "default.yml")).exists()) {
            plugin.saveResource("shop/default.yml", false);
        }
    }

    public static void loadAll(JavaPlugin plugin) {
        shopMap.clear();
        File folder = new File(plugin.getDataFolder(), "shop");
        if (!folder.exists()) {
            Bukkit.getLogger().warning("[ShopConfig] Shop folder not found.");
            return;
        }
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            Bukkit.getLogger().warning("[ShopConfig] No shop files found.");
            return;
        }
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration((File)file);
            String fileName = file.getName().replace(".yml", "");
            shopMap.put(fileName, (FileConfiguration)config);
            ConfigurationSection shopcurrencySection = config.getConfigurationSection("shopcurrency");
            if (shopcurrencySection != null) continue;
            Bukkit.getLogger().warning("[ShopConfig] 'shopcurrency' section is missing in shop '" + fileName + "'.");
        }
    }

    public static FileConfiguration getShopConfig(String shopId) {
        FileConfiguration config = shopMap.get(shopId);
        if (config == null) {
            Bukkit.getLogger().warning("[ShopConfig] Shop '" + shopId + "' configuration not found in shopMap.");
        }
        return config;
    }

    public static boolean hasShop(String shopId) {
        return shopMap.containsKey(shopId);
    }

    public static List<String> getShopItems(String shopId) {
        FileConfiguration config = shopMap.get(shopId);
        if (config == null) {
            return Collections.emptyList();
        }
        ArrayList<String> keys = new ArrayList<String>();
        for (String key : config.getKeys(false)) {
            if (!config.isConfigurationSection(key) || !config.contains(key + ".source")) continue;
            keys.add(key);
        }
        return keys;
    }

    public static String getSource(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getString("source", "AIR");
    }

    public static int getSlot(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getInt("slot", 0);
    }

    public static double getCurrentPrice(String shopId, String key) {
        double raw = ShopConfig.getSection(shopId, key).getDouble("current_price", 0.0);
        double min = ShopConfig.getMinPrice(shopId, key);
        double max = ShopConfig.getMaxPrice(shopId, key);
        return Math.max(min, Math.min(max, raw));
    }

    public static double getMinPrice(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getDouble("min_price", 0.0);
    }

    public static double getMaxPrice(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getDouble("max_price", 0.0);
    }

    public static double getSupply(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getDouble("supply", 0.0);
    }

    public static boolean isRealSupply(String shopId, String key) {
        ConfigurationSection section = ShopConfig.getSection(shopId, key);
        return section != null && section.getBoolean("real_supply", false);
    }

    public static double getDemand(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getDouble("demand", 0.0);
    }

    public static double getPriceChangeRate(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getDouble("price_change_rate", 0.0);
    }

    public static boolean canBuy(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getBoolean("buy_enabled", true);
    }

    public static boolean canSell(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getBoolean("sell_enabled", true);
    }

    public static double getBasePrice(String shopId, String key) {
        ConfigurationSection section = ShopConfig.getSection(shopId, key);
        if (section == null) {
            Bukkit.getLogger().warning("[SushiDynamicShop] Shop ID: " + shopId + " - Item Key: " + key + " not found.");
            return Double.NaN;
        }
        return section.getDouble("base_price", Double.NaN);
    }

    public static double getBuyMultiplier(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getDouble("buy_multiplier", 1.0);
    }

    public static double getSellMultiplier(String shopId, String key) {
        return ShopConfig.getSection(shopId, key).getDouble("sell_multiplier", 0.7);
    }

    public static void incrementSupply(String shopId, String key, int amount) {
        ConfigurationSection section = ShopConfig.getSection(shopId, key);
        if (section != null) {
            double currentSupply = section.getDouble("supply", 0.0);
            section.set("supply", (Object)(currentSupply + (double)amount));
            double newPrice = PriceHandler.getCurrentPrice(shopId, key);
            section.set("current_price", (Object)newPrice);
            ShopConfig.saveShop(shopId);
        }
    }

    public static void incrementDemand(String shopId, String key, int amount) {
        ConfigurationSection section = ShopConfig.getSection(shopId, key);
        if (section != null) {
            double currentDemand = section.getDouble("demand", 0.0);
            section.set("demand", (Object)(currentDemand + (double)amount));
            double newPrice = PriceHandler.getCurrentPrice(shopId, key);
            section.set("current_price", (Object)newPrice);
            ShopConfig.saveShop(shopId);
        }
    }

    public static Map<String, FileConfiguration> getShopConfigMap() {
        return shopMap;
    }

    public static void saveShop(String shopId) {
        File folder = new File("plugins/Sushidynamicshop/shop");
        File file = new File(folder, shopId + ".yml");
        try {
            shopMap.get(shopId).save(file);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ConfigurationSection getSection(String shopId, String key) {
        FileConfiguration config = shopMap.get(shopId);
        return config != null ? config.getConfigurationSection(key) : null;
    }

    public static ShopCurrency getCurrency(String shopId) {
        FileConfiguration config = ShopConfig.getShopConfig(shopId);
        if (config == null) {
            Bukkit.getLogger().warning("[ShopConfig] Shop '" + shopId + "' configuration not found.");
            return ShopCurrency.VAULT;
        }
        ConfigurationSection section = config.getConfigurationSection("shopcurrency");
        if (section == null) {
            Bukkit.getLogger().warning("[ShopConfig] 'shopcurrency' section is missing in shop '" + shopId + "'.");
            return ShopCurrency.VAULT;
        }
        String type = section.getString("type", "VAULT").toUpperCase();
        try {
            return ShopCurrency.valueOf(type);
        }
        catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("[ShopConfig] Invalid shopcurrency type for shop '" + shopId + "': " + type);
            return ShopCurrency.VAULT;
        }
    }

    public static String getCustomCurrencyId(String shopId) {
        FileConfiguration config = ShopConfig.getShopConfig(shopId);
        if (config == null) {
            Bukkit.getLogger().warning("[ShopConfig] Shop '" + shopId + "' configuration not found.");
            return "";
        }
        ConfigurationSection section = config.getConfigurationSection("shopcurrency");
        if (section == null) {
            Bukkit.getLogger().warning("[ShopConfig] 'shopcurrency' section is missing in shop '" + shopId + "'.");
            return "";
        }
        String type = section.getString("type", "VAULT").toUpperCase();
        String currencyId = section.getString("currency", "").toLowerCase();
        if (!"CUSTOM".equals(type)) {
            return "";
        }
        if (currencyId.isEmpty()) {
            Bukkit.getLogger().warning("[ShopConfig] Shop '" + shopId + "' is set to CUSTOM but no 'currency' key is defined.");
            return "no_currency_set";
        }
        return currencyId;
    }

    public static void createShop(String shopId, YamlConfiguration config) {
        File folder = new File("plugins/Sushidynamicshop/shop");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, shopId + ".yml");
        try {
            config.save(file);
            shopMap.put(shopId, (FileConfiguration)config);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addItemToShop(String shopId, String itemKey, ConfigurationSection section) {
        FileConfiguration config = shopMap.get(shopId);
        if (config == null) {
            return;
        }
        config.createSection(itemKey);
        for (String key : section.getKeys(false)) {
            config.set(itemKey + "." + key, section.get(key));
        }
        ShopConfig.saveShop(shopId);
    }

    public static void setSupply(String shopId, String key, int supply) {
        ConfigurationSection section = ShopConfig.getSection(shopId, key);
        if (section != null) {
            section.set("supply", (Object)supply);
            ShopConfig.saveShop(shopId);
        }
    }

    public static String getShopName(String shopId) {
        FileConfiguration config = ShopConfig.getShopConfig(shopId);
        if (config == null) {
            return shopId;
        }
        return config.getString("inventory.name", shopId);
    }

    public static int getSlotCount(String shopId) {
        FileConfiguration config = ShopConfig.getShopConfig(shopId);
        if (config == null) {
            return 54;
        }
        int slot = config.getInt("inventory.slot", 54);
        if (slot % 9 != 0 || slot < 9 || slot > 54) {
            slot = 54;
        }
        return slot;
    }

    public static Material getShopDeco(String shopId) {
        FileConfiguration config = ShopConfig.getShopConfig(shopId);
        if (config == null) {
            return Material.PINK_STAINED_GLASS_PANE;
        }
        String decoMaterial = config.getString("inventory.deco", "PINK_STAINED_GLASS_PANE");
        Material material = Material.matchMaterial((String)decoMaterial);
        return material != null ? material : Material.PINK_STAINED_GLASS_PANE;
    }
}

