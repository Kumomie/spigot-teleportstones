package kumomi.teleportstones.mechanics.create;

import java.util.Date;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.TeleportStoneAdder;
import kumomi.teleportstones.build.BlueprintDeterminator;
import kumomi.teleportstones.build.TeleportStoneBuilder;
import kumomi.teleportstones.build.TeleportStoneBuilder.BuildStatus;
import kumomi.teleportstones.build.TeleportStoneValidator;
import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.mechanics.Mechanic;
import kumomi.teleportstones.storage.model.SimpleSign;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.util.EventHandler;
import kumomi.teleportstones.util.Messenger;
import kumomi.teleportstones.util.TeleportStoneProtector;
import kumomi.teleportstones.util.EventHandler.Event;
import kumomi.teleportstones.util.EventHandler.EventTyp;

public class TeleportStoneCreator extends Mechanic {

    public TeleportStoneCreator(App app) {
        super(app);
    }

    private Optional<Blueprint> oBluePrint;
    private Optional<TeleportStone> oTeleportStone;

    public boolean create(SimpleSign simpleSign, Player player) {

        if (!determineBlueprint(simpleSign, player)) {
            return false;
        }

        if (!buildTeleportStone(simpleSign, player)) {
            return false;
        }

        TeleportStone teleportStone = oTeleportStone.get();

        TeleportStoneAdder adder = new TeleportStoneAdder(getApp());
        if (!adder.addTeleportStone(teleportStone)) {
            getMessenger().sendMessage(player, adder.getStatusMessage(), ChatColor.GOLD);
            return false;
        }

        World world = Bukkit.getWorld(teleportStone.getWorld());
        Block signBlock = world.getBlockAt( //
                teleportStone.getSign().getX(), //
                teleportStone.getSign().getY(), //
                teleportStone.getSign().getZ() //
        );

        if ((signBlock.getState() instanceof Sign)) {
            Sign sign = (Sign) signBlock.getState();
            sign.setLine(0, ChatColor.DARK_PURPLE + TeleportStoneValidator.keyword);
            sign.setLine(1, simpleSign.getLine(1));
            sign.setLine(2, simpleSign.getLine(2));
            sign.setLine(3, simpleSign.getLine(3));
            sign.update();
        }

        // protect TeleportStone
        TeleportStoneProtector protector = new TeleportStoneProtector(getApp());
        protector.protect(teleportStone);

        return true;
    }

    public boolean request(SimpleSign simpleSign, Player player) {

        if (!determineBlueprint(simpleSign, player)) {
            return false;
        }

        if (!buildTeleportStone(simpleSign, player)) {
            return false;
        }

        TeleportStone teleportStone = oTeleportStone.get();

        TeleportStoneAdder adder = new TeleportStoneAdder(getApp());
        if (!adder.requestTeleportStone(teleportStone)) {
            getMessenger().sendMessage(player, adder.getStatusMessage(), ChatColor.GOLD);
            return false;
        }

        World world = Bukkit.getWorld(teleportStone.getWorld());
        Block signBlock = world.getBlockAt( //
                teleportStone.getSign().getX(), //
                teleportStone.getSign().getY(), //
                teleportStone.getSign().getZ() //
        );

        if ((signBlock.getState() instanceof Sign)) {
            Sign sign = (Sign) signBlock.getState();
            sign.setLine(0, ChatColor.STRIKETHROUGH + TeleportStoneValidator.keyword);
            sign.setLine(1, simpleSign.getLine(1));
            sign.setLine(2, simpleSign.getLine(2));
            sign.setLine(3, ChatColor.RED + "Requested State");
            sign.update();
        }

        // protect TeleportStone
        TeleportStoneProtector protector = new TeleportStoneProtector(getApp());
        protector.protect(teleportStone);

        return true;

    }

    private boolean determineBlueprint(SimpleSign simpleSign, Player player) {

        BlueprintDeterminator bluePrintDeterminator = new BlueprintDeterminator();
        oBluePrint = bluePrintDeterminator.determine(simpleSign);

        if (!oBluePrint.isPresent()) {
            getMessenger().sendMessage( //
                    player, //
                    bluePrintDeterminator.getMessage(), //
                    ChatColor.GOLD //
            );
            return false;
        }

        return true;
    }

    private boolean buildTeleportStone(SimpleSign simpleSign, Player player) {

        // Build TeleportStone from sign and validate.
        TeleportStoneBuilder teleportStoneBuilder = new TeleportStoneBuilder();
        oTeleportStone = teleportStoneBuilder //
                .sign(simpleSign) //
                .blueprint(oBluePrint.get()) //
                .buildAndValidate();

        if (!oTeleportStone.isPresent()) {

            if (teleportStoneBuilder.getStatus() == BuildStatus.ERROR) {
                new Messenger().sendMessage( //
                        player, //
                        "An error occurred.", //
                        ChatColor.RED //
                );
                EventHandler.add(new Event(EventTyp.ERROR, teleportStoneBuilder.getMessage()));
            } else {
                getMessenger().sendMessage( //
                        player, //
                        teleportStoneBuilder.getMessage(), //
                        ChatColor.GOLD //
                );
            }

            return false;
        }

        TeleportStone teleportStone = oTeleportStone.get();
        
        teleportStone.setBuilder(player.getName());
        teleportStone.setBuilderUuid(player.getUniqueId());
        teleportStone.setOwner(player.getName());
        teleportStone.setOwnerUuid(player.getUniqueId());
        teleportStone.setCreationDate(new Date());
        
        return true;
    }

}
