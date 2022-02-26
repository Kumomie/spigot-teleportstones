package kumomi.teleportstones.storage.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public class User implements ConfigurationSerializable {

    private UUID uuid;
    private String name;

    private Map<String, SimpleDiscoveredTeleportStone> discoveredTeleportStones;
    private Map<String, SimpleDiscoveredTeleportStone> favorits;
    private SimpleDiscoveredTeleportStone home;

    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        this.discoveredTeleportStones = new HashMap<>();
        this.favorits = new HashMap<>();
        this.home = null;
    }

    public User(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.discoveredTeleportStones = new HashMap<>();
        this.favorits = new HashMap<>();
        this.home = null;
    }

    public User(OfflinePlayer player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.discoveredTeleportStones = new HashMap<>();
        this.favorits = new HashMap<>();
        this.home = null;
    }

    public User(Map<String, Object> serializedUser) {

        this.uuid = UUID.fromString((String) serializedUser.get("uuid"));
        this.name = (String) serializedUser.get("name");

        this.discoveredTeleportStones = new HashMap<>();

        List<?> discoveredTeleportStones = (List<?>) serializedUser.get("discoveredTeleportStones");
        if (discoveredTeleportStones == null) {

            this.discoveredTeleportStones = null;

        } else {

            for (Object object : discoveredTeleportStones) {
                if (object instanceof SimpleDiscoveredTeleportStone) {

                    SimpleDiscoveredTeleportStone s = (SimpleDiscoveredTeleportStone) object;
                    this.discoveredTeleportStones.put(s.getName(), s);

                } else {
                    Bukkit.getLogger().warning(
                            "Couldn't cast object to SimpleDiscoveredTeleportStone. Probably faulty configuration in users "
                                    + name + " discovered TeleportStones.");
                }
            }
        }

        this.favorits = new HashMap<>();

        List<?> favorits = (List<?>) serializedUser.get("favorits");
        if (favorits == null) {

            this.favorits = null;

        } else {

            for (Object object : favorits) {
                if (object instanceof SimpleDiscoveredTeleportStone) {
                    SimpleDiscoveredTeleportStone s = (SimpleDiscoveredTeleportStone) object;
                    this.favorits.put(s.getName(), s);
                } else {
                    Bukkit.getLogger().warning(
                            "Couldn't cast object to SimpleDiscoveredTeleportStone. Probably faulty configuration in users "
                                    + name + " favorits.");
                }
            }
        }

        if (serializedUser.get("home") instanceof SimpleDiscoveredTeleportStone) {
            this.home = (SimpleDiscoveredTeleportStone) serializedUser.get("home");

        } else if (serializedUser.get("home") instanceof String) {
            this.home = null;

            if (!((String) serializedUser.get("home")).equals("!$!NOT SET!$!")) {
                Bukkit.getLogger().warning(
                        "Couldn't cast object to SimpleDiscoveredTeleportStone. Probably faulty configuration in users "
                                + name + " home.");
            }
        } else {
            this.home = null;
            Bukkit.getLogger().warning(
                    "Couldn't cast object to SimpleDiscoveredTeleportStone. Probably faulty configuration in users "
                            + name + " home.");
        }

    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("uuid", uuid.toString());
        map.put("discoveredTeleportStones",
                new ArrayList<SimpleDiscoveredTeleportStone>(discoveredTeleportStones.values()));
        map.put("favorits", new ArrayList<SimpleDiscoveredTeleportStone>(favorits.values()));

        if (this.home == null) {
            map.put("home", "!$!NOT SET!$!");
        } else {
            map.put("home", this.home);
        }

        return map;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, SimpleDiscoveredTeleportStone> getDiscoveredTeleportStones() {
        return this.discoveredTeleportStones;
    }

    public void setDiscoveredTeleportStones(Map<String, SimpleDiscoveredTeleportStone> discoveredTeleportStones) {
        this.discoveredTeleportStones = discoveredTeleportStones;
    }

    public Map<String, SimpleDiscoveredTeleportStone> getFavorits() {
        return this.favorits;
    }

    public void setFavorits(Map<String, SimpleDiscoveredTeleportStone> favorits) {
        this.favorits = favorits;
    }

    public Optional<SimpleDiscoveredTeleportStone> getHome() {
        return Optional.ofNullable(this.home);
    }

    public void setHome(SimpleDiscoveredTeleportStone home) {
        this.home = home;
    }

    public List<SimpleDiscoveredTeleportStone> getAllDiscoveredTeleportStones() {
        List<SimpleDiscoveredTeleportStone> allDiscoveredTeleportStones = new ArrayList<>();

        allDiscoveredTeleportStones.addAll(this.discoveredTeleportStones.values());
        allDiscoveredTeleportStones.addAll(this.favorits.values());

        if (this.home != null) {
            allDiscoveredTeleportStones.add(this.home);
        }

        return allDiscoveredTeleportStones;
    }

    public boolean hasDiscovered(TeleportStone teleportStone) {

        if (this.discoveredTeleportStones.containsKey(teleportStone.getName()))
            if (this.discoveredTeleportStones.get(teleportStone.getName()).equals(teleportStone)) {
                return true;
            }

        if (this.favorits.containsKey(teleportStone.getName()))
            if (this.favorits.get(teleportStone.getName()).equals(teleportStone)) {
                return true;
            }

        if (this.home != null && this.home.equals(teleportStone)) {
            return true;
        }

        return false;
    }

    public boolean hasDiscovered(String teleportStoneName) {

        if (this.discoveredTeleportStones.containsKey(teleportStoneName))
            return true;

        if (this.favorits.containsKey(teleportStoneName))
            return true;

        if (this.home != null && this.home.getName().equals(teleportStoneName))
            return true;

        return false;
    }

    public boolean hasDiscovered(SimpleDiscoveredTeleportStone teleportStone) {

        if (this.discoveredTeleportStones.containsKey(teleportStone.getName()))
            if (this.discoveredTeleportStones.get(teleportStone.getName()).equals(teleportStone)) {
                return true;
            }

        if (this.favorits.containsKey(teleportStone.getName()))
            if (this.favorits.get(teleportStone.getName()).equals(teleportStone)) {
                return true;
            }

        if (this.home != null && this.home.equals(teleportStone)) {
            return true;
        }

        return false;
    }

    public boolean undiscover(TeleportStone teleportStone) {

        SimpleDiscoveredTeleportStone s = new SimpleDiscoveredTeleportStone(teleportStone);

        if (!this.discoveredTeleportStones.remove(s.getName(), s)) {
            if (!this.favorits.remove(s.getName(), s)) {
                if (this.home != null && this.home.equals(s)) {
                    this.home = null;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean undiscover(SimpleDiscoveredTeleportStone s) {

        if (!this.discoveredTeleportStones.remove(s.getName(), s)) {
            if (!this.favorits.remove(s.getName(), s)) {
                if (this.home != null && this.home.equals(s)) {
                    this.home = null;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean undiscover(String teleportStoneName) {

        if (this.discoveredTeleportStones.remove(teleportStoneName) == null) {
            if (this.favorits.remove(teleportStoneName) == null) {
                if (this.home != null && this.home.getName().equals(teleportStoneName)) {
                    this.home = null;
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 
     * 
     * @param teleportStone
     * @return
     */
    public boolean add(TeleportStone teleportStone) {

        if (hasDiscovered(teleportStone)) {
            return false;
        }

        this.discoveredTeleportStones.put(teleportStone.getName(), new SimpleDiscoveredTeleportStone(teleportStone));
        return true;
    }

    /**
     * 
     * 
     * @param teleportStone
     * @return
     */
    public boolean add(SimpleDiscoveredTeleportStone teleportStone) {

        if (hasDiscovered(teleportStone)) {
            return false;
        }

        this.discoveredTeleportStones.put(teleportStone.getName(), teleportStone);
        return true;
    }

}
