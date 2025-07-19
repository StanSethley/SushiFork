/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.configuration.file.FileConfiguration
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryHolder
 */
package mcsushi.dynamicshop.sushidynamicshop.gui;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShopHolder
implements InventoryHolder {
    private final String shopId;
    private final FileConfiguration config;
    private final File file;
    private final int page;
    private final int menuType;

    public ShopHolder(String shopId, FileConfiguration config, File file, int page, int menuType) {
        this.shopId = shopId;
        this.config = config;
        this.file = file;
        this.page = page;
        this.menuType = menuType;
    }

    public Inventory getInventory() {
        return null;
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public File getFile() {
        return this.file;
    }

    public int getPage() {
        return this.page;
    }

    public int getMenuType() {
        return this.menuType;
    }

    public String getShopId() {
        return this.shopId;
    }
}

