package org.goit.urlshortener.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.model.User;
import org.goit.urlshortener.model.dto.mapper.UrlMapper;
import org.goit.urlshortener.model.dto.request.UrlCreateRequest;
import org.goit.urlshortener.model.dto.request.UrlUpdateRequest;
import org.goit.urlshortener.model.dto.response.UrlResponse;
import org.goit.urlshortener.service.url.UrlService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Slf4j
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class UrlController {
    private final UrlService urlService;
    private final UrlMapper urlMapper;

    @GetMapping
    public Page<UrlResponse> listUrls(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "all") String status,
                                      @AuthenticationPrincipal User currentUser) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Url> urls = urlService.listUrlsByStatus(currentUser, status, pageRequest);
        return urls.map(urlMapper::toUrlResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UrlResponse create(@Valid @RequestBody UrlCreateRequest newUrlRequest,
                              @AuthenticationPrincipal User currentUser) {
        String originalUrl = newUrlRequest.originalUrl();
        Url savedUrl = urlService.createUrl(originalUrl, currentUser);
        return urlMapper.toUrlResponse(savedUrl);
    }

    @PutMapping("/{id}")
    public UrlResponse update(@PathVariable @Positive Long id,
                            @Valid @RequestBody UrlUpdateRequest urlUpdateRequest,
                            @AuthenticationPrincipal User currentUser) {
        Url updatedUrl = urlService.updateUrl(id,
                urlMapper.toUrl(urlUpdateRequest).toBuilder().id(id).build(),
                currentUser);
        return urlMapper.toUrlResponse(updatedUrl);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        urlService.deleteUrl(id, currentUser);
    }

    @GetMapping("/{id}")
    public UrlResponse getById(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal User currentUser) {
        return urlMapper.toUrlResponse(urlService.findByIdAndUser(id, currentUser));
    }

}
