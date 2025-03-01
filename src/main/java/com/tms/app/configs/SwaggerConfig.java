package com.tms.app.configs;

import com.tms.app.utils.AppConstants;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(AppConstants.DOC_TITLE)
                        .version(AppConstants.DOC_VERSION)
                        .description(AppConstants.DOC_DESCRIPTION)
                        .contact(new Contact()
                                .name(AppConstants.DOC_OWNER)
                                .email(AppConstants.DOC_EMAIL))
                        .license(new License()
                                .name(AppConstants.DOC_LICENSE_TITLE)
                                .url(AppConstants.DOC_LICENSE_URL)));
    }
}