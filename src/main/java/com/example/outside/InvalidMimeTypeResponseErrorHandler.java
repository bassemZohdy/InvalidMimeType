package com.example.outside;


import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class InvalidMimeTypeResponseErrorHandler extends DefaultResponseErrorHandler
{

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        // Check if the response has a mime type issue
        String contentType = response.getHeaders().getFirst("Content-Type");
        if (contentType == null || !contentType.contains("/")) {
            // If the mime type is invalid (e.g., "csv" instead of "text/csv")
            return true;
        }
        return false; // For other error scenarios, return false
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // First, fix the invalid mime type if needed
        fixInvalidContentType(response);

        // After fixing the mime type, we do not throw any errors, we simply pass the response forward
        // This way, the response continues to be processed without being interrupted by an exception.
    }

    private void fixInvalidContentType(ClientHttpResponse response) throws IOException {
        // Checking and fixing the Content-Type header if needed
        String contentType = response.getHeaders().getFirst("Content-Type");

        if ("csv".equals(contentType)) {
            response.getHeaders().set("Content-Type", "text/csv");
        }
    }
}
