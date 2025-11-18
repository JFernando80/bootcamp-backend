package br.com.impacta.bootcamp;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@ComponentScan
@EnableScheduling
@SpringBootApplication
public class BootcampApplication {

    public static boolean executouPermissoes = false;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate restTemplate () {
        return restTemplateBuilder
                .additionalInterceptors((request, body, execution) -> {
                    String correlationId = MDC.get("X-Correlation-Id");
                    if (correlationId != null) {
                        request.getHeaders().add("X-Correlation-Id", correlationId);
                    }
                    return execution.execute(request, body);
                })
                .connectTimeout(Duration.ofSeconds(300))
                .readTimeout(Duration.ofSeconds(300))
                .build();
    }

	public static void main(String[] args) {
		SpringApplication.run(BootcampApplication.class, args);
	}

}
