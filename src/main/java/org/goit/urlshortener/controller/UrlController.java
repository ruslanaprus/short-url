package org.goit.urlshortener.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@Slf4j
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@Tags(value = {
        @Tag(name = "URL controller", description = "Provides operations for managing URLs")
})
public class UrlController {
    private final UrlService urlService;
    private final UrlMapper urlMapper;

    @Operation(summary = "Display the list of URLs",
            description = """
                    Retrieve a paginated list of all URL's short codes belonging to the authenticated user.
                    
                    **Pagination Parameters:**
                    - `page` (optional, default: `0`): The page number (zero-based index) to retrieve.
                    - `size` (optional, default: `10`): The number of URLs per page.
                    - `status` (optional, default: `all`): Filter for `all`, `active`, `expired` URLs.
                    
                    **Example Request:**
                    `GET http://localhost:8080/api/v1/urls?page=0&size=10&status=all`
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to access this resource")
            })

    @GetMapping
    public Page<UrlResponse> listUrls(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "all") String status,
                                      @AuthenticationPrincipal User currentUser) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Url> urls = urlService.listUrlsByStatus(currentUser, status, pageRequest);
        return urls.map(urlMapper::toUrlResponse);
    }

    @Operation(
            summary = "Create a new short code for URL",
            description = "Add a new shortCode to the system for the authenticated user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New URL details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UrlCreateRequest.class),
                            examples = @ExampleObject(value = "{ \"originalUrl\": \"https://example.com\", \"shortCode\": \"example\"}"))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created a short code for a URL",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UrlResponse.class),
                                    examples = @ExampleObject(value = "{ \"originalUrl\": \"https://example.com\", \"shortCode\": \"example\", \"clickCount\": \"0\"}"))}),
                    @ApiResponse(responseCode = "400", description = "Invalid URL data provided",
                            content = @Content),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to access this resource",
                            content = @Content),
                    @ApiResponse(responseCode = "409", description = "URL already exists",
                            content = @Content)
            })

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UrlResponse create(@Valid @RequestBody UrlCreateRequest newUrlRequest,
                              @AuthenticationPrincipal User currentUser) {
        Url savedUrl = urlService.createUrl(newUrlRequest, currentUser);
        return urlMapper.toUrlResponse(savedUrl);
    }

    @Operation(summary = "Update URL information by ID",
            description = "Update the details of an existing URL using its unique identifier",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated URL details",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UrlUpdateRequest.class),
                            examples = @ExampleObject(value = "{ \"originalUrl\": \"https://example.com\", \"shortCode\": \"newExample\" }"))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated URL information",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UrlResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Invalid URL data or ID provided"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to access this resource"),
                    @ApiResponse(responseCode = "404", description = "URL not found"),
                    @ApiResponse(responseCode = "409", description = "URL already exists",
                            content = @Content)
            })

    @PutMapping("/{id}")
    public UrlResponse update(@PathVariable @Positive Long id,
                              @Valid @RequestBody UrlUpdateRequest urlUpdateRequest,
                              @AuthenticationPrincipal User currentUser) {
        Url updatedUrl = urlService.updateUrl(id,
                urlMapper.toUrl(urlUpdateRequest).toBuilder().id(id).build(),
                currentUser);
        return urlMapper.toUrlResponse(updatedUrl);
    }

    @Operation(summary = "Delete URL by ID",
            description = "Remove a specific URL using its unique identifier",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted URL with no response body"),
                    @ApiResponse(responseCode = "400", description = "Invalid ID provided"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to access this resource"),
                    @ApiResponse(responseCode = "404", description = "URL not found")
            })

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        urlService.deleteUrl(id, currentUser);
    }

    @Operation(summary = "Find URL by ID",
            description = "Retrieve details of a specific URL using its unique identifier",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved URL details",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UrlResponse.class))}),
                    @ApiResponse(responseCode = "400", description = "Invalid ID provided"),
                    @ApiResponse(responseCode = "403", description = "User does not have permission to access this resource"),
                    @ApiResponse(responseCode = "404", description = "URL not found")
            })

    @GetMapping("/{id}")
    public UrlResponse getById(
            @PathVariable @Positive Long id,
            @AuthenticationPrincipal User currentUser) {
        return urlMapper.toUrlResponse(urlService.findByIdAndUser(id, currentUser));
    }

    @Operation(summary = "Find URL by short code",
            description = "Retrieve details of a specific URL using its short code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved URL details",
                            content = {@Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UrlResponse.class))}),
                    @ApiResponse(responseCode = "404", description = "URL not found")
            })

    @GetMapping("/shortCode/{shortCode}")
    public UrlResponse getByShortCode(
            @PathVariable String shortCode) {
        return urlMapper.toUrlResponse(urlService.findByShortCode(shortCode));
    }
}
