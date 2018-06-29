package com.unbounce.scraper.provided.messagequeuereader;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unbounce.scraper.MessageHandler;
import com.unbounce.scraper.bootstrap.restricted.Message;
import com.unbounce.scraper.bootstrap.restricted.MessageQueue;

/**
 * Reads messages from a MessageQueue, and dispatches them to a MessageHandler.
 *
 * Messages read from the queue are not consumed, but are "invisible" to consumers
 * for an amount of time configured on the queue. If that time expires without a
 * message being deleted, it will be made visible again to consumers.
 *
 * After reading a message successfully processing it, it is deleted.
 */
public class MessageQueueReader implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueueReader.class);

    private final MessageHandler messageHandler;
    private final ExecutorService messageExecutor;
    private final MessageQueue messageQueue;
    private final int threads;
    private volatile boolean running = true;
    private final CountDownLatch completionLatch;

    public MessageQueueReader(final MessageQueue messageQueue,
                              final MessageHandler messageHandler) {
        // init with 2 threads by default
        this(messageQueue, messageHandler, 2);
    }

    public MessageQueueReader(final MessageQueue messageQueue,
                              final MessageHandler messageHandler,
                              final int threads) {
        this.messageHandler = messageHandler;
        this.messageExecutor = Executors.newFixedThreadPool(threads);
        this.messageQueue = messageQueue;
        this.threads = threads;
        this.completionLatch = new CountDownLatch(threads);
    }

    public void start() {
        for (int i = 0; i < threads; ++i) {
            messageExecutor.submit(this);
        }
    }

    @SuppressWarnings("unchecked")
    public void run() {
        try {
            while (running) {
                try {
                    final Message message = messageQueue.readMessage();
                    if (message == null) {
                        LOGGER.info("No message found");
                        try {
                            Thread.sleep(1000);
                        } catch (final InterruptedException e) {
                            throw new RuntimeException();
                        }
                    } else {
                        messageHandler.handleMessage(message.getBody());

                        messageQueue.deleteMessage(message.getId().toString());
                    }
                } catch (final NoSuchElementException e) {
                    LOGGER.error("Error deleting message", e);
                } catch (final Exception e) {
                    LOGGER.error("Error processing message", e);
                }
            }
        } finally {
            completionLatch.countDown();
        }
    }


    public void stop() {
        running = false;
        messageExecutor.shutdown();
    }

    public void awaitComplete() {
        try {
            completionLatch.await();
        } catch (final InterruptedException e) {
            return;
        }
    }
}
