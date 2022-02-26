package kumomi.teleportstones.mechanics.teleport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import kumomi.teleportstones.App;
import kumomi.teleportstones.accessmethods.UserTeleportStoneToggler;
import kumomi.teleportstones.build.structure.Blueprint;
import kumomi.teleportstones.storage.blueprint.BlueprintManager;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;

public class PageableInventory {

    private record Item(String name, Material material, ItemType itemType, List<String> reachableMessage) {
    };

    public static enum ItemType {
        BUTTON_PREVIOUS_PAGE //
        , BUTTON_NEXT_PAGE //
        , TELEPORT_DESTINATION //
        , TOGGLE_HOME //
        , TOGGLE_FAVORITE //
        , INFO //
    }

    private App app;

    private User user;
    private TeleportStone departure;

    private Map<Integer, List<Item>> pages;
    private Integer currentPage;

    private Inventory inventory;

    private List<Item> lastPage;

    private boolean isDepartureHome;
    private boolean isDepartureFavorite;

    public PageableInventory(App app, User user, TeleportStone departure) {
        this.app = app;
        this.pages = new HashMap<>();
        this.currentPage = 0;
        this.lastPage = new ArrayList<>();
        this.isDepartureHome = false;
        this.isDepartureFavorite = false;
        this.user = user;
        this.departure = departure;

        this.inventory = Bukkit.createInventory(null, 27, "Destinations: " + departure.getName());
    }

    public void create() {

        if (user.getHome().isPresent() && user.getHome().get().equals(departure)) {
            isDepartureHome = true;
        }

        if (user.getFavorits().containsKey(departure.getName())) {
            isDepartureFavorite = true;
        }

        TeleportReachableChecker teleportReachableChecker = new TeleportReachableChecker();

        List<Item> unreachables = new ArrayList<>();

        user.getHome().ifPresent((h) -> {
            if (teleportReachableChecker.checkIfReachableSync(departure, h)) {
                addToLastPage(new Item(h.getName(), Material.CAMPFIRE, ItemType.TELEPORT_DESTINATION,
                        teleportReachableChecker.getMessage()));
            } else {
                unreachables.add(new Item(h.getName(), Material.IRON_DOOR, ItemType.TELEPORT_DESTINATION,
                        teleportReachableChecker.getMessage()));
            }
        });

        List<Item> favoritReachables = new ArrayList<>();
        user.getFavorits().values().forEach(f -> {
            if (teleportReachableChecker.checkIfReachableSync(departure, f)) {
                favoritReachables.add(new Item(f.getName(), Material.DIAMOND, ItemType.TELEPORT_DESTINATION,
                        teleportReachableChecker.getMessage()));
            } else {
                unreachables.add(new Item(f.getName(), Material.IRON_DOOR, ItemType.TELEPORT_DESTINATION,
                        teleportReachableChecker.getMessage()));
            }
        });

        List<Item> normalReachables = new ArrayList<>();
        user.getDiscoveredTeleportStones().values().forEach(f -> {
            if (teleportReachableChecker.checkIfReachableSync(departure, f)) {
                normalReachables.add(new Item(f.getName(), Material.OAK_DOOR, ItemType.TELEPORT_DESTINATION,
                        teleportReachableChecker.getMessage()));
            } else {
                unreachables.add(new Item(f.getName(), Material.IRON_DOOR, ItemType.TELEPORT_DESTINATION,
                        teleportReachableChecker.getMessage()));
            }
        });

        Collections.sort(favoritReachables, (t1, t2) -> t1.name().compareTo(t2.name()));
        Collections.sort(normalReachables, (t1, t2) -> t1.name().compareTo(t2.name()));
        Collections.sort(unreachables, (t1, t2) -> t1.name().compareTo(t2.name()));

        favoritReachables.forEach(this::addToLastPage);
        normalReachables.forEach(this::addToLastPage);
        unreachables.forEach(this::addToLastPage);

        if (lastPage.size() != 0) {
            this.pages.put(currentPage, lastPage);
            this.lastPage = new ArrayList<>();
        }

        if (this.currentPage == 0 && this.pages.get(0) == null) {
            this.pages.put(0, new ArrayList<>());
        }

        this.currentPage = 0;

        Bukkit.getScheduler().runTaskAsynchronously(app, this::buildInventoryPage);

    }

    private void addToLastPage(Item item) {

        this.lastPage.add(item);

        if (this.lastPage.size() >= 18) {

            this.pages.put(this.currentPage, this.lastPage);
            this.lastPage = new ArrayList<>();
            this.currentPage++;
        }
    }

    public void openInventory(Player player) {
        player.openInventory(this.inventory);
    }

