package kumomi.teleportstones.mechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import kumomi.teleportstones.App;
import kumomi.teleportstones.build.TeleportStoneValidator;
import kumomi.teleportstones.mechanics.create.TeleportStoneCreator;
import kumomi.teleportstones.storage.model.SimpleSign;

public class SignChangeListener extends Mechanic implements Listener {

    public SignChangeListener(App app) {
        super(app);
    }

    @EventHandler
    public void listen(SignChangeEvent signEvent) {

        if (signEvent.isCancelled()) {
            return;
        }

        if (!(signEvent.getBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) signEvent.getBlock().getState();

        boolean isAlreadyTeleportStone = TeleportStoneValidator.checkKeyword(sign.getLine(0));

        boolean foundKeyword = TeleportStoneValidator.checkKeyword(signEvent.getLine(0));

        // Do nothing
        if (!isAlreadyTeleportStone && !foundKeyword) {
            return;
        }

        signEvent.setCancelled(true);

        SimpleSign simpleSign = new SimpleSign( //
                signEvent.getBlock().getX(), //
                signEvent.getBlock().getY(), //
                signEvent.getBlock().getZ(), //
                signEvent.getBlock().getWorld().getName(), //
                signEvent.getBlock().getType(), //
                signEvent.getLines() //
        );

        // Create / request new TeleportStone
        if (!isAlreadyTeleportStone && foundKeyword) {

            if (!getApp().getConfiguration().getYmlConfiguration().getBoolean("enablePermissions")) {
                create(simpleSign, signEvent.getPlayer());
                return;
            }

            if (signEvent.getPlayer().hasPermission("teleportstone.feature.create"))
                create(simpleSign, signEvent.getPlayer());
            else if (signEvent.getPlayer().hasPermission("teleportstone.feature.request"))
                request(simpleSign, signEvent.getPlayer());
            else
                getMessenger().sendMessage( //
                        signEvent.getPlayer(), //
                        "You don't have permission to create TeleportStones.", //
                        ChatColor.RED //
                );
        }

        // SimpleSign oldSimpleSign = new SimpleSign( //
        // sign.getBlock().getX(), //
        // sign.getBlock().getY(), //
        // sign.getBlock().getZ(), //
        // sign.getBlock().getWorld().getName(), //
        // sign.getBlock().getType(), //
        // sign.getLines() //
        // );

        // Delete TeleportStone
        // if (isAlreadyTeleportStone && !foundKeyword) {
        // signEvent.setCancelled(true);
        // return;
        // }

        // // Update TeleportStone
        // if (isAlreadyTeleportStone && foundKeyword) {
        // signEvent.setCancelled(true);
        // return;
        // }
    }

    private void create(SimpleSign simpleSign, Player player) {
        Bukkit.getScheduler().runTask(getApp(), () -> {
            TeleportStoneCreator teleportStoneCreator = new TeleportStoneCreator(getApp());
            teleportStoneCreator.create(simpleSign, player);
        }); 
    }

    private void request(SimpleSign simpleSign, Player player) {
        Bukkit.getScheduler().runTask(getApp(), () -> {
            TeleportStoneCreator teleportStoneCreator = new TeleportStoneCreator(getApp());
            teleportStoneCreator.request(simpleSign, player);
        }); 
    }

    // private boolean delete(SimpleSign simpleSign, SimpleSign oldSimpleSign,
    // Player player) {

    // String name = TeleportStoneBuilder.getNameFromSign(oldSimpleSign);

    // TeleportStoneDeleter teleportStoneDeleter = new TeleportStoneDeleter();

    // if (!teleportStoneDeleter.deleteTeleportStoneByName(name)) {
    // sendMessage(player, teleportStoneDeleter.getMessage(),
    // teleportStoneDeleter.getStatus());
    // return false;
    // }

    // sendMessage(player, teleportStoneDeleter.getMessage(),
    // teleportStoneDeleter.getStatus());
    // return true;
    // }

    // private boolean update(SimpleSign simpleSign, SimpleSign oldSimpleSign,
    // Player player) {

    // TeleportStoneUpdater teleportStoneUpdater = new TeleportStoneUpdater();

    // if (!teleportStoneUpdater.updateTeleportStoneSign(oldSimpleSign, simpleSign))
    // {
    // sendMessage(player, teleportStoneUpdater.getMessage(),
    // teleportStoneUpdater.getStatus());
    // return false;
    // }

    // sendMessage(player, teleportStoneUpdater.getMessage(),
    // teleportStoneUpdater.getStatus());
    // return true;
    // }

}
