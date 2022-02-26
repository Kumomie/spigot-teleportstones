package kumomi.teleportstones.mechanics.teleport;

import java.util.Optional;

import org.bukkit.entity.Player;

import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.DistanceCalculator;

public class TeleportUtil {

    /**
     * Returns closest TeleportStone in radius of 5 blocks from player.
     * @param player
     * @return
     */
    public Optional<TeleportStone> isTeleportStoneClose(Player player) {

        TeleportStone closestTeleportStone = null;

        // TODO improve performance
        // Check if player is near a TeleportStone
        // Look for closest TeleportStone
        double x, y, z;
        x = player.getLocation().getX();
        y = player.getLocation().getY();
        z = player.getLocation().getZ();

        String world;
        world = player.getLocation().getWorld().getName();

        double isNearTeleportStone = -1;
        for (TeleportStone teleportStone : StorageFactory.getCrudTeleportStoneStorage().findAll()) {

            if (!teleportStone.getWorld().equals(world))
                continue;

            // quick check, if teleportStone is near player.
            // This will reduce time per TeleportStone check.
            // Most TeleportStones will not run past first if statement
            if (Math.abs(x - teleportStone.getSign().getX()) > 5) {
                continue;
            }

            if (Math.abs(y - teleportStone.getSign().getY()) > 5) {
                continue;
            }

            if (Math.abs(z - teleportStone.getSign().getZ()) > 5) {
                continue;
            }

            // calculate exact distance
            DistanceCalculator calculator = new DistanceCalculator(//
                    x, //
                    y, //
                    z, //
                    teleportStone.getSign().getX(), //
                    teleportStone.getSign().getY(), //
                    teleportStone.getSign().getZ() //
            );

            if (calculator.isDistanceMoreThan(5))
                continue;

            if (calculator.isDistanceLessThan(isNearTeleportStone) || isNearTeleportStone == -1) {
                isNearTeleportStone = calculator.calcDistance();
                closestTeleportStone = teleportStone;
            }

        }

        return Optional.ofNullable(closestTeleportStone);
    }
}
