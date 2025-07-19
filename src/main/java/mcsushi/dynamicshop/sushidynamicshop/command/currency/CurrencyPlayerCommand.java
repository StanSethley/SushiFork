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

public class CurrencyPlayerCommand
implements SubCommand {
    private final List<String> actions = Arrays.asList("give", "remove", "set");

    @Override
    public boolean execute(CommandSender sender, Command command, String label, String[] args) {
        double amount;
        CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
        if (registry == null) {
            sender.sendMessage("\u00a7cThis feature is only available in the Premium version.");
            return true;
        }
        if (args.length < 5) {
            sender.sendMessage("\u00a7eUsage: /ds currency player <give/remove/set> <player> <currency> <amount>");
            return true;
        }
        String action = args[1].toLowerCase();
        String playerName = args[2];
        String currencyId = args[3].toLowerCase();
        String amountStr = args[4];
        if (!this.actions.contains(action)) {
            sender.sendMessage("\u00a7cInvalid action. Use give, remove, or set.");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer((String)playerName);
        UUID playerUUID = target.getUniqueId();
        if (!registry.currencyExists(currencyId)) {
            sender.sendMessage("\u00a7cCurrency not found: " + currencyId);
            return true;
        }
        try {
            amount = Double.parseDouble(amountStr);
        }
        catch (NumberFormatException e) {
            sender.sendMessage("\u00a7cInvalid amount. Must be a number.");
            return true;
        }
        boolean success = false;
        switch (action) {
            case "give": {
                success = registry.adjustPlayerBalance(playerUUID, currencyId, amount, "give");
                break;
            }
            case "remove": {
                success = registry.adjustPlayerBalance(playerUUID, currencyId, amount, "remove");
                break;
            }
            case "set": {
                Bukkit.getLogger().info("[DEBUG] Setting balance for '" + currencyId + "' to " + amount);
                success = registry.adjustPlayerBalance(playerUUID, currencyId, amount, "set");
            }
        }
        if (success) {
            sender.sendMessage("\u00a7aSuccessfully " + action + " " + amount + " " + currencyId + " to " + playerName);
        } else {
            sender.sendMessage("\u00a7cFailed to " + action + " currency.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, Command command, String alias, String[] args) {
        CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
        if (args.length == 2) {
            return this.actions;
        }
        if (args.length == 3) {
            return null;
        }
        if (args.length == 4) {
            return registry.getAllCurrencies();
        }
        if (args.length == 5) {
            return Arrays.asList("<amount>");
        }
        return Arrays.asList(new String[0]);
    }
}

