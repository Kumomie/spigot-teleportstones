package kumomi.teleportstones.mechanics;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.TeleportStoneProtector;
import kumomi.teleportstones.util.EventHandler.EventTyp;

public class PlayerJoinChecker extends Mechanic implements Listener {

    public PlayerJoinChecker(App app) {
        super(app);
    }

    @EventHandler
    public void checkUserCredentials(PlayerJoinEvent event) {

        Bukkit.getScheduler().runTaskAsynchronously(getApp(), new Runnable() {
            @Override
            public void run() {

                Player player = event.getPlayer();
                UUID uuid = player.getUniqueId();

                StorageInstance<UUID, User> uStorage = StorageFactory.getCrudUserStorage();
                Optional<User> oUser = uStorage.find(player.getUniqueId());

                if (!oUser.isPresent())
                    return;

                User user = oUser.get();

                if (!user.getName().equals(player.getName())) {

                    user.setName(player.getName());

                    StorageFactory.getCrudTeleportStoneStorage().findAll().forEach(t -> {

                        boolean wasChanged = false;

                        if (t.getBuilderUuid().equals(uuid)) {
                            t.setBuilder(user.getName());
                            wasChanged = true;
                        }

                        if (t.getOwnerUuid().equals(uuid)) {
                            t.setOwner(user.getName());
                            wasChanged = true;
                        }

                        if (wasChanged) {
                            Bukkit.getScheduler().runTask(getApp(), () -> {
                                TeleportStoneProtector protector = new TeleportStoneProtector(getApp());
                                protector.unprotect(t);
                                protector.protect(t);
                            });
                        }

                    });

                    StorageFactory.getCrudTeleportStoneRequestStorage().findAll().forEach(t -> {
                        boolean wasChanged = false;

                        if (t.getBuilderUuid().equals(uuid)) {
                            t.setBuilder(user.getName());
                            wasChanged = true;
                        }

                        if (t.getOwnerUuid().equals(uuid)) {
                            t.setOwner(user.getName());
                            wasChanged = true;
                        }

                        if (wasChanged) {
                            Bukkit.getScheduler().runTask(getApp(), () -> {
                                TeleportStoneProtector protector = new TeleportStoneProtector(getApp());
                                protector.unprotect(t);
                                protector.protect(t);
                            });
                        }
                    });

                    kumomi.teleportstones.util.EventHandler.add(new kumomi.teleportstones.util.EventHandler.Event(
                            EventTyp.INFO, "Player changed name from " + user.getName() + " to " + player.getName()));

                }

            }
        });
    }
}
