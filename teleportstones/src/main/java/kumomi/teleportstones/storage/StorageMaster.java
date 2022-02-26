package kumomi.teleportstones.storage;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import kumomi.teleportstones.App;
import kumomi.teleportstones.event.DataAccessEvent;
import kumomi.teleportstones.storage.blueprint.BlueprintManager;
import kumomi.teleportstones.storage.filestorage.FileStorageManager;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.storage.scope.ScopeManager;
import kumomi.teleportstones.util.TeleportStoneProtector;

public class StorageMaster implements Listener {

    private App app;
    private StorageManager storageManager;
    private ScopeManager scopeManager;
    private BlueprintManager bluePrintManager;
    private BukkitRunnable autosafe;

    public StorageMaster(App app) {
        this.app = app;
    }

    public boolean init() {

        this.scopeManager = new ScopeManager(app, app.getConfiguration());

        if (!this.scopeManager.init()) {
            return false;
        }

        this.bluePrintManager = new BlueprintManager(app, app.getConfiguration());

        if (!this.bluePrintManager.init()) {
            return false;
        }

        this.storageManager = new FileStorageManager(app);

        if (!storageManager.init()) {
            return false;
        }

        protectTeleportStones();

        StorageInstance<String, TeleportStone> crudTeleportStone = StorageFactory.getCrudTeleportStoneStorage();
        app.getLogger().info("Loaded " + crudTeleportStone.findAll().size() + " TeleportStone(s).");

        app.getLogger().info("Loaded " + StorageFactory.getCrudTeleportStoneRequestStorage().findAll().size() + " TeleportStone request(s).");

        StorageInstance<UUID, User> crudUser = StorageFactory.getCrudUserStorage();
        app.getLogger().info("Loaded " + crudUser.findAll().size() + " User(s).");

        enableAutoSafe();

        return true;
    }

    public boolean shutdown() {
        return storageManager.shutdown();
    }

    private void protectTeleportStones() {

        StorageInstance<String, TeleportStone> crud = StorageFactory.getCrudTeleportStoneStorage();

        TeleportStoneProtector protector = new TeleportStoneProtector(this.app);

        crud.findAll().forEach(protector::protect);
    }

    private void enableAutoSafe() {

        if (app.getConfiguration().getYmlConfiguration().getDouble("autoSafeIntervall") != -1) {

            double period = app.getConfiguration().getYmlConfiguration().getDouble("autoSafeIntervall");

            if (period < 0.3) {
                app.getLogger().warning("AutoSafePeriod less than 0.3 and will not be enabled for your own safety.");
            } else {

                // minutes
                period = period * 60;

                // seconds
                period = period * 60;

                // ticks
                period = period * 20;

                this.autosafe = new BukkitRunnable() {
                    @Override
                    public void run() {
                        getStorageManager().persistData();
                    }
                };

                this.autosafe.runTaskTimer(app, (int) period, (int) period);

                app.getLogger()
                        .info("Periodic auto safing enabled. Data will be safed every " + (int) period + " ticks / "
                                + app.getConfiguration().getYmlConfiguration().getDouble("autoSafeIntervall")
                                + " hours.");
            }

        }
    }

    // TODO add dataaccessevent or better something else
    @EventHandler
    public void listenForDataAccessEvents(DataAccessEvent dataAccessEvent) {

        switch (dataAccessEvent.getEventTyp()) {

            case ADD_TELEPORTSTONE:
            case DELETE_TELEPORTSTONE:

                storageManager.persistData();
                break;

            default:
                break;
        }
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

}
