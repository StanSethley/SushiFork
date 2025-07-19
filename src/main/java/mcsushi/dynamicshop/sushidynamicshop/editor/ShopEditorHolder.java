/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package mcsushi.dynamicshop.sushidynamicshop.editor;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShopEditorHolder
implements InventoryHolder {
    private final String shopId;
    private final String itemKey;

    public ShopEditorHolder(String shopId, String itemKey) {
        this.shopId = shopId;
        this.itemKey = itemKey;
    }

    public String getShopId() {
        return this.shopId;
    }

    public String getItemKey() {
        return this.itemKey;
    }

    public Inventory getInventory() {
        return null;
    }
}

