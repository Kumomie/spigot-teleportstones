package kumomi.teleportstones.command.commands;

import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.DistanceCalculator;
import kumomi.teleportstones.util.NameUtil;

public class CommandDistance extends CustomCommand {

    public CommandDistance(App app) {
        super(app, "distance");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            getMessenger().sendMessage(sender, "You are not a player and can't use this command.", ChatColor.RED);
            return false;
        }

        if (args.length == 0) {
            getMessenger().sendMessage(sender,
                    ChatColor.RED + "Missing argument. No name of a TeleportStone was specified.", ChatColor.RED);
            getMessenger().sendMessage(sender, usage());
            return true;
        }

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();

        String name = new NameUtil().argsToName(args);

        storage.find(name).ifPresentOrElse( //
                t -> calculateDistance(t, sender), //
                () -> {
                    getMessenger().sendMessage(sender, storage.getStatusMessage(), storage.getStatus());
                });

        return true;
    }

    private void calculateDistance(TeleportStone teleportStone, CommandSender sender) {
        Player player = (Player) sender;

        // TODO check if this works async (Otherwise add these as arguments, extract
        // them before)
        // Not in same world
        if (!player.getWorld().getName().equals(teleportStone.getWorld())) {
            getMessenger().sendMessage( //
                    player, //
                    "You and your specified TeleportStone are not in the same world. " //
                            + teleportStone.getName() //
                            + " is in world " //
                            + teleportStone.getWorld() //
                            + " and you are in world "//
                            + player.getWorld().getName() + ".", //
                    ChatColor.GOLD);
            return;
        }

        DistanceCalculator calculator = new DistanceCalculator(teleportStone, player);

        StringBuilder builder = new StringBuilder();

        builder.append("Distance to ");
        builder.append(teleportStone.getName());
        builder.append(" is ");
        builder.append(String.format("%,.2f", calculator.calcDistance()));
        builder.append(".");

        getMessenger().sendMessage(player, builder.toString());
    }

    @Override
    String customUsage() {
        return "distance <name>";
    }

    @Override
    String customDescription() {
        return "This command will calculate the distance between you and a discovered TeleportStone.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        if (!(sender instanceof Player)) {
            return null;
        }

        if (args.length == 0)
            return null;

        Optional<User> oUser = StorageFactory.getCrudUserStorage().find(((Player) sender).getUniqueId());

        if(!oUser.isPresent())
            return null;

        User user = oUser.get();

        final String teleportStoneNameString = new NameUtil().argsToName(args);
        return StorageFactory.getCrudTeleportStoneStorage().findAll().stream() //
                .parallel() //
                .filter(t -> t.getName().contains(teleportStoneNameString)) //
                .filter(user::hasDiscovered) //
                .map(t -> t.getName()) //
                .toList();
    }

}
