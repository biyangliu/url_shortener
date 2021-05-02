package com.biyang.url_shortener.service;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This will generate a unique ID based on current timestamp, server identifier
 * and a self incrementing sequence integer. Total 48 bits = 31 bits timestamp +
 * 5 bits server id + 12 bits sequence integer (can be further adjusted)
 *
 * @author byliu
 */
@Service
public class UniqueIdGenerator {

    /**
     * Fri Apr 30 2021 07:00:00 GMT+0000, fine to 0.1 seconds. Timestamp is
     * calculated as the delta between currentTime and START_TIMESTAMP. This can
     * last about 3.4 years before going backwards.
     */
    private final static long START_TIMESTAMP = 16197660000L;

    private final static long SEQUENCE_BIT = 12;
    private final static long SERVER_BIT = 5;
    private final static long TOTAL_BITS = 48;

    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    private final static long MAX_SERVER_NUM = -1L ^ (-1L << SERVER_BIT);

    private final static long SERVER_LEFT = SEQUENCE_BIT;
    private final static long TIMESTAMP_LEFT = SERVER_LEFT + SERVER_BIT;

    @Value("${server.index}")
    private int serverIndex;

    private static long lastTimestamp = START_TIMESTAMP;
    private static AtomicLong incrementalLong = new AtomicLong();

    public UniqueIdGenerator() {
        if (serverIndex > MAX_SERVER_NUM) {
            throw new RuntimeException("Server Index is greater than max server index allowed!");
        }
    }

    public long getUniqueId() {
        long curSequence;
        long curTimestamp = System.currentTimeMillis() / 100L;
        long tmp = lastTimestamp;
        if (curTimestamp == tmp) {
            curSequence = incrementalLong.incrementAndGet();
            curSequence %= MAX_SEQUENCE;
            // We have used up all the available sequence for this 0.1 seconds, then wait
            // for next timestamp.
            if (curSequence == 0) {
                while (curTimestamp == tmp) {
                    curTimestamp = System.currentTimeMillis() / 100L;
                }
            }
        } else {
            curSequence = incrementalLong.getAndSet(0L);
        }

        // Compare and set last timestamp. If not successful, we ignore it.
        if (tmp == lastTimestamp) {
            lastTimestamp = curTimestamp;
        }

        long res = ((curTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT | serverIndex << SERVER_LEFT | curSequence)
                & (-1L ^ (-1L << TOTAL_BITS));
//		System.out.format("Timestamp %d, Server Index %d, Cur Sequence %d, ID: %d\n", curTimestamp, serverIndex,
//				curSequence, res);
        return res;
    }
}
