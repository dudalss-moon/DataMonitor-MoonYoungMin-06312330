package org.example.store;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class DataStore {

    private final ConcurrentHashMap<String, DataEntry> entries = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Consumer<DataEntry>> listeners = new CopyOnWriteArrayList<>();

    public void put(DataEntry entry) {
        entries.put(entry.getId(), entry);
        notifyListeners(entry);
    }

    public void update(String id, String value, DataEntry.Status status) {
        DataEntry entry = entries.get(id);
        if (entry != null) {
            entry.update(value, status);
            notifyListeners(entry);
        }
    }

    public Collection<DataEntry> getAll() {
        List<DataEntry> list = new ArrayList<>(entries.values());
        list.sort(Comparator.comparing(DataEntry::getKey));
        return list;
    }

    public int count()          { return entries.size(); }
    public int countByStatus(DataEntry.Status s) {
        return (int) entries.values().stream().filter(e -> e.getStatus() == s).count();
    }

    public void addListener(Consumer<DataEntry> listener) {
        listeners.add(listener);
    }

    private void notifyListeners(DataEntry entry) {
        listeners.forEach(l -> l.accept(entry));
    }
}
