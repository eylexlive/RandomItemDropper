package io.github.eylexlive.randomitemdropper.runnable;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class AsyncDropRunnable extends BukkitRunnable {
    private int x = 0;
    private final Map<Location, ItemStack> map;
    public AsyncDropRunnable(Map<Location, ItemStack> map) {
        this.map = map;
    }
    @Override
    public void run() {
        if (this.x >= this.map.size()) {
            this.cancel();
            return;
        }
        final Location location = (Location) map.keySet().toArray()[this.x];
        final ItemStack itemStack = map.get(location);
        location.getWorld().dropItem(location, itemStack);
        this.x++;
    }
}
