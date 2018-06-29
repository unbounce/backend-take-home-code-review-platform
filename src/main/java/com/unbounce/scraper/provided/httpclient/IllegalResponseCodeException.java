package com.unbounce.scraper.provided.httpclient;

import java.io.IOException;

public class IllegalResponseCodeException extends IOException {
    private int statusCode;

    public IllegalResponseCodeException(final int statusCode) {
        super("statusCode " + statusCode);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
