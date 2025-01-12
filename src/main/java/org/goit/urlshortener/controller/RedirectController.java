package org.goit.urlshortener.controller;

import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.repository.UrlRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class RedirectController {
    private final UrlRepository urlRepository;

    public RedirectController(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @GetMapping("/simple")
    public RedirectView simpleRedirect() {
        RedirectView redirectView = new RedirectView("https://example.com");
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }

    @GetMapping("/s/{shortCode}")
    public RedirectView redirectToOriginalUrl(@PathVariable String shortCode) {
        Optional<Url> urlOptional = urlRepository.findByShortCode(shortCode);

        if (urlOptional.isPresent()) {
            RedirectView redirectView = new RedirectView(urlOptional.get().getOriginalUrl());
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            return redirectView;
        }

        RedirectView redirectView = new RedirectView("/error");
        redirectView.setStatusCode(HttpStatus.NOT_FOUND);
        return redirectView;
    }
}
