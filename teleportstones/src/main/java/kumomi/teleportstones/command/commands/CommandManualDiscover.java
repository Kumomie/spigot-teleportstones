package kumomi.teleportstones.command.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.TeleportStoneDiscoverer;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.NameUtil;

public class CommandManualDiscover extends CustomCommand {

    public CommandManualDiscover(App app) {
        super(app, "manualdiscover");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 2) {
            getMessenger().sendMessage( //
                    sender, //
                    "Missing arguments. No user and/or TeleportStone was specified.", //
                    ChatColor.RED //
            );
            return false;
        }

        String username = args[0];
        OfflinePlayer player = Bukkit.getPlayer(username);

        if (player == null) {
            getMessenger().sendMessage(sender, "Player couldn't be found.", ChatColor.GOLD);
            return true;
        }

        String teleportStoneName = new NameUtil().argsToName(args, 1);

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();
        storage.find(teleportStoneName).ifPresentOrElse( //
                t -> discover(t, sender, player), //
                () -> getMessenger().sendMessage(sender, storage.getStatusMessage(), storage.getStatus()) //
        );

        return true;
    }

    private void discover(TeleportStone teleportStone, CommandSender sender, OfflinePlayer player) {

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();

        Optional<User> oUser = storage.find(player.getUniqueId());

        // Check if player is registered. Otherwise register him.
        User user = null;
        if (!oUser.isPresent()) {

            user = new User(player);
            if (storage.add(user.getUuid(), user)) {
                getMessenger().sendMessage(sender, storage.getStatusMessage(), ChatColor.RED);
                return;
            }

            if (player.isOnline()) {
                getMessenger().sendMessage((Player) player, "You have been manually registered to TeleportStone's.");
                getMessenger().sendMessage((Player) player, "You successfully discovered your first TeleportStone.",
                        ChatColor.GREEN);
                getMessenger().sendMessage((Player) player,
                        "Find more TeleportStones so you can teleport from one to another! For more information check out /tpst help.",
                        ChatColor.BLUE);
            }

        } else {
            user = oUser.get();
        }

        TeleportStoneDiscoverer discoverer = new TeleportStoneDiscoverer(getApp());
        if (discoverer.discover(user, teleportStone)) {

            getMessenger().sendMessage( //
                    sender, //
                    "Successfully discovered TeleportStone for player.", //
                    ChatColor.GREEN //
            );

            if (player.isOnline())
                getMessenger().sendMessage( //
                        (Player) player, //
                        "TeleportStone " + teleportStone.getName() + " was manually discovered for you.", //
                        ChatColor.GOLD //
                );

        } else {
            getMessenger().sendMessage(sender, discoverer.getStatusMessage(), ChatColor.GOLD);
        }
    }

    @Override
    String customUsage() {
        return //
        "manualdiscover <username> <nameTeleportStone>";
    }

    @Override
    String customDescription() {
        return //
        "Add a TeleportStone to a user list of discovered TeleportStones";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        if (args.length == 0)
            return null;

        String playerNameString = args[0];

        OfflinePlayer[] allPlayers = Bukkit.getOfflinePlayers();

        List<OfflinePlayer> players = Arrays.stream(allPlayers) //
                .parallel() //
                .filter(p -> p.getName().contains(playerNameString)) //
                .toList();

        if (args.length < 2) {
            return players.parallelStream().map(p -> p.getName()).toList();
        }

        if (players.size() != 1) {
            return null;
        }

        StorageInstance<UUID, User> uStorage = StorageFactory.getCrudUserStorage();
        Optional<User> oUser = uStorage.find(players.get(0).getUniqueId());

        StorageInstance<String,TeleportStone> tStorage = StorageFactory.getCrudTeleportStoneStorage();

        if(!oUser.isPresent()){
            return tStorage.findAll().parallelStream().map(t -> t.getName()).toList();
        }

        User user = oUser.get();
        final String teleportStoneNameString = new NameUtil().argsToName(args, 1);
        return tStorage.findAll().parallelStream() //
            .filter(t -> t.getName().contains(teleportStoneNameString)) //
            .filter(t -> !user.hasDiscovered(t)) //
            .map(t -> t.getName()) //
            .toList();
            
    }
}
