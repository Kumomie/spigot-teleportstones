package kumomi.teleportstones.storage;

import java.util.Map;
import java.util.UUID;

import kumomi.teleportstones.configuration.Configuration;
import kumomi.teleportstones.storage.filestorage.FileStorageInstance;
import kumomi.teleportstones.storage.model.TeleportStone;
import kumomi.teleportstones.storage.model.User;

public class StorageFactory {

    private static Map<String, TeleportStone> teleportstoneStorage;
    private static Map<UUID, User> userStorage;
    private static Map<String, TeleportStone> teleportstoneRequestStorage;
    private static Configuration configuration;

    public static boolean init(Map<UUID, User> userStorage, Map<String, TeleportStone> teleportstoneStorage,
            Map<String, TeleportStone> teleportstoneRequestStorage, Configuration configuration) {
        StorageFactory.configuration = configuration;
        StorageFactory.teleportstoneStorage = teleportstoneStorage;
        StorageFactory.teleportstoneRequestStorage = teleportstoneRequestStorage;
        StorageFactory.userStorage = userStorage;
        return true;
    }

    public static StorageInstance<UUID, User> getCrudUserStorage() {
        return new FileStorageInstance<>(userStorage);
    }

    public static StorageInstance<String, TeleportStone> getCrudTeleportStoneStorage() {
        return new FileStorageInstance<>(teleportstoneStorage);
    }

    public static StorageInstance<String, TeleportStone> getCrudTeleportStoneRequestStorage() {
        return new FileStorageInstance<>(teleportstoneRequestStorage);
    }

}
