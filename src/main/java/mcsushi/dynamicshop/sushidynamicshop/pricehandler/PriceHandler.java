/*
 * Decompiled with CFR 0.152.
 */
package mcsushi.dynamicshop.sushidynamicshop.pricehandler;

import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;

public class PriceHandler {
    public static double getCurrentPrice(String shopId, String key) {
        double basePrice = ShopConfig.getBasePrice(shopId, key);
        double supply = ShopConfig.getSupply(shopId, key);
        double demand = ShopConfig.getDemand(shopId, key);
        double rate = ShopConfig.getPriceChangeRate(shopId, key);
        return basePrice + (demand - supply) * rate;
    }

    public static double getBuyPrice(String shopId, String key) {
        double currentPrice = PriceHandler.getCurrentPrice(shopId, key);
        double min = ShopConfig.getMinPrice(shopId, key);
        double max = ShopConfig.getMaxPrice(shopId, key);
        double clamped = Math.max(min, Math.min(max, currentPrice));
        double multiplier = ShopConfig.getBuyMultiplier(shopId, key);
        return clamped * multiplier;
    }

    public static double getSellPrice(String shopId, String key) {
        double currentPrice = PriceHandler.getCurrentPrice(shopId, key);
        double min = ShopConfig.getMinPrice(shopId, key);
        double max = ShopConfig.getMaxPrice(shopId, key);
        double clamped = Math.max(min, Math.min(max, currentPrice));
        double multiplier = ShopConfig.getSellMultiplier(shopId, key);
        return clamped * multiplier;
    }

    public static double calculateBuyPrice(String shopId, String key, int supply, int demand) {
        double base = ShopConfig.getBasePrice(shopId, key);
        double rate = ShopConfig.getPriceChangeRate(shopId, key);
        double multiplier = ShopConfig.getBuyMultiplier(shopId, key);
        double raw = base + (double)(demand - supply) * rate;
        double clamped = Math.max(ShopConfig.getMinPrice(shopId, key), Math.min(ShopConfig.getMaxPrice(shopId, key), raw));
        switch (ShopConfig.getCurrency(shopId)) {
            case POINTS: {
                return (int)Math.round(clamped * multiplier);
            }
        }
        return clamped * multiplier;
    }

    public static double calculateSellPrice(String shopId, String key, int supply, int demand) {
        double base = ShopConfig.getBasePrice(shopId, key);
        double rate = ShopConfig.getPriceChangeRate(shopId, key);
        double multiplier = ShopConfig.getSellMultiplier(shopId, key);
        double raw = base + (double)(demand - supply) * rate;
        double clamped = Math.max(ShopConfig.getMinPrice(shopId, key), Math.min(ShopConfig.getMaxPrice(shopId, key), raw));
        switch (ShopConfig.getCurrency(shopId)) {
            case POINTS: {
                return (int)Math.round(clamped * multiplier);
            }
        }
        return clamped * multiplier;
    }
}

