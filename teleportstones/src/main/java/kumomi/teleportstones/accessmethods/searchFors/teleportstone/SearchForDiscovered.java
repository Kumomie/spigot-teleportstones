package kumomi.teleportstones.accessmethods.searchFors.teleportstone;

import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;

public class SearchForDiscovered extends SearchFor {

    private User user;

    public SearchForDiscovered(boolean reverse, User user) {
        super(reverse);
        this.user = user;
    }

    @Override
    protected boolean fit(TeleportStone t) {
        return user.hasDiscovered(t);
    } 
}
