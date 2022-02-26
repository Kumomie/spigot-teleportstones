package kumomi.teleportstones.storage.blueprint;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import kumomi.teleportstones.App;
import kumomi.teleportstones.build.BlueprintValidator;
import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.configuration.Configuration;
import kumomi.teleportstones.configuration.Configuration.DefaultTeleportStones;
import kumomi.teleportstones.util.ConfigFile;

public class BlueprintManager {

    public static final HashMap<String, Blueprint> blueprints;

    static {
        blueprints = new HashMap<>();
    }

    private App app;
    private Configuration configuration;

    public BlueprintManager(App app, Configuration configuration) {
        this.app = app;
        this.configuration = configuration;
    }

    public boolean init() {

        // Load Default BluePrints
        DefaultTeleportStones selectedDefault;
        selectedDefault = DefaultTeleportStones
                .valueOf((String) configuration.getYmlConfiguration().get("defaultTeleportStones"));

        InputStream stream;

        switch (selectedDefault) {
            case ONE_STONE_TO_RULE_THEM_ALL:
                stream = app.getResource("defaultBluePrints/one stone to rule them all.yml");
                break;

            case SIMPLE:
                stream = app.getResource("defaultBluePrints/simple.yml");
                break;

            case NORMAL:
                stream = app.getResource("defaultBluePrints/normal.yml");
                break;

            case ADVANCED:
                stream = app.getResource("defaultBluePrints/advanced.yml");
                break;

            case DISABLE:
                stream = null;
                break;

            default:
                stream = null;
                break;
        }

        if (stream == null && selectedDefault != DefaultTeleportStones.DISABLE) {
            app.getLogger().severe("Failed to get resource for default TeleportStones.");
            return false;
        }

        if (stream != null) {
            FileConfiguration defaultTeleportStoneConfig = new YamlConfiguration();

            InputStreamReader reader = new InputStreamReader(stream);

            try {
                defaultTeleportStoneConfig.load(reader);
            } catch (IOException | InvalidConfigurationException e) {
                app.getLogger().severe("Failed to read/stream default of TeleportStone blueprints!" + e.getMessage());
                return false;
            }

            List<?> defaultTeleportStones = defaultTeleportStoneConfig.getList("blueprints");

            defaultTeleportStones.forEach((b) -> {

                if (!(b instanceof Blueprint)) {
                    app.getLogger().severe("Error in default TeleportStone blueprint!");
                    return;
                }

                Blueprint bluePrint = (Blueprint) b;

                BlueprintValidator bluePrintValidator = new BlueprintValidator();

                if (!bluePrintValidator.validateBeforeAdd(bluePrint)) {
                    app.getLogger().severe(bluePrintValidator.getMessage());
                    return;
                }

                BlueprintManager.blueprints.put(bluePrint.getId(), bluePrint);
            });
        }

        int loadedDefaultBlueprints = blueprints.size();
        app.getLogger().info("Loaded " + blueprints.size() + " default blueprints.");

        File customBlueprintsFolder = new File(app.getDataFolder(), "CustomBlueprints");

        if (!customBlueprintsFolder.exists()) {
            if (!customBlueprintsFolder.mkdir()) {
                app.getLogger().warning("Couldn't create CustomBlueprints folder.");
            }
        } else {

            for (String customBlueprintFileName : customBlueprintsFolder.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !dir.isDirectory() && name.endsWith(".yml");
                }
            })) {
                File customBlueprintFile = new File(customBlueprintsFolder, customBlueprintFileName);
                ConfigFile customBlueprintConfig = new ConfigFile(app, customBlueprintFile);

                if (!customBlueprintConfig.loadConfiguration())
                    continue;

                List<?> customBlueprints = customBlueprintConfig.getYmlConfiguration().getList("blueprints");

                if (customBlueprints == null)
                    continue;

                customBlueprints.forEach(b -> {
                    if (!(b instanceof Blueprint))
                        return;

                    Blueprint blueprint = (Blueprint) b;

                    BlueprintValidator blueprintValidator = new BlueprintValidator();
                    if (!blueprintValidator.validateBeforeAdd(blueprint)) {

                        app.getLogger().warning(blueprintValidator.getMessage() + " Blueprint will not be added!");
                        return;
                    }

                    BlueprintManager.blueprints.put(blueprint.getId(), blueprint);
                });
            }

        }

        app.getLogger().info("Loaded " + (blueprints.size() - loadedDefaultBlueprints) + " custom blueprints.");

        return true;
    }

}
