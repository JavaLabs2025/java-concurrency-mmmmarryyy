package org.labs.lunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

public class Programmer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Programmer.class);

    private final int id;
    private int portionsEaten = 0;
    private volatile boolean hasSoupPortion;
    private final Restaurant restaurant;
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final Object plateMonitor = new Object();

    private final long minThinkTime = 20;
    private final long maxThinkTime = 40;
    private final long minEatTime = 10;
    private final long maxEatTime = 20;

    public Programmer(int programmerId, Restaurant restaurant) {
        this.id = programmerId;
        this.hasSoupPortion = false;
        this.restaurant = restaurant;
    }

    @Override
    public void run() {
        try {
            while (restaurant.isRunning() && (restaurant.getPortionsCount().get() > 0 || hasSoupPortion)) {
                think();

                synchronized (plateMonitor) {
                    while (!hasSoupPortion && restaurant.isRunning()) {
                        if (restaurant.getPortionsCount().get() == 0) {
                            return;
                        }

                        restaurant.requestPortion(this);
                        plateMonitor.wait();
                    }
                }

                restaurant.getDinningTable().takeSpoons(id);
                eat();
                restaurant.getDinningTable().putSpoons(id);
            }
        } catch (InterruptedException ex) {
            logger.debug("Programmer {} interrupted", id);
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            logger.error("Programmer {} encountered unexpected exception", id, ex);
        }
    }

    private void think() throws InterruptedException {
        long duration = random.nextLong(minThinkTime, maxThinkTime);
        logger.debug("Programmer {} thinking for {}ms", id, duration);
        Thread.sleep(duration);
    }

    private void eat() throws InterruptedException {
        long duration = random.nextLong(minEatTime, maxEatTime);
        logger.debug("Programmer {} eating for {}ms", id, duration);
        Thread.sleep(duration);

        hasSoupPortion = false;
        portionsEaten += 1;
    }

    public void refillPlateWithSoup() {
        synchronized (plateMonitor) {
            hasSoupPortion = true;
            plateMonitor.notifyAll();
        }
    }


    public int getId() { return id; }
    public int getPortionsEaten() { return portionsEaten; }
    public Object getPlateMonitor() { return plateMonitor; }
}
