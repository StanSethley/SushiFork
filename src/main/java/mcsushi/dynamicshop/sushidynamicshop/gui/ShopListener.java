package mcsushi.dynamicshop.sushidynamicshop.gui;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import mcsushi.dynamicshop.sushidynamicshop.config.CategoryConfig;
import mcsushi.dynamicshop.sushidynamicshop.editor.CategoryEditorGUI;
import mcsushi.dynamicshop.sushidynamicshop.editor.ShopEditorGUI;
import mcsushi.dynamicshop.sushidynamicshop.gui.CategoryMenuGUI;
import mcsushi.dynamicshop.sushidynamicshop.gui.CategoryMenuHolder;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopGUI;
import mcsushi.dynamicshop.sushidynamicshop.gui.ShopHolder;
import mcsushi.dynamicshop.sushidynamicshop.hook.MMOItemHook;
import mcsushi.dynamicshop.sushidynamicshop.init.PremiumInitializer;
import mcsushi.dynamicshop.sushidynamicshop.pricehandler.CurrencyHandler;
import mcsushi.dynamicshop.sushidynamicshop.pricehandler.PriceHandler;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopConfig;
import mcsushi.dynamicshop.sushidynamicshop.shop.ShopCurrency;
import mcsushi.dynamicshop.sushidynamicshop.util.CurrencyRegistry;
import mcsushi.dynamicshop.sushidynamicshop.util.ItemAdderUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.MMOItemFactory;
import mcsushi.dynamicshop.sushidynamicshop.util.TranslationUtil;
import mcsushi.dynamicshop.sushidynamicshop.util.VanillaItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ShopListener implements Listener {

    private ItemStack createItem(String source) {
        if (source == null || source.trim().isEmpty()) {
            return new ItemStack(Material.BARRIER);
        }
        source = source.toLowerCase();
        try {
            String id;
            ItemStack item;
            if (source.startsWith("mmoitem:")) {
                ItemStack item2;
                if (MMOItemHook.isHooked() && (item2 = MMOItemFactory.createItem(source)) != null) {
                    return item2;
                }
                return new ItemStack(Material.BARRIER);
            }
            if (source.startsWith("itemadder:") && (item = ItemAdderUtil.createItem(id = source.substring("itemadder:".length()), 1)) != null) {
                return item;
            }
            return VanillaItemFactory.createItem(source);
        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.BARRIER);
        }
    }

    private void handleBackButton(Player player, FileConfiguration config, boolean isShop) {
        if (config == null || !config.contains("back_button.commands_enabled")) {
            if (isShop) {
                CategoryMenuGUI.open(player);
            } else {
                player.closeInventory();
            }
            return;
        }

        boolean enabled = config.getBoolean("back_button.commands_enabled", false);
        if (!enabled) {
            if (isShop) {
                CategoryMenuGUI.open(player);
            } else {
                player.closeInventory();
            }
            return;
        }

        if (config.contains("back_button.commands")) {
            for (String command : config.getStringList("back_button.commands")) {
                player.performCommand(command);
            }
        }

        if (config.getBoolean("back_button.close_menu_on_run", false)) {
            player.closeInventory();
        }

        if (config.contains("back_button.exit_sound")) {
            String soundStr = config.getString("back_button.exit_sound");
            try {
                Sound sound = Sound.valueOf(soundStr.toUpperCase());
                player.playSound(player.getLocation(), sound, SoundCategory.MASTER, 1f, 1f);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        double totalPrice;
        int tempSupply;
        int tempDemand;
        int amount;
        boolean success;
        CurrencyRegistry registry;
        boolean exists;
        String pronoun;

        Inventory top = event.getView().getTopInventory();

        if (top.getHolder() instanceof CategoryMenuHolder) {
            if (event.getRawSlot() < top.getSize()) {
                event.setCancelled(true);
                HumanEntity humanEntity = event.getWhoClicked();
                if (humanEntity instanceof Player player) {
                    int slot = event.getSlot();
                    FileConfiguration categoryConfig = CategoryConfig.get();
                    ConfigurationSection backSection = categoryConfig.getConfigurationSection("back_button");
                    int backSlot = switch (top.getSize()) {
                        case 27 -> 22;
                        case 36 -> 31;
                        case 45 -> 40;
                        case 54 -> 49;
                        default -> -1;
                    };
                    if (backSection != null && backSection.getBoolean("custom_slot_location", false)) {
                        backSlot = backSection.getInt("back_slot", backSlot);
                    }

                    if (slot == backSlot) {
                        handleBackButton(player, categoryConfig, false);
                        return;
                    }

                    if (event.getClick() == ClickType.SHIFT_RIGHT && player.isOp()) {
                        String categoryId = CategoryConfig.getCategoryIdBySlot(slot);
                        if (categoryId != null) {
                            CategoryEditorGUI.open(player, categoryId);
                        }
                        return;
                    }

                    CategoryMenuGUI.handleClick(player, slot);
                }
            }
            return;
        }

        InventoryHolder slot = top.getHolder();
        if (slot instanceof ShopHolder holder) {
            if (event.getRawSlot() < top.getSize()) {
                event.setCancelled(true);
                ShopGUI.handleClick(event);
                HumanEntity actor = event.getWhoClicked();
                if (!(actor instanceof Player player)) return;

                int slot2 = event.getSlot();
                String shopId = holder.getShopId();

                FileConfiguration shopConfig = ShopConfig.getShopConfig(shopId);
                ConfigurationSection backSection = shopConfig.getConfigurationSection("back_button");
                int backSlot = switch (top.getSize()) {
                    case 27 -> 22;
                    case 36 -> 31;
                    case 45 -> 40;
                    case 54 -> 49;
                    default -> -1;
                };
                if (backSection != null && backSection.getBoolean("custom_slot_location", false)) {
                    backSlot = backSection.getInt("back_slot", backSlot);
                }

                if (slot2 == backSlot) {
                    handleBackButton(player, shopConfig, true);
                    return;
                }

                if (event.getClick() == ClickType.DROP) {
                    if (!player.isOp()) {
                        return;
                    }
                    String existingKey = ShopConfig.getShopItems(shopId).stream().filter(k -> ShopConfig.getSlot(shopId, k) == slot2).findFirst().orElse(null);
                    if (existingKey != null) {
                        ShopEditorGUI.open(player, shopId, existingKey);
                        return;
                    }
                    String key = "new_item_" + System.currentTimeMillis();
                    FileConfiguration config = ShopConfig.getShopConfig(shopId);
                    ConfigurationSection section = config.createSection(key);
                    section.set("source", (Object)"STONE");
                    section.set("slot", (Object)slot2);
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
                    ShopConfig.getShopConfigMap().put(shopId, config);
                    try {
                        config.save(new File("plugins/Sushidynamicshop/shop/" + shopId + ".yml"));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    ShopEditorGUI.open(player, shopId, key);
                    return;
                }
                String key = ShopConfig.getShopItems(shopId).stream().filter(k -> ShopConfig.getSlot(shopId, k) == slot2).findFirst().orElse(null);
                if (key == null) {
                    return;
                }
                String source = ShopConfig.getSource(shopId, key);
                if (source != null && source.toLowerCase().startsWith("itemadder:")) {
                    return;
                }
                if (source != null && source.toLowerCase().startsWith("nexo:")) {
                    return;
                }
                boolean isShift = event.getClick().isShiftClick();
                boolean isLeft = event.isLeftClick();
                boolean isRight = event.isRightClick();
                ItemStack previewItem = this.createItem(source);
                int maxStack = previewItem.getMaxStackSize();
                int n = amount = isShift ? maxStack : 1;
                if (isLeft && ShopConfig.canBuy(shopId, key)) {
                    boolean realSupply = ShopConfig.isRealSupply(shopId, key);
                    int supply = (int)ShopConfig.getSupply(shopId, key);
                    if (realSupply && supply == 0) {
                        player.sendMessage(TranslationUtil.get("out_of_stock"));
                        event.setCancelled(true);
                        return;
                    }
                    totalPrice = 0.0;
                    tempSupply = (int)ShopConfig.getSupply(shopId, key);
                    tempDemand = (int)ShopConfig.getDemand(shopId, key);
                    for (int i = 0; i < amount; ++i) {
                        double unitPrice = PriceHandler.calculateBuyPrice(shopId, key, tempSupply, tempDemand + i);
                        totalPrice += unitPrice;
                        ++tempDemand;
                    }
                    ShopCurrency shopCurrency = ShopConfig.getCurrency(shopId);
                    String customCurrencyId = ShopConfig.getCustomCurrencyId(shopId);
                    success = false;
                    if (shopCurrency == ShopCurrency.CUSTOM) {
                        if ("no_currency_set".equals(customCurrencyId)) {
                            player.sendMessage(TranslationUtil.get("no_currency_set"));
                            return;
                        }
                        registry = PremiumInitializer.getCurrencyRegistry();
                        exists = registry.currencyExists(customCurrencyId);
                        if (!exists) {
                            player.sendMessage(TranslationUtil.get("no_currency_set"));
                            return;
                        }
                        pronoun = registry != null ? registry.getPronoun(customCurrencyId) : customCurrencyId;
                        boolean hasEnough = CurrencyHandler.hasEnough(player, customCurrencyId, totalPrice);
                        if (!hasEnough) {
                            player.sendMessage(TranslationUtil.get("not_enough_custom", Map.of("currency", pronoun, "required", String.format("%.2f", totalPrice))));
                            return;
                        }
                        success = CurrencyHandler.withdraw(player, customCurrencyId, totalPrice);
                    } else {
                        boolean hasEnough = CurrencyHandler.hasEnough(player, shopCurrency.name(), totalPrice);
                        if (!hasEnough) {
                            String messageKey = switch (shopCurrency) {
                                case ShopCurrency.EXP -> "not_enough_level";
                                case ShopCurrency.POINTS -> "not_enough_points";
                                default -> "not_enough_money";
                            };
                            player.sendMessage(TranslationUtil.get(messageKey, Map.of("required", String.format("%.2f", totalPrice))));
                            return;
                        }
                        success = CurrencyHandler.withdraw(player, shopCurrency.name(), totalPrice);
                    }
                    if (success) {
                        ItemStack give = this.createItem(source);
                        if (give.getType() == Material.BARRIER) {
                            player.sendMessage(String.valueOf(ChatColor.RED) + "You cannot buy this item.");
                            return;
                        }
                        give.setAmount(amount);
                        HashMap remaining = player.getInventory().addItem(new ItemStack[]{give});
                        if (!remaining.isEmpty()) {
                            player.sendMessage(TranslationUtil.get("not_enough_space"));
                            return;
                        }
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0f, 1.5f);
                        player.sendMessage(TranslationUtil.get("bought"));
                        if (realSupply) {
                            int remainingSupply = supply - amount;
                            remainingSupply = Math.max(0, remainingSupply);
                            ShopConfig.setSupply(shopId, key, remainingSupply);
                        }
                        ShopConfig.incrementDemand(shopId, key, amount);
                        ShopGUI.open(player, shopId);
                    }
                }
                if (isRight && ShopConfig.canSell(shopId, key)) {
                    int playerItemCount = this.countItem(player, source);
                    if (playerItemCount == 0) {
                        player.sendMessage(TranslationUtil.get("no_item_to_sell"));
                        return;
                    }
                    int actualAmount = Math.min(amount, playerItemCount);
                    totalPrice = 0.0;
                    tempSupply = (int)ShopConfig.getSupply(shopId, key);
                    tempDemand = (int)ShopConfig.getDemand(shopId, key);
                    for (int i = 0; i < actualAmount; ++i) {
                        double unitPrice = PriceHandler.calculateSellPrice(shopId, key, tempSupply, tempDemand - i);
                        totalPrice += unitPrice;
                        --tempDemand;
                    }
                    ShopCurrency shopCurrency = ShopConfig.getCurrency(shopId);
                    String customCurrencyId = ShopConfig.getCustomCurrencyId(shopId);
                    success = false;
                    if (shopCurrency == ShopCurrency.CUSTOM) {
                        if ("no_currency_set".equals(customCurrencyId)) {
                            player.sendMessage(TranslationUtil.get("no_currency_set"));
                            return;
                        }
                        registry = PremiumInitializer.getCurrencyRegistry();
                        exists = registry.currencyExists(customCurrencyId);
                        if (!exists) {
                            player.sendMessage(TranslationUtil.get("no_currency_set"));
                            return;
                        }
                        pronoun = registry != null ? registry.getPronoun(customCurrencyId) : customCurrencyId;
                        CurrencyHandler.deposit(player, customCurrencyId, totalPrice);
                        player.sendMessage(TranslationUtil.get("sold_custom", Map.of("currency", pronoun, "amount", String.format("%.2f", totalPrice))));
                        success = true;
                    } else {
                        CurrencyHandler.deposit(player, shopCurrency.name(), totalPrice);
                        String messageKey = switch (shopCurrency) {
                            case ShopCurrency.EXP -> "sold_exp";
                            case ShopCurrency.POINTS -> "sold_points";
                            default -> "sold";
                        };
                        player.sendMessage(TranslationUtil.get(messageKey, Map.of("amount", String.format("%.2f", totalPrice))));
                        success = true;
                    }
                    if (success) {
                        this.removeItem(player, source, actualAmount);
                        ShopConfig.incrementSupply(shopId, key, actualAmount);
                        ShopGUI.open(player, shopId);
                    }
                }
            }
        }
    }
    private int countItem(Player player, String source) {
        ItemStack target = this.createItem(source);
        if (target.getType() == Material.BARRIER) {
            return 0;
        }
        boolean isMMO = source.toLowerCase().startsWith("mmoitem:");
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (isMMO) {
                try {
                    NBTItem nbtTarget = NBTItem.get((ItemStack)target);
                    NBTItem nbt = NBTItem.get((ItemStack)item);
                    if (!nbt.hasType() || !nbt.getType().equals(nbtTarget.getType()) || !nbt.getString("MMOITEMS_ITEM_ID").equals(nbtTarget.getString("MMOITEMS_ITEM_ID"))) continue;
                    total += item.getAmount();
                }
                catch (Throwable throwable) {}
                continue;
            }
            if (!item.isSimilar(target)) continue;
            total += item.getAmount();
        }
        return total;
    }

    private void removeItem(Player player, String source, int amount) {
        ItemStack target = this.createItem(source);
        if (target.getType() == Material.BARRIER) {
            return;
        }
        boolean isMMO = source.toLowerCase().startsWith("mmoitem:");
        NBTItem nbtTarget = null;
        if (isMMO) {
            try {
                nbtTarget = NBTItem.get((ItemStack)target);
            }
            catch (Throwable ignored) {
                return;
            }
        }
        for (int i = 0; i < player.getInventory().getSize(); ++i) {
            boolean match;
            ItemStack item;
            block10: {
                item = player.getInventory().getItem(i);
                if (item == null) continue;
                match = false;
                if (isMMO && nbtTarget != null) {
                    try {
                        NBTItem nbt = NBTItem.get((ItemStack)item);
                        if (nbt.hasType() && nbt.getType().equals(nbtTarget.getType()) && nbt.getString("MMOITEMS_ITEM_ID").equals(nbtTarget.getString("MMOITEMS_ITEM_ID"))) {
                            match = true;
                        }
                        break block10;
                    }
                    catch (Throwable ignored) {
                        continue;
                    }
                }
                if (item.isSimilar(target)) {
                    match = true;
                }
            }
            if (!match) continue;
            int remove = Math.min(amount, item.getAmount());
            item.setAmount(item.getAmount() - remove);
            if ((amount -= remove) <= 0) break;
        }
    }
    @EventHandler
    public void onGUIDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top.getHolder() instanceof CategoryMenuHolder || top.getHolder() instanceof ShopHolder) {
            event.setCancelled(true);
        }
    }

}