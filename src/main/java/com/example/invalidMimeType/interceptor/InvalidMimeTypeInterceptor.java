package com.example.invalidMimeType.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;

import java.io.IOException;
import java.io.InputStream;

public class InvalidMimeTypeInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        var contentType = response.getHeaders().getFirst("Content-Type");
        if (contentType != null) {
            try {

                MimeType.valueOf(contentType);
            } catch (InvalidMimeTypeException e) {
                // Return a wrapped response with modified headers
                return new ClientHttpResponse() {
                    @Override
                    public HttpHeaders getHeaders() {
                        HttpHeaders headers = new HttpHeaders();
                        headers.putAll(response.getHeaders());

                        // Modify the Content-Type header if necessary
                        if ("csv".equals(headers.getFirst("Content-Type"))) {
                            headers.set("Content-Type", "text/csv");
                        }

                        return headers;
                    }

                    @Override
                    public InputStream getBody() throws IOException {
                        return response.getBody();
                    }

                    @Override
                    public HttpStatusCode getStatusCode() throws IOException {
                        return response.getStatusCode();
                    }

                    @Override
                    public String getStatusText() throws IOException {
                        return response.getStatusText();
                    }

                    @Override
                    public void close() {
                        response.close();
                    }
                };
            }
        }
        return response;
    }
}
