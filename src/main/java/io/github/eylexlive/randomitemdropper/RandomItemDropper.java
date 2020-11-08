package io.github.eylexlive.randomitemdropper;

import io.github.eylexlive.randomitemdropper.command.MainCommand;
import io.github.eylexlive.randomitemdropper.listener.EventListener;
import io.github.eylexlive.randomitemdropper.manager.DatabaseManager;
import io.github.eylexlive.randomitemdropper.runnable.AutoDropRunnable;
import io.github.eylexlive.randomitemdropper.util.DataSaver;
import io.github.eylexlive.randomitemdropper.util.Metrics;
import io.github.eylexlive.randomitemdropper.util.UpdateCheck;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomItemDropper extends JavaPlugin {
    @Getter private static RandomItemDropper instance;
    @Getter private DatabaseManager databaseManager;
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.databaseManager = new DatabaseManager();
        this.getCommand("rid").setExecutor(
                new MainCommand(this)
        );
        this.getServer().getPluginManager().registerEvents(
                new EventListener(), this
        );
        this.getServer().getScheduler().scheduleSyncRepeatingTask(
                        this,
                        new AutoDropRunnable(this.getConfig(), this.databaseManager.getDatabaseConfiguration()), 0L, 1200L
                );
        new Metrics(this);
        new DataSaver(this, this.databaseManager).save();
        new UpdateCheck(this).checkUpdate();
    }
    @Override
    public void onDisable() {
        this.databaseManager.saveDatabase();
    }
}
