package com.unbounce.scraper.provided.jsontool;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Simplified interface to JSON string generation.
 *
 * Feel free not to use this.
 */
public class JsonMapWriter {

    public String writeJson(final Map<String, Object> message) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(message);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize json", e);
        }
    }

    public String writeJson(final List<String> message) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(message);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize json", e);
        }
    }
}
