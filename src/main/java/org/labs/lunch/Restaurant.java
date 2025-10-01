package org.labs.lunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurant {
    private static final Logger logger = LoggerFactory.getLogger(Restaurant.class);

    private final AtomicInteger portionsCount;
    private final PriorityBlockingQueue<Programmer> portionsAskQueue;

    private final List<Programmer> programmersList;
    private final ExecutorService programmersExecutor;

    private final int waitersCount;
    private final ExecutorService waitersExecutor;

    private final DinningTable dinningTable;

    private volatile boolean isRunning = true;

    public Restaurant(int programmersCount, int waiterCount, int portionsCount) {
        this.portionsCount = new AtomicInteger(portionsCount);
        this.portionsAskQueue = new PriorityBlockingQueue<>(
                programmersCount,
                Comparator.comparingInt(Programmer::getPortionsEaten)
        );
        this.programmersList = new ArrayList<>();
        this.programmersExecutor = Executors.newFixedThreadPool(programmersCount);
        this.waitersCount = waiterCount;
        this.waitersExecutor = Executors.newFixedThreadPool(waiterCount);
        this.dinningTable = new DinningTable(programmersCount);

        for (int i = 0; i < programmersCount; i++) {
            Programmer programmer = new Programmer(i, this);
            programmersList.add(programmer);
        }
    }

    public void start() {
        for (Programmer programmer : programmersList) {
            programmersExecutor.submit(programmer);
        }

        for (int i = 0; i < this.waitersCount; i++) {
            Waiter waiter = new Waiter(i, this);
            waitersExecutor.submit(waiter);
        }
    }

    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
        programmersExecutor.shutdown();
        boolean completed = programmersExecutor.awaitTermination(timeout, unit);
        isRunning = false;
        waitersExecutor.shutdownNow();
        return completed;
    }

    public void shutdownNow() {
        isRunning = false;
        programmersExecutor.shutdownNow();
        waitersExecutor.shutdownNow();
    }

    public void printStatistics() {
        int totalEaten = 0;
        for (Programmer programmer : programmersList) {
            int eaten = programmer.getPortionsEaten();
            logger.info("Programmer {} ate {} portions", programmer.getId(), eaten);
            totalEaten += eaten;
        }

        logger.info("Total portions eaten: {} (remaining: {})", totalEaten, portionsCount.get());

        double average = totalEaten / (double) programmersList.size();
        double fairnessThreshold = average * 0.01;

        for (Programmer programmer : programmersList) {
            int eaten = programmer.getPortionsEaten();
            double deviation = Math.abs(eaten - average);
            if (deviation > fairnessThreshold) {
                logger.warn("Programmer {} deviation too high: {} > {}", programmer.getId(), deviation, fairnessThreshold);
            }
        }
    }

    void requestPortion(Programmer programmer) throws InterruptedException {
        portionsAskQueue.put(programmer);
    }


    public AtomicInteger getPortionsCount() { return portionsCount; }
    public PriorityBlockingQueue<Programmer> getPortionsAskQueue() { return portionsAskQueue; }
    public List<Programmer> getProgrammersList() { return programmersList; }
    public DinningTable getDinningTable() { return dinningTable; }
    public boolean isRunning() { return isRunning; }
}
