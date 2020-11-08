package io.github.eylexlive.randomitemdropper.manager;

import io.github.eylexlive.randomitemdropper.RandomItemDropper;
import io.github.eylexlive.randomitemdropper.runnable.AsyncDropRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DropManager {
    private final String name;
    private final FileConfiguration databaseConfig;
    public DropManager(String name, FileConfiguration databaseConfig) {
        this.name = name;
        this.databaseConfig = databaseConfig;
    }
    public void create() {
        if (this.isValid())
            return;
        for (int i = 1; i <= 2; i++)
            this.databaseConfig.set("drops." + this.name + ".loc" + (i), "LOCATION");
    }
    public void delete() {
        if (!this.isValid())
            return;
        this.databaseConfig.set("drops." + this.name, null);
    }
    public void setLoc(Location location, int loc) {
        if (!this.isValid())
            throw new IllegalStateException("Drop is not valid!");
        final String locationFromString = Objects.requireNonNull(
                location.getWorld()).getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
        this.databaseConfig.set("drops." + this.name + ".loc" + (loc), locationFromString);
    }
    public void addItem(Material material) {
        if (!this.isValid())
            throw new IllegalStateException("Drop is not valid!");
        this.databaseConfig.set("drops." + this.name +".items." + material.name().toUpperCase() + ".chance", 0);
    }
    public void removeItem(Material material) {
        if (!this.isValid())
            throw new IllegalStateException("Drop is not valid!");
        this.databaseConfig.set("drops." + this.name + ".items." + material.name().toUpperCase(), null);
    }
    public void setChance(Material material, double chance) {
        if (!this.isValid())
            throw new IllegalStateException("Drop is not valid!");
        this.databaseConfig.set("drops." + this.name +".items." + material.name().toUpperCase() + ".chance", chance);
    }
    public boolean setAsync() {
        if (!this.isValid())
            throw new IllegalStateException("Drop is not valid!");
        boolean isAsync = this.isAsync();
        this.databaseConfig.set("drops." + this.name + ".async.enabled", !isAsync);
        return !isAsync;
    }
    public void setAsyncDelay(int delay) {
        if (!this.isValid())
            throw new IllegalStateException("Drop is not valid!");
        this.databaseConfig.set("drops." + this.name + ".async.delay", delay);
    }
    public void dropItems(int size) {
        if (!this.isValid())
            throw new IllegalStateException("Drop is not valid!");
        final Map<Location, ItemStack> locationItemStackMap = new HashMap<>();
        for (int i = 1; i <= size; i++)  {
            final Location location = this.getRandomLocation();
            final ItemStack itemStack = this.getRandomItem();
            if (location == null || itemStack == null)
                break;
            locationItemStackMap.put(location, itemStack);
        }
        if (!this.isAsync()) {
            locationItemStackMap.forEach((key, value) -> Objects.requireNonNull(key.getWorld()).dropItem(key, value));
        } else {
            final int delay = this.getAsyncDelay();
            new AsyncDropRunnable(locationItemStackMap).runTaskTimer(RandomItemDropper.getInstance(), 0L,  delay);
        }
    }
    public Location getRandomLocation() {
        final Location[] loc = {this.getLocation(1), this.getLocation(2)};
        if (!loc[0].getWorld().getName().equalsIgnoreCase(loc[1].getWorld().getName()))
            throw new IllegalStateException("The between location 1 and location 2 too long!");
        final double[] minDoubles = {
                Math.min(loc[0].getX(), loc[1].getX()),
                Math.min(loc[0].getY(), loc[1].getY()),
                Math.min(loc[0].getZ(), loc[1].getZ())
        };
        final double[] maxDoubles = {
                Math.max(loc[0].getX(), loc[1].getX()),
                Math.max(loc[0].getY(), loc[1].getY()) ,
                Math.max(loc[0].getZ(), loc[1].getZ())
        };
        return new Location(loc[0].getWorld(), this.getRandomDouble(minDoubles[0], maxDoubles[0]), this.getRandomDouble(minDoubles[1], maxDoubles[1]), this.getRandomDouble(minDoubles[2], maxDoubles[2]));
    }
    private double getRandomDouble(double x, double y) {
        return x + ThreadLocalRandom.current().nextDouble(Math.abs(y - x + 1));
    }
    private Location getLocation(int loc) {
        final String locationFromString = this.getLocationFromString(loc);
        if (locationFromString == null)
            throw new NullPointerException("Location value null!");
        final String[] splitLocation = locationFromString.split(",");
        final double[] doubles = new double[3];
        for (int x = 0; x < 3; x++)
            doubles[x] = Double.parseDouble(splitLocation[x+1]);
        return new Location (Bukkit.getWorld(splitLocation[0]), doubles[0], doubles[1], doubles[2]);
    }
    private ItemStack getRandomItem() {
        final Random random = new Random();
        double d = random.nextDouble() * 100;
        for (String key : this.getItems(true)) {
            final String[] split = key.split("/");
            final double chance = Double.parseDouble(split[1]);
            if ((d -= chance) < 0)
                return new ItemStack(Material.getMaterial(split[0]));
        }
        return null;
    }
    private List<String> getItems(boolean bool) {
        final List<String> itemList = new ArrayList<>();
        this.databaseConfig.getConfigurationSection("drops." + this.name + ".items")
                .getKeys(false)
                .forEach(key -> {
                    final double chance = this.databaseConfig.getDouble("drops." + this.name + ".items." + key + ".chance");
                    itemList.add(bool ? key + "/" + chance : "§8- §f" + key + " §a" + chance +"%");
        });
        return itemList;
    }
    public String[] getInfoMessage() {
        final boolean isAsync = this.isAsync();
        return new String[] {
                "§8§m---------------------------------",
                "",
                "§fDrop: §a" + this.name,"§fAsync: §a" + (isAsync ? "Enabled" : "Disabled"),
                "§fAsync delay: §a" + (isAsync ? this.getAsyncDelay() / 20 + "s" : "Async disabled"),
                "§fLocation one: §a" + this.getLocationFromString(1),
                "§fLocation two: §a" + this.getLocationFromString(2) ,
                "",
                "§fItems:",
                String.join("\n", this.getItems(false)),
                "",
                "§8§m---------------------------------"
        };
    }
    public String getLocationFromString(int loc) {
        return this.databaseConfig.getString("drops." + this.name + ".loc" +  (loc));
    }
    public int getAsyncDelay() {
        return  this.databaseConfig.getInt("drops." + this.name + ".async.delay") * 20;
    }
    private boolean isAsync() {
        return this.databaseConfig.getBoolean("drops." + this.name + ".async.enabled");
    }
    private boolean isValid() {
        return this.databaseConfig.getConfigurationSection("drops." + this.name) != null;
    }
}
