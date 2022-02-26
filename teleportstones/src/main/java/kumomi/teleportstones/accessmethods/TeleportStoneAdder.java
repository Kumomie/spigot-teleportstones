package kumomi.teleportstones.accessmethods;

import java.util.Optional;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.StorageInstance.StorageStatus;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;

public class TeleportStoneAdder extends AccessMethod {

    private Optional<TeleportStone> oTeleportStone;

    public TeleportStoneAdder(App app) {
        super(app);
        oTeleportStone = Optional.empty();
    }

    public boolean addTeleportStone(TeleportStone teleportStone) {
        return add(teleportStone);
    }

    public boolean requestTeleportStone(TeleportStone teleportStone) {
        return request(teleportStone);
    }

    private boolean add(TeleportStone teleportStone) {

        StorageInstance<String, TeleportStone> rStorage = StorageFactory.getCrudTeleportStoneRequestStorage();

        if (rStorage.find(teleportStone.getName()).isPresent()) {
            if (rStorage.getStatus() == StorageStatus.MULTIPLE_ENTRIES)
                setStatusMessage("Specified TeleportStone already exists as request.");
            else
                setStatusMessage(rStorage.getStatusMessage());

            return false;
        }

        StorageInstance<String, TeleportStone> tStorage = StorageFactory.getCrudTeleportStoneStorage();

        if (!tStorage.add(teleportStone.getName(), teleportStone)) {
            if (tStorage.getStatus() == StorageStatus.MULTIPLE_ENTRIES)
                setStatusMessage("Specified TeleportStone already exists.");
            else
                setStatusMessage(tStorage.getStatusMessage());

            return false;
        }

        this.oTeleportStone = Optional.of(teleportStone);

        setStatusMessage("Successfully added TeleportStone.");

        StringBuilder builder = new StringBuilder();
        builder.append("TeleportStone ");
        builder.append(teleportStone.getName());
        builder.append(" in world ");
        builder.append(teleportStone.getWorld());
        builder.append(" at Coordinates ");
        builder.append(teleportStone.getSign().getX());
        builder.append(" | ");
        builder.append(teleportStone.getSign().getY());
        builder.append(" | ");
        builder.append(teleportStone.getSign().getZ());

        EventHandler.add(new Event(EventTyp.CREATE, builder.toString()));

        return true;
    }

    private boolean request(TeleportStone teleportStone) {

        StorageInstance<String, TeleportStone> tStorage = StorageFactory.getCrudTeleportStoneStorage();

        if (tStorage.find(teleportStone.getName()).isPresent()) {
            if (tStorage.getStatus() == StorageStatus.MULTIPLE_ENTRIES)
                setStatusMessage("Specified TeleportStone already exists.");
            else
                setStatusMessage(tStorage.getStatusMessage());

            return false;
        }

        StorageInstance<String, TeleportStone> rStorage = StorageFactory.getCrudTeleportStoneRequestStorage();

        if (!rStorage.add(teleportStone.getName(), teleportStone)) {
            if (rStorage.getStatus() == StorageStatus.MULTIPLE_ENTRIES)
                setStatusMessage("Specified TeleportStone already exists as request.");
            else
                setStatusMessage(rStorage.getStatusMessage());

            return false;
        }

        this.oTeleportStone = Optional.of(teleportStone);

        setStatusMessage("Successfully requested TeleportStone.");

        StringBuilder builder = new StringBuilder();
        builder.append("TeleportStone ");
        builder.append(teleportStone.getName());
        builder.append(" in world ");
        builder.append(teleportStone.getWorld());
        builder.append(" at Coordinates ");
        builder.append(teleportStone.getSign().getX());
        builder.append(" | ");
        builder.append(teleportStone.getSign().getY());
        builder.append(" | ");
        builder.append(teleportStone.getSign().getZ());

        EventHandler.add(new Event(EventTyp.REQUEST, builder.toString()));

        return true;
    }

    public Optional<TeleportStone> getTeleportStone() {
        return oTeleportStone;
    }
}
