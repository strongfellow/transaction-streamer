/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.strongfellow.transactions.stomp.components;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 *
 * @author andrew
 */
@Component
@Profile("test")
public class RandomTransactionSource implements TransactionSource {

    private static final Random RANDOM = new Random();
    private final Logger logger = LoggerFactory.getLogger(RandomTransactionSource.class);
    
    private final BlockingQueue<String> q = new ArrayBlockingQueue<>(100);

    public RandomTransactionSource() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(RANDOM.nextInt(5000));
                        q.put(UUID.randomUUID().toString());
                    } catch (InterruptedException ex) {
                        logger.error("problem putting to queue", ex);
                    }
                }
            }
        }).start();
    }
    
    @Override
    public String next() {
        try {
            return q.take();
        } catch (InterruptedException ex) {
            logger.error("problem taking from queue", ex);
            return null;
        }
    }
    
}
    
