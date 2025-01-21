package org.goit.urlshortener.url.controller;

import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.url.model.Url;
import org.goit.urlshortener.url.service.UrlService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class RedirectController {

    private final UrlService urlService;

    @Cacheable("RedirectView")
    @GetMapping("/s/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) {
        try {
            Url url = urlService.getValidUrl(shortCode);

            urlService.incrementClickCount(url);

            RedirectView redirectView = new RedirectView(url.getOriginalUrl());
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return redirectView;
        } catch (RuntimeException e) {
            RedirectView error = new RedirectView("/error");
            error.setStatusCode(HttpStatus.GONE);
            return error;
        }
    }
}
