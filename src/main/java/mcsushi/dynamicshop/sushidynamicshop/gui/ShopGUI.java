package mcsushi.dynamicshop.sushidynamicshop.gui;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import mcsushi.dynamicshop.sushidynamicshop.util.ColorUtil;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorGUI;
import mcsushi.dynamicshop.sushidynamicshop.hook.MMOItemHook;
import mcsushi.dynamicshop.sushidynamicshop.init.PremiumInitializer;
import mcsushi.dynamicshop.sushidynamicshop.pricehandler.PriceHandler;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopCurrency;
import mcsushi.dynamicshop.sushidynamicshop.util.CurrencyRegistry;
import mcsushi.dynamicshop.sushidynamicshop.util.ItemAdderUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.ItemDisplayUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.MMOItemFactory;
import mcsushi.dynamicshop.sushidynamicshop.util.Nexoutil;
import mcsushi.dynamicshop.sushidynamicshop.util.TranslationUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.VanillaItemFactory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopGUI {

    private static int cachedBackSlot = 49;

    public static void open(Player player, String shopId) {
        if (!ShopConfig.hasShop(shopId)) {
            return;
        }

        HashSet<Integer> usedSlots = new HashSet<>();
        List<String> itemKeys = ShopConfig.getShopItems(shopId);
        File file = new File("plugins/Sushidynamicshop/shop/" + shopId + ".yml");
        int slotCount = ShopConfig.getSlotCount(shopId);

        Inventory inv = Bukkit.createInventory(
                new ShopHolder(shopId, ShopConfig.getShopConfig(shopId), file, 0, 0),
                slotCount,
                ColorUtil.parseColors(ShopConfig.getShopName(shopId))
        );

        Material decoMaterial = ShopConfig.getShopDeco(shopId);
        ItemStack border = new ItemStack(decoMaterial);
        ItemMeta meta = border.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            border.setItemMeta(meta);
        }

        int size = inv.getSize();
        for (int i = 0; i < size; i++) {
            boolean isBorder = i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8;
            if (isBorder) {
                inv.setItem(i, border);
            }
        }

        // Back button configuration
        ConfigurationSection buttonCfg = ShopConfig.getShopConfig(shopId).getConfigurationSection("back_button");
        boolean useCustom = buttonCfg != null && buttonCfg.getBoolean("custom_slot_location", false);

        int backSlot;
        if (useCustom) {
            backSlot = buttonCfg.getInt("back_slot", 49);
        } else {
            switch (slotCount) {
                case 27 -> backSlot = 22;
                case 36 -> backSlot = 31;
                case 45 -> backSlot = 40;
                case 54 -> backSlot = 49;
                default -> {
                    Bukkit.getLogger().warning("[Sushidynamicshop] Invalid slot count: " + slotCount);
                    return;
                }
            }
        }

        cachedBackSlot = backSlot;

        ItemStack back = null;
        if (buttonCfg != null) {
            String source = buttonCfg.getString("material", "BARRIER");
            if (source.toLowerCase().startsWith("itemadder:")) {
                String id = source.substring("itemadder:".length());
                back = ItemAdderUtil.createItem(id, 1);
            } else if (source.toLowerCase().startsWith("mmoitem:")) {
                back = MMOItemHook.isHooked() ? MMOItemFactory.createItem(source) : new ItemStack(Material.BARRIER);
            } else if (source.toLowerCase().startsWith("nexo:")) {
                String id = source.substring("nexo:".length());
                back = Nexoutil.createItem(id, 1);
                if (back == null || back.getType() == Material.BARRIER) {
                    back = new ItemStack(Material.BARRIER);
                }
            } else {
                back = VanillaItemFactory.createItem(source);
            }

            if (back == null) {
                back = new ItemStack(Material.BARRIER);
            }

            ItemMeta backMeta = back.getItemMeta();
            if (backMeta != null) {
                String displayName = buttonCfg.getString("display_name", TranslationUtil.get("back"));
                backMeta.setDisplayName(ColorUtil.parseColors(displayName));
                back.setItemMeta(backMeta);
            }
        } else {
            back = new ItemStack(Material.BARRIER);
            ItemMeta backMeta = back.getItemMeta();
            if (backMeta != null) {
                backMeta.setDisplayName(TranslationUtil.get("back"));
                back.setItemMeta(backMeta);
            }
        }

        inv.setItem(backSlot, back);

        // Load shop items
        for (String key : itemKeys) {
            ItemStack item;
            String source = ShopConfig.getSource(shopId, key);
            int slot = ShopConfig.getSlot(shopId, key);
            double buyPrice = PriceHandler.getBuyPrice(shopId, key);
            double sellPrice = PriceHandler.getSellPrice(shopId, key);
            double supply = ShopConfig.getSupply(shopId, key);
            double demand = ShopConfig.getDemand(shopId, key);
            boolean canBuy = ShopConfig.canBuy(shopId, key);
            boolean canSell = ShopConfig.canSell(shopId, key);

            if (source.toLowerCase().startsWith("itemadder:")) {
                String id = source.substring("itemadder:".length());
                item = ItemAdderUtil.createItem(id, 1);
            } else if (source.toLowerCase().startsWith("mmoitem:")) {
                item = MMOItemHook.isHooked() ? MMOItemFactory.createItem(source) : new ItemStack(Material.BARRIER);
            } else if (source.toLowerCase().startsWith("nexo:")) {
                String id = source.substring("nexo:".length());
                item = Nexoutil.createItem(id, 1);
                if (item == null || item.getType() == Material.BARRIER) {
                    item = new ItemStack(Material.BARRIER);
                }
            } else {
                item = VanillaItemFactory.createItem(source);
            }

            if (item == null) {
                continue;
            }

            ShopCurrency currency = ShopConfig.getCurrency(shopId);
            String displayCurrency = "";
            String customCurrencyId = null;

            if (currency == ShopCurrency.CUSTOM) {
                customCurrencyId = ShopConfig.getCustomCurrencyId(shopId);
                if (customCurrencyId != null && !customCurrencyId.isEmpty()) {
                    CurrencyRegistry registry = PremiumInitializer.getCurrencyRegistry();
                    displayCurrency = (registry != null) ? registry.getPronoun(customCurrencyId) : customCurrencyId;
                }
            } else if (currency != ShopCurrency.VAULT) {
                displayCurrency = currency.name();
            }

            ItemDisplayUtil.appendShopLore(item, shopId, key, buyPrice, sellPrice, supply, demand, canBuy, canSell, displayCurrency);

            if (!usedSlots.add(slot)) {
                Bukkit.getLogger().warning("[SushiShop] Duplicate slot in shop: " + shopId + " at slot " + slot);
                continue;
            }

            inv.setItem(slot, item);
        }

        player.openInventory(inv);
    }

    public static void handleClick(InventoryClickEvent event) {
        InventoryHolder inventoryHolder = event.getInventory().getHolder();
        if (!(inventoryHolder instanceof ShopHolder)) {
            return;
        }

        ShopHolder shopHolder = (ShopHolder) inventoryHolder;
        HumanEntity humanEntity = event.getWhoClicked();
        if (!(humanEntity instanceof Player)) {
            return;
        }

        Player player = (Player) humanEntity;
        int clickedSlot = event.getRawSlot();

        int maxSlot = 0;
        if (shopHolder.getInventory() != null) {
            maxSlot = shopHolder.getInventory().getSize();
        }

        if (clickedSlot < 0 || clickedSlot >= maxSlot) {
            return;
        }

        if (clickedSlot == cachedBackSlot) {
            ConfigurationSection buttonCfg = ShopConfig.getShopConfig(shopHolder.getShopId()).getConfigurationSection("back_button");
            if (buttonCfg != null && buttonCfg.contains("command")) {
                String command = buttonCfg.getString("command");
                if (command != null && !command.isEmpty()) {
                    player.performCommand(command.startsWith("/") ? command.substring(1) : command);
                    return;
                }
            }
            player.closeInventory();
            return;
        }

        ClickType click = event.getClick();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        String shopId = shopHolder.getShopId();
        List<String> keys = ShopConfig.getShopItems(shopId);

        for (String key : keys) {
            if (ShopConfig.getSlot(shopId, key) != clickedSlot || click != ClickType.DROP) {
                continue;
            }
            event.setCancelled(true);
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You must be OP to edit shop items.");
                return;
            }
            ShopEditorGUI.open(player, shopId, key);
            return;
        }
    }
}