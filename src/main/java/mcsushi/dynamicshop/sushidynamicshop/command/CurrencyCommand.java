/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.plugin.Plugin
 */
package mcsushi.dynamicshop.sushidynamicshop.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mcsushi.dynamicshop.sushidynamicshop.Sushidynamicshop;
import mcsushi.dynamicshop.sushidynamicshop.command.SubCommand;
import mcsushi.dynamicshop.sushidynamicshop.command.currency.CurrencyAddCommand;
import mcsushi.dynamicshop.sushidynamicshop.command.currency.CurrencyEditCommand;
import mcsushi.dynamicshop.sushidynamicshop.command.currency.CurrencyInfoCommand;
import mcsushi.dynamicshop.sushidynamicshop.command.currency.CurrencyPlayerCommand;
import mcsushi.dynamicshop.sushidynamicshop.command.currency.CurrencyRemoveCommand;
import mcsushi.dynamicshop.sushidynamicshop.init.PremiumInitializer;
import mcsushi.dynamicshop.sushidynamicshop.util.CurrencyRegistry;
import mcsushi.dynamicshop.sushidynamicshop.util.DefaultCurrencyRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CurrencyCommand
implements SubCommand {
    private final Map<String, SubCommand> subCommands = new HashMap<String, SubCommand>();

    public CurrencyCommand() {
        this.subCommands.put("info", new CurrencyInfoCommand());
        this.subCommands.put("add", new CurrencyAddCommand());
        this.subCommands.put("remove", new CurrencyRemoveCommand((Plugin)Sushidynamicshop.getInstance()));
        this.subCommands.put("edit", new CurrencyEditCommand());
        this.subCommands.put("player", new CurrencyPlayerCommand());
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
        if (registry instanceof DefaultCurrencyRegistry) {
            sender.sendMessage("\u00a7cThis feature is available only in the Premium version.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("\u00a7eUsage: /ds currency <subcommand>");
            return true;
        }
        String subCommandKey = args[1].toLowerCase();
        SubCommand subCommand = this.subCommands.get(subCommandKey);
        if (subCommand != null) {
            return subCommand.execute(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        sender.sendMessage("\u00a7cUnknown currency subcommand: " + subCommandKey);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        SubCommand subCommand;
        if (args.length == 2) {
            ArrayList<String> completions = new ArrayList<String>(this.subCommands.keySet());
            completions.removeIf(s -> !s.startsWith(args[1].toLowerCase()));
            return completions;
        }
        if (args.length > 2 && (subCommand = this.subCommands.get(args[1].toLowerCase())) != null) {
            return subCommand.tabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
        }
        return Collections.emptyList();
    }
}

