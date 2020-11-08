package io.github.eylexlive.randomitemdropper.util;

import io.github.eylexlive.randomitemdropper.RandomItemDropper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateCheck {
    private final RandomItemDropper plugin;
    public UpdateCheck(RandomItemDropper plugin) {
        this.plugin = plugin;
    }
    public void checkUpdate() {
        String ver = this.plugin.getDescription().getVersion();
        System.out.println("-----------------------------");
        System.out.println("   RandomItemDropper Updater   ");
        System.out.println(" ");
        System.out.println("v" + ver + " running now");
        if (this.isAvailable()) {
            System.out.println("A new update is available at");
            System.out.println("spigotmc.org/resources/80938");
            System.out.println(" ");
        } else {
            System.out.println("The last version of");
            System.out.println("RandomItemDropper");
            System.out.println(" ");
        }
        System.out.println("-----------------------------");
        System.out.println(" ");
        this.plugin.getLogger().info("The plugin enabled! (v" + ver + ")");
        this.plugin.getLogger().info("by EylexLive");
    }
    private boolean isAvailable() {
        final URLConnection urlConnection;
        final String spigotPluginVersion;
        try {
            urlConnection = new URL(
                    "https://api.spigotmc.org/legacy/update.php?resource=80938"
            ).openConnection();
            spigotPluginVersion = new BufferedReader(
                    new InputStreamReader(
                            urlConnection.getInputStream())
            ).readLine();
        } catch (IOException e) {
            return false;
        }
        return !this.plugin.getDescription().getVersion().equals(spigotPluginVersion);
    }
}
