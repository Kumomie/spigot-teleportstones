package kumomi.teleportstones.configuration;

import java.io.File;

import kumomi.teleportstones.App;
import kumomi.teleportstones.util.ConfigFile;

public class Configuration extends ConfigFile {

    public static enum DefaultTeleportStones {
        ONE_STONE_TO_RULE_THEM_ALL, SIMPLE, NORMAL, ADVANCED, DISABLE
    }

    public static enum TeleportStoneProtection {
        INTERN
    }

    public static enum FailedStartupValidationHandling {
        PURGE, MOVE_BIN
    }

    public static enum logDetail {
        ALL, HIGH, LOW
    }

    public Configuration(App app) {
        super(app, new File(app.getDataFolder(), "config.yml"));
    }

    public boolean validateConfig() {

        boolean isValid = true;

        String path;

        path = "enablePlugin";
        if (getYmlConfiguration().contains(path)) {
            if (!getYmlConfiguration().isBoolean(path)) {
                isValid = false;
                getApp().getLogger().warning("Config entry " + path + " has wrong type.");
            }
        } else {
            isValid = false;
            getApp().getLogger().warning("Missing config entry: " + path);
        }

        path = "enablePermissions";
        if (getYmlConfiguration().contains(path)) {
            if (!getYmlConfiguration().isBoolean(path)) {
                isValid = false;
                getApp().getLogger().warning("Config entry " + path + " has wrong type.");
            }
        } else {
            isValid = false;
            getApp().getLogger().warning("Missing config entry: " + path);
        }

        path = "keyword";
        if (getYmlConfiguration().contains(path)) {
            if (!getYmlConfiguration().isString(path)) {
                isValid = false;
                getApp().getLogger().warning("Config entry " + path + " has wrong type.");
            }
        } else {
            isValid = false;
            getApp().getLogger().warning("Missing config entry: " + path);
        }

        path = "defaultTeleportStones";
        if (getYmlConfiguration().contains(path)) {
            if (DefaultTeleportStones.valueOf((String) getYmlConfiguration().get(path)) == null) {
                isValid = false;
                getApp().getLogger().warning("Config entry " + path + " has wrong type.");
            }
        } else {
            isValid = false;
            getApp().getLogger().warning("Missing config entry: " + path);
        }

        path = "minimalTeleportStoneNameLength";
        if (getYmlConfiguration().contains(path)) {
            if (!getYmlConfiguration().isInt(path)) {
                isValid = false;
                getApp().getLogger().warning("Config entry " + path + " has wrong type.");
            }
        } else {
            isValid = false;
            getApp().getLogger().warning("Missing config entry: " + path);
        }

        path = "teleportStoneProtectMethod";
        if (getYmlConfiguration().contains(path)) {

            if (TeleportStoneProtection.valueOf((String) getYmlConfiguration().get(path)) == null) {
                isValid = false;
                getApp().getLogger().warning("Config entry " + path + " has wrong type.");
            }

        } else {
            isValid = false;
            getApp().getLogger().warning("Missing config entry: " + path);
        }

        // path = "onFailedStartUpValidation";
        // if (getYmlConfiguration().contains(path)) {

        //     if (FailedStartupValidationHandling.valueOf((String) getYmlConfiguration().get(path)) == null) {
        //         isValid = false;
        //         getApp().getLogger().warning("Config entry " + path + " has wrong type.");
        //     }

        // } else {
        //     isValid = false;
        //     getApp().getLogger().warning("Missing config entry: " + path);
        // }

        // path = "logDetail";
        // if (getYmlConfiguration().contains(path)) {

        //     if (logDetail.valueOf((String) getYmlConfiguration().get(path)) == null) {
        //         isValid = false;
        //         getApp().getLogger().warning("Config entry " + path + " has wrong type.");
        //     }

        // } else {
        //     isValid = false;
        //     getApp().getLogger().warning("Missing config entry: " + path);
        // }

        path = "autoSafeIntervall";
        if (getYmlConfiguration().contains(path)) {
            if (!getYmlConfiguration().isDouble(path)) {
                isValid = false;
                getApp().getLogger().warning("Config entry " + path + " has wrong type.");
            } else {
                if (getYmlConfiguration().getDouble(path) < 0 && getYmlConfiguration().getDouble(path) != -1) {
                    isValid = false;
                    getApp().getLogger().warning("Config entry " + path + " is a negative number, that's not -1.0.");
                }
            }
        } else {
            isValid = false;
            getApp().getLogger().warning("Missing config entry: " + path);
        }

        path = "enableTabCompletion";
        if (getYmlConfiguration().contains(path)) {
            if (!getYmlConfiguration().isBoolean(path)) {
                isValid = false;
                getApp().getLogger().warning("Config entry " + path + " has wrong type.");
            } 
        } else {
            isValid = false;
            getApp().getLogger().warning("Missing config entry: " + path);
        }

        return isValid;
    }

}
