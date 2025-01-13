package org.goit.urlshortener.service.url;

import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UrlValidator {
    public boolean isValidUrl(String originalUrl) {
        try {
            new URI(originalUrl).toURL();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
    }
}

