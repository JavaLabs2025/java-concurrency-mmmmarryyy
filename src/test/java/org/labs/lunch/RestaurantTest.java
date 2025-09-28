package org.labs.lunch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Test
    @Timeout(10)
    void testSingleProgrammerScenario() throws InterruptedException {
        int programmersCount = 1;
        int waitersCount = 1;
        int portionsCount = 10;

        Restaurant restaurant = new Restaurant(programmersCount, waitersCount, portionsCount);
        restaurant.start();
        restaurant.awaitCompletion(5, TimeUnit.SECONDS);

        assertEquals(portionsCount, restaurant.getProgrammersList().getFirst().getPortionsEaten());
        assertEquals(0, restaurant.getPortionsCount().get());
    }

    @Test
    @Timeout(10)
    void testInterruptionHandling() throws InterruptedException {
        int programmersCount = 3;
        int waitersCount = 1;
        int portionsCount = 1000;

        Restaurant restaurant = new Restaurant(programmersCount, waitersCount, portionsCount);
        restaurant.start();
        Thread.sleep(100);
        restaurant.shutdownNow();

        restaurant.awaitCompletion(2, TimeUnit.SECONDS);
        int totalEaten = restaurant.getProgrammersList().stream().mapToInt(Programmer::getPortionsEaten).sum();

        assertTrue(totalEaten < portionsCount);
        assertTrue(totalEaten > 0);
    }
}