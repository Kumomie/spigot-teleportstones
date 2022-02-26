package kumomi.teleportstones.command.commands;

import java.util.List;
import java.util.Queue;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.App;
import kumomi.teleportstones.util.EventHandler.Event;

public class CommandEvents extends CustomCommand {

    public CommandEvents(App app) {
        super(app, "events");
    }

    // TODO this is not concurrent ready
    @Override
    boolean execute(CommandSender sender, Command command, String label, String[] args) {

        Queue<Event> queue = kumomi.teleportstones.util.EventHandler.getQueue();

        if (queue.isEmpty()) {
            getMessenger().sendMessage(sender, "There are no new events since last called.", ChatColor.BLUE);
            return true;
        }
        
        StringBuilder builder = new StringBuilder();

        while (!queue.isEmpty()) {

            Event event;
            event = queue.poll();

            builder.append("\n");
            builder.append(ChatColor.BLUE);
            builder.append(event.getDay());
            builder.append(".");
            builder.append(event.getMonth());
            builder.append(" - ");
            builder.append(event.getHour());
            builder.append(":");
            builder.append(event.getMinute());
            builder.append(" ");

            switch (event.getEventTyp()) {
                case INFO:
                    builder.append(ChatColor.BLUE);
                    break;

                case ERROR:
                    builder.append(ChatColor.RED);
                    break;

                case CREATE:
                case DISCOVER:
                    builder.append(ChatColor.GREEN);
                    break;

                case DESTROY:
                case DELETE:
                    builder.append(ChatColor.GOLD);
                    break;

                case TELEPORT:
                    builder.append(ChatColor.DARK_PURPLE);
                    break;

                case REQUEST:
                    builder.append(ChatColor.DARK_GREEN);
                    break;

                default:
                    builder.append(ChatColor.WHITE);
                    break;
            }

            builder.append("[");
            builder.append(event.getEventTyp().toString());
            builder.append("]");

            builder.append(ChatColor.BLUE);
            builder.append(" ");
            builder.append(event.getMessage());
        }

        getMessenger().sendMessage(sender, builder.toString(), ChatColor.BLUE);
        return true;
    }

    @Override
    String customUsage() {
        return "events";
    }

    @Override
    String customDescription() {
        return //
        "This command will show all critical events, " //
                + "that occured since last time this command was used.";
    }

    @Override
    public List<String> tabComplete(String[] args, CommandSender sender) {
        return null;
    }

}
