package org.goit.urlshortener.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RedirectController {
    @GetMapping("/simple")
    public RedirectView simpleRedirect() {
        RedirectView redirectView = new RedirectView("https://example.com");
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }
}
