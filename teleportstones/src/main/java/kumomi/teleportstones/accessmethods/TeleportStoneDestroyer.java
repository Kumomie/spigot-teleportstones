package kumomi.teleportstones.accessmethods;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.model.SimpleBlock;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;

public class TeleportStoneDestroyer extends AccessMethod {

    public TeleportStoneDestroyer(App app) {
        super(app);
    }

    /**
     * <p> DONT CALL FROM SYNC. WILL BLOCK MAIN THREAD </p>
     * 
     * Destroy TeleportStone
     * @param teleportStoneToDestory
     * @return
     */
    public boolean destroyTeleportStone(TeleportStone teleportStoneToDestory) {
        Future<Boolean> success = Bukkit.getScheduler().callSyncMethod(getApp(), new Callable<Boolean>(){
            @Override
            public Boolean call() throws Exception {
                return destroy(teleportStoneToDestory);
            }
        });

        try {
            return success.get();
        } catch (InterruptedException e) {
            setStatusMessage("ERROR: TeleportStone destroyer was interrupted.");
            getApp().getLogger().warning("TeleportStone destroyer was interrupted. " + e.getMessage());
            return false;
        } catch (ExecutionException e) {
            setStatusMessage("ERROR: Failed to execute TeleportSTone destroyer.");
            getApp().getLogger().warning("eleportStone destroyer had an execution exception. " + e.getMessage());
            return false;
        }
    }

    private boolean destroy(TeleportStone teleportStoneToDestory) {

        World world = Bukkit.getWorld(teleportStoneToDestory.getWorld());

        Block signBlock = world.getBlockAt( //
                teleportStoneToDestory.getSign().getX(), //
                teleportStoneToDestory.getSign().getY(), //
                teleportStoneToDestory.getSign().getZ() //
        );

        if (!signBlock.breakNaturally()) {

            EventHandler.add(new Event(EventTyp.ERROR, //
                    "An Error occured, while destroying TeleportStone " //
                            + teleportStoneToDestory.getName()) //
            );

            setStatusMessage("Failed to destroy TeleportStone sign. ");
            return false;
        }

        for (SimpleBlock structureBlock : teleportStoneToDestory.getStructure()) {

            Block block = world.getBlockAt( //
                    structureBlock.getX(), //
                    structureBlock.getY(), //
                    structureBlock.getZ() //
            );

            if (!block.breakNaturally()) {

                EventHandler.add(new Event(EventTyp.ERROR, //
                        "An Error occured, while destroying TeleportStone " //
                                + teleportStoneToDestory.getName()) //
                );

                setStatusMessage("Failed to destroy TeleportStone structure.");
                return false;
            }
        }

        EventHandler.add(new Event(EventTyp.DESTROY, //
                "TeleportStone " //
                        + teleportStoneToDestory.getName() //
                        + " destroyed.") //
        );

        return true;
    }

}
