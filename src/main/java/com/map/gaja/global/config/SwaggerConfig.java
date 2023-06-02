package com.map.gaja.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi userApi(){
        return GroupedOpenApi.builder()
                .group("user-api")
                .pathsToMatch("/api/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi bundleApi(){
        return GroupedOpenApi.builder()
                .group("bundle-api")
                .pathsToMatch("/api/bundle/**")
                .build();
    }

    @Bean
    public GroupedOpenApi clientApi(){
        return GroupedOpenApi.builder()
                .group("client-api")
                .pathsToMatch("/api/client/**")
                .build();
    }

    @Bean
    public OpenAPI openApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("GaJaMap API")
                        .description("API 명세서")
                        .version("0.0.1"));
    }
}
