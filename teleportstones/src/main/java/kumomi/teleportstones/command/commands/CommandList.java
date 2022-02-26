package kumomi.teleportstones.command.commands;

import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.NameUtil;

public class CommandList extends CustomCommand {

    public CommandList(App app) {
        super(app, "list");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            listAll(sender);
            return true;
        }

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();
        storage.find(new NameUtil().argsToName(args)).ifPresentOrElse( //
                t -> listSpecific(t, sender), //
                () -> getMessenger().sendMessage(sender, storage.getStatusMessage(), ChatColor.GOLD) //
        );

        return true;
    }

    private void listAll(CommandSender sender) {
        Collection<TeleportStone> teleportStones = StorageFactory.getCrudTeleportStoneStorage().findAll();

        if (teleportStones.isEmpty()) {
            getMessenger().sendMessage(sender, "The are currently no TeleportStones on the server.", ChatColor.GOLD);
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (TeleportStone teleportStone : teleportStones) {

            builder.append("\n");
            builder.append(String.format("%-20s", teleportStone.getName()));
            builder.append(": ");
            builder.append(teleportStone.getWorld());
            builder.append(" (");
            builder.append(teleportStone.getSign().getX());
            builder.append(" | ");
            builder.append(teleportStone.getSign().getY());
            builder.append(" | ");
            builder.append(teleportStone.getSign().getZ());
            builder.append(")");

        }

        getMessenger().sendMessage(sender, builder.toString());
    }

    private void listSpecific(TeleportStone teleportStone, CommandSender sender) {

        StringBuilder builder = new StringBuilder();

        builder.append("----- TELEPORTSTONE INFO -----");

        builder.append(ChatColor.GOLD);
        builder.append("\nName: ");
        builder.append(ChatColor.BLUE);
        builder.append(teleportStone.getName());

        builder.append(ChatColor.GOLD);
        builder.append("\nBlueprint: ");
        builder.append(ChatColor.BLUE);
        builder.append(teleportStone.getBluePrintId());

        builder.append(ChatColor.GOLD);
        builder.append("\nWorld: ");
        builder.append(ChatColor.BLUE);
        builder.append(teleportStone.getWorld());

        builder.append(ChatColor.GOLD);
        builder.append("\nCoordinates: ");
        builder.append(ChatColor.BLUE);
        builder.append(teleportStone.getSign().getX());
        builder.append(" | ");
        builder.append(teleportStone.getSign().getY());
        builder.append(" | ");
        builder.append(teleportStone.getSign().getZ());

        builder.append(ChatColor.GOLD);
        builder.append("\nOwner: ");
        builder.append(ChatColor.BLUE);
        builder.append(teleportStone.getOwner());

        builder.append(ChatColor.GOLD);
        builder.append("\nBuilder: ");
        builder.append(ChatColor.BLUE);
        builder.append(teleportStone.getBuilder());

        builder.append(ChatColor.GOLD);
        builder.append("\nCreation Date: ");
        builder.append(ChatColor.BLUE);
        builder.append(teleportStone.getCreationDate());

        getMessenger().sendMessage(sender, builder.toString());
    }

    @Override
    String customUsage() {
        return "list [teleportStoneName]";
    }

    @Override
    String customDescription() {
        return //
        "This command will list all existing TeleportStones or return specific info about given TeleportStone.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {

        final String teleportStoneNameString = new NameUtil().argsToName(args);
        return StorageFactory.getCrudTeleportStoneStorage().findAll().stream() //
            .parallel() //
            .filter(t -> t.getName().contains(teleportStoneNameString)) //
            .map(t -> t.getName()) //
            .toList();
    }

}
