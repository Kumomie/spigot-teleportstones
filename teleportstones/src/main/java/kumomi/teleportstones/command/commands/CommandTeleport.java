package kumomi.teleportstones.command.commands;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kumomi.teleportstones.App;
import kumomi.teleportstones.mechanics.teleport.Teleport;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.NameUtil;

public class CommandTeleport extends CustomCommand {

    public CommandTeleport(App app) {
        super(app, "tp");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            getMessenger().sendMessage(sender, "Missing argument. No name of target TeleportStone was specified.",
                    ChatColor.RED);
            return false;
        }

        if (!(sender instanceof Player)) {
            getMessenger().sendMessage(sender, "You are not a player and can't use this command.", ChatColor.RED);
            return false;
        }

        Player player = (Player) sender;

        StorageInstance<UUID, User> uStorage = StorageFactory.getCrudUserStorage();
        Optional<User> oUser = uStorage.find(player.getUniqueId());

        User user;
        if (oUser.isPresent()) {
            user = oUser.get();
        } else {
            getMessenger().sendMessage(sender, "Unregistered players can't teleport. Discover a TeleportStone first.",
                    ChatColor.GOLD);
            return true;
        }

        String teleportStoneName = new NameUtil().argsToName(args);

        if (!user.hasDiscovered(teleportStoneName)) {
            getMessenger().sendMessage(sender, "You haven't discovered such a TeleportStone.", ChatColor.GOLD);
            return true;
        }

        StorageInstance<String, TeleportStone> tStorage = StorageFactory.getCrudTeleportStoneStorage();
        tStorage.find(teleportStoneName).ifPresentOrElse(t -> {
            teleport(player, user, t);
        }, () -> {
            getMessenger().sendMessage(sender, "TeleportStone couldn't be found. Please contact a admin.",
                    ChatColor.GOLD);
            getApp().getLogger().warning("User has an unknown TeleportStone called " + teleportStoneName
                    + " in his/her discovered TeleportStone list.");
        });
        

        return true;
    }

    private void teleport(Player player, User user, TeleportStone destination){
        
        Teleport teleport = new Teleport(getApp());
        teleport.teleportFromNearest(player, user, destination);

        getMessenger().sendMessage(player, teleport.getMessage(), teleport.getStatus());
    }

    @Override
    String customUsage() {
        return "tp <name>";
    }

    @Override
    String customDescription() {
        return //
        "This command will teleport you to another TeleportStone."
                + " You can only teleport, when you are near a TeleportStone."
                + " You only need to provide the name of your target TeleportStone.";
    }

    /**
     * This method will return all discovered TeleportStone of the player, filtered by typing.
     * 
     * It was a concious decision to not filter by reachability.
     */
    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {

        if (!(sender instanceof Player)) 
            return null;
        
        Player player = (Player) sender;

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();
        Optional<User> oUser = storage.find(player.getUniqueId());

        if(!oUser.isPresent())
            return null;
        
        User user = oUser.get();

        final String teleportStoneNameString = new NameUtil().argsToName(args);

        return user.getAllDiscoveredTeleportStones().parallelStream() //
            .filter(t -> t.getName().contains(teleportStoneNameString)) //
            .map(t -> t.getName()) //
            .toList();
    }
}
