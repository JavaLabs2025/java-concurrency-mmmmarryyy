package org.labs;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.labs.lunch.Restaurant;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);

        int programmersCount = 7;
        int waitersCount = 2;
        int portionsCount = 5_000;
        int timeout = 60;

        Options options = new Options();
        options.addOption("p", "programmers", true, "Number of programmers");
        options.addOption("w", "waiters", true, "Number of waiters");
        options.addOption("f", "food", true, "Total portions count");
        options.addOption("t", "timeout", true, "Timeout in seconds");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("p")) {
                programmersCount = Integer.parseInt(cmd.getOptionValue("p"));
            }
            if (cmd.hasOption("w")) {
                waitersCount = Integer.parseInt(cmd.getOptionValue("w"));
            }
            if (cmd.hasOption("f")) {
                portionsCount = Integer.parseInt(cmd.getOptionValue("f"));
            }
            if (cmd.hasOption("t")) {
                timeout = Integer.parseInt(cmd.getOptionValue("t"));
            }
        } catch (ParseException e) {
            logger.error("Error parsing arguments", e);
            new HelpFormatter().printHelp("dining-philosophers", options);
            return;
        }

        logger.info(
                "Starting simulation with {} programmers, {} waiters, {} portions",
                programmersCount,
                waitersCount,
                portionsCount
        );

        Restaurant restaurant = new Restaurant(programmersCount, waitersCount, portionsCount);
        restaurant.start();

        try {
            if (!restaurant.awaitCompletion(timeout, TimeUnit.SECONDS)) {
                logger.error("Simulation timed out!");
                restaurant.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            restaurant.shutdownNow();
        } finally {
            restaurant.printStatistics();
        }
    }
}