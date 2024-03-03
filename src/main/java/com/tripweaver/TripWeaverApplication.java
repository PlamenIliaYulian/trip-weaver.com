package com.tripweaver;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(title = "www.trip-weaver.com REST API", version = "1.0.0"),
        servers = {
                @Server(url = "http://localhost:8080"),
                @Server(url = "http://www.trip-weaver.com")
        },
        tags = {
                @Tag(name = "Authentication", description = "Operations related to the email verification available in the API."),
                @Tag(name = "Travel", description = "Operations related to the Travel model available in the API."),
                @Tag(name = "User", description = "Operations related to the User model available in the API.")
        }
)
@SecurityScheme(
        name = "Authorization",
        /*Това се прави по този начин само, за да работи в нашия проект, защото
        * нашата Basic authentication имплементация не отговаря на 100% на стандарта. Пример
        * не encode-ваме.*/
        type = SecuritySchemeType.APIKEY,
        /*scheme = "basic",*/
        description = "'Basic Authentication' header used in the project.",
        in = SecuritySchemeIn.HEADER)
@SpringBootApplication
public class TripWeaverApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripWeaverApplication.class, args);

    }

}
