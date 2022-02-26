package kumomi.teleportstones.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.UserDeleter;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.util.NameUtil;

public class CommandUnregister extends CustomCommand {

    public CommandUnregister(App app) {
        super(app, "unregister");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args == null || args.length == 0) {
            getMessenger().sendMessage(sender, "Missing arguments.", ChatColor.RED);
            return false;
        }

        UserDeleter userDeleter = new UserDeleter(getApp());

        if (userDeleter.delete(new NameUtil().argsToName(args)))
            getMessenger().sendMessage(sender, userDeleter.getStatusMessage(), ChatColor.GREEN);
        else
            getMessenger().sendMessage(sender, userDeleter.getStatusMessage(), ChatColor.GOLD);

        return true;
    }

    @Override
    String customUsage() {
        return "unregister <username>";
    }

    @Override
    String customDescription() {
        return //
        "Remove a user and delete his discovered TeleportStones.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {

        if (args.length == 0)
            return null;

        final String userNameString = new NameUtil().argsToName(args);
        return StorageFactory.getCrudUserStorage().findAll().parallelStream() //
                .filter(t -> t.getName().contains(userNameString)) //
                .map(t -> t.getName()) //
                .toList();
    }
}
