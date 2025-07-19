/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 */
package mcsushi.dynamicshop.sushidynamicshop.util;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import mcsushi.dynamicshop.sushidynamicshop.init.PremiumInitializer;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.util.PremiumChecker;
import mcsushi.dynamicshop.sushidynamicshop.util.TranslationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemDisplayUtil {
    private static final Pattern unicodePattern = Pattern.compile("[\u2300-\u23ff\u25a0-\u25ff\u2b50-\u2bff]");

    public static String resolveName(FileConfiguration config, String itemKey, ItemStack item, boolean isMMOItem) {
        String customName;
        if (config.contains("items." + itemKey + ".name") && (customName = config.getString("items." + itemKey + ".name")) != null && !customName.trim().isEmpty()) {
            return customName;
        }
        if (isMMOItem) {
            return ItemDisplayUtil.getFilteredMMOItemName(item);
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        return ItemDisplayUtil.formatMaterialName(item.getType());
    }

    private static String formatMaterialName(Material mat) {
        String name = mat.name().toLowerCase().replace("_", " ");
        String[] parts = name.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            builder.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
        }
        return builder.toString().trim();
    }

    private static String getFilteredMMOItemName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return unicodePattern.matcher(meta.getDisplayName()).replaceAll("").trim();
        }
        return "Unnamed MMOItem";
    }

    public static void appendShopLore(ItemStack item, String shopId, String itemId, double buyPrice, double sellPrice, double supply, double demand, boolean canBuy, boolean canSell, String currency) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null && (meta = Bukkit.getItemFactory().getItemMeta(item.getType())) == null) {
            return;
        }
        ArrayList<String> lore = meta.hasLore() ? new ArrayList<String>(meta.getLore()) : new ArrayList();
        lore.add("");
        boolean isPremium = PremiumChecker.isPremium();
        double basePrice = ShopConfig.getBasePrice(shopId, itemId);
        String buyFluctuation = " ";
        String sellFluctuation = " ";
        if (isPremium) {
            buyFluctuation = PremiumInitializer.getFluctuationDisplay(basePrice, buyPrice);
            sellFluctuation = PremiumInitializer.getFluctuationDisplay(basePrice, sellPrice);
        }
        if (canBuy) {
            String buyLore = TranslationUtil.get("buy", Map.of("buy_price", String.format("%.2f", buyPrice), "currency", currency.isEmpty() ? "" : " " + currency)) + " " + buyFluctuation;
            lore.add(buyLore.trim());
        }
        if (canSell) {
            String sellLore = TranslationUtil.get("sell", Map.of("sell_price", String.format("%.2f", sellPrice), "currency", currency.isEmpty() ? "" : " " + currency)) + " " + sellFluctuation;
            lore.add(sellLore.trim());
        }
        lore.add(TranslationUtil.get("supply_demand", Map.of("stock", String.valueOf((int)supply), "demand", String.valueOf((int)demand))));
        lore.add(TranslationUtil.get("left_click_buy"));
        lore.add(TranslationUtil.get("shift_left_click_buy_all"));
        lore.add(TranslationUtil.get("right_click_sell"));
        lore.add(TranslationUtil.get("shift_right_click_sell_all"));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}

