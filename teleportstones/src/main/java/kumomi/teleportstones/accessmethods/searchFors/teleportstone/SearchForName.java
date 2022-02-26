package kumomi.teleportstones.accessmethods.searchFors.teleportstone;

import kumomi.teleportstones.storage.model.TeleportStone;

public class SearchForName extends SearchFor {

    private String name;

    public SearchForName(boolean reverse, String name) {
        super(reverse);
        this.name = name;
    }

    @Override
    protected boolean fit(TeleportStone t) {
        return t.getName().contains(this.name);
    }

}
