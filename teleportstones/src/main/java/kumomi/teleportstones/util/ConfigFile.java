package kumomi.teleportstones.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import kumomi.teleportstones.App;

public class ConfigFile {

    private App app;
    private File ymlConfigFile;

    private FileConfiguration ymlConfiguration;

    public ConfigFile(App app, File ymlConfigFile) {
        this.app = app;
        this.ymlConfigFile = ymlConfigFile;
    }

    public boolean loadConfiguration() {

        if (!ymlConfigFile.exists()) {
            ymlConfigFile.getParentFile().mkdirs();
            app.saveResource(ymlConfigFile.getName(), false);

            app.getLogger().info("Creating " + ymlConfigFile.getName() + " file.");
        }

        ymlConfiguration = new YamlConfiguration();

        try {
            ymlConfiguration.load(ymlConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            app.getLogger().severe("Couldn't read " + ymlConfigFile.getName() + ". " + e.getMessage());
            return false;
        }

        return true;
    }

    public boolean persit() {
        try {
            ymlConfiguration.save(ymlConfigFile);
        } catch (IOException e) {
            app.getLogger().severe("Couldn't persist data of " + ymlConfigFile.getName());
            return false;
        }

        return true;
    }

    public void save(String path, Object value) {
        ymlConfiguration.set(path, value);
    }

    public File getYmlConfigFile() {
        return this.ymlConfigFile;
    }

    public FileConfiguration getYmlConfiguration() {
        return this.ymlConfiguration;
    }

    public App getApp() {
        return app;
    }
}
