package org.labs.lunch;

public class DinningTable {

    private final int programmersCount;
    private final Spoon[] spoons;

    public DinningTable(int programmersCount) {
        this.programmersCount = programmersCount;

        this.spoons = new Spoon[programmersCount];
        for (int i = 0; i < programmersCount; i++) {
            this.spoons[i] = new Spoon(i);
        }

    }

    private class SpoonPair {
        int firstSpoonId;
        int secondSpoonId;

        SpoonPair(int firstSpoonId, int secondSpoonId) {
            this.firstSpoonId = firstSpoonId;
            this.secondSpoonId = secondSpoonId;
        }
    }

    private SpoonPair getOrderedSpoonIds(int programmerId) {
        int leftSpoonId = programmerId;
        int rightSpoonId = (programmerId + 1) % programmersCount;

        if (leftSpoonId < rightSpoonId) {
            return new SpoonPair(leftSpoonId, rightSpoonId);
        } else {
            return new SpoonPair(rightSpoonId, leftSpoonId);
        }
    }

    void takeSpoons(int programmerId) {
        SpoonPair spoonPair = getOrderedSpoonIds(programmerId);

        spoons[spoonPair.firstSpoonId].lock();
        spoons[spoonPair.secondSpoonId].lock();
    }

    void putSpoons(int programmerId) {
        SpoonPair spoonPair = getOrderedSpoonIds(programmerId);

        spoons[spoonPair.secondSpoonId].unlock();
        spoons[spoonPair.firstSpoonId].unlock();
    }
}
