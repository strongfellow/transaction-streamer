package com.strongfellow.transactions.stomp;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.strongfellow.transactions.stomp.data.Utils;
import com.strongfellow.transactions.stomp.data.Utils.TransactionMessage;

@Controller
public class TransactionPostController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionPostController.class);

    private final MessageSendingOperations<String> messagingTemplate;

    @Autowired
    public TransactionPostController(final MessageSendingOperations<String> messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    @RequestMapping(value = "/transactions/{hash}", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<String> generateReport(@PathVariable String hash,
            HttpServletRequest request) throws Exception {
        logger.info("received transaction: " + hash);
        InputStream inputStream = request.getInputStream();
        TransactionMessage message = Utils.parse(inputStream);
        inputStream.close();
        if (!message.hash.equals(hash)) {
            throw new Exception("hashes didn't match");
        }
        logger.info("publishing transaction: " + message.hash);
        this.messagingTemplate.convertAndSend("/topic/transactions", message.hash);                
        return new ResponseEntity<String>(HttpStatus.ACCEPTED);
    }
}
