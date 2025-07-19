/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package mcsushi.dynamicshop.sushidynamicshop.pricehandler;

import java.util.UUID;
import mcsushi.dynamicshop.sushidynamicshop.init.PremiumInitializer;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopCurrency;
import mcsushi.dynamicshop.sushidynamicshop.util.CurrencyRegistry;
import mcsushi.dynamicshop.sushidynamicshop.util.ExpUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.PointsUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.VaultUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CurrencyHandler {
    public static boolean hasEnough(Player player, String currencyId, double amount) {
        ShopCurrency shopCurrency;
        currencyId = currencyId.toLowerCase();
        CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
        if (registry != null && registry.currencyExists(currencyId)) {
            double balance;
            UUID playerUUID = player.getUniqueId();
            String balanceStr = registry.getPlayerBalance(playerUUID, currencyId, "");
            try {
                balance = Double.parseDouble(balanceStr.replaceAll("[^0-9.]", ""));
            }
            catch (NumberFormatException e) {
                balance = 0.0;
            }
            return balance >= amount;
        }
        try {
            shopCurrency = ShopCurrency.valueOf(currencyId.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            shopCurrency = ShopCurrency.VAULT;
        }
        switch (shopCurrency) {
            case EXP: {
                return ExpUtil.hasEnough(player, (int)amount);
            }
            case POINTS: {
                return PointsUtil.hasPoints(player, (int)amount);
            }
            case VAULT: {
                Economy eco = VaultUtil.getEconomy();
                boolean hasEnough = eco != null && eco.has((OfflinePlayer)player, amount);
                return hasEnough;
            }
        }
        return false;
    }

    public static boolean withdraw(Player player, String currencyId, double amount) {
        ShopCurrency shopCurrency;
        currencyId = currencyId.toLowerCase();
        CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
        if (registry != null && registry.currencyExists(currencyId)) {
            UUID playerUUID = player.getUniqueId();
            boolean success = registry.adjustPlayerBalance(playerUUID, currencyId, amount, "remove");
            return success;
        }
        try {
            shopCurrency = ShopCurrency.valueOf(currencyId.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            shopCurrency = ShopCurrency.VAULT;
        }
        switch (shopCurrency) {
            case EXP: {
                return ExpUtil.removeLevel(player, (int)amount);
            }
            case POINTS: {
                return PointsUtil.withdrawPoints(player, (int)amount);
            }
            case VAULT: {
                Economy eco = VaultUtil.getEconomy();
                boolean success = eco != null && eco.withdrawPlayer((OfflinePlayer)player, amount).transactionSuccess();
                return success;
            }
        }
        return false;
    }

    public static void deposit(Player player, String currencyId, double amount) {
        ShopCurrency shopCurrency;
        currencyId = currencyId.toLowerCase();
        CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
        if (registry != null && registry.currencyExists(currencyId)) {
            UUID playerUUID = player.getUniqueId();
            registry.adjustPlayerBalance(playerUUID, currencyId, amount, "give");
            return;
        }
        try {
            shopCurrency = ShopCurrency.valueOf(currencyId.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            shopCurrency = ShopCurrency.VAULT;
        }
        switch (shopCurrency) {
            case EXP: {
                ExpUtil.addLevel(player, (int)amount);
                break;
            }
            case POINTS: {
                PointsUtil.depositPoints(player, (int)amount);
                break;
            }
            case VAULT: {
                Economy eco = VaultUtil.getEconomy();
                if (eco == null) break;
                eco.depositPlayer((OfflinePlayer)player, amount);
            }
        }
    }

    public static double getBalance(Player player, String currencyId) {
        ShopCurrency shopCurrency = ShopCurrency.fromString(currencyId = currencyId.toLowerCase());
        if (shopCurrency == ShopCurrency.CUSTOM) {
            CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
            if (registry != null) {
                UUID playerUUID = player.getUniqueId();
                String balanceStr = registry.getPlayerBalance(playerUUID, currencyId, "").replaceAll("[^0-9.]", "");
                return balanceStr.isEmpty() ? 0.0 : Double.parseDouble(balanceStr);
            }
            return 0.0;
        }
        switch (shopCurrency) {
            case EXP: {
                return ExpUtil.getLevel(player);
            }
            case POINTS: {
                return PointsUtil.getPoints(player);
            }
            case VAULT: {
                Economy eco = VaultUtil.getEconomy();
                return eco != null ? eco.getBalance((OfflinePlayer)player) : 0.0;
            }
        }
        return 0.0;
    }
}

