package kumomi.teleportstones.accessmethods;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.searchFors.teleportstone.SearchFor;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.model.TeleportStone;

public class TeleportStoneSearcher extends AccessMethod {

    private Collection<TeleportStone> foundTeleportStones;

    public TeleportStoneSearcher(App app) {
        super(app);
        foundTeleportStones = new HashSet<>();

    }

    public boolean search(List<SearchFor> searchFors) {
        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();

        TELEPORTSTONES: for (TeleportStone t : storage.findAll()) {

            for (SearchFor searchFor : searchFors) {
                if (!searchFor.doesMatch(t))
                    continue TELEPORTSTONES;
            }

            this.foundTeleportStones.add(t);
        }

        setStatusMessage("Successfully searched for TeleportStones.");
        return true;
    }

    public Collection<TeleportStone> getFoundTeleportStones() {
        return foundTeleportStones;
    }

    public List<String> getFoundTeleportStonesAsStringList() {

        List<String> results = new LinkedList<>();

        for (TeleportStone teleportStone : foundTeleportStones) {
            results.add(teleportStone.getName());
        }

        return results;
    }

}
