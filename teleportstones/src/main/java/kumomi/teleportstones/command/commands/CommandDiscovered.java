package kumomi.teleportstones.command.commands;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kumomi.teleportstones.App;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.StorageInstance.StorageStatus;
import kumomi.teleportstones.storage.model.User;

public class CommandDiscovered extends CustomCommand {

    public CommandDiscovered(App app) {
        super(app, "discovered");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            getMessenger().sendMessage(sender, "You are not a player and can't use this command.", ChatColor.RED);
            return false;
        }

        Player player = (Player) sender;

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();
        Optional<User> oUser = storage.find(player.getUniqueId());

        if (!oUser.isPresent()) {
            if (storage.getStatus() == StorageStatus.NOT_FOUND) {
                getMessenger().sendMessage( //
                        sender, //
                        "You aren't registered. You need to discover a TeleportStone.", //
                        ChatColor.GOLD //
                );
                return true;
            }

            getMessenger().sendMessage(sender, storage.getStatusMessage(), storage.getStatus());
            return true;
        }

        if (oUser.get().getAllDiscoveredTeleportStones().isEmpty()) {
            getMessenger().sendMessage(sender, "You haven't discovered a TeleportStone.", ChatColor.GOLD);
            return true;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("You discovered the following TeleportStones:");
        oUser.get().getAllDiscoveredTeleportStones().forEach(dt -> {
            builder.append("\n");
            builder.append(dt.getName());
        });

        getMessenger().sendMessage(sender, builder.toString());

        return true;
    }

    @Override
    String customUsage() {
        return "discovered";
    }

    @Override
    String customDescription() {
        return "This command will list all your discovered TeleportStones.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        return null;
    }

}
