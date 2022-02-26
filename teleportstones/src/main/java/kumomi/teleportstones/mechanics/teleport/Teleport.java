package kumomi.teleportstones.mechanics.teleport;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;

import kumomi.teleportstones.App;
import kumomi.teleportstones.mechanics.Mechanic;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;

public class Teleport extends Mechanic {

    public Teleport(App app) {
        super(app);
    }

    public static enum TeleportStatus {
        NOT_RUN, //
        ERROR, //
        NOT_CLOSE_TO_TELEPORTSTONE, //
        NOT_DISCOVERED, //
        NOT_REACHABLE, //
        NOT_FOUND, //
        SUCCESs_TELEPORT //
    }

    private TeleportStatus status;
    private String message;

    /**
     * This method assumes, that the user/player has discovered the destination.
     * Method checks if there is a close discovered TeleportStone and teleports the
     * player if found. (close = 5 block radius)
     * 
     * @param player      Player to teleport.
     * @param user        = player
     * @param destination Where to port.
     * @return Was teleport successfull?
     */
    public boolean teleportFromNearest(Player player, User user, TeleportStone destination) {

        TeleportUtil teleportUtil = new TeleportUtil();

        Optional<TeleportStone> oTeleportStone = teleportUtil.isTeleportStoneClose(player);

        if (!oTeleportStone.isPresent()) {
            this.status = TeleportStatus.NOT_CLOSE_TO_TELEPORTSTONE;
            this.message = "You are not close enough to a TeleportStone to teleport.";
            return false;
        }

        TeleportStone departure = oTeleportStone.get();

        if (!user.hasDiscovered(departure)) {
            this.status = TeleportStatus.NOT_DISCOVERED;
            this.message = "You need to activate the TeleportStone near you, before you can teleport from it.";
            return false;
        }

        TeleportReachableChecker checker = new TeleportReachableChecker();

        if (!checker.checkIfReachableSync(departure, destination)) {
            this.status = TeleportStatus.NOT_REACHABLE;

            StringBuilder builder = new StringBuilder();
            checker.getMessage().forEach(builder::append);
            this.message = builder.toString();

            return false;
        }

        return teleport(player, departure.getName(), destination);
    }

    /**
     * <p> DO NOT CALL THIS METHOD IN SYNC. IT WILL BLOCK MAINTHREAD OTHERWISE </p>
     * 
     * Use this method to teleport. Method assumes that destination is reachable.
     * There are no checks in this method, so it will always teleport.
     * 
     * @param player        Player to teleport.
     * @param departureName From where to teleport. (Just for logging)
     * @param destination   Where to teleport.
     * @return
     */
    public boolean teleport(Player player, String departureName, TeleportStone destination) {

        if (teleportUnchecked(player, destination)) {
            this.message = "You have been teleported.";
            this.status = TeleportStatus.SUCCESs_TELEPORT;

            EventHandler.add(new Event( //
                    EventTyp.TELEPORT, //
                    player.getName() //
                            + " teleported" //
                            + " from " + departureName //
                            + " to " + destination.getName() //
            ));

        } else {

            EventHandler.add(new Event( //
                    EventTyp.ERROR, //
                    player.getName() //
                            + " couldn't teleport" //
                            + " from " + departureName //
                            + " to " + destination.getName() //
            ));
            return false;
        }

        return true;
    }

    /**
     * Dont use.
     * 
     * @param player
     * @param destination
     * @return
     */
    private boolean teleportUnchecked(Player player, TeleportStone destination) {

        Callable<Boolean> callable = new Callable<Boolean>(){
            @Override
            public Boolean call() throws Exception {
                // DO THE TELEPORT SCOTTY
                World w = Bukkit.getWorld(destination.getWorld());

                Block signBlock = w.getBlockAt( //
                        destination.getSign().getX(), //
                        destination.getSign().getY(), //
                        destination.getSign().getZ() //
                );

                WallSign wallSign = (WallSign) signBlock.getState().getBlockData();

                BlockFace facing = wallSign.getFacing();

                double tx, ty, tz;
                tx = destination.getSign().getX();
                ty = destination.getSign().getY();
                tz = destination.getSign().getZ();

                switch (facing) {
                    case NORTH:
                    case EAST:
                    case SOUTH:
                    case WEST:
                        tx += 0.5;
                        tz += 0.5;
                        break;
                    default:
                        setStatus(TeleportStatus.ERROR);
                        setMessage("Internal Error.");

                        EventHandler.add(new Event( //
                                EventTyp.ERROR, //
                                "Sign facing unimplemented direction: " + facing.name()) //
                        );

                        getApp().getLogger().warning("Sign facing unimplemented direction: " + facing.name());
                        return false;
                }

                Location destinationLocation = new Location( //
                        Bukkit.getWorld(destination.getWorld()), //
                        tx, //
                        ty - 1, //
                        tz //
                );

                return player.teleport(destinationLocation);
            }
        };
        
        Future<Boolean> success = Bukkit.getScheduler().callSyncMethod(getApp(), callable);

        try {
            return success.get();
        } catch (InterruptedException e) {
            setStatus(TeleportStatus.ERROR);
            setMessage("ERROR: Teleporter was interrupted.");
            getApp().getLogger().warning("Teleporter was interrupted. " + e.getMessage());
            return false;
        } catch (ExecutionException e) {
            setStatus(TeleportStatus.ERROR);
            setMessage("ERROR: Failed to execute Teleporter.");
            getApp().getLogger().warning("Teleporter had an execution exception. " + e.getMessage());
            return false;
        }
    }

    public String getMessage() {
        return message;
    }

    public TeleportStatus getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(TeleportStatus status) {
        this.status = status;
    }

}
