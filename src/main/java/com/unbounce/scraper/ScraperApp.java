package com.unbounce.scraper;

import com.unbounce.scraper.bootstrap.restricted.FailureMode;
import com.unbounce.scraper.bootstrap.restricted.MessageMode;
import com.unbounce.scraper.bootstrap.restricted.MessageQueue;
import com.unbounce.scraper.provided.database.DatabaseClient;
import com.unbounce.scraper.provided.eventbroadcast.EventBroadcaster;
import com.unbounce.scraper.provided.messagequeuereader.MessageQueueReader;

public final class ScraperApp {
    private ScraperApp() {
        // private utility class constructor
    }

    public static void main(final String[] args) {

        final String broadcastChannelName = "scraper-notifications";
        final String dbURL = "sql://localhost/scraper";

        final DatabaseClient databaseClient = new DatabaseClient(dbURL);
        final EventBroadcaster eventBroadcaster = new EventBroadcaster(broadcastChannelName);

        final MessageHandler messageHandler = new LoggingMessageHandler();

        final MessageQueueBuilder messageQueueBuilder = new MessageQueueBuilder(MessageMode.HAPPY_PATH);
        // Replace HAPPY_PATH with REALISTIC when you're ready to deal with error handling for some
        // messages that don't always exactly conform to the schema, or have other quirks.

        // A number of failure modes are available for various components, to aid in testing error
        // handling, retries, etc.
        FailureMode.SELECTED_MODE.set(FailureMode.NO_FAILURES);

        final MessageQueue messageQueue = messageQueueBuilder.createMessageQueue(10000);
        final MessageQueueReader messageQueueReader = new MessageQueueReader(
            messageQueue,
            messageHandler);
        messageQueueReader.start();
        messageQueue.awaitCompletion();
        messageQueueReader.stop();
        messageQueueReader.awaitComplete();
    }

}
