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
 */
package mcsushi.dynamicshop.sushidynamicshop.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import mcsushi.dynamicshop.sushidynamicshop.config.CategoryConfig;
import mcsushi.dynamicshop.sushidynamicshop.editor.CategoryEditorGUI;
import mcsushi.dynamicshop.sushidynamicshop.editor.CategoryField;
import mcsushi.dynamicshop.sushidynamicshop.util.GuiSlotHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CategoryEditorInputManager
implements Listener {
    private static final Map<Player, CategorySession> inputMap = new HashMap<Player, CategorySession>();

    public static void startInput(Player player, String categoryId, CategoryField field) {
        inputMap.put(player, new CategorySession(categoryId, field));
        player.sendMessage(String.valueOf(ChatColor.AQUA) + "Enter value for " + field.name().toLowerCase() + ":");
        player.closeInventory();
    }

    public static void cancelInput(Player player) {
        inputMap.remove(player);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!inputMap.containsKey(player)) {
            return;
        }
        event.setCancelled(true);
        CategorySession session = inputMap.remove(player);
        String input = event.getMessage();
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("Sushidynamicshop"), () -> {
            ConfigurationSection section = CategoryConfig.get().getConfigurationSection("categories." + session.categoryId);
            if (section == null) {
                player.sendMessage(String.valueOf(ChatColor.RED) + "Category not found.");
                return;
            }
            Bukkit.getLogger().info("[DEBUG] Input received: " + input + " for field: " + session.field.name());
            switch (session.field) {
                case SHOPID: {
                    section.set("shopid", (Object)input);
                    break;
                }
                case NAME: {
                    section.set("name", (Object)input);
                    break;
                }
                case PERMISSION: {
                    section.set("permission", (Object)input);
                    break;
                }
                case LORE: {
                    Object lore = input.contains("&") ? input : "&f" + input;
                    section.set("lore", Collections.singletonList(lore));
                    break;
                }
                case SLOT: {
                    try {
                        int slot = Integer.parseInt(input);
                        if (slot < 0 || slot >= 54) {
                            player.sendMessage(String.valueOf(ChatColor.RED) + "\u274c Invalid slot. Please enter a number between 0-53.");
                            return;
                        }
                        if (GuiSlotHolder.getCategoryEditorReservedSlots().contains(slot)) {
                            player.sendMessage(String.valueOf(ChatColor.RED) + "\u274c This slot is reserved by the GUI.");
                            return;
                        }
                        String conflict = CategoryConfig.getCategoryIdBySlot(slot);
                        if (conflict != null && !conflict.equals(session.categoryId)) {
                            player.sendMessage(String.valueOf(ChatColor.RED) + "\u274c This slot is already used by category '" + conflict + "'.");
                            return;
                        }
                        section.set("slot", (Object)slot);
                        player.sendMessage(String.valueOf(ChatColor.GREEN) + "\u2705 Slot updated to " + slot);
                        break;
                    }
                    catch (NumberFormatException e) {
                        player.sendMessage(String.valueOf(ChatColor.RED) + "\u274c Invalid number format.");
                        return;
                    }
                }
                default: {
                    player.sendMessage(String.valueOf(ChatColor.RED) + "Field not supported yet.");
                }
            }
            CategoryConfig.save();
            CategoryEditorGUI.open(player, session.categoryId);
        });
    }

    private record CategorySession(String categoryId, CategoryField field) {
    }
}

