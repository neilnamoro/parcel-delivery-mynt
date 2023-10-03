package com.mynt.parceldelivery.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * This is to configure swagger ui have it running when we run the spring boot application.
 */
@Configuration
public class OpenApiConfig {

    @Value("${parceldelivery.openapi.dev.url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in development environment");

        Contact contact = new Contact();
        contact.email("test@namoro.com");
        contact.setName("test");
        contact.setUrl("https://www.namoro.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Parcel Delivery API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for parcel delivery service.").termsOfService("https://www.namoro.com/terms")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
