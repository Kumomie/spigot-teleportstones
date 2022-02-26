package kumomi.teleportstones.accessmethods;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.searchFors.user.SearchFor;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.model.User;

public class UserSearcher extends AccessMethod {

    private Collection<User> foundUsers;

    public UserSearcher(App app) {
        super(app);
        foundUsers = new HashSet<>();
    }

    public boolean search(List<SearchFor> searchFors) {

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();

        USERS: for (User u : storage.findAll()) {

            for (SearchFor searchFor : searchFors) {
                if (!searchFor.doesMatch(u))
                    continue USERS;
            }

            foundUsers.add(u);
        }

        setStatusMessage("Successfully searched for TeleportStones.");
        return true;
    }

    public Collection<User> getFoundUsers() {
        return foundUsers;
    }

    public List<String> getFoundUsersAsStringList() {

        List<String> results = new LinkedList<>();

        for (User u : foundUsers) {
            results.add(u.getName());
        }

        return results;
    }
}
