/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 */
package mcsushi.dynamicshop.sushidynamicshop.editor;

import java.util.HashMap;
import java.util.Map;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopField;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopGUI;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ShopEditorInputManager
implements Listener {
    private static final Map<Player, ShopSession> inputMap = new HashMap<Player, ShopSession>();

    public static void startInput(Player player, String shopId, String itemKey, ShopField field) {
        inputMap.put(player, new ShopSession(shopId, itemKey, field));
        player.sendMessage(String.valueOf(ChatColor.AQUA) + "Enter value for " + field.name().toLowerCase() + ":");
        player.closeInventory();
    }

    public static void cancelInput(Player player) {
        inputMap.remove(player);
    }

    public static void startConfirmCreate(Player player, String shopId, int slot) {
        inputMap.put(player, new ShopSession(shopId, String.valueOf(slot), null));
        player.sendMessage(String.valueOf(ChatColor.YELLOW) + "Type 'yes' to confirm creating a new item at slot " + slot + ", or 'no' to cancel.");
        player.closeInventory();
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Sushidynamicshop"), () -> {
            if (inputMap.containsKey(player)) {
                inputMap.remove(player);
                player.sendMessage(String.valueOf(ChatColor.RED) + "\u23f1 Timed out.");
                ShopGUI.open(player, shopId);
            }
        }, 300L);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!inputMap.containsKey(player)) {
            return;
        }
        ShopSession session = inputMap.get(player);
        if (session.field == null) {
            event.setCancelled(true);
            inputMap.remove(player);
            String input = event.getMessage().trim().toLowerCase();
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("Sushidynamicshop"), () -> {
                if (input.equals("yes")) {
                    ConfigurationSection section = ShopConfig.getShopConfig(session.shopId).createSection(session.itemKey);
                    section.set("source", (Object)"STONE");
                    section.set("slot", (Object)Integer.parseInt(session.itemKey));
                    section.set("base_price", (Object)10.0);
                    section.set("current_price", (Object)10.0);
                    section.set("buy_multiplier", (Object)1.0);
                    section.set("sell_multiplier", (Object)0.7);
                    section.set("min_price", (Object)1.0);
                    section.set("max_price", (Object)100.0);
                    section.set("price_change_rate", (Object)0.5);
                    section.set("supply", (Object)0);
                    section.set("demand", (Object)0);
                    section.set("buy_enabled", (Object)true);
                    section.set("sell_enabled", (Object)true);
                    ShopGUI.open(player, session.shopId);
                    player.sendMessage(String.valueOf(ChatColor.GREEN) + "\u2705 Item created.");
                } else if (input.equals("no")) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "\u274c Cancelled.");
                    ShopGUI.open(player, session.shopId);
                } else {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "\u274c Invalid input. Please type 'yes' or 'no'.");
                    ShopGUI.open(player, session.shopId);
                }
            });
        } else {
            event.setCancelled(true);
            inputMap.remove(player);
            String input = event.getMessage();
            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("Sushidynamicshop"), () -> {
                ConfigurationSection section = ShopConfig.getShopConfig(session.shopId).getConfigurationSection(session.itemKey);
                if (section == null) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Item not found.");
                    return;
                }
                try {
                    switch (session.field) {
                        case SLOT: {
                            section.set("slot", (Object)Integer.parseInt(input));
                            break;
                        }
                        case BASE_PRICE: {
                            section.set("base_price", (Object)Double.parseDouble(input));
                            break;
                        }
                        case BUY_MULTIPLIER: {
                            section.set("buy_multiplier", (Object)Double.parseDouble(input));
                            break;
                        }
                        case SELL_MULTIPLIER: {
                            section.set("sell_multiplier", (Object)Double.parseDouble(input));
                            break;
                        }
                        case MIN_PRICE: {
                            section.set("min_price", (Object)Double.parseDouble(input));
                            break;
                        }
                        case MAX_PRICE: {
                            section.set("max_price", (Object)Double.parseDouble(input));
                            break;
                        }
                        case SOURCE: {
                            section.set("source", (Object)input);
                            break;
                        }
                        case SUPPLY: {
                            section.set("supply", (Object)Double.parseDouble(input));
                            break;
                        }
                        case DEMAND: {
                            section.set("demand", (Object)Double.parseDouble(input));
                            break;
                        }
                        case PRICE_RANGE: {
                            String[] parts = input.split("-");
                            if (parts.length != 2) {
                                player.sendMessage(String.valueOf(ChatColor.RED) + "Please enter in format: min-max");
                                return;
                            }
                            double min = Double.parseDouble(parts[0].trim());
                            double max = Double.parseDouble(parts[1].trim());
                            section.set("min_price", (Object)min);
                            section.set("max_price", (Object)max);
                        }
                    }
                    player.sendMessage(String.valueOf(ChatColor.GREEN) + "\u2705 Updated " + session.field.name().toLowerCase() + " successfully.");
                    ShopGUI.open(player, session.shopId);
                }
                catch (Exception e) {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "\u274c Invalid input.");
                }
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        inputMap.remove(event.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        inputMap.remove(event.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        inputMap.remove(event.getPlayer());
    }

    private record ShopSession(String shopId, String itemKey, ShopField field) {
    }
}

