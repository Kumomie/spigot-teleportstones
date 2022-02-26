package kumomi.teleportstones.accessmethods;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;
import kumomi.teleportstones.util.TeleportStoneProtector;

/**
 * This class deletes TeleportStones from storage (not request storage).
 */
public class TeleportStoneDeleter extends AccessMethod {

    public TeleportStoneDeleter(App app) {
        super(app);
    }

    /**
     * <p> DONT CALL THIS FROM SYNC. IT WILL BLOCK MAIN THREAD OTHERWISE. </p>s
     * @param teleportStoneToDelete
     * @return
     */
    public boolean delete(TeleportStone teleportStoneToDelete) {

        if (!deleteFromDB(teleportStoneToDelete)) {
            return false;
        }

        // TODO fallback on failute
        if (!changeSign(teleportStoneToDelete)) {
            return false;
        }

        // TODO fallback on failute
        if (!unprotect(teleportStoneToDelete)) {
            return false;
        }

        EventHandler.add(new Event(EventTyp.DELETE, //
                "TeleportStone " //
                        + teleportStoneToDelete.getName() //
                        + " destroyed.") //
        );

        setStatusMessage("Successfully deleted TeleportStone.");
        return true;
    }

    private boolean deleteFromDB(TeleportStone teleportStoneToDelete) {

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();

        boolean success = storage.remove(teleportStoneToDelete.getName()).isPresent();
        setStatusMessage(storage.getStatusMessage());

        StorageFactory.getCrudUserStorage().findAll().forEach(u -> u.undiscover(teleportStoneToDelete));

        return success;
    }

    /**
     * <p> DONT CALL THIS METHOD FROM SYNC. OR IT WILL BLOCK THE SERVER. </p>
     * @param teleportStoneToDelete
     * @return
     */
    private boolean changeSign(TeleportStone teleportStoneToDelete) {
        Future<Boolean> success = Bukkit.getScheduler().callSyncMethod(getApp(), new Callable<Boolean>(){
            @Override
            public Boolean call() throws Exception {
                World world = Bukkit.getWorld(teleportStoneToDelete.getWorld());
                Block signBlock = world.getBlockAt( //
                        teleportStoneToDelete.getSign().getX(), //
                        teleportStoneToDelete.getSign().getY(), //
                        teleportStoneToDelete.getSign().getZ() //
                );

                if (!(signBlock.getState() instanceof Sign) && !signBlock.getType().isAir()) {
                    setStatusMessage("SimpleSign from TeleportStone is not a instance of Sign. Pls report this.");
                    return false;
                }

                Sign sign = (Sign) signBlock.getState();

                sign.setLine(0, "");
                sign.setLine(1, "");
                sign.setLine(2, "");
                sign.setLine(3, "");

                if (sign.update()) {
                    setStatusMessage("Successfully removed text on sign.");
                    return true;
                } else {
                    setStatusMessage("Couldn't remove text on sign.");
                    return false;
                }
            }
        });

        try {
            return success.get();
        } catch (InterruptedException e) {
            setStatusMessage("Error: TeleportStone deleter was interupted, while changing sign.");
            getApp().getLogger().warning("TeleportStone deleter was interrupted, while changing sign. " + e.getMessage());
            return false;
        } catch (ExecutionException e) {
            setStatusMessage("Error: TeleportStone deleter failed to execute, while changing sign.");
            getApp().getLogger().warning("TeleportStone deleter failed to execute, while changing sign. " + e.getMessage());
            return false;
        }

    }

    /**
     * <p> DONT CALL THIS METHOD FROM SYNC. OR IT WILL BLOCK THE SERVER. </p>
     * @param teleportStoneToDelete
     * @return
     */
    private boolean unprotect(TeleportStone teleportStoneToDelete) {

        Future<Boolean> success = Bukkit.getScheduler().callSyncMethod(getApp(), new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                TeleportStoneProtector protector = new TeleportStoneProtector(getApp());
                protector.unprotect(teleportStoneToDelete);
                return true;
            }
        });

        try {
            return success.get();
        } catch (InterruptedException e) {
            setStatusMessage("Error: TeleportStone deleter was interupted, while unprotecting TeleportStone.");
            getApp().getLogger().warning("TeleportStone deleter was interrupted, while unportecting TeleportStone. " + e.getMessage());
            return false;
        } catch (ExecutionException e) {
            setStatusMessage("Error: TeleportStone deleter failed to execute, while unprotecting TeleportStone.");
            getApp().getLogger().warning("TeleportStone deleter failed to execute, while unportecting TeleportStone. " + e.getMessage());
            return false;
        }
    }
}
