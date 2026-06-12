package org.example;

import org.example.generator.DataGenerator;
import org.example.monitor.ConsoleMonitor;
import org.example.store.DataStore;

public class DataMonitorApp {

    public static void main(String[] args) throws InterruptedException {
        DataStore store = new DataStore();
        DataGenerator generator = new DataGenerator(store);
        ConsoleMonitor monitor = new ConsoleMonitor(store);

        generator.init();
        monitor.start();
        generator.startAutoUpdate();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            generator.stop();
            monitor.stop();
            System.out.println("\n모니터링을 종료합니다.");
        }));

        Thread.currentThread().join();
    }
}
