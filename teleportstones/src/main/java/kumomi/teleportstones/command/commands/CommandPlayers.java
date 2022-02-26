package kumomi.teleportstones.command.commands;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.model.User;

public class CommandPlayers extends CustomCommand {

    public CommandPlayers(App app) {
        super(app, "players");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        Collection<User> users = StorageFactory.getCrudUserStorage().findAll();

        if (users.isEmpty()) {
            getMessenger().sendMessage(sender, "The are currently no registered users.", ChatColor.BLUE);
            return true;
        }

        StringBuilder builder = new StringBuilder();

        Iterator<User> iterator = users.iterator();

        builder.append("The following users have been registered: ");
        builder.append(iterator.next().getName());
        while (iterator.hasNext()) {
            builder.append(", ");
            builder.append(iterator.next().getName());
        }

        getMessenger().sendMessage(sender, builder.toString());

        return true;
    }

    @Override
    String customUsage() {
        return "players";
    }

    @Override
    String customDescription() {
        return //
        "This command will list all players that used this plugin, meaning having at one point at least one registered TeleportStone.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        return null;
    }

}
