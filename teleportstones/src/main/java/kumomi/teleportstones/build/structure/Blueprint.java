package kumomi.teleportstones.build.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Blueprint implements ConfigurationSerializable {

    private String id;
    private String scope;
    private List<OffsetBlock> offsetBlocks;
    private List<Material> signMaterials;
    private Boolean isWorldBound;
    private Float range;

    public Blueprint(Map<String, Object> serializedBlueprint) {
        this.id = (String) serializedBlueprint.get("id");
        this.scope = (String) serializedBlueprint.get("scope");
        this.isWorldBound = (Boolean) serializedBlueprint.get("isWorldBound");

        this.offsetBlocks = new ArrayList<>();
        List<?> offsetBlocks = (List<?>) serializedBlueprint.get("offsetBlocks");

        for (Object object : offsetBlocks) {
            if (object instanceof OffsetBlock) {
                this.offsetBlocks.add((OffsetBlock) object);
            } else {
                Bukkit.getLogger().warning( //
                        "[TeleportStone]" //
                                + " Couldn't read OffsetBlock from blueprint.");
            }
        }

        this.signMaterials = new ArrayList<>();
        List<?> signMaterials = (List<?>) serializedBlueprint.get("signMaterials");

        for (Object object : signMaterials) {
            if (object instanceof String) {

                Material material = Material.valueOf((String) object);

                if (material == null) {
                    Bukkit.getLogger().warning( //
                            "[TeleportStone]" //
                                    + " Unknown material in signMaterials in blueprint." //
                    );
                } else {
                    this.signMaterials.add(material);
                }
            } else {
                Bukkit.getLogger().warning( //
                        "[TeleportStone]" //
                                + " Couldn't read SignMaterial from blueprint.");
            }
        }

        Object rangeObject = serializedBlueprint.get("range");
        if(rangeObject == null){
            this.range = null;
        } else {
            this.range = Float.parseFloat(String.valueOf(rangeObject));
        }
         
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<OffsetBlock> getOffsetBlocks() {
        return this.offsetBlocks;
    }

    public void setOffsetBlocks(List<OffsetBlock> offsetBlocks) {
        this.offsetBlocks = offsetBlocks;
    }

    public List<Material> getSignMaterials() {
        return this.signMaterials;
    }

    public void setSignMaterials(List<Material> signMaterials) {
        this.signMaterials = signMaterials;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean isWorldBound() {
        return this.isWorldBound;
    }

    public void setWorldBound(Boolean worldBound) {
        this.isWorldBound = worldBound;
    }

    public Float getRange() {
        return this.range;
    }

    public void setRange(Float range) {
        this.range = range;
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();

        map.put("id", id);
        map.put("scope", scope);
        map.put("offsetBlocks", offsetBlocks);
        map.put("signMaterials", signMaterials);
        map.put("isWorldBound", isWorldBound);
        map.put("range", range);

        return map;
    }

}
