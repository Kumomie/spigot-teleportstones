package kumomi.teleportstones.accessmethods.searchFors.user;

import kumomi.teleportstones.storage.model.User;

public class SearchForName extends SearchFor{

    private String name;

    public SearchForName(String name) {
        this.name = name;
    }

    @Override
    public boolean doesMatch(User u) {
        return u.getName().contains(this.name);
    }
}
