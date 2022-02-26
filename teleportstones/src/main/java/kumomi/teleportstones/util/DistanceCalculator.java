package kumomi.teleportstones.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import kumomi.teleportstones.storage.model.TeleportStone;

public class DistanceCalculator {

    public static double calcDistance(double x1, double y1, double z1, int x2, int y2, int z2) {

        return Math.sqrt( //
                Math.pow(x1 - x2, 2) //
                        + Math.pow(y1 - y2, 2) //
                        + Math.pow(z1 - z2, 2) //
        );
    }

    private double distSquare;
    private double dist;

    private double x1;
    private double y1;
    private double z1;
    private int x2;
    private int y2;
    private int z2;

    public DistanceCalculator(double x1, double y1, double z1, int x2, int y2, int z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;

        this.distSquare = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2);
        this.dist = -1;
    }

    public DistanceCalculator(TeleportStone teleportStone1, TeleportStone teleportStone2) {
        this.x1 = teleportStone1.getSign().getX();
        this.y1 = teleportStone1.getSign().getY();
        this.z1 = teleportStone1.getSign().getZ();
        this.x2 = teleportStone2.getSign().getX();
        this.y2 = teleportStone2.getSign().getY();
        this.z2 = teleportStone2.getSign().getZ();

        this.distSquare = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2);
        this.dist = -1;
    }

    public DistanceCalculator(TeleportStone teleportStone, Player player) {
        this.x1 = teleportStone.getSign().getX();
        this.y1 = teleportStone.getSign().getY();
        this.z1 = teleportStone.getSign().getZ();

        Location loc = player.getLocation();
        this.x2 = (int) loc.getX();
        this.y2 = (int) loc.getY();
        this.z2 = (int) loc.getZ();

        this.distSquare = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2);
        this.dist = -1;
    }

    public boolean isDistanceLessThan(double compare) {
        return distSquare < Math.pow(compare, 2);
    }

    public boolean isDistanceMoreThan(double compare) {
        return distSquare > Math.pow(compare, 2);
    }

    public double calcDistance() {
        if (dist == -1) {
            dist = Math.sqrt(distSquare);
        }

        return dist;
    }

    public double getDistNotSquare() {
        return distSquare;
    }

    

}
