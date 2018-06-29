package com.unbounce.scraper.provided.htmlparser;

import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Element;

public class Document {

    private final org.jsoup.nodes.Document doc;

    public Document(final org.jsoup.nodes.Document doc) {
        this.doc = doc;
    }

    public List<String> getResourceURLs() {
        final List<String> urls = new LinkedList<>();
        for (final Element elem : doc.select("link")) {
            final String url = elem.attr("href");
            if (url != null) {
                urls.add(url);
            }
        }
        for (final Element elem : doc.getElementsByAttribute("background")) {
            final String url = elem.attr("background");
            if (url != null) {
                urls.add(url);
            }
        }
        for (final Element elem : doc.select("img")) {
            final String url = elem.attr("src");
            if (url != null) {
                urls.add(url);
            }
        }
        return urls;
    }
}
