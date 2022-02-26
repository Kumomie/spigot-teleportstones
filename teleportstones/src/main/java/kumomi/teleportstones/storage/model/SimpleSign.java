package kumomi.teleportstones.storage.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.bukkit.Material;

public class SimpleSign extends SimpleBlock {

    private String[] lines;

    public SimpleSign() {
        super();
    }

    public SimpleSign(Map<String, Object> serializedTeleportStone) {
        super(serializedTeleportStone);

        List<?> lineList = (ArrayList<?>) serializedTeleportStone.get("lines");

        if (lineList.size() > 4) {
            throw new IllegalArgumentException("Too many lines in sign entry in a TeleportStone.");
        }

        this.lines = new String[4];

        for (int i = 0; i < lines.length; i++) {

            if (lineList.get(i) instanceof String) {
                lines[i] = (String) lineList.get(i);
            } else {
                throw new IllegalArgumentException("Line in sign entry in a TeleportStone is not of type String.");
            }

        }

    }

    public SimpleSign(int x, int y, int z, String world, Material material, String[] lines) {
        super(x, y, z, world, material);
        this.lines = lines;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("x", getX());
        map.put("y", getY());
        map.put("z", getZ());

        map.put("world", getWorld());
        map.put("material", getMaterial().name());
        map.put("lines", lines);

        return map;
    }

    public void setLines(String[] lines) {
        this.lines = lines;
    }

    public String[] getLines() {
        return lines;
    }

    public String getLine(int i) {
        if (lines == null) {
            return "";
        }

        if (i < 0 || i >= lines.length)
            return "";

        if (lines[i] == null)
            return "";

        return lines[i];
    }

    public boolean equals(SimpleSign simpleSign){

        if(!equals(simpleSign)){
            return false;
        }

        for (int i = 0; i < 4; i++) {
            if(!this.lines[i].equals(simpleSign.getLine(i))){
                return false;
            }
        }

        return true;
    }

}
