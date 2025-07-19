/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 */
package mcsushi.dynamicshop.sushidynamicshop.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mcsushi.dynamicshop.sushidynamicshop.command.AddCommand;
import mcsushi.dynamicshop.sushidynamicshop.command.CurrencyCommand;
import mcsushi.dynamicshop.sushidynamicshop.command.ReloadCommand;
import mcsushi.dynamicshop.sushidynamicshop.command.SubCommand;
import mcsushi.dynamicshop.sushidynamicshop.debug.Custommodeldatachecker;
import mcsushi.dynamicshop.sushidynamicshop.gui.CategoryMenuGUI;
import mcsushi.dynamicshop.sushidynamicshop.util.TranslationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class SushishopCommand
implements CommandExecutor,
TabCompleter {
    private final Map<String, SubCommand> subcommands = new HashMap<String, SubCommand>();

    public SushishopCommand() {
        this.subcommands.put("reload", new ReloadCommand());
        this.subcommands.put("add", new AddCommand());
        this.subcommands.put("checkheld", new Custommodeldatachecker());
        this.subcommands.put("currency", new CurrencyCommand());
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("sushishop.open") && !sender.isOp()) {
            sender.sendMessage(TranslationUtil.get("nopermission"));
            return true;
        }
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                CategoryMenuGUI.open(player);
            } else {
                sender.sendMessage(TranslationUtil.get("console_use"));
            }
            return true;
        }
        String sub = args[0].toLowerCase();
        SubCommand subCommand = this.subcommands.get(sub);
        if (subCommand != null) {
            return subCommand.execute(sender, command, label, args);
        }
        sender.sendMessage(TranslationUtil.get("unknowncommand"));
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        SubCommand sub;
        if (!sender.hasPermission("sushishop.open") && !sender.isOp()) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            ArrayList<String> completions = new ArrayList<String>(this.subcommands.keySet());
            completions.removeIf(s -> !s.startsWith(args[0].toLowerCase()));
            return completions;
        }
        if (args.length > 1 && (sub = this.subcommands.get(args[0].toLowerCase())) != null) {
            return sub.tabComplete(sender, command, alias, args);
        }
        return Collections.emptyList();
    }
}

