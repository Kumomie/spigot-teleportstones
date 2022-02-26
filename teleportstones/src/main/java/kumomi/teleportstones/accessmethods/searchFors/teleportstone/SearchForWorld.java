package kumomi.teleportstones.accessmethods.searchFors.teleportstone;

import kumomi.teleportstones.storage.model.TeleportStone;

public class SearchForWorld extends SearchFor {

    private String world;

    public SearchForWorld(boolean reverse, String world) {
        super(reverse);
        this.world = world;
    }

    @Override
    protected boolean fit(TeleportStone t) {
        return t.getWorld().equals(world);
    }

}
