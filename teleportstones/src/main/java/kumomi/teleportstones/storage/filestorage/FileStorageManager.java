package kumomi.teleportstones.storage.filestorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import kumomi.teleportstones.App;
import kumomi.teleportstones.build.TeleportStoneValidator;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageManager;
import kumomi.teleportstones.storage.model.SimpleDiscoveredTeleportStone;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.ConfigFile;
import kumomi.teleportstones.util.UserValidator;

public class FileStorageManager implements StorageManager {

    private static final ConcurrentMap<String, TeleportStone> TELEPORTSTONE_STORAGE;
    private static final ConcurrentMap<String, TeleportStone> TELEPORTSTONE_REQUEST_STORAGE;
    private static final ConcurrentMap<UUID, User> USER_STORAGE;

    static {
        TELEPORTSTONE_STORAGE = new ConcurrentHashMap<>();
        USER_STORAGE = new ConcurrentHashMap<>();
        TELEPORTSTONE_REQUEST_STORAGE = new ConcurrentHashMap<>();
    }

    private App app;

    // TeleportStone Config
    private ConfigFile TSConfig;

    // TeleportStone Config
    private ConfigFile TSRConfig;

    // User Config
    private ConfigFile UConfig;

    public FileStorageManager(App app) {
        this.app = app;

        StorageFactory.init(USER_STORAGE, TELEPORTSTONE_STORAGE, TELEPORTSTONE_REQUEST_STORAGE, app.getConfiguration());
    }

    public boolean init() {

        // app.getLogger().info("Initializing storage");

        TSConfig = new ConfigFile(app, new File(app.getDataFolder(), "storageTeleportStones.yml"));
        if (!TSConfig.loadConfiguration()) {
            return false;
        }

        TSRConfig = new ConfigFile(app, new File(app.getDataFolder(), "storageTeleportStoneRequests.yml"));
        if (!TSRConfig.loadConfiguration()) {
            return false;
        }

        UConfig = new ConfigFile(app, new File(app.getDataFolder(), "storageUsers.yml"));
        if (!UConfig.loadConfiguration()) {
            return false;
        }

        initAndValidateTeleportStoneStorage();
        initAndValidateTeleportStoneRequestStorage();
        initAndValidateUserStorage();
        

        return true;
    }

    private void initAndValidateTeleportStoneRequestStorage() {
        List<?> objTeleportStones = TSRConfig.getYmlConfiguration().getList("teleportstones");

        if (objTeleportStones == null) {

            app.getLogger().warning("Creating new teleportstone request list!");
            TSRConfig.save("teleportstones", new ArrayList<>());
            return;

        }

        for (Object objTeleportStone : objTeleportStones) {

            if (!(objTeleportStone instanceof TeleportStone)) {
                app.getLogger().warning( //
                        "Found faulty config entry in saved TeleportStone requests." //
                                + " Will be deleted after restart." //
                );

                continue;
            }

            TeleportStone teleportStone = (TeleportStone) objTeleportStone;

            if (teleportStone.getName() == null) {
                app.getLogger().warning( //
                        " TeleportStone name is missing in request. TeleportStone request will not be loaded and is gone after restart.");
                continue;
            }

            if (TELEPORTSTONE_REQUEST_STORAGE.containsKey(teleportStone.getName())) {
                app.getLogger().warning( //
                        "Found TeleportStone with duplicate name " + teleportStone.getName()
                                + " in requests. TeleportStone will not be added and is gone after restart.");
                continue;
            }

            if (TELEPORTSTONE_STORAGE.containsKey(teleportStone.getName())) {
                app.getLogger().warning( //
                        "Found TeleportStone with duplicate name " + teleportStone.getName()
                                + ". TeleportStone request will not be added and is gone after restart.");
                continue;
            }

            TeleportStoneValidator teleportStoneValidator = new TeleportStoneValidator();

            if (!teleportStoneValidator.validate(teleportStone)) {
                if (teleportStone.getSign() != null) {
                    app.getLogger().warning( //
                            teleportStone.getName() //
                                    + " at Coordinates " //
                                    + teleportStone.getSign().getX() //
                                    + " | " //
                                    + teleportStone.getSign().getY() //
                                    + " | " //
                                    + teleportStone.getSign().getZ() //
                                    + " in world " //
                                    + teleportStone.getSign().getWorld() //
                                    + ": " //
                                    + teleportStoneValidator.getMessage() //
                                    + " TeleportStone request will not be added and is gone after restart.");
                } else {
                    app.getLogger().warning( //
                            teleportStone.getName() //
                                    + ": " //
                                    + teleportStoneValidator.getMessage() //
                                    + " TeleportStone request will not be added and is gone after restart.");
                }

                continue;
            }

            TELEPORTSTONE_REQUEST_STORAGE.put(teleportStone.getName(), teleportStone);
        }
    }

