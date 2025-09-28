package org.labs.lunch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class FairnessTest {

    @Test
    @Timeout(30)
    void testFairDistributionWithSevenProgrammers() throws InterruptedException {
        int programmersCount = 7;
        int waitersCount = 2;
        int portionsCount = 1_000;

        Restaurant restaurant = new Restaurant(programmersCount, waitersCount, portionsCount);
        restaurant.start();
        restaurant.awaitCompletion(10, TimeUnit.SECONDS);

        int totalEaten = restaurant.getProgrammersList().stream().mapToInt(Programmer::getPortionsEaten).sum();

        assertEquals(portionsCount, totalEaten);

        double average = totalEaten / (double) programmersCount;
        double expectedMin = average * 0.05;
        double expectedMax = average * 1.05;

        for (var programmer : restaurant.getProgrammersList()) {
            int eaten = programmer.getPortionsEaten();

            assertTrue(
                    eaten >= expectedMin,
                    String.format("Programmer %d ate too little: %d < %.1f", programmer.getId(), eaten, expectedMin)
            );
            assertTrue(
                    eaten <= expectedMax,
                    String.format("Programmer %d ate too much: %d > %.1f", programmer.getId(), eaten, expectedMax)
            );
        }
    }
}