package com.unbounce.scraper.provided.httpclient;

import java.io.IOException;

public class RedirectException extends IOException {
    private int statusCode;
    private final String location;

    public RedirectException(final int statusCode, final String location) {
        super("statusCode " + statusCode + ", location " + location);
        this.statusCode = statusCode;
        this.location = location;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getLocation() {
        return location;
    }
}