    private void initAndValidateTeleportStoneStorage() {

        // app.getLogger().info("Loading TeleportStones.");

        List<?> objTeleportStones = TSConfig.getYmlConfiguration().getList("teleportstones");

        if (objTeleportStones == null) {

            app.getLogger().warning("Creating new teleportstone list!");
            TSConfig.save("teleportstones", new ArrayList<>());
            return;

        }

        for (Object objTeleportStone : objTeleportStones) {

            if (!(objTeleportStone instanceof TeleportStone)) {
                app.getLogger().warning( //
                        "Found faulty config entry in saved TeleportStones." //
                                + " Will be deleted after restart." //
                );

                continue;
            }

            TeleportStone teleportStone = (TeleportStone) objTeleportStone;

            if (teleportStone.getName() == null) {
                app.getLogger().warning( //
                        " TeleportStone name is missing. TeleportStone will not be added and is gone after restart.");
                continue;
            }

            if (TELEPORTSTONE_STORAGE.containsKey(teleportStone.getName())) {
                app.getLogger().warning( //
                        "Found TeleportStone with duplicate name " + teleportStone.getName()
                                + ". TeleportStone will not be added and is gone after restart.");
                continue;
            }

            if (TELEPORTSTONE_REQUEST_STORAGE.containsKey(teleportStone.getName())) {
                app.getLogger().warning( //
                        "Found TeleportStone with duplicate name " + teleportStone.getName()
                                + " in requests. TeleportStone will not be added and is gone after restart.");
                continue;
            }

            TeleportStoneValidator teleportStoneValidator = new TeleportStoneValidator();

            if (!teleportStoneValidator.validate(teleportStone)) {
                if (teleportStone.getSign() != null) {
                    app.getLogger().warning( //
                            teleportStone.getName() //
                                    + " at Coordinates " //
                                    + teleportStone.getSign().getX() //
                                    + " | " //
                                    + teleportStone.getSign().getY() //
                                    + " | " //
                                    + teleportStone.getSign().getZ() //
                                    + " in world " //
                                    + teleportStone.getSign().getWorld() //
                                    + ": " //
                                    + teleportStoneValidator.getMessage() //
                                    + " TeleportStone will not be added and is gone after restart.");
                } else {
                    app.getLogger().warning( //
                            teleportStone.getName() //
                                    + ": " //
                                    + teleportStoneValidator.getMessage() //
                                    + " TeleportStone will not be added and is gone after restart.");
                }

                continue;
            }

            TELEPORTSTONE_STORAGE.put(teleportStone.getName(), teleportStone);
        }

    }

    private void initAndValidateUserStorage() {

        List<?> objUsers = UConfig.getYmlConfiguration().getList("users");
        if (objUsers == null) {
            app.getLogger().warning("Creating new user list!");
            UConfig.save("users", new ArrayList<>());
            return;
        }

        for (Object objUser : objUsers) {

            if (!(objUser instanceof User)) {
                app.getLogger().warning( //
                        "Found faulty config entry in saved users." //
                                + " Will be deleted after restart." //
                );
                continue;
            }

            User user = (User) objUser;

            if (user.getUuid() == null) {
                app.getLogger().warning( //
                        " User UUID is missing. User will not be added and is gone after restart.");
                continue;
            }

            if (USER_STORAGE.containsKey(user.getUuid())) {
                app.getLogger().warning( //
                        "Found User with duplicate UUID " + user.getUuid().toString()
                                + ". User will not be added and is gone after restart.");
                continue;
            }

            UserValidator validator = new UserValidator();

            if (!validator.validateBeforeAdd(user)) {
                app.getLogger().warning( //
                        "[" //
                                + user.getUuid().toString() //
                                + "] " //
                                + user.getName() //
                                + ": " //
                                + validator.getMessage() //
                                + " User will not be added and is gone after restart.");
                continue;
            }

            USER_STORAGE.put(user.getUuid(), user);

            List<SimpleDiscoveredTeleportStone> unknownTeleportStones = new ArrayList<>();
            for (SimpleDiscoveredTeleportStone st : user.getAllDiscoveredTeleportStones()) {

                if (!TELEPORTSTONE_STORAGE.containsKey(st.getName())) {
                    unknownTeleportStones.add(st);
                }
            }

            if (!unknownTeleportStones.isEmpty()) {

                app.getLogger().warning( //
                        "User " //
                                + user.getName() //
                                + " has " //
                                + unknownTeleportStones.size() //
                                + " unkown TeleportStones in the list of discovered TeleportStones." //
                                + " These will be deleted from the list.");

                for (SimpleDiscoveredTeleportStone st : unknownTeleportStones) {
                    user.undiscover(st);
                }
            }

        }

    }

    public boolean shutdown() {
        app.getLogger().info("Shutdown file storage ...");
        return persistData();
    }

    public boolean persistData() {

        boolean success = true;

        TSConfig.save("teleportstones",  new ArrayList<TeleportStone>(TELEPORTSTONE_STORAGE.values()));
        if (!TSConfig.persit()) {
            success = false;
        }

        TSRConfig.save("teleportstones",  new ArrayList<TeleportStone>(TELEPORTSTONE_REQUEST_STORAGE.values()));
        if (!TSRConfig.persit()) {
            success = false;
        }

        UConfig.save("users", new ArrayList<User>(USER_STORAGE.values()));
        if (!UConfig.persit()) {
            success = false;
        }

        if (success) {
            app.getLogger().info("Persisted data.");
        } else {
            app.getLogger().warning("Failed to persit data!");
        }

        return success;
    }
}
