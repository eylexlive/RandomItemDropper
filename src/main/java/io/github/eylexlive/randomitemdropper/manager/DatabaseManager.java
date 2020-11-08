package io.github.eylexlive.randomitemdropper.manager;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class DatabaseManager {
    @Getter private final FileConfiguration databaseConfiguration;
    private final File databaseFile;
    public DatabaseManager() {
        this.databaseFile = new File("plugins/RandomItemDropper/drops.yml");
        this.databaseConfiguration = YamlConfiguration.loadConfiguration(this.databaseFile);
    }
    public void saveDatabase() {
        try {
            this.databaseConfiguration.save(this.databaseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
