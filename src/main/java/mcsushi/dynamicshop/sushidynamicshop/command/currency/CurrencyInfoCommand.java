/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 */
package mcsushi.dynamicshop.sushidynamicshop.command.currency;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import mcsushi.dynamicshop.sushidynamicshop.command.SubCommand;
import mcsushi.dynamicshop.sushidynamicshop.init.PremiumInitializer;
import mcsushi.dynamicshop.sushidynamicshop.util.CurrencyRegistry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CurrencyInfoCommand
implements SubCommand {
    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
        if (registry == null) {
            sender.sendMessage("\u00a7cThis feature is only available in the Premium version.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("\u00a7eUsage: /ds currency info <currency> [player]");
            return true;
        }
        String currencyId = args[1].toLowerCase();
        String pronoun = registry.getPronoun(currencyId);
        if (!registry.currencyExists(currencyId)) {
            sender.sendMessage("\u00a7cCurrency ID not found: " + currencyId);
            return true;
        }
        if (args.length == 3) {
            OfflinePlayer target = Bukkit.getOfflinePlayer((String)args[2]);
            UUID targetUUID = target.getUniqueId();
            String balanceMessage = registry.getPlayerBalance(targetUUID, currencyId, pronoun);
            sender.sendMessage(balanceMessage);
            return true;
        }
        sender.sendMessage("\u00a76Currency ID: " + currencyId + " \u00a77| Pronoun: \u00a76" + pronoun);
        List<String> topPlayers = registry.getTop10Players(currencyId, pronoun);
        if (topPlayers.isEmpty()) {
            sender.sendMessage("\u00a77No players found for this currency.");
        } else {
            for (String line : topPlayers) {
                sender.sendMessage(line);
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
        if (args.length == 2) {
            return registry.getAllCurrencies();
        }
        if (args.length == 3) {
            return null;
        }
        return Arrays.asList(new String[0]);
    }
}

