package com.unbounce.scraper.provided.eventbroadcast;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unbounce.scraper.bootstrap.restricted.FailureMode;
import com.unbounce.scraper.provided.jsontool.JsonMapWriter;

/**
 * (Fake) Broadcasts messages to other services that may be interested.
 *
 * This implementation writes a file (events-[channelname].json) to disk, after every call.
 */
public class EventBroadcaster {

    private final Random random = new Random();
    private final List<String> events = new LinkedList<>();
    private final String broadcastChannelName;

    public EventBroadcaster(final String broadcastChannelName) {

        this.broadcastChannelName = broadcastChannelName;
    }

    public void broadcastEvent(final String event) throws IOException {
        FailureMode.maybeInjectEventBroadcastingFailure();
        events.add(event);
        saveToFile();
    }

    private void saveToFile() throws IOException {
        try (Writer out = new FileWriter("events-" + broadcastChannelName + ".json")) {
            try {
                final String str = new JsonMapWriter().writeJson(events);
                out.write(str);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException("Unable to serialize json", e);
            }
        }
    }
}
