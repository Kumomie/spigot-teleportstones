package kumomi.teleportstones.util;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import kumomi.teleportstones.storage.StorageInstance;
import kumomi.teleportstones.storage.StorageFactory;
import kumomi.teleportstones.storage.model.SimpleDiscoveredTeleportStone;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;

public class UserValidator {
    public static enum ValidationStatus {
        ERROR //
        , NOT_RUN //
        , MISSING_USER //
        , MISSING_UUID //
        , MISSING_NAME //
        , EMPTY_NAME //
        , UUID_ALREADY_EXITS //
        , NAME_ALREADY_EXISTS //
        , MISSING_LIST_DISCOVERED_TELEPORTSTONES //
        , MISSING_FAVORITS //
        , UNKNOWN_PLAYER //
        , UUID_NAME_NOT_MATCHING //
        , UNKNOWN_DISCOVERED_TELEPORTSTONE //
        , SUCCESS
    }

    private ValidationStatus status;
    private String message;

    public UserValidator() {
        this.status = ValidationStatus.NOT_RUN;
        this.message = "";
    }

    public boolean validateBeforeAdd(User user) {

        if (user == null) {
            this.status = ValidationStatus.MISSING_USER;
            this.message = "Missing user.";
            return false;
        }

        if (user.getUuid() == null) {
            this.status = ValidationStatus.MISSING_UUID;
            this.message = "Missing UUID.";
            return false;
        }

        if (user.getName() == null) {
            this.status = ValidationStatus.MISSING_NAME;
            this.message = "Missing name of user.";
            return false;
        }

        if (user.getName().isEmpty()) {
            this.status = ValidationStatus.EMPTY_NAME;
            this.message = "Empty name of user.";
            return false;
        }

        if (user.getDiscoveredTeleportStones() == null) {
            this.status = ValidationStatus.MISSING_LIST_DISCOVERED_TELEPORTSTONES;
            this.message = "Missing list of discovered TeleportStones.";
            return false;
        }

        if (user.getFavorits() == null) {
            this.status = ValidationStatus.MISSING_FAVORITS;
            this.message = "Missing list of favorite TeleportStones.";
            return false;
        }

        StorageInstance<UUID, User> storage = StorageFactory.getCrudUserStorage();

        Optional<User> oUser = storage.find(user.getUuid());

        if (oUser.isPresent()) {
            this.status = ValidationStatus.UUID_ALREADY_EXITS;
            this.message = "UUID of user already exists.";
            return false;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(user.getUuid());

        if (offlinePlayer == null) {
            this.status = ValidationStatus.UNKNOWN_PLAYER;
            this.message = "Player is unknown to server.";
            return false;
        }

        if (!offlinePlayer.hasPlayedBefore()) {
            this.status = ValidationStatus.UNKNOWN_PLAYER;
            this.message = "Player has never played on the server or is unknown.";
            return false;
        }

        if (!offlinePlayer.getName().equals(user.getName())) {
            this.status = ValidationStatus.UUID_NAME_NOT_MATCHING;
            this.message = "Player UUID and name do not match with user UUID and name.";
            return false;
        }

        this.status = ValidationStatus.SUCCESS;
        this.message = "User successfully validated.";
        return true;
    }

    public boolean validateAfterAdd(User user) {

        StorageInstance<String, TeleportStone> storage = StorageFactory.getCrudTeleportStoneStorage();

        for (SimpleDiscoveredTeleportStone st : user.getAllDiscoveredTeleportStones()) {

            if (!storage.find(st.getName()).isPresent()) {
                this.status = ValidationStatus.UNKNOWN_DISCOVERED_TELEPORTSTONE;
                this.message = "User has an unknown TeleportStone added to list of discovered TeleportStones.";
                return false;
            }
        }

        this.status = ValidationStatus.SUCCESS;
        this.message = "User successfully validated.";
        return true;
    }

    public String getMessage() {
        return message;
    }

    public ValidationStatus getStatus() {
        return status;
    }
}
