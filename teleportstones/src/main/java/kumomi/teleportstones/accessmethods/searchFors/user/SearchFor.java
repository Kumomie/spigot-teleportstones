package kumomi.teleportstones.accessmethods.searchFors.user;

import kumomi.teleportstones.storage.model.User;

public abstract class SearchFor {
    
    abstract public boolean doesMatch(User t);
}