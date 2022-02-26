package kumomi.teleportstones.accessmethods;

import java.util.Optional;
import java.util.UUID;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;

public class UserDeleter extends AccessMethod {
    private Optional<User> oUser;

    public UserDeleter(App app) {
        super(app);
        this.oUser = Optional.empty();
    }

    public boolean delete(String username) {

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();

        User user = null;
        for (User u : storage.findAll()) {
            if (u.getName().equals(username)) {
                user = u;
                break;
            }
        }

        if (user == null) {
            setStatusMessage("Specified user couldn't be found.");
            return false;
        }

        this.oUser = Optional.of(user);
        return delete(user);
    }

    public boolean delete(UUID uuid) {
        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();

        Optional<User> oUser = storage.find(uuid);

        if (!oUser.isPresent()) {
            setStatusMessage(storage.getStatusMessage());
            return false;
        }

        return delete(oUser.get());
    }

    public boolean delete(User user) {

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();

        StorageInstance<String, TeleportStone> tStorage = StorageFactory.getCrudTeleportStoneStorage();
        for (TeleportStone teleportStone : tStorage.findAll()) {

            if (teleportStone.getOwnerUuid().equals(user.getUuid())) {

                StringBuilder builder = new StringBuilder();
                builder.append("Can't remove users that own TeleportStones. ");
                builder.append("User owns TeleportStone ");
                builder.append(teleportStone.getName());
                builder.append(". Delete this TeleportStone first.");

                setStatusMessage(builder.toString());
                return false;
            }

            if (teleportStone.getBuilderUuid().equals(user.getUuid())) {
                teleportStone.setBuilderUuid(teleportStone.getOwnerUuid());
                teleportStone.setBuilder(teleportStone.getOwner());

                getApp().getLogger().info("Changed builder of TeleportStone " + teleportStone.getName()
                        + " to owner, because old builder user is about to be deleted.");
            }
        }

        Optional<User> oUser = storage.remove(user.getUuid());
        if (!oUser.isPresent()) {
            setStatusMessage(storage.getStatusMessage());
            getApp().getLogger().warning("Couldn't delete user " + user + ". " + storage.getStatusMessage());
            return false;
        }

        EventHandler.add(new Event(EventTyp.DELETE, "Deleted user " + user.getName()));
        setStatusMessage("Deleted user.");
        return true;
    }

    public Optional<User> getUser() {
        return oUser;
    }
}
