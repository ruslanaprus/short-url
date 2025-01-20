package org.goit.urlshortener.model.dto.mapper;

import org.goit.urlshortener.model.Url;
import org.goit.urlshortener.model.dto.request.UrlCreateRequest;
import org.goit.urlshortener.model.dto.request.UrlUpdateRequest;
import org.goit.urlshortener.model.dto.response.UrlResponse;
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
