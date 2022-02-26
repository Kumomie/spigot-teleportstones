package kumomi.teleportstones.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;

public class CommandVersion extends CustomCommand {

    public CommandVersion(App app) {
        super(app, "version");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {
        getMessenger().sendMessage(sender, "Version " + getApp().getDescription().getVersion(), ChatColor.GOLD);
        return true;
    }

    @Override
    String customUsage() {
        return "version";
    }

    @Override
    String customDescription() {
        return "Shows the current version of the TeleportStone plugin.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        return null;
    }


}
