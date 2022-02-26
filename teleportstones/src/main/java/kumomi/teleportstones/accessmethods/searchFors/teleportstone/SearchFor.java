package kumomi.teleportstones.accessmethods.searchFors.teleportstone;

import kumomi.teleportstones.storage.model.TeleportStone;

public abstract class SearchFor {

    private boolean reverse;


    public SearchFor(boolean reverse) {
        this.reverse = reverse;
    }

    abstract protected boolean fit(TeleportStone t);

    public boolean doesMatch(TeleportStone t){
        return fit(t) ^ reverse;
    };
}
