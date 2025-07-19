/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.inventory.InventoryDragEvent
 */
package mcsushi.dynamicshop.sushidynamicshop.debug;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;

public class DragEventDebugger
implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void onAnyDrag(InventoryDragEvent event) {
        System.out.println("[DEBUGGER] DragEventDebugger triggered");
        System.out.println("[DEBUGGER] Holder: " + (event.getView().getTopInventory().getHolder() != null ? event.getView().getTopInventory().getHolder().getClass().getSimpleName() : "null"));
        System.out.println("[DEBUGGER] Slots: " + String.valueOf(event.getRawSlots()));
    }
}

