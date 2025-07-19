/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.PluginCommand
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.java.JavaPlugin
 */
package mcsushi.dynamicshop.sushidynamicshop;

import java.util.logging.Logger;
import mcsushi.dynamicshop.sushidynamicshop.bstats.Metrics;
import mcsushi.dynamicshop.sushidynamicshop.command.SushishopCommand;
import mcsushi.dynamicshop.sushidynamicshop.command.currency.CurrencyRemoveCommand;
import mcsushi.dynamicshop.sushidynamicshop.config.CategoryConfig;
import mcsushi.dynamicshop.sushidynamicshop.debug.DragEventDebugger;
import mcsushi.dynamicshop.sushidynamicshop.editor.CategoryEditorInputManager;
import mcsushi.dynamicshop.sushidynamicshop.editor.CategoryEditorListener;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorAddItemListener;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorInputManager;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorListener;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopListener;
import mcsushi.dynamicshop.sushidynamicshop.hook.ItemAdderShopListener;
import mcsushi.dynamicshop.sushidynamicshop.hook.MMOItemHook;
import mcsushi.dynamicshop.sushidynamicshop.hook.NexoHook;
import mcsushi.dynamicshop.sushidynamicshop.hook.NexoShoplistener;
import mcsushi.dynamicshop.sushidynamicshop.init.Initializer;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.util.PointsUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.PremiumChecker;
import mcsushi.dynamicshop.sushidynamicshop.util.ShopValidator;
import mcsushi.dynamicshop.sushidynamicshop.util.TranslationUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.VaultUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Sushidynamicshop
extends JavaPlugin {
    private static Sushidynamicshop instance;
    private static Economy economy;

    public void onEnable() {
        instance = this;
        Logger logger = this.getLogger();
        new Initializer(logger).init();
        this.saveDefaultConfig();
        this.reloadConfig();
        int pluginId = 25798;
        Metrics metrics = new Metrics((Plugin)this, pluginId);
        TranslationUtil.load();
        ShopConfig.ensureDefaultShop(this);
        ShopConfig.loadAll(this);
        if (ShopConfig.hasShop("default")) {
            ShopValidator.validateShopFile("default");
        }
        CategoryConfig.setup(this);
        PointsUtil.setupPlayerPoints();
        PluginCommand cmd = this.getCommand("sushishop");
        if (cmd != null) {
            cmd.setExecutor((CommandExecutor)new SushishopCommand());
        } else {
            this.getLogger().warning("Failed to register /sushishop command.");
        }
        this.getServer().getPluginManager().registerEvents((Listener)new ShopListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new DragEventDebugger(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new CategoryEditorListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new CategoryEditorInputManager(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ShopEditorListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ShopEditorInputManager(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ShopEditorAddItemListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new ItemAdderShopListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new CurrencyRemoveCommand((Plugin)this), (Plugin)this);
        if (!this.setupVault()) {
            Bukkit.getConsoleSender().sendMessage(String.valueOf(ChatColor.RED) + "[Sushidynamicshop] Vault not detected or failed to hook. Plugin disabled.");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        VaultUtil.setupEconomy(economy);
        Bukkit.getConsoleSender().sendMessage(String.valueOf(ChatColor.GREEN) + "[Sushidynamicshop] Vault detected and hooked.");
        if (Bukkit.getPluginManager().isPluginEnabled("MMOItems")) {
            if (MMOItemHook.setup()) {
                Bukkit.getConsoleSender().sendMessage(String.valueOf(ChatColor.GREEN) + "[Sushidynamicshop] MMOitem detected and hooked.");
            } else {
                Bukkit.getConsoleSender().sendMessage(String.valueOf(ChatColor.YELLOW) + "[Sushidynamicshop] MMOitem detected but can't hook.");
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(String.valueOf(ChatColor.YELLOW) + "[Sushidynamicshop] MMOitem not detected.");
        }
        Bukkit.getScheduler().runTaskLater((Plugin)this, () -> {
            if (NexoHook.isHooked()) {
                Bukkit.getConsoleSender().sendMessage(String.valueOf(ChatColor.GREEN) + "[Sushidynamicshop] Nexo detected. Registering NexoShoplistener...");
                Bukkit.getPluginManager().registerEvents((Listener)new NexoShoplistener(), (Plugin)this);
            } else {
                Bukkit.getConsoleSender().sendMessage(String.valueOf(ChatColor.YELLOW) + "[Sushidynamicshop] Nexo not detected. Skipping NexoShoplistener.");
            }
        }, 1L);
    }

    public void onDisable() {
        Logger logger = this.getLogger();
        if (PremiumChecker.isPremium()) {
            try {
                Class<?> managerClass = Class.forName("mcsushi.dynamicshop.sushidynamicshop.premium.database.DatabaseManager");
                managerClass.getMethod("shutdown", new Class[0]).invoke(null, new Object[0]);
                logger.info("[Sushidynamicshop] Database connection closed successfully.");
            }
            catch (Exception e) {
                logger.warning("[Sushidynamicshop] Failed to close Database connection: " + e.getMessage());
            }
        }
    }

    private boolean setupVault() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = (Economy)rsp.getProvider();
        return economy != null;
    }

    public static Sushidynamicshop getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static Logger getLoggerInstance() {
        return Sushidynamicshop.getInstance().getLogger();
    }
}

