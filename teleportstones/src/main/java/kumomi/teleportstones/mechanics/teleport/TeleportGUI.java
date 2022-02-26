package kumomi.teleportstones.mechanics.teleport;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import kumomi.teleportstones.App;
import kumomi.teleportstones.mechanics.Mechanic;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;

public class TeleportGUI extends Mechanic implements Listener {
    public TeleportGUI(App app) {
        super(app);
    }

    public static final ConcurrentHashMap<Player, PageableInventory> inventories;

    static {
        inventories = new ConcurrentHashMap<>();
    }

    public void openInventory(Player player, User user, TeleportStone departure) {
        PageableInventory pageableInventory = new PageableInventory(getApp(), user, departure);
        pageableInventory.create();

        inventories.put(player, pageableInventory);

        Bukkit.getScheduler().runTask(getApp(), () -> pageableInventory.openInventory(player));
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getWhoClicked();

        if (!inventories.containsKey(player)) {
            return;
        }

        PageableInventory pageableInventory = inventories.get(player);

        if (pageableInventory == null) {
            return;
        }

        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir())
            return;

        if (e.getSlot() <= 17) {

            switch (clickedItem.getType()) {

                case OAK_DOOR:
                case DIAMOND:
                case CAMPFIRE:

                    StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();
                    Optional<TeleportStone> oTeleportStone = storage.find(clickedItem.getItemMeta().getDisplayName());

                    if (!oTeleportStone.isPresent()) {
                        for (HumanEntity viewer : e.getInventory().getViewers()) {
                            viewer.closeInventory();
                            if (viewer instanceof Player p) {
                                getMessenger().sendMessage( //
                                        p, //
                                        "Inventory was closed due change in TeleportStone grid.", //
                                        ChatColor.GOLD //
                                );
                            }
                        }
                        return;
                    }

                    Teleport teleport = new Teleport(getApp());
                    Bukkit.getScheduler().runTaskAsynchronously(getApp(), () -> {
                        teleport.teleport( //
                                player, //
                                pageableInventory.getDeparture().getName(), //
                                oTeleportStone.get() //
                        );
                    });

                    break;

                default:
                    break;
            }

        } else {

            switch (clickedItem.getType()) {

                case LANTERN:
                    if (clickedItem.getItemMeta().getDisplayName().equals("Previous Page")) {
                        pageableInventory.previousPage();
                    }
                    break;

                case SOUL_LANTERN:
                    if (clickedItem.getItemMeta().getDisplayName().equals("Next Page")) {
                        pageableInventory.nextPage();
                    }
                    break;

                case SOUL_CAMPFIRE:
                case CAMPFIRE:
                    pageableInventory.toggleHome();
                    pageableInventory.reload();
                    break;

                case PAINTING:
                case ITEM_FRAME:
                    pageableInventory.toggleFavorite();
                    pageableInventory.reload();
                    break;

                default:
                    break;
            }
        }

    }

    @EventHandler
    public void onInventorDrag(final InventoryDragEvent e) {

        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) e.getWhoClicked();

        if (!inventories.containsKey(player)) {
            return;
        }

        PageableInventory pageableInventory = inventories.get(player);

        if (pageableInventory == null) {
            return;
        }

        if (e.getInventory().equals(pageableInventory.getInventory())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();

        inventories.remove(player);
    }
}
