package kumomi.teleportstones.command.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.DistanceCalculator;

public class CommandNearest extends CustomCommand {

    public CommandNearest(App app) {
        super(app, "nearest");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            getMessenger().sendMessage(sender, "You are not a player and can't use this command.", ChatColor.RED);
            return false;
        }

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();

        if (storage.findAll().isEmpty()) {
            getMessenger().sendMessage(sender, "There are no TeleportStones on your server.", ChatColor.GOLD);
            return false;
        }

        Player player = (Player) sender;

        double x, y, z;
        x = player.getLocation().getX();
        y = player.getLocation().getY();
        z = player.getLocation().getZ();

        String world;
        world = player.getLocation().getWorld().getName();

        TeleportStone nextNear = null;
        double nextNearDistance = -1;
        for (TeleportStone teleportStone : storage.findAll()) {

            if (!teleportStone.getWorld().equals(world))
                continue;

            if (nextNear == null) {
                nextNear = teleportStone;
                nextNearDistance = DistanceCalculator.calcDistance( //
                        x, y, z, //
                        nextNear.getSign().getX(), nextNear.getSign().getY(), nextNear.getSign().getZ());
                continue;
            }

            double dist = DistanceCalculator.calcDistance( //
                    x, y, z, //
                    teleportStone.getSign().getX(), teleportStone.getSign().getY(), teleportStone.getSign().getZ());

            if (dist < nextNearDistance) {
                nextNear = teleportStone;
                nextNearDistance = dist;
            }
        }

        if (nextNear == null) {
            getMessenger().sendMessage(sender, "There are no TeleportStones in your world.", ChatColor.BLUE);
            return true;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("The nearest TeleportStone is ");
        builder.append(nextNear.getName());
        builder.append(" at the coordinates ");
        builder.append(nextNear.getSign().getX());
        builder.append(" | ");
        builder.append(nextNear.getSign().getY());
        builder.append(" | ");
        builder.append(nextNear.getSign().getZ());
        builder.append(" with a distance of ");
        builder.append(String.format("%,.2f", nextNearDistance));
        builder.append(".");

        getMessenger().sendMessage(sender, builder.toString());

        return true;

    }

    @Override
    String customUsage() {
        return "nearest";
    }

    @Override
    String customDescription() {
        return """
                Will send you the nearest TeleportStone realtive to you.
                The TeleportStone has to be in your world. For example:
                This command will ignore TeleportStones in the nether, when you are in the overworld.""";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        return null;
    }

}
