package kumomi.teleportstones.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import kumomi.teleportstones.mechanics.teleport.Teleport.TeleportStatus;
import kumomi.teleportstones.storage.StorageInstance.StorageStatus;

public class Messenger {

    public void sendMessage(CommandSender sender, String message, ChatColor color) {
        sender.sendMessage( //
                ChatColor.DARK_AQUA //
                        + "[TeleportStone] " //
                        + color //
                        + message //
        );
    }

    /**
     * Message has a default color of blue. Use sendMessage(CommandSender sender,
     * String message, ChatColor color) if you need another color.
     * 
     * @param sender  Who receives the message.
     * @param message The message.
     */
    public void sendMessage(CommandSender sender, String message) {
        sendMessage(sender, message, ChatColor.BLUE);
    }

    public void sendMessage(CommandSender sender, String message, TeleportStatus status) {
        String buildMessage = ChatColor.DARK_AQUA + "[TeleportStone] ";

        switch (status) {
            case ERROR:
                buildMessage += ChatColor.RED;
                break;

            case SUCCESs_TELEPORT:
                buildMessage += ChatColor.DARK_PURPLE;
                break;

            default:
                buildMessage += ChatColor.GOLD;
                break;
        }

        buildMessage += message;

        sender.sendMessage(buildMessage);
    }

    /**
     * 
     * @param sender  Who receives the message.
     * @param message The message.
     * @param status  Status determines the color of the message.
     */
    public void sendMessage(CommandSender sender, String statusMessage, StorageStatus status) {
        String buildMessage = ChatColor.DARK_AQUA + "[TeleportStone] ";

        switch (status) {
            case ERROR:
                buildMessage += ChatColor.RED;
                break;

            case SUCCESS:
                buildMessage += ChatColor.GREEN;
                break;

            default:
                buildMessage += ChatColor.GOLD;
                break;
        }

        buildMessage += statusMessage;

        sender.sendMessage(buildMessage);
    }

}
