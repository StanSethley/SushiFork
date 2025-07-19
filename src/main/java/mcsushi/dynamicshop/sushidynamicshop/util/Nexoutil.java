/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nexomc.nexo.api.NexoItems
 *  com.nexomc.nexo.items.ItemBuilder
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package mcsushi.dynamicshop.sushidynamicshop.util;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import mcsushi.dynamicshop.sushidynamicshop.hook.NexoHook;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Nexoutil {
    public static boolean isNexoItem(ItemStack item) {
        if (!NexoHook.isHooked() || item == null) {
            return false;
        }
        try {
            return NexoItems.idFromItem((ItemStack)item) != null;
        }
        catch (Throwable t) {
            return false;
        }
    }

    @Nullable
    public static String getNexoId(ItemStack item) {
        if (!NexoHook.isHooked() || item == null) {
            return null;
        }
        try {
            return NexoItems.idFromItem((ItemStack)item);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static ItemStack createItem(String id, int amount) {
        if (!NexoHook.isHooked() || id == null || id.isEmpty()) {
            return Nexoutil.buildBarrier("Nexo not available or ID invalid");
        }
        try {
            ItemBuilder builder = NexoItems.itemFromId((String)id);
            if (builder == null) {
                return Nexoutil.buildBarrier("Unknown Nexo ID: " + id);
            }
            ItemStack item = builder.build();
            item.setAmount(amount);
            return item;
        }
        catch (Throwable t) {
            return Nexoutil.buildBarrier("Exception creating Nexo item: " + id);
        }
    }

    private static ItemStack buildBarrier(String reason) {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        return barrier;
    }
}

