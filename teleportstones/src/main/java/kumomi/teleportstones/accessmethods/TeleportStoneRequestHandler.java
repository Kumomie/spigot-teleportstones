package kumomi.teleportstones.accessmethods;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scheduler.BukkitRunnable;

import kumomi.teleportstones.App;
import kumomi.teleportstones.build.TeleportStoneValidator;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;
import kumomi.teleportstones.util.TeleportStoneProtector;

public class TeleportStoneRequestHandler extends AccessMethod {

    public TeleportStoneRequestHandler(App app) {
        super(app);
    }

    public static enum RequestType {
        accept, deny, show
    }

    public boolean accept(TeleportStone requestedTeleportStone, User owner) {

        StorageInstance<String, TeleportStone> tStorage = StorageFactory.getCrudTeleportStoneStorage();
        StorageInstance<String, TeleportStone> rtStorage = StorageFactory.getCrudTeleportStoneRequestStorage();

        Optional<TeleportStone> oRemoved = rtStorage.remove(requestedTeleportStone.getName());

        if (!oRemoved.isPresent()) {
            setStatusMessage(rtStorage.getStatusMessage());
            return false;
        }

        requestedTeleportStone.setOwner(owner.getName());
        requestedTeleportStone.setOwnerUuid(owner.getUuid());

        if (!tStorage.add(requestedTeleportStone.getName(), requestedTeleportStone)) {
            setStatusMessage(tStorage.getStatusMessage());
            return false;
        }

        Object lock = new Object();
        AtomicBoolean success = new AtomicBoolean(true);
        TeleportStone finalRequestedTeleportStone = requestedTeleportStone;
        BukkitRunnable runnable = new BukkitRunnable() {

            @Override
            public void run() {
                World world = Bukkit.getWorld(finalRequestedTeleportStone.getWorld());
                Block signBlock = world.getBlockAt( //
                        finalRequestedTeleportStone.getSign().getX(), //
                        finalRequestedTeleportStone.getSign().getY(), //
                        finalRequestedTeleportStone.getSign().getZ() //
                );

                if ((signBlock.getState() instanceof Sign)) {
                    Sign sign = (Sign) signBlock.getState();
                    sign.setLine(0, ChatColor.DARK_PURPLE + TeleportStoneValidator.keyword);
                    sign.setLine(1, finalRequestedTeleportStone.getSign().getLine(1));
                    sign.setLine(2, finalRequestedTeleportStone.getSign().getLine(2));
                    sign.setLine(3, finalRequestedTeleportStone.getSign().getLine(3));
                    if (!sign.update()) {
                        success.set(false);
                        setStatusMessage("Failed to update sign block text.");
                        synchronized(lock){
                            lock.notify();
                        }
                        return;
                    }
                }

                TeleportStoneProtector protector = new TeleportStoneProtector(getApp());
                protector.unprotect(finalRequestedTeleportStone);
                protector.protect(finalRequestedTeleportStone);

                synchronized(lock){
                    lock.notify();
                }
            }

        };

        runnable.runTask(getApp());

        try {
            synchronized(lock){
                lock.wait();
            }
        } catch (InterruptedException e) {
            setStatusMessage("Request handler was interrupted. Please tell your admin.");
            getApp().getLogger().warning("Request handler was interrupted." + e.getMessage());
            return false;
        }

        if (!success.get()) {
            return false;
        }

        EventHandler.add(
                new Event(EventTyp.REQUEST, "Accepted " + requestedTeleportStone.getName() + " by " + owner.getName()));

        setStatusMessage("Accepted request.");
        return true;
    }

    public boolean deny(TeleportStone requestedTeleportStone, User owner) {

        StorageInstance<String, TeleportStone> rtStorage = StorageFactory.getCrudTeleportStoneRequestStorage();

        Optional<TeleportStone> oRemoved = rtStorage.remove(requestedTeleportStone.getName());

        if (!oRemoved.isPresent()) {
            setStatusMessage(rtStorage.getStatusMessage());
            return false;
        }

        Object lock = new Object();
        AtomicBoolean success = new AtomicBoolean(true);
        Bukkit.getScheduler().runTask(getApp(), new Runnable() {
            @Override
            public void run() {

                World world = Bukkit.getWorld(requestedTeleportStone.getWorld());
                Block signBlock = world.getBlockAt( //
                        requestedTeleportStone.getSign().getX(), //
                        requestedTeleportStone.getSign().getY(), //
                        requestedTeleportStone.getSign().getZ() //
                );

                if ((signBlock.getState() instanceof Sign)) {
                    Sign sign = (Sign) signBlock.getState();

                    sign.setLine(0, "");
                    sign.setLine(1, "");
                    sign.setLine(2, "");
                    sign.setLine(3, "");
                    if (sign.update()) {
                        success.set(false);
                        setStatusMessage("Couldn't clear sign.");
                        synchronized(lock){
                            lock.notify();
                        }
                        return;
                    }
                }

                TeleportStoneProtector protector = new TeleportStoneProtector(getApp());
                // TODO unprotector boolean
                protector.unprotect(requestedTeleportStone);

                synchronized(lock){
                    lock.notify();
                }
                

            }
        });

        try {
            synchronized(lock){
                lock.wait();
            }
        } catch (InterruptedException e) {
            setStatusMessage("Request handler was interrupted. Please tell your admin.");
            getApp().getLogger().warning("Request handler was interrupted." + e.getMessage());
            return false;
        }

        if (!success.get()) {
            return false;
        }

        EventHandler.add(
                new Event(EventTyp.REQUEST, "Denied " + requestedTeleportStone.getName() + " by " + owner.getName()));

        return true;
    }

    public boolean show(TeleportStone teleportStone, AtomicReference<String> message) {

        if (teleportStone == null) {
            setStatusMessage("TeleportStone is null");
            return false;
        }

        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.GOLD);
        builder.append("TeleportStone: ");
        builder.append(teleportStone.getName());
        builder.append("\nBuilder: ");
        builder.append(teleportStone.getBuilder());
        builder.append("\nCreation Date: ");
        builder.append(teleportStone.getCreationDate().toString());
        builder.append("\nBlueprint: ");
        builder.append(teleportStone.getBluePrintId());
        builder.append("\nCoordinates: ");
        builder.append(teleportStone.getSign().getX());
        builder.append(" | ");
        builder.append(teleportStone.getSign().getY());
        builder.append(" | ");
        builder.append(teleportStone.getSign().getZ());
        builder.append("\nWorld: ");
        builder.append(teleportStone.getSign().getWorld());

        builder.append("\nClosest TeleportStone: ");

        TeleportStoneDistanceUtil distanceUtil = new TeleportStoneDistanceUtil(getApp());
        Optional<TeleportStone> oClosestTeleportStone = distanceUtil.searchClosestTeleportStone(
                teleportStone.getWorld(), teleportStone.getSign().getX(), teleportStone.getSign().getY(),
                teleportStone.getSign().getZ());

        if (oClosestTeleportStone.isPresent()) {
            TeleportStone closestTeleportStone = oClosestTeleportStone.get();

            builder.append(closestTeleportStone.getName());
            builder.append(" (");
            builder.append(closestTeleportStone.getBluePrintId());
            builder.append(") with a distance of ");
            builder.append(distanceUtil.getDistance());

        } else {
            builder.append(" - ");
        }

        message.set(builder.toString());
        return true;
    }
}
