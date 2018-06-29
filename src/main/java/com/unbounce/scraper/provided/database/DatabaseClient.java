package com.unbounce.scraper.provided.database;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unbounce.scraper.bootstrap.restricted.FailureMode;
import com.unbounce.scraper.provided.jsontool.JsonMapWriter;

/**
 * (Fake) Writes page sizes to a database.
 *
 * This implementation writes a file (database.json) to disk, after every call.
 */
public class DatabaseClient {

    final List<Map<String, Object>> data = new LinkedList<>();
    private final String dbURL;

    public DatabaseClient(final String dbURL) {
        this.dbURL = dbURL;
    }

    /**
     * Transactionally record a page size found on a given domain.
     * @param domain The domain the page was from.
     * @param size The total size of the page
     * @throws IOException
     */
    public void recordPageSize(final String domain, final long size) throws IOException {
        FailureMode.maybeInjectDatabaseFailure();
        final Map<String, Object> item = new LinkedHashMap<>();
        item.put("domain", domain);
        item.put("size", size);
        data.add(item);

        saveToFile();
    }

    private void saveToFile() throws IOException {
        try (Writer out = new FileWriter("database.json")) {
            final Map<String, Object> database = new LinkedHashMap<>();
            database.put("url", dbURL);
            database.put("items", data);

            try {
                final String str = new JsonMapWriter().writeJson(database);
                out.write(str);
            } catch (final JsonProcessingException e) {
                throw new RuntimeException("Unable to serialize json", e);
            }
        }
    }
}
