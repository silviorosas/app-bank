package com.banking.appbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "Java App Bank",
		description = "Backend Api Rest Spring Boot App Bank",
		version = "v1.0",
		contact = @Contact(
			name = "Silvio Rosas",
			email = "silviorosas@yahoo.com.ar",
			url = "mi github"
		),
		license = @License(
			name = "Cuyano Dev",
			url="Mi github"
		)
	),
	externalDocs = @ExternalDocumentation(
		description = "Cuyano Dev Doc de App Bank",
		url = "Mi github"
	)
)
public class AppBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppBankApplication.class, args);
	}

}
