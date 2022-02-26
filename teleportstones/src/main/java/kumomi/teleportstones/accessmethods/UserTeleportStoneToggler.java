package kumomi.teleportstones.accessmethods;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.model.SimpleDiscoveredTeleportStone;
import kumomi.teleportstones.storage.model.User;

public class UserTeleportStoneToggler extends AccessMethod {

    public UserTeleportStoneToggler(App app) {
        super(app);
    }

    public boolean toggleHome(User user, String teleportStoneName) {

        SimpleDiscoveredTeleportStone found = null;

        // TeleportStone is in favorite list
        found = user.getFavorits().get(teleportStoneName);
        if (found != null) {

            if (user.getHome().isPresent()) {
                user.add(user.getHome().get());
            }

            user.getFavorits().remove(found.getName());
            user.setHome(found);

            setStatusMessage(found.getName() + " is now your home and no longer a favorite.");
            return true;
        }

        // TeleportStone is in normal discovered TeleportStone list
        found = user.getDiscoveredTeleportStones().get(teleportStoneName);
        if (found != null) {

            if (user.getHome().isPresent()) {
                user.add(user.getHome().get());
            }
            user.getDiscoveredTeleportStones().remove(found.getName());
            user.setHome(found);

            setStatusMessage(found.getName() + " is now your home.");
            return true;
        }

        // TeleportStone is home
        if (user.getHome().isPresent() && user.getHome().get().getName().equals(teleportStoneName)) {
            found = user.getHome().get();
            user.setHome(null);
            user.add(found);

            setStatusMessage(found.getName() + " is no longer your home.");
            return true;
        }

        // Not found
        setStatusMessage("You haven't discovered such a TeleportStone.");
        return false;
    }

    public boolean toggleFavorite(User user, String teleportStoneName) {
        SimpleDiscoveredTeleportStone found = null;

        // TeleportStone is already in favorite list
        found = user.getFavorits().get(teleportStoneName);
        if (found != null) {
            user.getFavorits().remove(found.getName());
            user.add(found);

            setStatusMessage(found.getName() + " is no longer a favorite.");
            return true;
        }

        // TeleportStone is in normal discovered TeleportStone list
        found = user.getDiscoveredTeleportStones().get(teleportStoneName);
        if (found != null) {
            user.getDiscoveredTeleportStones().remove(found.getName());
            user.getFavorits().put(found.getName(), found);

            setStatusMessage(found.getName() + " is now a favorite.");
            return true;
        }

        // TeleportStone is home
        if (user.getHome().isPresent() && user.getHome().get().getName().equals(teleportStoneName)) {
            found = user.getHome().get();
            user.setHome(null);
            user.getFavorits().put(found.getName(), found);

            setStatusMessage(found.getName() + " is now a favorite and no longer your home.");
            return true;
        }

        // Not found
        setStatusMessage("You haven't discovered such a TeleportStone.");
        return false;
    }

}
