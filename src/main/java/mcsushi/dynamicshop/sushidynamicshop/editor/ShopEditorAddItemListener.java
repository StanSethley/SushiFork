/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 */
package mcsushi.dynamicshop.sushidynamicshop.editor;

import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorInputManager;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopHolder;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.util.GuiSlotHolder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ShopEditorAddItemListener
implements Listener {
    @EventHandler
    public void onShiftLeftClickEmptySlot(InventoryClickEvent event) {
        Inventory top = event.getView().getTopInventory();
        InventoryHolder holder = top.getHolder();
        if (!(holder instanceof ShopHolder)) {
            return;
        }
        ShopHolder shopHolder = (ShopHolder)holder;
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }
        Player player = (Player)humanEntity;
        if (!event.getClick().equals((Object)ClickType.SHIFT_LEFT)) {
            return;
        }
        if (!player.isOp()) {
            return;
        }
        int slot = event.getRawSlot();
        if (GuiSlotHolder.getReservedSlotsFor(holder).contains(slot)) {
            return;
        }
        if (slot >= top.getSize()) {
            return;
        }
        ItemStack currentItem = top.getItem(slot);
        if (currentItem != null && currentItem.getType().isItem()) {
            return;
        }
        String shopId = shopHolder.getShopId();
        boolean slotUsed = ShopConfig.getShopItems(shopId).stream().anyMatch(k -> ShopConfig.getSlot(shopId, k) == slot);
        if (slotUsed) {
            return;
        }
        event.setCancelled(true);
        ShopEditorInputManager.startConfirmCreate(player, shopId, slot);
    }
}

