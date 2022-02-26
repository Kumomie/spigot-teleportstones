package kumomi.teleportstones.command;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.command.commands.CommandBlueprints;
import kumomi.teleportstones.command.commands.CommandDelete;
import kumomi.teleportstones.command.commands.CommandDestroy;
import kumomi.teleportstones.command.commands.CommandDiscovered;
import kumomi.teleportstones.command.commands.CommandDistance;
import kumomi.teleportstones.command.commands.CommandEvents;
import kumomi.teleportstones.command.commands.CommandFavorite;
import kumomi.teleportstones.command.commands.CommandHelp;
import kumomi.teleportstones.command.commands.CommandHome;
import kumomi.teleportstones.command.commands.CommandList;
import kumomi.teleportstones.command.commands.CommandManualDiscover;
import kumomi.teleportstones.command.commands.CommandNearest;
import kumomi.teleportstones.command.commands.CommandPlayers;
import kumomi.teleportstones.command.commands.CommandRequest;
import kumomi.teleportstones.command.commands.CommandScopes;
import kumomi.teleportstones.command.commands.CommandTeleport;
import kumomi.teleportstones.command.commands.CommandUndiscover;
import kumomi.teleportstones.command.commands.CommandUnregister;
import kumomi.teleportstones.command.commands.CommandVersion;
import kumomi.teleportstones.command.commands.CustomCommand;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;

public class CommandTeleportStone implements CommandExecutor {

    private App app;

    public final static HashMap<String, CustomCommand> commands;

    static {
        commands = new HashMap<>();
    }

    public CommandTeleportStone(App app) {

        this.app = app;

        CommandDiscovered commandDiscovered = new CommandDiscovered(app);
        commands.put(commandDiscovered.keyword(), commandDiscovered);

        CommandDistance commandDistance = new CommandDistance(app);
        commands.put(commandDistance.keyword(), commandDistance);

        CommandNearest commandNearest = new CommandNearest(app);
        commands.put(commandNearest.keyword(), commandNearest);

        CommandPlayers commandPlayers = new CommandPlayers(app);
        commands.put(commandPlayers.keyword(), commandPlayers);

        CommandList commandList = new CommandList(app);
        commands.put(commandList.keyword(), commandList);

        CommandTeleport commandTeleport = new CommandTeleport(app);
        commands.put(commandTeleport.keyword(), commandTeleport);

        CommandHelp commandHelp = new CommandHelp(app);
        commands.put(commandHelp.keyword(), commandHelp);

        CommandVersion commandVersion = new CommandVersion(app);
        commands.put(commandVersion.keyword(), commandVersion);

        CommandEvents commandEvents = new CommandEvents(app);
        commands.put(commandEvents.keyword(), commandEvents);

        CommandDelete commandDelete = new CommandDelete(app);
        commands.put(commandDelete.keyword(), commandDelete);

        CommandDestroy commandDestroy = new CommandDestroy(app);
        commands.put(commandDestroy.keyword(), commandDestroy);

        CommandUnregister commandUnregister = new CommandUnregister(app);
        commands.put(commandUnregister.keyword(), commandUnregister);

        CommandUndiscover commandUndiscover = new CommandUndiscover(app);
        commands.put(commandUndiscover.keyword(), commandUndiscover);

        CommandManualDiscover commandManualDiscover = new CommandManualDiscover(app);
        commands.put(commandManualDiscover.keyword(), commandManualDiscover);

        CommandBlueprints commandBlueprints = new CommandBlueprints(app);
        commands.put(commandBlueprints.keyword(), commandBlueprints);

        CommandScopes commandScopes = new CommandScopes(app);
        commands.put(commandScopes.keyword(), commandScopes);

        CommandFavorite commandFavorite = new CommandFavorite(app);
        commands.put(commandFavorite.keyword(), commandFavorite);

        CommandHome commandHome = new CommandHome(app);
        commands.put(commandHome.keyword(), commandHome);

        CommandRequest commandRequest = new CommandRequest(app);
        commands.put(commandRequest.keyword(), commandRequest);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args == null || args.length == 0) {
            sender.sendMessage(ChatColor.DARK_AQUA + "[TeleportStone] " //
                    + ChatColor.RED + "Not enough arguments. Use help for help.");
            return false;
        }

        String[] args2 = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            args2[i - 1] = args[i];
        }

        CustomCommand customCommand = CommandTeleportStone.commands.get(args[0].toLowerCase());

        if (customCommand == null) {
            sender.sendMessage( //
                    ChatColor.DARK_AQUA + "[TeleportStone] " //
                            + ChatColor.RED //
                            + "Command  unknown. Try the help command." //
            );
            return false;
        }

        try {

            CustomCommand commandInstance = customCommand.getClass().getConstructor(App.class).newInstance(this.app);
            Bukkit.getScheduler().runTaskAsynchronously(this.app, () -> {
                CustomCommand customCommand2 = commandInstance;
                customCommand2.onCommand(sender, command, customCommand2.keyword(), args2);
            });

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {

            sender.sendMessage( //
                    ChatColor.DARK_AQUA + "[TeleportStone] " //
                            + ChatColor.RED //
                            + "Something went wrong internaly." //
            );
            EventHandler.add(new Event(EventTyp.ERROR, "Couldn't execute command."));
            this.app.getLogger().warning("Couldn't create new command instance. " + e.getMessage());
            return true;
        }

        return true;
    }

}
