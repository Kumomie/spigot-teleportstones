package kumomi.teleportstones.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import kumomi.teleportstones.command.commands.CustomCommand;

/**
 * Suggest tab completions for TeleportStone plugin commands.
 */
public class TapCompleterTeleportStone implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            CommandTeleportStone.commands.keySet().forEach((keyword) -> {
                if (keyword.contains(args[0])) {
                    completions.add(keyword);
                }
            });
            return completions;
        }

        //TODO do I need to make this Tread-Safe or is this called in sync ?
        CustomCommand customCommand = CommandTeleportStone.commands.get(args[0]);
        if (customCommand == null)
            return null;

        String[] args2 = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            args2[i - 1] = args[i];
        }

        return customCommand.tabComplete(args2, sender);

    }

}
