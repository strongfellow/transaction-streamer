/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.strongfellow.transactions.stomp.components;

import org.apache.commons.codec.binary.Hex;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.GetRecordsRequest;
import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.GetShardIteratorRequest;
import com.amazonaws.services.kinesis.model.GetShardIteratorResult;
import com.amazonaws.services.kinesis.model.Record;
import com.amazonaws.services.kinesis.model.Shard;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author andrew
 */
@Component
public class KinesisTransactionSource implements TransactionSource {
    
    private final String streamName = "strongfellow-us-west-2-dev-giraffe";
    private final AmazonKinesisClient kinesis;
    
    private final Logger logger = LoggerFactory.getLogger(KinesisTransactionSource.class);
    
    private final BlockingQueue<String> q = new ArrayBlockingQueue<>(100);
    
    
    public KinesisTransactionSource() {
        List<Shard> shards = new ArrayList<>();
        this.kinesis = new AmazonKinesisClient();
        this.kinesis.setRegion(Region.getRegion(Regions.US_WEST_2));
        DescribeStreamRequest request = new DescribeStreamRequest();
        request.setStreamName(this.streamName);
        String shard = null;
        while (true) {
            request.setExclusiveStartShardId(shard);
            DescribeStreamResult result = kinesis.describeStream(request);
            shards.addAll(result.getStreamDescription().getShards());
            if (result.getStreamDescription().getHasMoreShards() && shards.size() > 0) {
                shard = shards.get(shards.size() - 1).getShardId();
            } else {
                break;
            }
        }
        
        for (Shard s : shards) {
            GetShardIteratorRequest gsiRequest = new GetShardIteratorRequest();
            gsiRequest.setStreamName(this.streamName);
            gsiRequest.setShardId(s.getShardId());
            gsiRequest.setShardIteratorType("LATEST");
            GetShardIteratorResult getShardIteratorResult = kinesis.getShardIterator(gsiRequest);
            String shardIterator = getShardIteratorResult.getShardIterator();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            GetRecordsRequest grRequest = new GetRecordsRequest();
                            grRequest.setShardIterator(shardIterator);
                            GetRecordsResult getRecordsResult = kinesis.getRecords(grRequest);
                            List<Record> records = getRecordsResult.getRecords();
                            logger.info("we got records: " + records.size());
                            for (Record r : records) {
                                String data = Hex.encodeHexString(r.getData().array());
                                q.put(data);                        
                            }
                            if (records.size() == 0) {
                                Thread.sleep(1000);
                            }
                        } catch (InterruptedException ex) {
                            logger.error("problem putting to queue", ex);
                        }
                    }
                }
            }).start();
        }
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
