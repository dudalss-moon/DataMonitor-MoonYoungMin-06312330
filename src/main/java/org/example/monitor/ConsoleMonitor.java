package org.example.monitor;

import org.example.store.DataEntry;
import org.example.store.DataEntry.Status;
import org.example.store.DataStore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ConsoleMonitor {

    // ANSI 색상 코드
    private static final String RESET   = "[0m";
    private static final String BOLD    = "[1m";
    private static final String RED     = "[31m";
    private static final String GREEN   = "[32m";
    private static final String YELLOW  = "[33m";
    private static final String BLUE    = "[34m";
    private static final String CYAN    = "[36m";
    private static final String WHITE   = "[37m";
    private static final String BG_DARK = "[40m";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int REFRESH_SECONDS = 2;

    private final DataStore store;
    private final AtomicLong eventCount = new AtomicLong(0);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ConsoleMonitor(DataStore store) {
        this.store = store;
        store.addListener(entry -> eventCount.incrementAndGet());
    }

    public void start() {
        clearScreen();
        scheduler.scheduleAtFixedRate(this::render, 0, REFRESH_SECONDS, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    private void render() {
        moveCursorHome();
        Collection<DataEntry> entries = store.getAll();

        printHeader();
        printStats(entries);
        printDivider();
        printTableHeader();
        printDivider();
        entries.forEach(this::printRow);
        printDivider();
        printFooter();
    }

    private void printHeader() {
        String time = LocalDateTime.now().format(FMT);
        System.out.println(BOLD + CYAN + "╔══════════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(BOLD + CYAN + "║" + RESET + "          " + BOLD + WHITE + "DataMonitor  —  실시간 데이터 상태 조회" + RESET + "          " + CYAN + "║" + RESET);
        System.out.printf( CYAN + "║" + RESET + "  시각: %-55s" + CYAN + "║%n" + RESET, time);
        System.out.println(BOLD + CYAN + "╚══════════════════════════════════════════════════════════════════╝" + RESET);
    }

    private void printStats(Collection<DataEntry> entries) {
        int total    = store.count();
        int active   = store.countByStatus(Status.ACTIVE);
        int warning  = store.countByStatus(Status.WARNING);
        int error    = store.countByStatus(Status.ERROR);
        int inactive = store.countByStatus(Status.INACTIVE);

        System.out.println();
        System.out.printf("  전체: " + BOLD + WHITE + "%-4d" + RESET +
                        "  " + GREEN  + "● ACTIVE %-4d" + RESET +
                        "  " + YELLOW + "● WARNING %-4d" + RESET +
                        "  " + RED    + "● ERROR %-4d" + RESET +
                        "  " + WHITE  + "● INACTIVE %-4d" + RESET +
                        "  이벤트: " + CYAN + "%d%n" + RESET,
                total, active, warning, error, inactive, eventCount.get());
        System.out.println();
    }

    private void printDivider() {
        System.out.println(BLUE + "  " + "─".repeat(64) + RESET);
    }

    private void printTableHeader() {
        System.out.printf("  " + BOLD + WHITE + "%-6s  %-20s  %-18s  %-10s  %6s  %-8s%n" + RESET,
                "ID", "KEY", "VALUE", "STATUS", "갱신수", "갱신시각");
    }

    private void printRow(DataEntry e) {
        String statusColor = switch (e.getStatus()) {
            case ACTIVE   -> GREEN;
            case WARNING  -> YELLOW;
            case ERROR    -> RED;
            case INACTIVE -> WHITE;
        };
        String statusLabel = switch (e.getStatus()) {
            case ACTIVE   -> "● ACTIVE";
            case WARNING  -> "▲ WARNING";
            case ERROR    -> "✖ ERROR";
            case INACTIVE -> "○ INACTIVE";
        };

        String value = truncate(e.getValue(), 18);
        System.out.printf("  %-6s  %-20s  %-18s  %s%-10s%s  %6d  %-8s%n",
                e.getId(),
                truncate(e.getKey(), 20),
                value,
                statusColor, statusLabel, RESET,
                e.getUpdateCount(),
                e.getUpdatedAtStr());
    }

    private void printFooter() {
        System.out.println();
        System.out.println("  " + CYAN + "새로고침 주기: " + REFRESH_SECONDS + "초" + RESET +
                           "   종료: " + YELLOW + "Ctrl+C" + RESET);
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void moveCursorHome() {
        System.out.print("\033[H");
        System.out.flush();
    }
}
