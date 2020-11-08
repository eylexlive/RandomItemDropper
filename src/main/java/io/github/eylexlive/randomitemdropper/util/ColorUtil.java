package io.github.eylexlive.randomitemdropper.util;

import org.bukkit.ChatColor;

public class ColorUtil {
    public static String translate(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
