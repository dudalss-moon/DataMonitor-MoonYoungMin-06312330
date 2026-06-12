package org.example.store;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataEntry {

    public enum Status { ACTIVE, WARNING, ERROR, INACTIVE }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String id;
    private final String key;
    private volatile String value;
    private volatile Status status;
    private volatile LocalDateTime updatedAt;
    private volatile long updateCount;

    public DataEntry(String id, String key, String value, Status status) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        this.updateCount = 0;
    }

    public void update(String value, Status status) {
        this.value = value;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        this.updateCount++;
    }

    public String getId()          { return id; }
    public String getKey()         { return key; }
    public String getValue()       { return value; }
    public Status getStatus()      { return status; }
    public long getUpdateCount()   { return updateCount; }
    public String getUpdatedAtStr(){ return updatedAt.format(FMT); }
}
