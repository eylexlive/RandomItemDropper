package io.github.eylexlive.randomitemdropper.util;

import io.github.eylexlive.randomitemdropper.RandomItemDropper;
import io.github.eylexlive.randomitemdropper.manager.DatabaseManager;

public class DataSaver {
    private final RandomItemDropper plugin;
    private final DatabaseManager databaseManager;
    public DataSaver(RandomItemDropper plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }
    public void save() {
        this.plugin.getServer().getScheduler()
                .runTaskTimer(this.plugin, this.databaseManager::saveDatabase, 0L, 20*60*30L);
    }
}
