package com.quickmedicalcare.backend;

import com.quickmedicalcare.backend.config.ContextInitializer;
import com.quickmedicalcare.backend.registerLogin.SuperTokensInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QuickMedicalCareApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(QuickMedicalCareApplication.class);
		app.addInitializers(new ContextInitializer());
		app.run(args);
	}

}
