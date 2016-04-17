
package com.strongfellow.transactions.stomp.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@Component
public class TransactionPublisher {

    private static final Logger logger = LoggerFactory.getLogger(TransactionPublisher.class);

    @Scheduled(fixedDelay=500)
    public void demoPublisher() {
        logger.info("publishing");
    }
}
