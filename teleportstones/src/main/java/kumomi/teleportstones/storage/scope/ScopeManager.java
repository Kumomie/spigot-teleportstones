package kumomi.teleportstones.storage.scope;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import kumomi.teleportstones.App;
import kumomi.teleportstones.build.ScopeValidator;
import kumomi.teleportstones.build.structure.Scope;
import kumomi.teleportstones.configuration.Configuration;
import kumomi.teleportstones.configuration.Configuration.DefaultTeleportStones;
import kumomi.teleportstones.util.ConfigFile;

public class ScopeManager {

    public static final List<String> defaultScopes;
    public static final HashMap<String, Scope> scopes;

    static {

        defaultScopes = Arrays.asList("All", "Reachable");
        scopes = new HashMap<>();
    }

    private App app;
    private Configuration configuration;

    public ScopeManager(App app, Configuration configuration) {
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
                stream = app.getResource("defaultScopes/one stone to rule them all.yml");
                break;

            case SIMPLE:
                stream = app.getResource("defaultScopes/simple.yml");
                break;

            case NORMAL:
                stream = app.getResource("defaultScopes/normal.yml");
                break;

            case ADVANCED:
                stream = app.getResource("defaultScopes/advanced.yml");
                break;

            case DISABLE:
                stream = null;
                break;

            default:
                stream = null;
                break;
        }

        if (stream == null && selectedDefault != DefaultTeleportStones.DISABLE) {
            app.getLogger().severe("Failed to get resource for default scopes.");
            return false;
        }

        if (stream != null) {
            FileConfiguration defaultScopeConfig = new YamlConfiguration();

            InputStreamReader reader = new InputStreamReader(stream);

            try {
                defaultScopeConfig.load(reader);
            } catch (IOException | InvalidConfigurationException e) {
                app.getLogger().severe("Failed to read/stream default scopes!" + e.getMessage());
                return false;
            }

            List<?> defaultScopes = defaultScopeConfig.getList("scopes");

            defaultScopes.forEach((s) -> {

                if (!(s instanceof Scope)) {
                    app.getLogger().warning("Error in default TeleportStone scope!");
                    return;
                }

                Scope scope = (Scope) s;

                ScopeValidator scopeValidator = new ScopeValidator();

                if (!scopeValidator.validateBeforeAdd(scope)) {
                    app.getLogger().warning("Error in default TeleportStone blueprint! " + scopeValidator.getMessage());
                    return;
                }

                ScopeManager.scopes.put(scope.getName(), scope);
            });
        }

        int loadedDefaultScopes = scopes.size();
        app.getLogger().info("Loaded " + scopes.size() + " default scopes.");

        File customScopesFolder = new File(app.getDataFolder(), "CustomScopes");

        if (!customScopesFolder.exists()) {
            if (!customScopesFolder.mkdir()) {
                app.getLogger().warning("Couldn't create CustomScopes folder.");
            }
        } else {

            for (String customScopeFileName : customScopesFolder.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return !dir.isDirectory() && name.endsWith(".yml");
                }
            })) {
                File customScopeFile = new File(customScopesFolder, customScopeFileName);
                ConfigFile customScopeConfig = new ConfigFile(app, customScopeFile);

                if (!customScopeConfig.loadConfiguration())
                    continue;

                List<?> customScopes = customScopeConfig.getYmlConfiguration().getList("scopes");

                if (customScopes == null)
                    continue;

                customScopes.forEach(s -> {
                    if (!(s instanceof Scope))
                        return;

                    Scope scope = (Scope) s;

                    ScopeValidator scopeValidator = new ScopeValidator();
                    if (!scopeValidator.validateBeforeAdd(scope)) {

                        app.getLogger().warning(scopeValidator.getMessage() + " Scope will not be added!");
                        return;
                    }

                    ScopeManager.scopes.put(scope.getName(), scope);
                });
            }

        }

        ScopeValidator scopeValidator = new ScopeValidator();
        if (!scopeValidator.validateScopeManager()) {
            app.getLogger().warning(scopeValidator.getMessage());

            // TODO decide what should happen here
            // TODO Maybe add a serverty to all validations (serve needs diable of plugin,
            // warning is just a warning)
            // return false;
        }

        app.getLogger().info("Loaded " + (scopes.size() - loadedDefaultScopes) + " custom scopes.");

        return true;
    }

}
