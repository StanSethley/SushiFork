package mcsushi.dynamicshop.sushidynamicshop.util;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mcsushi.dynamicshop.sushidynamicshop.Sushidynamicshop;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TranslationUtil {
    private static FileConfiguration translation;

    public static void load() {
        File file = new File(Sushidynamicshop.getInstance().getDataFolder(), "translation.yml");
        if (!file.exists()) {
            Sushidynamicshop.getInstance().saveResource("translation.yml", false);
        }
        translation = YamlConfiguration.loadConfiguration(file);
        validateKeys(file);
    }

    private static void validateKeys(File file) {
        YamlConfiguration userConfig = YamlConfiguration.loadConfiguration(file);
        InputStream stream = Sushidynamicshop.getInstance().getResource("translation.yml");
        if (stream == null) {
            return;
        }
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration((Reader) new InputStreamReader(stream));
        ConfigurationSection defaultSection = defaultConfig.getConfigurationSection("translation");
        boolean updated = false;

        if (defaultSection != null) {
            for (String key : defaultSection.getKeys(false)) {
                String fullKey = "translation." + key;
                if (!userConfig.contains(fullKey)) {
                    userConfig.set(fullKey, defaultConfig.get(fullKey));
                    updated = true;
                }
            }
        }

        String[][] requiredKeys = {
                {"buy", "&eBuy: %buy_price% %currency%"},
                {"sell", "&eSell: %sell_price% %currency%"},
                {"buy_level", "&aBuy: &f%level_price% Levels"},
                {"sell_level", "&cSell: &f%level_price% Levels"},
                {"buy_points", "&aBuy: &f%points_price% Points"},
                {"sell_points", "&cSell: &f%points_price% Points"},
                {"supply_demand", "&7Stock: &f%stock% &8| &7Demand: &f%demand%"},
                {"fluctuation_positive", "&a+%percentage%%"},
                {"fluctuation_negative", "&c%percentage%%"},
                {"fluctuation_zero", "&7%percentage%%"},
                {"fluctuation_na", "&7N/A"},
                {"bought", "&aYou bought item successfully."},
                {"sold", "&aYou received %amount% for selling items."},
                {"sold_custom", "&aYou received %amount% %currency% for selling items."},
                {"sold_exp", "&aYou received %amount% EXP for selling items."},
                {"sold_points", "&aYou received %amount% Points for selling items."},
                {"not_enough_money", "&cYou don't have enough money."},
                {"not_enough_level", "&cYou need at least %required% levels."},
                {"not_enough_points", "&cYou don't have enough points."},
                {"no_item_to_sell", "&cYou don't have item to sell."},
                {"nopermission", "You do not have permission to use /sushishop."},
                {"usage", "[SushiShop] Usage: /sushishop <reload|...>"},
                {"unknowncommand", "Unknown subcommand. Use /sushishop <reload|...>"},
                {"console_use", "Console cannot use this command."},
                {"nopermission_reload", "&cYou do not have permission to use /sushishop reload."},
                {"no_shop_permission", "&cYou do not have permission to open this shop."},
                {"reload_success", "&a[SushiShop] Reloaded %count% YML file(s) in %time% ms."},
                {"next", "&b\u00bb Next"},
                {"previous", "&b\u00ab Previous"},
                {"close", "&c\u2715 Close"},
                {"back", "&cBack"},
                {"left_click_buy", "&eLeft click to Buy"},
                {"shift_left_click_buy_all", "&eShift + Left Click to Buy all"},
                {"right_click_sell", "&6Right click to Sell"},
                {"shift_right_click_sell_all", "&6Shift + Right Click to Sell all"},
                {"no_currency_set", "&cThis shop has no custom currency set."},
                {"not_enough_custom", "&cYou do not have enough %currency%. Required: %required%"},
                {"not_enough_space", "&cYou do not have enough inventory space to buy this item"},
                {"out_of_stock", "&cThis item is out of stock."}
        };

        for (String[] pair : requiredKeys) {
            String fullKey = "translation." + pair[0];
            if (!userConfig.contains(fullKey)) {
                userConfig.set(fullKey, pair[1]);
                updated = true;
            }
        }

        if (updated) {
            try {
                userConfig.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String get(String key) {
        if (translation == null) {
            load();
        }
        String raw = translation.getString("translation." + key, key);
        raw = ChatColor.translateAlternateColorCodes('&', raw);
        return applyGradient(raw);
    }

    public static String get(String key, Map<String, String> placeholders) {
        String text = get(key);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String value = entry.getValue();
                if ("amount".equalsIgnoreCase(entry.getKey())) {
                    try {
                        double amount = Double.parseDouble(value);
                        value = String.format("%.2f", amount);
                    } catch (NumberFormatException ignored) {}
                }
                text = text.replace("%" + entry.getKey() + "%", value);
            }
        }
        return text;
    }

    public static String getFluctuation(double percentage) {
        String key;
        if (Double.isNaN(percentage)) {
            key = "fluctuation_na";
        } else if (percentage > 0.0) {
            key = "fluctuation_positive";
        } else if (percentage < 0.0) {
            key = "fluctuation_negative";
        } else {
            key = "fluctuation_zero";
        }
        return get(key, Map.of("percentage", String.format("%.0f", percentage)));
    }

    private static String applyGradient(String text) {
        Pattern pattern = Pattern.compile("<gradient:(#[0-9a-fA-F]{6}):(#(?:[0-9a-fA-F]{6}))>(.*?)</gradient>");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String startColor = matcher.group(1);
            String endColor = matcher.group(2);
            String content = matcher.group(3);
            String gradientText = generateGradient(content, startColor, endColor);
            text = text.replace(matcher.group(0), gradientText);
        }
        return text;
    }

    private static String generateGradient(String content, String startColor, String endColor) {
        StringBuilder builder = new StringBuilder();
        Color start = Color.decode(startColor);
        Color end = Color.decode(endColor);
        int length = content.length();
        for (int i = 0; i < length; i++) {
            float ratio = (float) i / (length - 1);
            int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
            int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
            int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
            String hex = String.format("#%02x%02x%02x", red, green, blue);
            builder.append(ChatColor.of(hex)).append(content.charAt(i));
        }
        return builder.toString();
    }
}
