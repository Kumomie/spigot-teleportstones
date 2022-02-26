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
import kumomi.teleportstones.accessmethods.UserSearcher;
import kumomi.teleportstones.accessmethods.searchFors.user.SearchForName;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.NameUtil;

public class CommandUndiscover extends CustomCommand {

    public CommandUndiscover(App app) {
        super(app, "undiscover");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 2) {
            getMessenger().sendMessage(sender, "Missing arguments.", ChatColor.GOLD);
            return false;
        }

        String username = args[0];
        // TODO this requires again a quick search by name in user storage aka storage upgrade
        OfflinePlayer player = Bukkit.getPlayer(username);

        if (player == null) {
            getMessenger().sendMessage(sender, "Player couldn't be found or is offline.", ChatColor.GOLD);
            return true;
        }

        final String teleportStoneName = new NameUtil().argsToName(args, 1);

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();
        storage.find(teleportStoneName).ifPresentOrElse( //
                t -> undiscover(t, sender, player), //
                () -> getMessenger().sendMessage(sender, storage.getStatusMessage(), storage.getStatus()) //
        );

        return true;
    }

    private void undiscover(TeleportStone teleportStone, CommandSender sender, OfflinePlayer player) {

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();

        Optional<User> oUser = storage.find(player.getUniqueId());

        User user = null;
        if (!oUser.isPresent()) {
            getMessenger().sendMessage(sender, storage.getStatusMessage(), storage.getStatus());
            return;
        } else
            user = oUser.get();

        TeleportStoneDiscoverer discoverer = new TeleportStoneDiscoverer(getApp());

        if (discoverer.undiscover(user, teleportStone)) {

            getMessenger().sendMessage( //
                    sender, //
                    "Successfully undiscovered TeleportStone from player.", //
                    ChatColor.GREEN //
            );
            
            if (player.isOnline())
                getMessenger().sendMessage( //
                        (Player) player, //
                        "TeleportStone " + teleportStone.getName() + " was manually undiscovered from you.", //
                        ChatColor.GOLD //
                );

        } else {
            getMessenger().sendMessage(sender, discoverer.getStatusMessage(), ChatColor.GOLD);
        }
    }

    @Override
    String customUsage() {
        return "undiscover <username> <nameTeleportStone>";
    }

    @Override
    String customDescription() {
        return //
        "Remove a discovered TeleportStone from a user.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        if (args.length == 0)
            return null;

        final String playerNameString = args[0];

        if (args.length < 2) {

            UserSearcher userSearcher = new UserSearcher(getApp());
            userSearcher.search(Arrays.asList(new SearchForName(playerNameString)));

            return userSearcher.getFoundUsersAsStringList();
        }

        // TODO This may require an upgraded user storage, so you can quick search by name.
        StorageInstance<UUID, User> uStorage = StorageFactory.getCrudUserStorage();
        Optional<User> oUser = uStorage.findAll().parallelStream() //
            .filter(u -> u.getName().contains(playerNameString)) //
            .findFirst();

        if(!oUser.isPresent())
            return null;

        User user = oUser.get();

        final String teleportStoneNameString = new NameUtil().argsToName(args, 1);

        return user.getAllDiscoveredTeleportStones().parallelStream() //
            .filter(t -> t.getName().contains(teleportStoneNameString)) //
            .map(t -> t.getName()) //
            .toList();

    }

}
