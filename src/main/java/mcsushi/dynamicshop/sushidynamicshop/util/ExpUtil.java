/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package mcsushi.dynamicshop.sushidynamicshop.util;

import org.bukkit.entity.Player;

public class ExpUtil {
    public static boolean hasEnough(Player player, int requiredLevels) {
        return player.getLevel() >= requiredLevels;
    }

    public static boolean removeLevel(Player player, int levelsToRemove) {
        int current = player.getLevel();
        if (current < levelsToRemove) {
            return false;
        }
        player.setLevel(current - levelsToRemove);
        return true;
    }

    public static void addLevel(Player player, int levelsToAdd) {
        player.setLevel(player.getLevel() + levelsToAdd);
    }

    public static int getLevel(Player player) {
        return player.getLevel();
    }
}

