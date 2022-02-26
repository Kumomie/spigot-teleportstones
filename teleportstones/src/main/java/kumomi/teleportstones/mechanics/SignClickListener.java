package kumomi.teleportstones.mechanics;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import kumomi.teleportstones.App;
import kumomi.teleportstones.build.TeleportStoneBuilder;
import kumomi.teleportstones.build.TeleportStoneValidator;
import kumomi.teleportstones.mechanics.teleport.TeleportGUI;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.EventHandler.EventTyp;

public class SignClickListener extends Mechanic implements Listener {

    public SignClickListener(App app) {
        super(app);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void clickSign(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final String clickedTeleportStoneName = isClickedBlockTeleportStoneSign(event.getClickedBlock());

        if (clickedTeleportStoneName == null) {
            return;
        }

        final TeleportStone clickedTeleportStone;

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();
        Optional<TeleportStone> oClickedTeleportStone = storage.find(clickedTeleportStoneName);

        if (oClickedTeleportStone.isPresent()) {
            clickedTeleportStone = oClickedTeleportStone.get();
        } else {
            if (isClickedBlockRequestedTeleportStoneSign(clickedTeleportStoneName)) {
                getMessenger().sendMessage(event.getPlayer(),
                        "This TeleportStone is in a request state and needs approval of a qualified player.");
            }
            return;
        }

        // Check if player is registered
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(getApp(), new Runnable() {

            @Override
            public void run() {

                StorageInstance<UUID, User> userStorage = StorageFactory.getCrudUserStorage();
                Optional<User> oUser = userStorage.find(player.getUniqueId());

                if (oUser.isPresent()) {

                    User user = oUser.get();

                    // If player has discovered clicked TeleportStone, open GUI
                    if (user.hasDiscovered(clickedTeleportStone)) {

                        if (getApp().getConfiguration().getYmlConfiguration().getBoolean("enablePermissions"))
                            if (!player.hasPermission("teleportstone.feature.teleportgui")) {
                                getMessenger().sendMessage(player, "You are not allowed to use the TeleportStoneGUI.",
                                        ChatColor.RED);
                                return;
                            }

                        TeleportGUI teleportGUI = new TeleportGUI(getApp());
                        teleportGUI.openInventory(player, user, clickedTeleportStone);
                        return;
                    }

                    if (getApp().getConfiguration().getYmlConfiguration().getBoolean("enablePermissions"))
                        if (!player.hasPermission("teleportstone.feature.discover")) {
                            getMessenger().sendMessage(player, "You are not allowed to discover TeleportStones.",
                                    ChatColor.RED);
                            return;
                        }

                    // Add discovered TeleportStone to user list
                    if (!user.add(clickedTeleportStone)) {
                        getMessenger().sendMessage(player, "An error occurred.", ChatColor.RED);
                        return;
                    }

                    getMessenger().sendMessage( //
                            player, //
                            "You discovered a new TeleportStone.", //
                            ChatColor.GREEN //
                    );

                    kumomi.teleportstones.util.EventHandler.add(new kumomi.teleportstones.util.EventHandler.Event( //
                            EventTyp.DISCOVER, //
                            "TeleportStone " + clickedTeleportStone.getName() + " discovered by " + player.getName() //
                    ));

                    return;
                }

                if (!oUser.isPresent()) {

                    // If player is not registered, register player and add TeleportStone
                    User user = new User(player);
                    user.add(clickedTeleportStone);

                    StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();
                    if (!storage.add(user.getUuid(), user)) {
                        getMessenger().sendMessage(player, storage.getStatusMessage(), storage.getStatus());
                        return;
                    }

                    getMessenger().sendMessage(player, "You successfully discovered your first TeleportStone.",
                            ChatColor.GREEN);
                    getMessenger().sendMessage(player,
                            "Find more TeleportStones so you can teleport from one to another! For more information check out /tpst help.",
                            ChatColor.BLUE);

                    kumomi.teleportstones.util.EventHandler.add(new kumomi.teleportstones.util.EventHandler.Event( //
                            EventTyp.DISCOVER, //
                            "Player " + player.getName() + " discovered his/her first TeleportStone." //
                    ));

                    return;
                }

            }

        });

    }

    private boolean isClickedBlockRequestedTeleportStoneSign(String clickedTeleportStoneName) {
        return StorageFactory.getCrudTeleportStoneRequestStorage().find(clickedTeleportStoneName).isPresent();
    }

    private String isClickedBlockTeleportStoneSign(Block clickedBlock) {

        // if (!SignTypes.signTypes.contains(clickedBlock.getType())) {
        // return null;
        // }

        if (!(clickedBlock.getState() instanceof Sign)) {
            return null;
        }

        Sign sign = (Sign) clickedBlock.getState();

        if (!TeleportStoneValidator.checkKeyword(sign.getLine(0))) {
            return null;
        }

        return TeleportStoneBuilder.getNameFromSign(sign);
    }
}
