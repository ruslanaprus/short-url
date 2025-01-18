package org.goit.urlshortener.controller;

import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.service.url.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/simple")
    public RedirectView simpleRedirect() {
        RedirectView redirectView = new RedirectView("https://example.com");
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }

    @GetMapping("/s/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) {
        try {
            Url url = urlService.getValidUrl(shortCode);

            urlService.incrementClickCount(shortCode);

            RedirectView redirectView = new RedirectView(url.getOriginalUrl());
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return redirectView;
        } catch (RuntimeException e) {
            RedirectView error = new RedirectView("/error");
            error.setStatusCode(HttpStatus.NOT_FOUND);
            return error;
        }
    }

}
