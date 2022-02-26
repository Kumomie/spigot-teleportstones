package kumomi.teleportstones.command.commands;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.build.structure.Scope;
import kumomi.teleportstones.storage.scope.ScopeManager;

public class CommandScopes extends CustomCommand {

    public CommandScopes(App app) {
        super(app, "scopes");
    }

    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {

            StringBuilder builder = new StringBuilder();

            builder.append("Loaded scopes: ");

            List<String> defaultscopes = ScopeManager.defaultScopes;
            Iterator<String> defaultIterator = defaultscopes.iterator();

            if (defaultIterator.hasNext()) {
                builder.append(defaultIterator.next());
            }

            while (defaultIterator.hasNext()) {
                builder.append(", ");
                builder.append(defaultIterator.next());
            }

            Collection<Scope> scopes = ScopeManager.scopes.values();
            Iterator<Scope> iterator = scopes.iterator();

            while (iterator.hasNext()) {
                builder.append(", ");
                builder.append(iterator.next().getName());
            }

            getMessenger().sendMessage(sender, builder.toString());

            return true;
        }

        if (ScopeManager.defaultScopes.contains(args[0])) {
            getMessenger().sendMessage(sender, args[0] + " is a default scope.", ChatColor.GOLD);
            return true;
        }

        Scope scope = ScopeManager.scopes.get(args[0]);

        if (scope == null) {
            getMessenger().sendMessage(sender, "Couldn't find scope: " + args[0], ChatColor.GOLD);
            return true;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("----- SCOPE INFO -----");

        builder.append(ChatColor.GOLD);
        builder.append("\nName: ");
        builder.append(ChatColor.BLUE);
        builder.append(scope.getName());

        builder.append(ChatColor.GOLD);
        builder.append("\nReachable Scopes: ");
        builder.append(ChatColor.BLUE);
        scope.getReachableScopes().forEach(s -> {
            builder.append(ChatColor.GOLD);
            builder.append("\n- ");
            builder.append(ChatColor.BLUE);
            builder.append(s);
        });

        getMessenger().sendMessage(sender, builder.toString());

        return true;
    }

    @Override
    String customUsage() {
        return "scopes [scopeName]";
    }

    @Override
    String customDescription() {
        return "Returns a list of all loaded scopes or info about specified scope.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        return ScopeManager.scopes.keySet().stream() //
                .parallel() //
                .filter(s -> s.contains(args[0])) //
                .toList();
    }
}
