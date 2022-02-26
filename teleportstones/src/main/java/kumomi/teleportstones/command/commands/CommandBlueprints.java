package kumomi.teleportstones.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.storage.blueprint.BlueprintManager;

public class CommandBlueprints extends CustomCommand {

    public CommandBlueprints(App app) {
        super(app, "blueprints");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            StringBuilder builder = new StringBuilder();
            builder.append("Loaded blueprints:\n");

            BlueprintManager.blueprints.keySet().stream().forEach(bp -> {
                builder.append(bp);
                builder.append("\n");
            });

            getMessenger().sendMessage(sender, builder.toString());
            return true;
        }

        Blueprint blueprint = BlueprintManager.blueprints.get(args[0]);

        if (blueprint == null) {
            getMessenger().sendMessage(sender, "Unknown blueprint.", ChatColor.GOLD);
            return true;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("----- BLUEPRINT INFO -----");

        builder.append(ChatColor.GOLD);
        builder.append("\nID: ");
        builder.append(ChatColor.BLUE);
        builder.append(blueprint.getId());

        builder.append(ChatColor.GOLD);
        builder.append("\nScope: ");
        builder.append(ChatColor.BLUE);
        builder.append(blueprint.getScope());

        builder.append(ChatColor.GOLD);
        builder.append("\nRange: ");
        builder.append(ChatColor.BLUE);
        builder.append(blueprint.getRange() == null ? "-" : blueprint.getRange());

        builder.append(ChatColor.GOLD);
        builder.append("\nOffset blocks: ");
        builder.append(ChatColor.BLUE);
        blueprint.getOffsetBlocks().forEach(b -> {
            builder.append(ChatColor.GOLD);
            builder.append("\n- ");
            builder.append(ChatColor.BLUE);
            builder.append(b.toString());
        });

        builder.append(ChatColor.GOLD);
        builder.append("\nSign materials: ");
        builder.append(ChatColor.BLUE);
        blueprint.getSignMaterials().forEach(m -> {
            builder.append(ChatColor.GOLD);
            builder.append("\n- ");
            builder.append(ChatColor.BLUE);
            builder.append(m.toString());
        });

        getMessenger().sendMessage(sender, builder.toString());
        return true;
    }

    @Override
    String customUsage() {
        return "blueprints [blueprintName]";
    }

    @Override
    String customDescription() {
        return "Gives a list of all loaded blueprints or info about the given blueprint.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {

        if (args.length == 0)
            return BlueprintManager.blueprints.keySet().stream().toList();

        final String name = args[0];
        return BlueprintManager.blueprints.keySet().stream() //
                .parallel() //
                .filter(b -> b.contains(name)) //
                .toList();
    }

}
