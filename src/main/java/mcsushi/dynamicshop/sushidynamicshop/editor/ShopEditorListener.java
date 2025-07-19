/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryDragEvent
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package mcsushi.dynamicshop.sushidynamicshop.editor;

import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorGUI;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorHolder;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorInputManager;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopField;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopGUI;
import mcsushi.dynamicshop.sushidynamicshop.hook.MMOItemHook;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.util.MMOItemFactory;
import mcsushi.dynamicshop.sushidynamicshop.util.VanillaItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ShopEditorListener
implements Listener {
    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (!(holder instanceof ShopEditorHolder)) {
            return;
        }
        ShopEditorHolder shopHolder = (ShopEditorHolder)holder;
        int rawSlot = event.getRawSlot();
        if (rawSlot >= event.getView().getTopInventory().getSize()) {
            return;
        }
        event.setCancelled(true);
        Player player = (Player)event.getWhoClicked();
        switch (rawSlot) {
            case 10: {
                ShopEditorInputManager.startInput(player, shopHolder.getShopId(), shopHolder.getItemKey(), ShopField.SLOT);
                break;
            }
            case 11: {
                ShopEditorInputManager.startInput(player, shopHolder.getShopId(), shopHolder.getItemKey(), ShopField.BASE_PRICE);
                break;
            }
            case 12: {
                player.sendMessage(String.valueOf(ChatColor.GRAY) + "Current price is auto-calculated.");
                break;
            }
            case 13: {
                ShopEditorInputManager.startInput(player, shopHolder.getShopId(), shopHolder.getItemKey(), ShopField.BUY_MULTIPLIER);
                break;
            }
            case 14: {
                ShopEditorInputManager.startInput(player, shopHolder.getShopId(), shopHolder.getItemKey(), ShopField.SELL_MULTIPLIER);
                break;
            }
            case 19: {
                ShopEditorInputManager.startInput(player, shopHolder.getShopId(), shopHolder.getItemKey(), ShopField.MIN_PRICE);
                break;
            }
            case 20: {
                ShopEditorInputManager.startInput(player, shopHolder.getShopId(), shopHolder.getItemKey(), ShopField.MAX_PRICE);
                break;
            }
            case 21: {
                ShopEditorInputManager.startInput(player, shopHolder.getShopId(), shopHolder.getItemKey(), ShopField.SUPPLY);
                break;
            }
            case 22: {
                ShopEditorInputManager.startInput(player, shopHolder.getShopId(), shopHolder.getItemKey(), ShopField.DEMAND);
                break;
            }
            case 23: {
                boolean current = ShopConfig.getShopConfig(shopHolder.getShopId()).getBoolean(shopHolder.getItemKey() + ".buy_enabled", true);
                ShopConfig.getShopConfig(shopHolder.getShopId()).set(shopHolder.getItemKey() + ".buy_enabled", (Object)(!current ? 1 : 0));
                ShopEditorGUI.open(player, shopHolder.getShopId(), shopHolder.getItemKey());
                break;
            }
            case 24: {
                boolean current = ShopConfig.getShopConfig(shopHolder.getShopId()).getBoolean(shopHolder.getItemKey() + ".sell_enabled", true);
                ShopConfig.getShopConfig(shopHolder.getShopId()).set(shopHolder.getItemKey() + ".sell_enabled", (Object)(!current ? 1 : 0));
                ShopEditorGUI.open(player, shopHolder.getShopId(), shopHolder.getItemKey());
                break;
            }
            case 30: {
                ShopEditorInputManager.startInput(player, shopHolder.getShopId(), shopHolder.getItemKey(), ShopField.SOURCE);
                break;
            }
            case 31: {
                String source = ShopConfig.getSource(shopHolder.getShopId(), shopHolder.getItemKey());
                ItemStack preview = source.toLowerCase().startsWith("mmoitem:") ? (MMOItemHook.isHooked() ? MMOItemFactory.createItem(source) : new ItemStack(Material.BARRIER)) : VanillaItemFactory.createItem(source);
                player.getInventory().addItem(new ItemStack[]{preview});
                player.sendMessage(String.valueOf(ChatColor.GREEN) + "Item preview added to your inventory.");
                break;
            }
            case 40: {
                ShopGUI.open(player, shopHolder.getShopId());
                break;
            }
            default: {
                player.sendMessage(String.valueOf(ChatColor.RED) + "This field is not editable yet.");
            }
        }
    }

    @EventHandler
    public void onGUIDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getView().getTopInventory().getHolder();
        if (holder instanceof ShopEditorHolder) {
            event.setCancelled(true);
        }
    }
}

