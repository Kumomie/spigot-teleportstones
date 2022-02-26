package kumomi.teleportstones.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.model.TeleportStone;

public class TeleportStoneProtector {

    private App app;

    public TeleportStoneProtector(App app) {
        this.app = app;
    }

    public void protect(TeleportStone t) {

        World world = Bukkit.getWorld(t.getWorld());

        t.getStructure().forEach(simpleBlock -> {
            Block block = world.getBlockAt(simpleBlock.getX(), simpleBlock.getY(), simpleBlock.getZ());

            block.setMetadata("TeleportStone", new FixedMetadataValue(app, mapTeleportStone(t)));
        });

        Block sign = world.getBlockAt(t.getSign().getX(), t.getSign().getY(), t.getSign().getZ());
        sign.setMetadata("TeleportStone", new FixedMetadataValue(app, mapTeleportStone(t)));
    }

    public void unprotect(TeleportStone t) {
        World world = Bukkit.getWorld(t.getWorld());

        t.getStructure().forEach(simpleBlock -> {
            Block block = world.getBlockAt(simpleBlock.getX(), simpleBlock.getY(), simpleBlock.getZ());

            block.removeMetadata("TeleportStone", app);
        });

        Block sign = world.getBlockAt(t.getSign().getX(), t.getSign().getY(), t.getSign().getZ());
        sign.removeMetadata("TeleportStone", app);
    }

    private Map<String, Object> mapTeleportStone(TeleportStone teleportStone) {

        Map<String, Object> map = new HashMap<>();

        map.put("name", teleportStone.getName());
        map.put("builder", teleportStone.getBuilder());
        map.put("builderUUID", teleportStone.getBuilderUuid());
        map.put("owner", teleportStone.getOwner());
        map.put("ownerUUID", teleportStone.getOwnerUuid());
        map.put("creationDate", teleportStone.getCreationDate());
        map.put("teleportStone", teleportStone);

        return map;
    }
}
