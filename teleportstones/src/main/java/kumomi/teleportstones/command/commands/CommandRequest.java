package kumomi.teleportstones.command.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.TeleportStoneRequestHandler;
import kumomi.teleportstones.accessmethods.TeleportStoneRequestHandler.RequestType;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;
import kumomi.teleportstones.util.NameUtil;

public class CommandRequest extends CustomCommand {

    private static List<String> subkeyword = Arrays.asList("accept", "deny", "show");

    public CommandRequest(App app) {
        super(app, "request");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            getMessenger().sendMessage(sender, "Missing arguments. Try help command.", ChatColor.RED);
            return false;
        }

        if (!(sender instanceof Player)) {
            getMessenger().sendMessage(sender,
                    "You are not a player and should not accept requests. (e.g. from console)", ChatColor.RED);
            return false;
        }

        Player requester = (Player) sender;

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();

        Optional<User> oUser = storage.find(requester.getUniqueId());

        User owner;
        if (oUser.isPresent()) {
            owner = oUser.get();
        } else {
            owner = new User(requester);
            if (!storage.add(owner.getUuid(), owner)) {
                getMessenger().sendMessage(sender, "ERROR: Couldn't register you. " + storage.getStatusMessage(),
                        ChatColor.RED);
                return true;
            } else {
                getMessenger().sendMessage(sender, "Registered you.", ChatColor.GOLD);
            }
        }

        TeleportStone requestedTeleportStone = null;
        StorageInstance<String, TeleportStone> requestStorage = StorageFactory.getCrudTeleportStoneRequestStorage();
        if (args.length > 1) {

            Optional<TeleportStone> oTeleportStone = requestStorage.find(new NameUtil().argsToName(args, 1));
            if (!oTeleportStone.isPresent()) {
                getMessenger().sendMessage(sender, "Request with a TeleportStone called "
                        + new NameUtil().argsToName(args, 1) + " could not be found.", ChatColor.GOLD);
                return true;
            }
            requestedTeleportStone = oTeleportStone.get();

        } else {

            if (requestStorage.findAll().isEmpty()) {
                getMessenger().sendMessage(sender, "The are currently no requests.", ChatColor.GOLD);
                return true;
            }
            requestedTeleportStone = requestStorage.findAll().iterator().next();
        }

        switch (RequestType.valueOf(args[0].toLowerCase())) {
            case accept:
                accept(requestedTeleportStone, owner, sender);
                break;

            case deny:
                deny(requestedTeleportStone, owner, sender);
                break;

            case show:
                show(requestedTeleportStone, sender);
                break;

            default:
                getMessenger().sendMessage(sender, "Unknown argument. " + args[0], ChatColor.RED);
                return false;
        }

        return true;
    }

    private void accept(TeleportStone requestedTeleportStone, User owner, CommandSender sender) {

        TeleportStoneRequestHandler handler = new TeleportStoneRequestHandler(getApp());

        if (handler.accept(requestedTeleportStone, owner)) {
            getMessenger().sendMessage(sender, handler.getStatusMessage(), ChatColor.GOLD);
            return;
        }

        Player player = Bukkit.getPlayer(requestedTeleportStone.getBuilderUuid());
        if (player.isOnline()) {
            StringBuilder builder = new StringBuilder();

            builder.append("Your TeleportStone request ");
            builder.append(requestedTeleportStone.getName());
            builder.append(" was accepted by ");
            builder.append(owner.getName());
            builder.append(".");

            getMessenger().sendMessage(player, builder.toString(), ChatColor.GREEN);
        }

        getMessenger().sendMessage(sender, "Successfully accepted request.", ChatColor.GREEN);
    }

    private void deny(TeleportStone requestedTeleportStone, User owner, CommandSender sender) {

        TeleportStoneRequestHandler handler = new TeleportStoneRequestHandler(getApp());

        if (handler.deny(requestedTeleportStone, owner)) {
            getMessenger().sendMessage(sender, handler.getStatusMessage(), ChatColor.GOLD);
            return;
        }

        Player player = Bukkit.getPlayer(requestedTeleportStone.getBuilderUuid());
        if (player.isOnline()) {
            StringBuilder builder = new StringBuilder();

            builder.append("Your TeleportStone request ");
            builder.append(requestedTeleportStone.getName());
            builder.append(" was denied by ");
            builder.append(sender.getName());
            builder.append(".");

            getMessenger().sendMessage(player, builder.toString(), ChatColor.GOLD);
        }

        getMessenger().sendMessage(sender, "Successfully dinied request.", ChatColor.GREEN);
    }

    private void show(TeleportStone requestedTeleportStone, CommandSender sender) {

        TeleportStoneRequestHandler handler = new TeleportStoneRequestHandler(getApp());
        AtomicReference<String> message = new AtomicReference<>();

        if (!handler.show(requestedTeleportStone, message)) {
            getMessenger().sendMessage(sender, handler.getStatusMessage(), ChatColor.GOLD);
            return;
        }

        getMessenger().sendMessage(sender, message.get());
    }

    @Override
    String customUsage() {
        return "request (accept | deny | show) [requestTeleportStoneName]";
    }

    @Override
    String customDescription() {
        return """
                This command allows judgement of request.
                You accept or deny request for TeleportStones as well as show additional information about a request.
                Use without [requestTeleportStoneName] to work on the next request in queue""";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {

        if (args.length == 1) {
            return subkeyword.stream().filter(k -> k.contains(args[0])).toList();
        }

        if (args.length > 1) {

            final String nameString = new NameUtil().argsToName(args, 1);

            return StorageFactory.getCrudTeleportStoneRequestStorage().findAll().stream() //
                    .parallel() //
                    .filter(t -> t.getName().contains(nameString)) //
                    .map(t -> t.getName()) //
                    .toList();

        }

        return null;
    }

}
