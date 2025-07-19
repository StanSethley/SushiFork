/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.lone.itemsadder.api.CustomStack
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package mcsushi.dynamicshop.sushidynamicshop.util;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemAdderUtil {
    public static ItemStack createItem(String source, int amount) {
        if (source == null || source.isEmpty()) {
            System.out.println("[Sushidynamicshop] ItemAdder source is null or empty.");
            return new ItemStack(Material.BARRIER);
        }
        try {
            if (!CustomStack.isInRegistry((String)source)) {
                System.out.println("[Sushidynamicshop] ItemAdder item not in registry: " + source);
                return new ItemStack(Material.BARRIER);
            }
            CustomStack stack = CustomStack.getInstance((String)source);
            if (stack == null) {
                System.out.println("[Sushidynamicshop] Failed to get CustomStack instance for: " + source);
                return new ItemStack(Material.BARRIER);
            }
            ItemStack item = stack.getItemStack();
            item.setAmount(amount);
            return item;
        }
        catch (Exception e) {
            System.out.println("[Sushidynamicshop] Exception while building ItemAdder item: " + e.getMessage());
            e.printStackTrace();
            return new ItemStack(Material.BARRIER);
        }
    }

    public static boolean isItemAdderItem(ItemStack item) {
        return CustomStack.byItemStack((ItemStack)item) != null;
    }

    public static String getNamespacedId(ItemStack item) {
        CustomStack stack = CustomStack.byItemStack((ItemStack)item);
        return stack != null ? stack.getNamespacedID() : null;
    }
}

