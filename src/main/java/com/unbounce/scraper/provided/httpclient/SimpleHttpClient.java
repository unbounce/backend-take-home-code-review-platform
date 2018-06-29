package com.unbounce.scraper.provided.httpclient;

import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.unbounce.scraper.bootstrap.restricted.FailureMode;

/**
 * Simplified wrapper around apache HttpClient for the purposes of this coding exercise.
 */
public class SimpleHttpClient {

    private final Random random = new Random();
    private final CloseableHttpClient client;

    public SimpleHttpClient() {
        this(10000);
    }

    public SimpleHttpClient(final int timeoutMillis) {
        client = HttpClientBuilder.create()
                                  .disableRedirectHandling()
                                  .setDefaultRequestConfig(
                                      RequestConfig.custom()
                                                   .setConnectTimeout(timeoutMillis)
                                                   .setConnectionRequestTimeout(timeoutMillis)
                                                   .setSocketTimeout(timeoutMillis)
                                                   .build())
                                  .build();
    }

    /**
     * Makes an HTTP GET request to a URL.
     */
    public CloseableHttpResponse sendGetRequest(final String url) throws IOException {
        final HttpGet get = new HttpGet(url);
        return sendRequest(get);
    }

    /**
     * Makes an HTTP request to a URL.
     */
    public CloseableHttpResponse sendRequest(final HttpUriRequest request) throws IOException {
        FailureMode.maybeInjectHttpFailure();
        final CloseableHttpResponse response = client.execute(request);
        return response;
    }

    /**
     * Returns the content, as a String, from a URL. Does not follow redirects. On non-2xx status codes, will throw
     * a RedirectException for 3xx, or IllegalResponseCodeException for anything else.
     */
    public String getContent(final String url) throws IllegalResponseCodeException, RedirectException, IOException {
        try (final CloseableHttpResponse response = sendGetRequest(url)) {
            checkStatusCode(response);
            final HttpEntity entity = response.getEntity();
            final ContentType contentType = ContentType.getOrDefault(entity);
            final Charset charset = contentType.getCharset();
            //return IOUtils.toString(entity.getContent(), charset);
            
            if(entity!=null) { 
            	return EntityUtils.toString(entity); 
            }
            
            return "";
        }
    }

    /**
     * Returns the content length, from a URL. Does not follow redirects. On non-2xx status codes, will throw
     * a RedirectException for 3xx, or IllegalResponseCodeException for anything else.
     */
    public long getContentLength(final String url) throws IllegalResponseCodeException, RedirectException, IOException {
        try (final CloseableHttpResponse response = sendGetRequest(url)) {
            checkStatusCode(response);
            return response.getEntity().getContentLength();
        }
    }

    private void checkStatusCode(final CloseableHttpResponse response)
        throws IllegalResponseCodeException, RedirectException {
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode < 200 || statusCode >= 300) {
            if (statusCode >= 300 && statusCode < 400) {
                final Header locationHeader = response.getFirstHeader("location");
                if (locationHeader != null) {
                    final String location = locationHeader.getValue();
                    throw new RedirectException(statusCode, location);
                }
            }
            throw new IllegalResponseCodeException(statusCode);
        }
    }

}
