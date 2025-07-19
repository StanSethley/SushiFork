/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 */
package mcsushi.dynamicshop.sushidynamicshop.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;

public class VaultUtil {
    private static Economy economy;

    public static void setupEconomy(Economy eco) {
        economy = eco;
    }

    public static Economy getEconomy() {
        if (economy == null) {
            economy = (Economy)Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        }
        return economy;
    }
}

