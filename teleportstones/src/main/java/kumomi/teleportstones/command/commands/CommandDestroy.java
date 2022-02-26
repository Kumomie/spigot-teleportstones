package kumomi.teleportstones.command.commands;

import java.util.List;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.TeleportStoneDeleter;
import kumomi.teleportstones.accessmethods.TeleportStoneDestroyer;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;
import kumomi.teleportstones.util.NameUtil;

public class CommandDestroy extends CustomCommand {

    public CommandDestroy(App app) {
        super(app, "destroy");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            getMessenger().sendMessage(sender, "Missing arguments.", ChatColor.RED);
            return false;
        }

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();
        Optional<TeleportStone> oTeleportStone = storage.find(new NameUtil().argsToName(args));
        if (!oTeleportStone.isPresent()) {
            getMessenger().sendMessage(sender, "Couldn't find TeleportStone.", storage.getStatus());
            return false;
        }

        TeleportStoneDeleter deleter = new TeleportStoneDeleter(getApp());
        if (!deleter.delete(oTeleportStone.get())) {
            getMessenger().sendMessage(sender, deleter.getStatusMessage(), ChatColor.GOLD);
            return true;
        }

        TeleportStoneDestroyer destroyer = new TeleportStoneDestroyer(getApp());
        if (!destroyer.destroyTeleportStone(oTeleportStone.get())) {
            getMessenger().sendMessage(sender, destroyer.getStatusMessage(), ChatColor.GOLD);
            return true;
        }

        callEvent(new Event( //
                EventTyp.INFO, //
                sender.getName() + " destroyed TeleportStone " + oTeleportStone.get().getName() + "." //
        ));

        getMessenger().sendMessage(sender, "Successfully destroyed TeleportStone.", ChatColor.GREEN);

        return true;
    }

    @Override
    String customUsage() {
        return "destroy <name>";
    }

    @Override
    String customDescription() {
        return //
        "Delete and destroy the choosen TeleportStone.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {

        if (args.length == 0)
            return null;

        final String teleportStoneNameString = new NameUtil().argsToName(args);
        return StorageFactory.getCrudTeleportStoneStorage().findAll().stream() //
                .parallel() //
                .filter(t -> t.getName().contains(teleportStoneNameString)) //
                .map(t -> t.getName()) //
                .toList();
    }

}
