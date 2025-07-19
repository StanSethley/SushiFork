/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.Sound
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.ClickType
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 */
package mcsushi.dynamicshop.sushidynamicshop.hook;

import java.util.Map;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopGUI;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopHolder;
import mcsushi.dynamicshop.sushidynamicshop.pricehandler.PriceHandler;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.util.Nexoutil;
import mcsushi.dynamicshop.sushidynamicshop.util.TranslationUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NexoShoplistener
implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player)e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (!(inv.getHolder() instanceof ShopHolder)) {
            return;
        }
        ShopHolder shopHolder = (ShopHolder)inv.getHolder();
        String shopId = shopHolder.getShopId();
        int slot = e.getSlot();
        String key = null;
        for (String k : ShopConfig.getShopItems(shopId)) {
            if (ShopConfig.getSlot(shopId, k) != slot) continue;
            key = k;
            break;
        }
        if (key == null) {
            return;
        }
        String source = ShopConfig.getSource(shopId, key);
        if (source == null || !source.toLowerCase().startsWith("nexo:")) {
            return;
        }
        e.setCancelled(true);
        ClickType click = e.getClick();
        boolean isShift = click.isShiftClick();
        boolean isLeft = click.isLeftClick();
        boolean isRight = click.isRightClick();
        int amount = isShift ? 64 : 1;
        String itemId = source.substring("nexo:".length());
        ItemStack item = Nexoutil.createItem(itemId, amount);
        if (item.getType() == Material.BARRIER) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            player.sendMessage("\u00a7c[!] Unable to create Nexo item: " + itemId);
            return;
        }
        if (isLeft && ShopConfig.canBuy(shopId, key)) {
            double price = PriceHandler.calculateBuyPrice(shopId, key, amount, 0);
            if (VaultUtil.getEconomy().getBalance((OfflinePlayer)player) < price) {
                player.sendMessage(TranslationUtil.get("not_enough_money"));
                return;
            }
            VaultUtil.getEconomy().withdrawPlayer((OfflinePlayer)player, price);
            player.getInventory().addItem(new ItemStack[]{item});
            ShopConfig.incrementDemand(shopId, key, amount);
            Map<String, String> placeholders = Map.of("{item}", key, "{amount}", String.valueOf(amount), "{price}", String.valueOf(price));
            player.sendMessage(TranslationUtil.get("bought", placeholders));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.2f);
        }
        if (isRight && ShopConfig.canSell(shopId, key)) {
            int count = this.countItem(player, item);
            if (count == 0) {
                player.sendMessage(TranslationUtil.get("no_item_to_sell"));
                return;
            }
            int sellAmount = Math.min(amount, count);
            double price = PriceHandler.calculateSellPrice(shopId, key, sellAmount, 0);
            this.removeItem(player, item, sellAmount);
            VaultUtil.getEconomy().depositPlayer((OfflinePlayer)player, price);
            ShopConfig.incrementSupply(shopId, key, sellAmount);
            Map<String, String> placeholders = Map.of("{item}", key, "{amount}", String.valueOf(sellAmount), "{price}", String.valueOf(price));
            player.sendMessage(TranslationUtil.get("sold", placeholders));
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 0.9f);
        }
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Sushidynamicshop"), () -> ShopGUI.open(player, shopId), 1L);
    }

    private int countItem(Player player, ItemStack target) {
        String id = Nexoutil.getNexoId(target);
        if (id == null) {
            return 0;
        }
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            String itemId;
            if (item == null || !id.equalsIgnoreCase(itemId = Nexoutil.getNexoId(item))) continue;
            count += item.getAmount();
        }
        return count;
    }

    private void removeItem(Player player, ItemStack target, int amount) {
        String id = Nexoutil.getNexoId(target);
        if (id == null) {
            return;
        }
        int remaining = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; ++i) {
            String itemId;
            ItemStack item = contents[i];
            if (item == null || !id.equalsIgnoreCase(itemId = Nexoutil.getNexoId(item))) continue;
            int amt = item.getAmount();
            if (amt <= remaining) {
                contents[i] = null;
                remaining -= amt;
                continue;
            }
            item.setAmount(amt - remaining);
            remaining = 0;
        }
        player.getInventory().setContents(contents);
    }
}

