/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package mcsushi.dynamicshop.sushidynamicshop.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class NexoHook {
    private static final String PLUGIN_NAME = "Nexo";

    public static boolean isHooked() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(PLUGIN_NAME);
        return plugin != null && plugin.isEnabled();
    }
}

