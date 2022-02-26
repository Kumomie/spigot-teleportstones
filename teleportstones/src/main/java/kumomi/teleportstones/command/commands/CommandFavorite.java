package kumomi.teleportstones.command.commands;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.UserTeleportStoneToggler;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.NameUtil;

public class CommandFavorite extends CustomCommand {

    public CommandFavorite(App app) {
        super(app, "favorite");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length <= 0) {
            getMessenger().sendMessage(sender, "Missing name of TeleportStone.", ChatColor.RED);
            return false;
        }

        if (!(sender instanceof Player)) {
            getMessenger().sendMessage(sender, "You are not a player.", ChatColor.RED);
            return false;
        }

        Player player = (Player) sender;

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();

        storage.find(player.getUniqueId()).ifPresentOrElse(u -> {

            UserTeleportStoneToggler toggler = new UserTeleportStoneToggler(getApp());
            if (toggler.toggleFavorite(u, new NameUtil().argsToName(args)))
                getMessenger().sendMessage(sender, toggler.getStatusMessage(), ChatColor.GREEN);
            else
                getMessenger().sendMessage(sender, toggler.getStatusMessage(), ChatColor.GOLD);

        }, () -> getMessenger().sendMessage(sender, storage.getStatusMessage(), ChatColor.GOLD));

        return true;
    }

    @Override
    String customUsage() {
        return "favorite <TeleportStone Name>";
    }

    @Override
    String customDescription() {
        return "Add or remove TeleportStone as favorite.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {

        if (!(sender instanceof Player))
            return null;

        Player player = (Player) sender;

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();
        Optional<User> oUser = storage.find(player.getUniqueId());

        if (!oUser.isPresent()) {
            return null;
        }

        User user = oUser.get();
        final String finalName = new NameUtil().argsToName(args);

        return user.getAllDiscoveredTeleportStones().parallelStream() //
                .filter(t -> t.getName().contains(finalName)) //
                .map(t -> t.getName()) //
                .toList();
    }

}
