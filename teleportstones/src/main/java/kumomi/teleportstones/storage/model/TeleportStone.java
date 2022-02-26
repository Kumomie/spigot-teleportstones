package kumomi.teleportstones.storage.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * A pojo for the database with a little extra (nvm, I deleted the little
 * extra).
 */
public class TeleportStone implements ConfigurationSerializable {

    /*
     * The database will only store primitive data types. This will ensure a simple
     * and compatible format.
     * 
     * The information that will be persisted in the database is ID, name, world (as
     * String) and x,y,z of sign.
     * 
     * When the server starts up, it will initalize an array of TeleportStones and
     * performs a validation step. The coordinates of each TeleportStone need to be
     * checked to ensure the existence of the given TeleportStone. If there is not
     * sign present, the TeleportStone will be deleted. If there is a matching sign,
     * the initialization can be completed by initializing the missing values in
     * this object.
     */

    private String bluePrintId;

    // Name of TpS
    private String world;
    private String name;

    // Coordinates of sign
    private SimpleSign sign;

    private UUID builderUuid;
    private String builder;
    private UUID ownerUuid;
    private String owner;

    private Date creationDate;

    // // Coordinates of middle Block
    // private SimpleBlock middleBlock;

    // All blocks of the structure without sign
    private List<SimpleBlock> structure;

    public TeleportStone() {
    }

    public TeleportStone(Map<String, Object> serializedTeleportStone) {

        try {

            this.bluePrintId = (String) serializedTeleportStone.get("bluePrintId");

            this.name = (String) serializedTeleportStone.get("name");
            this.world = (String) serializedTeleportStone.get("world");
            this.sign = (SimpleSign) serializedTeleportStone.get("sign");

            List<?> list = (ArrayList<?>) serializedTeleportStone.get("structure");
            this.structure = new ArrayList<>();
            for (Object object : list) {
                structure.add((SimpleBlock) object);
            }

            this.builder = (String) serializedTeleportStone.get("builder");
            this.owner = (String) serializedTeleportStone.get("owner");

            this.builderUuid = UUID.fromString((String) serializedTeleportStone.get("builderUuid"));
            this.ownerUuid = UUID.fromString((String) serializedTeleportStone.get("ownerUuid"));

            this.creationDate = (Date) serializedTeleportStone.get("creationDate");

        } catch (ClassCastException e) {
            Bukkit.getLogger().warning( //
                    "[TeleportStones] Fields in TeleportStone object have wrong type. Check configuration. " //
                            + e.getMessage());
        } catch (NullPointerException e) {
            Bukkit.getLogger().warning( //
                    "[TeleportStones] Missing fields in TeleportStone object. Check configuration. " //
                            + e.getMessage());
        }

    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("bluePrintId", bluePrintId);
        map.put("name", name);
        map.put("world", world);
        map.put("sign", sign);
        map.put("structure", structure);
        map.put("owner", owner);
        map.put("builder", builder);
        map.put("ownerUuid", ownerUuid.toString());
        map.put("builderUuid", builderUuid.toString());
        map.put("creationDate", creationDate);

        return map;
    }

    public String getBluePrintId() {
        return this.bluePrintId;
    }

    public void setBluePrintId(String bluePrintId) {
        this.bluePrintId = bluePrintId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return this.world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public SimpleSign getSign() {
        return this.sign;
    }

    public void setSign(SimpleSign sign) {
        this.sign = sign;
    }

    public List<SimpleBlock> getStructure() {
        return this.structure;
    }

    public void setStructure(List<SimpleBlock> structure) {
        this.structure = structure;
    }


    public UUID getBuilderUuid() {
        return this.builderUuid;
    }

    public void setBuilderUuid(UUID builderUuid) {
        this.builderUuid = builderUuid;
    }

    public String getBuilder() {
        return this.builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public UUID getOwnerUuid() {
        return this.ownerUuid;
    }

    public void setOwnerUuid(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(this.name);
        builder.append(" [");
        builder.append(this.bluePrintId);
        builder.append("] World:");
        builder.append(this.world);
        builder.append(" Coordinates: ");
        builder.append(this.getSign().getX());
        builder.append(" | ");
        builder.append(this.getSign().getY());
        builder.append(" | ");
        builder.append(this.getSign().getZ());
        
        return builder.toString();
    }

}
