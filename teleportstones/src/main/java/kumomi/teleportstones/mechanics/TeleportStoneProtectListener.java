package kumomi.teleportstones.mechanics;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.MetadataValue;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.TeleportStoneDeleter;
import kumomi.teleportstones.storage.model.TeleportStone;

public class TeleportStoneProtectListener extends Mechanic implements Listener {

    public TeleportStoneProtectListener(App app) {
        super(app);
    }

    @EventHandler
    @SuppressWarnings("unchecked")
    public void listenBlockBreak(BlockBreakEvent event) {

        MetadataValue metadataValue = checkIfTeleportStoneBlock(event.getBlock());
        if (metadataValue == null) {
            return;
        }

        event.setCancelled(true);

        if (!(metadataValue.value() instanceof Map)) {
            getMessenger().sendMessage(event.getPlayer(), "Something went wrong internally.", ChatColor.RED);
            getApp().getLogger().warning("Couldn't cast meta data value to map in TeleportStoneProtectListener.");
            return;
        }

        Map<String, Object> map = (Map<String, Object>) metadataValue.value();

        if (!event.getPlayer().getName().equals(map.get("builder"))
                && !event.getPlayer().getName().equals(map.get("owner"))) {

            if (getApp().getConfiguration().getYmlConfiguration().getBoolean("enablePermissions")) {
                if (!event.getPlayer().hasPermission("teleportstone.feature.break")) {
                    getMessenger().sendMessage( //
                            event.getPlayer(), //
                            "You are not allowed to break this TeleportStone.", //
                            ChatColor.RED //
                    );
                    return;
                }
            } else {
                getMessenger().sendMessage( //
                        event.getPlayer(), //
                        "You are not allowed to break this TeleportStone.", //
                        ChatColor.RED //
                );
                return;
            }

        }

        Bukkit.getScheduler().runTaskAsynchronously(getApp(), new Runnable() {
            @Override
            public void run() {
                TeleportStoneDeleter deleter = new TeleportStoneDeleter(getApp());
                if (deleter.delete((TeleportStone) map.get("teleportStone")))
                    getMessenger().sendMessage(event.getPlayer(), deleter.getStatusMessage(), ChatColor.GREEN);
                else
                    getMessenger().sendMessage(event.getPlayer(), deleter.getStatusMessage(), ChatColor.GOLD);
            }
        });

    }

    @EventHandler
    public void endermanGrief(EntityChangeBlockEvent event) {

        if (checkIfTeleportStoneBlock(event.getBlock()) == null) {
            return;
        }

        if (event.getEntity() instanceof Player) {
            getMessenger().sendMessage(event.getEntity(), "You are not allowed to change this TeleportStone.",
                    ChatColor.RED);
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void piston(BlockPistonExtendEvent event) {
        for (Block movedBlock : event.getBlocks()) {
            if (checkIfTeleportStoneBlock(movedBlock) != null) {
                event.setCancelled(true);
                event.getBlock().breakNaturally();
                return;
            }
        }
    }

    @EventHandler
    public void piston(BlockPistonRetractEvent event) {

        for (Block movedBlock : event.getBlocks()) {
            if (checkIfTeleportStoneBlock(movedBlock) != null) {
                event.setCancelled(true);
                event.getBlock().breakNaturally();
                return;
            }
        }
    }

    @EventHandler
    public void explosion(EntityExplodeEvent event) {
        for (Block block : event.blockList()) {
            if (checkIfTeleportStoneBlock(block) != null) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private MetadataValue checkIfTeleportStoneBlock(Block block) {

        if (block.getMetadata("TeleportStone") == null || block.getMetadata("TeleportStone").isEmpty()) {
            return null;
        }

        List<MetadataValue> metas = block.getMetadata("TeleportStone");

        for (MetadataValue metadataValue : metas) {
            if (metadataValue.getOwningPlugin().equals(getApp())) {
                return metadataValue;
            }
        }

        return null;
    }
}
