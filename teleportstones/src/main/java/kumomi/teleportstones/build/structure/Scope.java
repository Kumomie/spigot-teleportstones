package kumomi.teleportstones.build.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Scope implements ConfigurationSerializable {

    private String name;
    private List<String> reachableScopes;

    public Scope(Map<String, Object> serializedTeleportStone) {

        this.name = (String) serializedTeleportStone.get("scopeName");

        this.reachableScopes = new ArrayList<>();

        List<?> tempReachableScopes = (List<?>) serializedTeleportStone.get("reachableScopes");

        tempReachableScopes.forEach((e) -> {
            if (e instanceof String) {
                this.reachableScopes.add((String) e);
            }
        });
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("scopeName", name);
        map.put("reachableScopes", reachableScopes);

        return map;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getReachableScopes() {
        return this.reachableScopes;
    }

    public void setReachableScopes(List<String> reachableScopes) {
        this.reachableScopes = reachableScopes;
    }

    
}
