package kumomi.teleportstones.storage.model;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class SimpleBlock implements ConfigurationSerializable {
    private int x;
    private int y;
    private int z;

    private String world;

    // This will be persisted in a file as String and needs to be casted to Material
    // while deserializing.
    private Material material;

    public SimpleBlock() {

    }

    public SimpleBlock(int x, int y, int z, String world, Material material) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.material = material;
    }

    public SimpleBlock(Map<String, Object> serializedTeleportStone) {

        this.x = (int) serializedTeleportStone.get("x");
        this.y = (int) serializedTeleportStone.get("y");
        this.z = (int) serializedTeleportStone.get("z");

        this.world = (String) serializedTeleportStone.get("world");
        this.material = Material.getMaterial((String) serializedTeleportStone.get("material"));
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorld() {
        return this.world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("x", x);
        map.put("y", y);
        map.put("z", z);

        map.put("world", world);
        map.put("material", material.name());

        return map;
    }

    public boolean equals(Block block) {
        return //
        this.x == block.getX() //
                && this.y == block.getY() //
                && this.z == block.getZ() //
                && this.world.equals(block.getWorld().getName()) //
                && this.material == block.getType() //
        ;
    }

    public boolean equals(SimpleBlock block) {
        return //
        this.x == block.getX() //
                && this.y == block.getY() //
                && this.z == block.getZ() //
                && this.world.equals(block.getWorld()) //
                && this.material == block.getMaterial() //
        ;
    }

}
