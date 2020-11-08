package io.github.eylexlive.randomitemdropper.runnable;

import io.github.eylexlive.randomitemdropper.manager.DropManager;
import io.github.eylexlive.randomitemdropper.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AutoDropRunnable implements Runnable {
    private final FileConfiguration configuration;
    private final FileConfiguration database;
    public AutoDropRunnable(FileConfiguration configuration, FileConfiguration database) {
        this.configuration = configuration;
        this.database = database;
    }
    @Override
    public void run() {
        if (this.configuration.getBoolean("auto-dropper.enabled")) {
            final DateFormat dateFormat = new SimpleDateFormat("HH:mm");
            final Date date = new Date();
            final String dateString = dateFormat.format(date);
            if (this.configuration.getConfigurationSection("auto-dropper." + dateString) != null) {
                final String dropName = this.configuration.getString("auto-dropper." + dateString +".drop-name");
                final int size = this.configuration.getInt("auto-dropper." + dateString +".drop-size");
                final DropManager dropManager = new DropManager(dropName, this.database);
                dropManager.dropItems(size);
                if (this.configuration.getBoolean("auto-dropper." + dateString + ".broadcast-message-enabled")) {
                    String msg = this.configuration.getString("auto-dropper." + dateString + ".broadcast-message");
                    msg = msg.replace("%drop_size%", String.valueOf(size));
                    for (int i = 1; i <= 2; i++)
                        msg = msg.replace("%location_" + (i) +"%",dropManager.getLocationFromString(i));
                    Bukkit.broadcastMessage(ColorUtil.translate(msg));
                }
            } else if (this.configuration.getBoolean("auto-dropper.minutes-remaining-broadcast-enabled")) {
                final Calendar calendar = Calendar.getInstance();
                this.configuration.getConfigurationSection("auto-dropper.minutes-remaining-broadcast")
                        .getKeys(false)
                        .forEach(key -> {
                            final int x = Integer.parseInt(key);
                            calendar.add(Calendar.MINUTE, x);
                            final String calenderTime = dateFormat.format(calendar.getTime());
                            this.configuration.getConfigurationSection("auto-dropper").getKeys(false).stream().filter(calenderTime::equals).forEach(k -> {
                                String msg = this.configuration.getString("auto-dropper.minutes-remaining-broadcast." + key + ".message");
                                msg = msg.replace("%drop_size%", this.configuration.getInt("auto-dropper." + k + ".drop-size") + "");
                                msg = msg.replace("%minutes%", key);
                                final DropManager dropManager = new DropManager(this.configuration.getString("auto-dropper." + k + ".drop-name"), this.database);
                                for (int i = 1; i <= 2; i++)
                                    msg = msg.replace("%location_" + (i) +"%",dropManager.getLocationFromString(i));
                                Bukkit.broadcastMessage(ColorUtil.translate(msg));
                            });
                            calendar.add(Calendar.MINUTE, x * -1);
                        });
            }
        }
    }
}
