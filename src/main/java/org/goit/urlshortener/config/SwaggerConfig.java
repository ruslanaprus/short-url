package org.goit.urlshortener.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("Bearer Authorisation");

        Components components = new Components()
                .addSecuritySchemes("Bearer Authorisation", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .bearerFormat("JWT")
                        .scheme("bearer"));

        Info apiInfo = new Info()
                .title("URL Shortener API")
                .description("Easily transform long links into short, shareable URLs. Track clicks, edit your links, and manage them all in one place!")
                .version("1.0");

        Paths paths = new Paths()
                .addPathItem("/api/v1/signup", new PathItem()
                        .post(new Operation()
                                .summary("Sign up")
                                .addTagsItem("Authentication controller")
                                .security(List.of())))
                .addPathItem("/api/v1/login", new PathItem()
                        .post(new Operation()
                                .summary("Login")
                                .addTagsItem("Authentication controller")
                                .security(List.of())));

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components)
                .info(apiInfo)
                .paths(paths);
    }

    @Bean
    public WebSecurityCustomizer swaggerWebSecurityCustomizer() {

        final String[] SWAGGER_WHITELIST = {
                "/swagger-ui.html",
                "/swagger-resources/**",
                "/api-docs/**",
                "/swagger-ui/**"
        };

        return web -> web.ignoring().requestMatchers(SWAGGER_WHITELIST);
    }

}
