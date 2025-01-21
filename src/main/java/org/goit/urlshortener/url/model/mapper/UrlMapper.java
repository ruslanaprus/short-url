package org.goit.urlshortener.url.model.mapper;

import org.goit.urlshortener.url.model.Url;
import org.goit.urlshortener.url.model.dto.UrlCreateRequest;
import org.goit.urlshortener.url.model.dto.UrlUpdateRequest;
import org.goit.urlshortener.url.model.dto.UrlResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UrlMapper {

    @Mapping(target = "id", ignore = true)
    Url toUrl(UrlCreateRequest request);

    Url toUrl(UrlUpdateRequest request);

    UrlResponse toUrlResponse(Url url);
}