    private void buildInventoryPage() {

        List<Item> currentPage = this.pages.get(this.currentPage);

        for (int i = 0; i < currentPage.size(); i++) {
            this.inventory.setItem(i,
                    createGuiTeleportItem(currentPage.get(i)));
        }

        if (this.currentPage > 0) {
            this.inventory.setItem(18, createGuiItem("Previous Page", Material.LANTERN, ItemType.BUTTON_PREVIOUS_PAGE));
        }

        if (this.currentPage + 1 < this.pages.size()) {
            this.inventory.setItem(26, createGuiItem("Next Page", Material.SOUL_LANTERN, ItemType.BUTTON_NEXT_PAGE));
        }

        if (this.isDepartureFavorite) {
            this.inventory.setItem(20,
                    createGuiItem("Remove from favorits.", Material.PAINTING, ItemType.TOGGLE_FAVORITE));
        } else {
            this.inventory.setItem(20,
                    createGuiItem("Add to favorits.", Material.ITEM_FRAME, ItemType.TOGGLE_FAVORITE));
        }

        if (this.isDepartureHome) {
            this.inventory.setItem(21, createGuiItem("Remove Home.", Material.SOUL_CAMPFIRE, ItemType.TOGGLE_HOME));
        } else {
            this.inventory.setItem(21, createGuiItem("Make Home.", Material.CAMPFIRE, ItemType.TOGGLE_HOME));
        }

        List<String> departureInfo = new ArrayList<>();

        departureInfo.add(ChatColor.GOLD + "Name: " + ChatColor.BLUE + this.departure.getName());
        departureInfo.add(ChatColor.GOLD + "Blueprint: " + ChatColor.BLUE + this.departure.getBluePrintId());

        Blueprint blueprint = BlueprintManager.blueprints.get(this.departure.getBluePrintId());
        departureInfo.add(ChatColor.GOLD + "Scope: " + ChatColor.BLUE + blueprint.getScope());
        departureInfo.add(ChatColor.GOLD + "Range: " + ChatColor.BLUE + (blueprint.getRange() == null ? "-" : blueprint.getRange()));

        departureInfo.add(ChatColor.GOLD + "Builder: " + ChatColor.BLUE + this.departure.getBuilder());
        departureInfo.add(ChatColor.GOLD + "Owner: " + ChatColor.BLUE + this.departure.getOwner());
        departureInfo.add(ChatColor.GOLD + "Creation Date: " + ChatColor.BLUE + this.departure.getCreationDate());
        
        this.inventory.setItem(23, createGuiItem("Departure Info", Material.BOOK, ItemType.INFO, departureInfo));

    }

    private ItemStack createGuiTeleportItem(Item item) {
        ItemStack itemStack = new ItemStack(item.material(), 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(item.name());

        List<String> lore = new ArrayList<>();

        switch (item.material()) {
            case IRON_DOOR:
                lore.add(ChatColor.DARK_GRAY + "Not Reachable.");
                item.reachableMessage.forEach(s -> lore.add(ChatColor.GOLD + s));
                break;

            case DIAMOND:
                lore.add("Favorite.");
                lore.add(ChatColor.DARK_PURPLE + "Click to teleport!");
                break;

            case CAMPFIRE:
                lore.add("Home.");
                lore.add(ChatColor.DARK_PURPLE + "Click to teleport!");
                break;

            case OAK_DOOR:
                lore.add(ChatColor.DARK_PURPLE + "Click to teleport!");
                break;

            default:
                break;
        }

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;

    }

    private ItemStack createGuiItem(String name, Material material, ItemType itemType, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    private ItemStack createGuiItem(String name, Material material, ItemType itemType) {
        ItemStack itemStack = new ItemStack(material, 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(name);

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public void nextPage() {
        if (this.currentPage + 1 >= this.pages.size()) {
            return;
        }

        this.inventory.clear();
        this.currentPage++;

        buildInventoryPage();
    }

    public void previousPage() {
        if (this.currentPage <= 0) {
            return;
        }

        this.inventory.clear();
        this.currentPage--;

        buildInventoryPage();
    }

    public void toggleHome() {
        UserTeleportStoneToggler userTeleportStoneToggler = new UserTeleportStoneToggler(app);
        userTeleportStoneToggler.toggleHome(user, departure.getName());

    }

    public void toggleFavorite() {
        UserTeleportStoneToggler userTeleportStoneToggler = new UserTeleportStoneToggler(app);
        userTeleportStoneToggler.toggleFavorite(user, departure.getName());
    }

    public void reload() {
        this.pages = new HashMap<>();
        this.currentPage = 0;
        this.lastPage = new ArrayList<>();
        this.isDepartureHome = false;
        this.isDepartureFavorite = false;

        this.inventory.clear();

        Bukkit.getScheduler().runTaskAsynchronously(app, this::create);
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public TeleportStone getDeparture() {
        return departure;
    }
}
