package kumomi.teleportstones.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.Messenger;
import kumomi.teleportstones.util.EventHandler.Event;

public abstract class CustomCommand {

    private final App app;
    private final String name;
    private final Messenger messenger;

    public CustomCommand(App app, String name) {
        this.app = app;
        this.name = name;

        this.messenger = new Messenger();
    }

    Messenger getMessenger() {
        return messenger;
    }

    public String usage() {
        String message = "";

        // message += "----Command Usage: " + name + "----\n";
        message += ChatColor.DARK_PURPLE;
        message += "Usage: ";
        message += ChatColor.GOLD;
        message += "/tpst ";
        message += customUsage();

        return message;
    }

    public String description() {
        String message = "";

        message += "---- Command Description: " + name + " ----\n";
        message += customDescription();

        return message;
    }

    public String help() {
        String message = "";

        message += "---- Command Help: " + name + " ----\n";
        message += customDescription();
        message += "\n";
        message += usage();

        return message;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1 && args[0].equals("usage")) {
            getMessenger().sendMessage(sender, usage(), ChatColor.GOLD);
            return true;
        }

        if (args.length == 1 && args[0].equals("description")) {
            getMessenger().sendMessage(sender, description(), ChatColor.GOLD);
            return true;
        }

        if (args.length == 1 && args[0].equals("help")) {
            getMessenger().sendMessage(sender, help(), ChatColor.GOLD);
            return true;
        }

        if (app.getConfiguration().getYmlConfiguration().getBoolean("enablePermissions"))
            if (!sender.hasPermission("teleportstone.command." + getName())) {
                getMessenger().sendMessage(sender, "You don't have permission to use this command.", ChatColor.RED);
                return true;
            }

        return execute(sender, command, label, args);
    }

    public void callEvent(Event event){
        EventHandler.add(event);
    }

    public final String keyword() {
        return name;
    }

    public final App getApp() {
        return app;
    }

    public final String getName() {
        return name;
    }

    abstract boolean execute(CommandSender sender, Command command, String label, String[] args);

    abstract String customUsage();

    abstract String customDescription();

    public abstract List<String> tabComplete(String[] args, CommandSender sender);
}
