package kumomi.teleportstones.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.build.TeleportStoneValidator;
import kumomi.teleportstones.command.CommandTeleportStone;

public class CommandHelp extends CustomCommand {

    public CommandHelp(App app) {
        super(app, "help");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        StringBuilder builder = new StringBuilder();

        builder.append("Hello this is the help command of the TeleportStone plugin.");
        builder.append(
                " By typing \"/tpst <subcommand> help\", you can check out help commands of specific commands for more information.");
        builder.append("\n");
        builder.append("Build a TeleportStone from a blueprint, place a sign on it, write " + TeleportStoneValidator.keyword);
        builder.append(" on the first line and name on the second and third line. Click Done.");
        builder.append("\n");
        // TODO link here
        builder.append("For more information visit: <LINK HERE>");

        getMessenger().sendMessage(sender, builder.toString(), ChatColor.GOLD);

        List<String> availableCommands = new ArrayList<>();

        CommandTeleportStone.commands.keySet().forEach(availableCommands::add);

        if (availableCommands.size() == 0) {
            getMessenger().sendMessage(sender, "There are currently no commands available.", ChatColor.GOLD);
            return true;
        }

        builder = new StringBuilder();

        builder.append("Available Commands: ");
        builder.append(availableCommands.get(0));

        for (int i = 1; i < availableCommands.size(); i++) {
            builder.append(", ");
            builder.append(availableCommands.get(i));
        }

        getMessenger().sendMessage(sender, builder.toString(), ChatColor.GOLD);

        return true;
    }

    @Override
    String customUsage() {
        return "help";
    }

    @Override
    String customDescription() {
        return "This is the help command. It will inform you about available commands.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        return null;
    }

}
