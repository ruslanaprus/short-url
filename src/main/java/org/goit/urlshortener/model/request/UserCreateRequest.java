package org.goit.urlshortener.model.request;

import lombok.Builder;

@Builder
public record UserCreateRequest(String email, String password) {

}
