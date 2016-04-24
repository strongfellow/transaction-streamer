
package com.strongfellow.transactions.stomp.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.messaging.core.MessageSendingOperations;

@Component
public class TransactionPublisher {

    private TransactionSource transactionSource = null;
    
    @Autowired
    public void setTxSource(TransactionSource source) {
	this.transactionSource = source;
    }

    private final MessageSendingOperations<String> messagingTemplate;

    @Autowired
    public TransactionPublisher(final MessageSendingOperations<String> messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private static final Logger logger = LoggerFactory.getLogger(TransactionPublisher.class);

    
    @Scheduled(fixedDelay=1)
    public void demoPublisher() {
	String transaction = this.transactionSource.next();
        this.messagingTemplate.convertAndSend("/topic/transactions", transaction);
        logger.info("publishing");
    }
}
