/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 */
package mcsushi.dynamicshop.sushidynamicshop.command.currency;

import java.util.Arrays;
import java.util.List;
import mcsushi.dynamicshop.sushidynamicshop.command.SubCommand;
import mcsushi.dynamicshop.sushidynamicshop.init.PremiumInitializer;
import mcsushi.dynamicshop.sushidynamicshop.util.CurrencyRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CurrencyAddCommand
implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
        if (registry == null) {
            sender.sendMessage("\u00a7cThis feature is only available in the Premium version.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage("\u00a7eUsage: /ds currency add <currency_id> <pronoun>");
            return true;
        }
        String currencyId = args[1].toLowerCase();
        String pronoun = args[2];
        if (registry.currencyExists(currencyId)) {
            sender.sendMessage("\u00a7cCurrency ID already exists: " + currencyId);
            return true;
        }
        boolean success = registry.addCurrency(currencyId, pronoun);
        if (success) {
            sender.sendMessage("\u00a7aCurrency added: " + currencyId + " (" + pronoun + ")");
        } else {
            sender.sendMessage("\u00a7cFailed to add currency.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            return Arrays.asList("<currency_id>");
        }
        if (args.length == 3) {
            return Arrays.asList("<pronoun>");
        }
        return Arrays.asList(new String[0]);
    }
}

