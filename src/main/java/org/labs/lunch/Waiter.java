package org.labs.lunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class Waiter implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Waiter.class);

    private final int id;
    private final Restaurant restaurant;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final long minGetSoupTime = 5;
    private final long maxGetSoupTime = 10;

    public Waiter(int waiterId, Restaurant restaurant) {
        this.id = waiterId;
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (restaurant.isRunning()) {
                Programmer programmer = restaurant.getPortionsAskQueue().take();

                if (!restaurant.isRunning()) break;

                int currentPortions;
                do {
                    currentPortions = restaurant.getPortionsCount().get();
                    if (currentPortions <= 0) {
                        break;
                    }
                } while (!restaurant.getPortionsCount().compareAndSet(currentPortions, currentPortions - 1));

                if (currentPortions > 0) {
                    Thread.sleep(random.nextLong(minGetSoupTime, maxGetSoupTime));
                    programmer.refillPlateWithSoup();
                    logger.debug("Waiter {} served programmer {}", id, programmer.getId());
                } else {
                    synchronized (programmer.getPlateMonitor()) {
                        programmer.getPlateMonitor().notifyAll();
                    }
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            logger.debug("Waiter {} interrupted", id);
        } catch (Exception ex) {
            logger.error("Waiter {} encountered unexpected error", id, ex);
        }
    }
}