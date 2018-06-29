package com.unbounce.scraper.provided.jsontool;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simplified interface to JSON parsing.
 *
 * Feel free not to use this.
 */
public class JsonMapParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Map<String, Object> parseJson(final String jsonString) {

        try {
            return MAPPER.readValue(jsonString, Map.class);
        } catch (final IOException e) {
            throw new RuntimeException("This shouldn't ever happen when parsing a string", e);
        }
    }
}
