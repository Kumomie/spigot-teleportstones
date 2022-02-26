package kumomi.teleportstones.accessmethods;

import java.util.Optional;

import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.DistanceCalculator;

public class TeleportStoneDistanceUtil extends AccessMethod {

    public TeleportStoneDistanceUtil(App app) {
        super(app);
    }

    private double distance;

    public Optional<TeleportStone> searchClosestTeleportStone(String world, double x, double y, double z) {

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();

        if (storage.findAll().isEmpty()) {
            return Optional.empty();
        }

        TeleportStone nextNear = null;
        double nextNearDistanceNotSquared = -1;

        for (TeleportStone teleportStone : storage.findAll()) {

            if (!teleportStone.getWorld().equals(world))
                continue;

            int tx = teleportStone.getSign().getX();
            int ty = teleportStone.getSign().getY();
            int tz = teleportStone.getSign().getZ();

            DistanceCalculator calculator = new DistanceCalculator(x, y, z, tx, ty, tz);

            if (nextNear == null) {

                nextNear = teleportStone;
                nextNearDistanceNotSquared = calculator.getDistNotSquare();
                continue;
            }

            if (calculator.getDistNotSquare() < nextNearDistanceNotSquared) {

                nextNear = teleportStone;
                nextNearDistanceNotSquared = calculator.getDistNotSquare();
            }
        }

        this.distance = Math.sqrt(nextNearDistanceNotSquared);
        return Optional.ofNullable(nextNear);
    }

    public double getDistance() {
        return distance;
    }
}
