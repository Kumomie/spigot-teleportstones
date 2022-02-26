package kumomi.teleportstones.accessmethods;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;

public class TeleportStoneDiscoverer extends AccessMethod {

    private Optional<User> user;
    private Optional<TeleportStone> teleportStone;

    private boolean newUser;

    public TeleportStoneDiscoverer(App app) {
        super(app);
        this.user = Optional.empty();
        this.teleportStone = Optional.empty();

        this.newUser = false;
    }

    public boolean discover(String teleportStoneName, OfflinePlayer player) {

        StorageInstance<UUID, User> uStorage = StorageFactory.getCrudUserStorage();
        User user = null;
        Optional<User> oUser = uStorage.find(player.getUniqueId());
        if (oUser.isPresent()) {
            user = oUser.get();
        } else {

            user = new User(player);
            if (!uStorage.add(user.getUuid(), user)) {
                setStatusMessage(uStorage.getStatusMessage());
                return false;
            }

            this.newUser = true;
        }

        StorageInstance<String, TeleportStone> tStorage = StorageFactory.getCrudTeleportStoneStorage();
        Optional<TeleportStone> oTeleportStone = tStorage.find(teleportStoneName);
        if (!oTeleportStone.isPresent()) {
            setStatusMessage("Couldn't find TeleportStone.");
            return false;
        }

        return discover(user, oTeleportStone.get());
    }

    public boolean discover(User user, TeleportStone teleportStone) {

        if (user.hasDiscovered(teleportStone)) {
            setStatusMessage("This user already discovered this TeleportStone.");
            return false;
        }

        user.add(teleportStone);

        this.user = Optional.of(user);
        this.teleportStone = Optional.of(teleportStone);

        setStatusMessage("Succesfully discovered TeleportStone.");

        EventHandler.add(new Event(EventTyp.DISCOVER, //
                "TeleportStone: " //
                        + teleportStone.getName() //
                        + ", User: " //
                        + user.getName()) //
        );

        return true;
    }

    public boolean undiscover(String teleportStoneName, OfflinePlayer player) {

        StorageInstance<UUID, User> uStorage = StorageFactory.getCrudUserStorage();
        User user = null;
        Optional<User> oUser = uStorage.find(player.getUniqueId());

        if (oUser.isPresent()) {
            user = oUser.get();
        } else {
            setStatusMessage("This player hasn't discovered a TeleportStone yet.");
            return false;
        }

        StorageInstance<String, TeleportStone> tStorage = StorageFactory.getCrudTeleportStoneStorage();
        Optional<TeleportStone> oTeleportStone = tStorage.find(teleportStoneName);
        if (!oTeleportStone.isPresent()) {
            setStatusMessage("Couldn't find TeleportStone.");
            return false;
        }

        return undiscover(user, oTeleportStone.get());
    }

    public boolean undiscover(User user, TeleportStone teleportStone) {

        if (!user.hasDiscovered(teleportStone)) {
            setStatusMessage("User hasn't discovered this TeleportStone.");
            return false;
        }

        user.undiscover(teleportStone);

        this.user = Optional.of(user);
        this.teleportStone = Optional.of(teleportStone);

        setStatusMessage("Succesfully undiscovered TeleportStone.");

        EventHandler.add(new Event(EventTyp.DELETE, //
                "Undiscovered TeleportStone: " //
                        + teleportStone.getName() //
                        + ", User: " //
                        + user.getName()) //
        );

        return true;
    }

    public Optional<User> getUser() {
        return user;
    }

    public Optional<TeleportStone> getTeleportStone() {
        return teleportStone;
    }

    public boolean isNewUser() {
        return newUser;
    }

}
