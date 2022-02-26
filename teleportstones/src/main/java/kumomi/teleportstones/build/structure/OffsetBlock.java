package kumomi.teleportstones.build.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class OffsetBlock implements ConfigurationSerializable {

    private Integer x;
    private Integer y;
    private Integer z;
    private List<Material> materials;

    public OffsetBlock(Integer x, Integer y, Integer z, List<Material> materials) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.materials = materials;
    }

    public OffsetBlock(Map<String, Object> serializedOffsetBlock) {

        this.x = (Integer) serializedOffsetBlock.get("x");
        this.y = (Integer) serializedOffsetBlock.get("y");
        this.z = (Integer) serializedOffsetBlock.get("z");

        this.materials = new ArrayList<>();

        List<?> tempMaterials = (List<?>) serializedOffsetBlock.get("materials");

        tempMaterials.forEach((e) -> {
            if (e instanceof String) {
                try {
                    this.materials.add(Material.getMaterial((String) e));
                } catch (NullPointerException | ClassCastException exc) {
                    Bukkit.getLogger().warning( //
                            "[TeleportStones] " //
                                    + "Unknown material " //
                                    + (String) e //
                                    + " specified in blueprint. " //
                                    + exc.getMessage() //
                    );
                }
            }
        });
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("x", x);
        map.put("y", y);
        map.put("z", z);

        List<String> materialNames = new ArrayList<>();

        materials.forEach((m) -> {
            materialNames.add(m.name());
        });

        map.put("materials", materialNames);

        return map;
    }

    public Integer getX() {
        return this.x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return this.y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return this.z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("X:");
        builder.append(x);
        builder.append(" Y:");
        builder.append(y);
        builder.append(" Z:");
        builder.append(z);
        builder.append(" Materials: ");
        materials.forEach(builder::append);

        return builder.toString();
    }

}
