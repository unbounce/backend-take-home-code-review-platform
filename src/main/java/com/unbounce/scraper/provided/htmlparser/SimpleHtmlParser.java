package com.unbounce.scraper.provided.htmlparser;

import org.jsoup.Jsoup;

/**
 * Provides a simplified interface to JSoup for parsing HTML.
 *
 * Returns a Document class specific to this coding exercise, not the JSoup Document class.
 */
public class SimpleHtmlParser {
    public Document parseDocument(final String html) {
        final org.jsoup.nodes.Document doc = Jsoup.parse(html);
        return new Document(doc);
    }
}
