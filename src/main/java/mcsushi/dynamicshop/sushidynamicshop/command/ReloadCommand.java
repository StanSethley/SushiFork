/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.configuration.file.YamlConfiguration
 */
package mcsushi.dynamicshop.sushidynamicshop.command;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import mcsushi.dynamicshop.sushidynamicshop.Sushidynamicshop;
import mcsushi.dynamicshop.sushidynamicshop.command.SubCommand;
import mcsushi.dynamicshop.sushidynamicshop.config.CategoryConfig;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.util.TranslationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class ReloadCommand
implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sushishop.reload") && !sender.isOp()) {
            sender.sendMessage(TranslationUtil.get("nopermission_reload"));
            return true;
        }
        long start = System.currentTimeMillis();
        int count = 0;
        File dataFolder = Sushidynamicshop.getInstance().getDataFolder();
        if (dataFolder.exists()) {
            TranslationUtil.load();
            CategoryConfig.reload();
            ShopConfig.clearCache();
            ShopConfig.loadAll(Sushidynamicshop.getInstance());
            count = this.reloadAllYamlFiles(dataFolder);
        }
        long duration = System.currentTimeMillis() - start;
        sender.sendMessage(TranslationUtil.get("reload_success", Map.of("count", String.valueOf(count), "time", String.valueOf(duration))));
        return true;
    }

    private int reloadAllYamlFiles(File folder) {
        int count = 0;
        File[] files = folder.listFiles();
        if (files == null) {
            return 0;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                count += this.reloadAllYamlFiles(file);
                continue;
            }
            if (!file.getName().endsWith(".yml")) continue;
            YamlConfiguration.loadConfiguration((File)file);
            ++count;
        }
        return count;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}

