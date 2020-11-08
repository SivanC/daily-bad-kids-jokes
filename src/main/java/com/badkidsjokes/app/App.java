package com.badkidsjokes.app;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.time.*;

/**
 * Runs the joke sending app
 *
 * @version 0.0.1
 * @author Sivan Cooperman
 */
public class App {
    public static void main(String[] args) throws MessagingException, InterruptedException {
        while ( true ) {
            LocalTime startup = LocalTime.now();
            LocalTime jokeTime = LocalTime.of(17, 30);
            Duration wait = Duration.between(startup, jokeTime);
            if (wait.isNegative()) {
                wait = Duration.ofHours(24).minus(Duration.between(jokeTime, startup));
            }
            System.out.println("Waiting for " + wait.toString() + "...");
            Thread.sleep(wait.toMillis());
            JokeGetter jokeGetter = new JokeGetter();
            JokeSender jokeSender = new JokeSender("src/main/resources/data.txt");

            String joke = jokeGetter.getJoke();
            jokeSender.sendMessage(joke);
            Thread.sleep(120000);
        }
    }
}
