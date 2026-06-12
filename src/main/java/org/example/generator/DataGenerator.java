package org.example.generator;

import org.example.store.DataEntry;
import org.example.store.DataEntry.Status;
import org.example.store.DataStore;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataGenerator {

    private static final List<String[]> SAMPLE_KEYS = List.of(
            new String[]{"CPU_USAGE",        "서버 CPU 사용률"},
            new String[]{"MEMORY_USAGE",     "서버 메모리 사용률"},
            new String[]{"DISK_USAGE",       "디스크 사용률"},
            new String[]{"NETWORK_IN",       "네트워크 수신"},
            new String[]{"NETWORK_OUT",      "네트워크 송신"},
            new String[]{"DB_CONN_POOL",     "DB 커넥션 풀"},
            new String[]{"ACTIVE_SESSIONS",  "활성 세션 수"},
            new String[]{"QUEUE_SIZE",       "메시지 큐 크기"},
            new String[]{"CACHE_HIT_RATE",   "캐시 적중률"},
            new String[]{"ERROR_RATE",       "오류율"},
            new String[]{"RESPONSE_TIME",    "평균 응답시간"},
            new String[]{"BATCH_STATUS",     "배치 작업 상태"}
    );

    private final DataStore store;
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public DataGenerator(DataStore store) {
        this.store = store;
    }

    public void init() {
        for (int i = 0; i < SAMPLE_KEYS.size(); i++) {
            String id = String.format("D%03d", i + 1);
            String[] entry = SAMPLE_KEYS.get(i);
            store.put(new DataEntry(id, entry[0], generateValue(entry[0]), randomStatus(30)));
        }
    }

    public void startAutoUpdate() {
        scheduler.scheduleAtFixedRate(this::updateRandom, 500, 800, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    private void updateRandom() {
        int idx = random.nextInt(SAMPLE_KEYS.size());
        String id = String.format("D%03d", idx + 1);
        String key = SAMPLE_KEYS.get(idx)[0];
        // ERROR 확률 10%, WARNING 20%, 나머지 ACTIVE
        int roll = random.nextInt(100);
        Status status = roll < 10 ? Status.ERROR : roll < 30 ? Status.WARNING : Status.ACTIVE;
        store.update(id, generateValue(key), status);
    }

    private String generateValue(String key) {
        return switch (key) {
            case "CPU_USAGE"       -> random.nextInt(101) + "%";
            case "MEMORY_USAGE"    -> random.nextInt(101) + "%";
            case "DISK_USAGE"      -> random.nextInt(101) + "%";
            case "NETWORK_IN"      -> (random.nextInt(1000)) + " MB/s";
            case "NETWORK_OUT"     -> (random.nextInt(500)) + " MB/s";
            case "DB_CONN_POOL"    -> random.nextInt(200) + "/200";
            case "ACTIVE_SESSIONS" -> String.valueOf(random.nextInt(5000));
            case "QUEUE_SIZE"      -> String.valueOf(random.nextInt(10000));
            case "CACHE_HIT_RATE"  -> String.format("%.1f%%", 60 + random.nextDouble() * 39);
            case "ERROR_RATE"      -> String.format("%.2f%%", random.nextDouble() * 5);
            case "RESPONSE_TIME"   -> (random.nextInt(500) + 10) + " ms";
            case "BATCH_STATUS"    -> random.nextBoolean() ? "RUNNING" : "IDLE";
            default                -> "N/A";
        };
    }

    private Status randomStatus(int warningPct) {
        int roll = random.nextInt(100);
        if (roll < 10) return Status.ERROR;
        if (roll < warningPct) return Status.WARNING;
        return Status.ACTIVE;
    }
}
