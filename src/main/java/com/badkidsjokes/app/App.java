package com.badkidsjokes.app;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import java.time.*;

/**
 * Runs the joke sending app
 *
 * @version 0.1.4
 * @author Sivan Cooperman
 */
public class App {
    public static void main(String[] args) throws MessagingException, InterruptedException {
        int hour;
        int minute;

        // Try to get time from jar arguments
        try {
            hour = Integer.parseInt(args[0]);
            minute = Integer.parseInt(args[1]);
        } catch ( Exception e ) {
            hour = 13;
            minute = 00;
        }

        while ( true ) {
            Duration wait = getWait(hour, minute);

            System.out.println("Waiting for " + wait.toString() + "...");
            Thread.sleep(wait.toMillis());

            JokeGetter jokeGetter = new JokeGetter();
            JokeSender jokeSender = new JokeSender("src/main/resources/data.txt");

            String joke = jokeGetter.getJoke();
            jokeSender.sendText(joke);
            // Wait two minutes to avoid any shenanigans
            Thread.sleep(120000);
        }
    }

    /**
     * Gets a Duration between the current time and the specified hour and
     * minute, in 24-hour time.
     *
     * @param hour The hour in 24-hour time of the end of the duration.
     * @param minute The minute of the end of the duration.
     * @return A Duration the length of the time between the current time and
     * the specified time.
     */
    private static Duration getWait(int hour, int minute) {
        LocalTime startup = LocalTime.now();
        LocalTime jokeTime = LocalTime.of(hour, minute);
        Duration wait = Duration.between(startup, jokeTime);
        // Flip the time around if the duration is negative (e.g. it's 2pm and
        // the end time is 13:00).
        if (wait.isNegative()) {
            wait = Duration.ofHours(24).minus(Duration.between(jokeTime, startup));
        }

        return wait;
    }
}
