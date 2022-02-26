package kumomi.teleportstones.storage.model;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class SimpleDiscoveredTeleportStone implements ConfigurationSerializable {
    
    private String name;

    public SimpleDiscoveredTeleportStone(TeleportStone teleportStone) {
        this.name = teleportStone.getName();
    }

    public SimpleDiscoveredTeleportStone(Map<String, Object> serialized) {
        this.name = (String) serialized.get("name");
    }

    @Override
    public Map<String, Object> serialize() {

        Map<String, Object> map = new HashMap<>();

        map.put("name", name);

        return map;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(TeleportStone teleportStone) {

        if (teleportStone == null)
            return false;

        return (teleportStone.getName().equals(this.name));
    }

    public boolean equals(SimpleDiscoveredTeleportStone simpleDiscoveredTeleportStone) {
        if (simpleDiscoveredTeleportStone == null)
            return false;

        return (simpleDiscoveredTeleportStone.getName().equals(this.name));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleDiscoveredTeleportStone))
            return false;

        return equals((SimpleDiscoveredTeleportStone) o);
    }

}
