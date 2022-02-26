package kumomi.teleportstones.storage.filestorage;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import kumomi.teleportstones.storage.StorageInstance;

public class FileStorageInstance<K, V> implements StorageInstance<K, V> {

    private final Map<K, V> storage;
    private StorageStatus status;
    private String message;

    public FileStorageInstance(Map<K, V> storage) {
        this.storage = storage;
        this.status = StorageStatus.NOT_RUN;
        this.message = "Not run.";
    }

    public final Collection<V> findAll() {
        this.status = StorageStatus.SUCCESS;
        this.message = "Was found.";
        return storage.values();
    }

    public final Optional<V> find(K key) {

        V entry = storage.get(key);

        if (entry == null) {
            this.status = StorageStatus.NOT_FOUND;
            this.message = "Couldn't be found.";
        } else {
            this.status = StorageStatus.SUCCESS;
            this.message = entry.getClass() + " was found.";
        }

        return Optional.ofNullable(entry);
    }

    public final boolean add(K key, V value) {
        if (storage.containsKey(key)) {
            this.status = StorageStatus.MULTIPLE_ENTRIES;
            this.message = "Entry already exists.";
            return false;
        }

        storage.put(key, value);
        this.status = StorageStatus.SUCCESS;
        this.message = "Was added.";
        return true;
    }

    public final Optional<V> remove(K key) {

        V entry = storage.remove(key);

        if (entry == null) {
            this.status = StorageStatus.NOT_FOUND;
            this.message = "Couldn't be found.";
        } else {
            this.status = StorageStatus.SUCCESS;
            this.message = entry.getClass() + " was removed.";
        }

        return Optional.ofNullable(entry);
    }

    public final Optional<V> update(K key, V value) {
        if (!storage.containsKey(key)) {
            this.status = StorageStatus.NOT_FOUND;
            this.message = "Couldn't find entry for update.";
            return Optional.empty();
        }

        V updatedEntry = storage.put(key, value);

        if (updatedEntry == null) {
            this.status = StorageStatus.ERROR;
            this.message = "Couldn't update entry.";
        } else {
            this.status = StorageStatus.SUCCESS;
            this.message = updatedEntry.getClass() + " was updated.";
        }

        return Optional.ofNullable(updatedEntry);
    }

    @Override
    public StorageStatus getStatus() {
        return this.status;
    }

    @Override
    public String getStatusMessage() {
        return message;
    }
}
