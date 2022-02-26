package kumomi.teleportstones.storage;

import java.util.Collection;
import java.util.Optional;

public interface StorageInstance<K, V> {

    public static enum StorageStatus {
        SUCCESS, NOT_FOUND, ERROR, NOT_RUN, MULTIPLE_ENTRIES
    }

    public StorageStatus getStatus();
    public String getStatusMessage();

    public Collection<V> findAll();

    public Optional<V> find(K key);

    public boolean add(K key, V value);

    public Optional<V> remove(K key);

    public Optional<V> update(K key, V value);
}
