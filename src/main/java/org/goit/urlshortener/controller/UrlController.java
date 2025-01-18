package org.goit.urlshortener.controller;

import lombok.RequiredArgsConstructor;
import org.goit.urlshortener.controller.dto.UrlResponseDTO;
import org.goit.urlshortener.service.UrlService;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor

public class UrlController {
    private final UrlService urlService;

    @PostMapping
    public ResponseEntity<UrlResponseDTO> createUrl(@RequestBody UrlRequestDTO urlRequestDTO, Principal principal) {
        UrlResponseDTO response = urlService.createUrl(urlRequestDTO, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UrlResponseDTO> getUrl(@PathVariable Long id, Principal principal) {
        UrlResponseDTO response = urlService.getUrlByIdAndUser(id, principal.getName());
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<Page<UrlResponseDTO>> listUrls(@PageableDefault(size = 10) Pageable pageable, Principal principal) {
        Page<UrlResponseDTO> urls = urlService.getUrlsByUser(principal.getName(), pageable);
        return ResponseEntity.ok(urls);
    }

    @GetMapping
    public ResponseEntity<Page<UrlResponseDTO>> listUrls(SpringDataWebProperties.Pageable pageable, Principal principal) {
        Page<UrlResponseDTO> urls = urlService.getUrlsByUser(principal.getName(), pageable);
        return ResponseEntity.ok(urls);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UrlResponseDTO> updateUrl(@PathVariable Long id, @RequestBody UrlRequestDTO urlRequestDTO, Principal principal) {
        UrlResponseDTO response = urlService.updateUrl(id, urlRequestDTO, principal.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id, Principal principal) {
        urlService.deleteUrlByIdAndUser(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
    
}
