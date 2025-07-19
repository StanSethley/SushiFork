/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package mcsushi.dynamicshop.sushidynamicshop.editor.holder;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CategoryEditorHolder
implements InventoryHolder {
    private final String categoryId;

    public CategoryEditorHolder(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public Inventory getInventory() {
        return null;
    }
}

