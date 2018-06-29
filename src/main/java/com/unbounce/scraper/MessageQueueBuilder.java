package com.unbounce.scraper;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.unbounce.scraper.bootstrap.restricted.FailureMode;
import com.unbounce.scraper.bootstrap.restricted.Message;
import com.unbounce.scraper.bootstrap.restricted.MessageGenerator;
import com.unbounce.scraper.bootstrap.restricted.MessageMode;
import com.unbounce.scraper.bootstrap.restricted.MessageQueue;

/**
 * DON'T CHANGE THIS CLASS.
 * This class is intended to simulate the deliver of messages from a Queueing service to the application.
 */
public class MessageQueueBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageQueueBuilder.class);

    private final MessageMode messageMode;
    private final MessageGenerator messageGenerator = new MessageGenerator();

    public MessageQueueBuilder(final MessageMode messageMode) {
        this.messageMode = messageMode;
    }

    @SuppressWarnings("unchecked")
    public MessageQueue createMessageQueue(final long visibilityTimeout) {
        final List<String> urls;
        try {
            final String urlsString = Resources.toString(Resources.getResource(
                "com/unbounce/scraper/bootstrap/urls.lst"), Charsets.UTF_8);
            // Parsing out the URLs, bypassing comments prefixed with #
            urls = Arrays.stream(urlsString.split("[\n\r]+"))
                         .map(url -> url.replaceFirst(" *#.*$", ""))
                         .collect(Collectors.toList());
        } catch (final IOException e) {
            throw new RuntimeException("Could not read url list", e);
        }

        final List<Message> messages = new LinkedList<>();
        for (final Map<String, Object> message : messageGenerator.generateMessages(messageMode, urls)) {
            messages.add(new Message(UUID.randomUUID(), convertMessageToString(message)));
        }
        return new MessageQueue(messages, visibilityTimeout);
    }

    private String convertMessageToString(final Map<String, Object> message) {
        try {
            return new ObjectMapper().writeValueAsString(message);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize json", e);
        }
    }
}
