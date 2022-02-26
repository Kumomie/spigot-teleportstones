package kumomi.teleportstones.mechanics.teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.blueprint.BlueprintManager;
import kumomi.teleportstones.storage.model.SimpleDiscoveredTeleportStone;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.storage.scope.ScopeManager;
import kumomi.teleportstones.util.DistanceCalculator;

public class TeleportReachableChecker {

    public static enum ReachableStatus {
        NOT_RUN, //
        ERROR, //
        INVALID_ARGUMENT, //
        OUT_OF_SCOPE, //
        WORLD_BOUND, //
        OUT_OF_REACH, //
        REACHABLE //
    }

    private ReachableStatus status;
    private List<String> message;

    public TeleportReachableChecker() {
        this.status = ReachableStatus.NOT_RUN;
        this.message = new ArrayList<>();
    }

    public boolean checkIfReachableSync(TeleportStone departure, SimpleDiscoveredTeleportStone destination) {

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();
        Optional<TeleportStone> oTeleportStone = storage.find(destination.getName());

        if (oTeleportStone.isPresent()) {
            return checkIfReachableSync(departure, oTeleportStone.get());
        }

        this.message.add("Couldn't find TeleportStone.");
        this.status = ReachableStatus.INVALID_ARGUMENT;
        return false;
    }

    public boolean checkIfReachableSync(TeleportStone departure, TeleportStone destination) {

        this.status = ReachableStatus.NOT_RUN;
        this.message = new ArrayList<>();

        Blueprint destinationBP = BlueprintManager.blueprints.get(destination.getBluePrintId());
        Blueprint departureBP = BlueprintManager.blueprints.get(departure.getBluePrintId());

        // Scope Check
        if (!departureBP.getScope().equals("All") && !destinationBP.getScope().equals("Reachable")
                && !departureBP.getScope().equals("All")) {

            if (!ScopeManager.scopes.get(departureBP.getScope()).getReachableScopes()
                    .contains(destinationBP.getScope())) {
                this.message.add("Not in a reachable scope.");
                this.message
                        .add("(" + destinationBP.getScope() + " not reachable from " + departureBP.getScope() + ")");

                this.status = ReachableStatus.OUT_OF_SCOPE;
                return false;
            }
        }

        // World Bound Check
        if (departureBP.isWorldBound())
            if (!departure.getWorld().equals(destination.getWorld())) {
                this.message.add("Departure TeleportStone is world bound");
                this.message.add(" and destination is in another world.");
                this.status = ReachableStatus.WORLD_BOUND;
                return false;
            }

        // Range Check
        if (departureBP.getRange() != null) {
            double distance = DistanceCalculator.calcDistance( //
                    departure.getSign().getX() //
                    , departure.getSign().getY() //
                    , departure.getSign().getZ() //
                    , destination.getSign().getX() //
                    , destination.getSign().getY() //
                    , destination.getSign().getZ() //
            );

            if (distance > departureBP.getRange()) {
                this.message.add("Destination is out of range.");
                this.message
                        .add("(" + String.format("%,.2f", (distance - departureBP.getRange())) + " blocks too far)");

                this.status = ReachableStatus.OUT_OF_REACH;
                return false;
            }
        }

        // TODO Group Check
        // TODO specific world check
        // TODO specific TeleportStone check
        this.message.add("Reachable.");
        this.status = ReachableStatus.REACHABLE;
        return true;
    }

    public Optional<String> collectHomeReachableTeleportStone(User user, TeleportStone teleportStone) {

        if (!user.getHome().isPresent()) {
            return Optional.empty();
        }

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();
        Optional<TeleportStone> oTeleportStone = storage.find(user.getHome().get().getName());

        if (oTeleportStone.isPresent()) {
            if (checkIfReachableSync(teleportStone, oTeleportStone.get())) {
                return Optional.of(user.getHome().get().getName());
            }
        }

        return Optional.empty();
    }

    public List<String> collectFavoriteReachableTeleportStones(User user, TeleportStone teleportStone) {

        List<String> reachableTeleportStone = new ArrayList<>();
        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();

        user.getFavorits().values().forEach(dt -> {

            Optional<TeleportStone> oTeleportStone = storage.find(dt.getName());
            if (oTeleportStone.isPresent()) {
                if (checkIfReachableSync(teleportStone, oTeleportStone.get())) {
                    reachableTeleportStone.add(dt.getName());
                }
            }
        });

        return reachableTeleportStone;
    }

    public List<String> collectReachableTeleportStones(User user, TeleportStone teleportStone) {

        List<String> reachableTeleportStone = new ArrayList<>();
        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();
        
        user.getDiscoveredTeleportStones().values().forEach(dt -> {
            Optional<TeleportStone> oTeleportStone = storage.find(dt.getName());
            if (oTeleportStone.isPresent()) {
                if (checkIfReachableSync(teleportStone, oTeleportStone.get())) {
                    reachableTeleportStone.add(dt.getName());
                }
            }
        });

        return reachableTeleportStone;
    }

    public List<String> collectAllReachableTeleportStones(User user, TeleportStone teleportStone) {

        List<String> reachableTeleportStone = new ArrayList<>();
        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();

        user.getAllDiscoveredTeleportStones().forEach(dt -> {
            Optional<TeleportStone> oTeleportStone = storage.find(dt.getName());
            if (oTeleportStone.isPresent()) {
                if (checkIfReachableSync(teleportStone, oTeleportStone.get())) {
                    reachableTeleportStone.add(dt.getName());
                }
            }
        });

        return reachableTeleportStone;
    }

    public List<String> getMessage() {
        return message;
    }

    public ReachableStatus getStatus() {
        return status;
    }
}
