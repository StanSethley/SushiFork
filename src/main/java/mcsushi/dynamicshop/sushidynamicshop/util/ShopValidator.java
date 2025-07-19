/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 */
package mcsushi.dynamicshop.sushidynamicshop.util;

import java.util.List;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class ShopValidator {
    public static void validateShopFile(String shopId) {
        FileConfiguration config = ShopConfig.getShopConfig(shopId);
        boolean changed = false;
        if (!config.contains("inventory.name")) {
            config.set("inventory.name", (Object)"&fUnnamed Shop");
            changed = true;
        }
        if (!config.contains("inventory.slot")) {
            config.set("inventory.slot", (Object)54);
            changed = true;
        }
        if (!config.contains("inventory.deco")) {
            config.set("inventory.deco", (Object)"PINK_STAINED_GLASS_PANE");
            changed = true;
        }
        if (!config.contains("shopcurrency")) {
            config.createSection("shopcurrency");
            config.set("shopcurrency.type", (Object)"VAULT");
            config.set("shopcurrency.currency", (Object)"test01");
            changed = true;
        }
        List<String> keys = ShopConfig.getShopItems(shopId);
        for (String key : keys) {
            String path = key + ".";
            if (!config.contains(path + "source")) {
                config.set(path + "source", (Object)"STONE");
                changed = true;
            }
            if (!config.contains(path + "slot")) {
                config.set(path + "slot", (Object)10);
                changed = true;
            }
            if (!config.contains(path + "base_price")) {
                config.set(path + "base_price", (Object)10.0);
                changed = true;
            }
            if (!config.contains(path + "current_price")) {
                config.set(path + "current_price", (Object)10.0);
                changed = true;
            }
            if (!config.contains(path + "min_price")) {
                config.set(path + "min_price", (Object)1.0);
                changed = true;
            }
            if (!config.contains(path + "max_price")) {
                config.set(path + "max_price", (Object)100.0);
                changed = true;
            }
            if (!config.contains(path + "price_change_rate")) {
                config.set(path + "price_change_rate", (Object)0.05);
                changed = true;
            }
            if (!config.contains(path + "supply")) {
                config.set(path + "supply", (Object)0);
                changed = true;
            }
            if (!config.contains(path + "demand")) {
                config.set(path + "demand", (Object)0);
                changed = true;
            }
            if (!config.contains(path + "buy_enabled")) {
                config.set(path + "buy_enabled", (Object)true);
                changed = true;
            }
            if (!config.contains(path + "sell_enabled")) {
                config.set(path + "sell_enabled", (Object)true);
                changed = true;
            }
            if (config.contains(path + "real_supply")) continue;
            config.set(path + "real_supply", (Object)false);
            changed = true;
        }
        if (changed) {
            ShopConfig.saveShop(shopId);
            System.out.println("[SushiShop] Patched missing keys in shop: " + shopId);
        }
    }
}

