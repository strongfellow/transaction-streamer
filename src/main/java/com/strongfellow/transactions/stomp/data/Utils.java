package com.strongfellow.transactions.stomp.data;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

public class Utils {

    public static class TransactionMessage {
        public String host;
        public String network;
        public long timestamp;
        public byte[] tx;
        public String hash;
    }
    
    public static TransactionMessage parse(InputStream inputStream) throws IOException {
        TransactionMessage m = new TransactionMessage();
        m.timestamp = System.currentTimeMillis();
        byte[] tx = IOUtils.toByteArray(inputStream);
        m.tx = tx;
        try {
            MessageDigest md;
            md = MessageDigest.getInstance("SHA-256");
            md.update(m.tx);
            byte[] digest = md.digest();
            md.reset();
            md.update(digest);
            byte[] bs = md.digest();
            ArrayUtils.reverse(bs);
            m.hash = new String(Hex.encodeHex(bs));
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return m;
    }

}
