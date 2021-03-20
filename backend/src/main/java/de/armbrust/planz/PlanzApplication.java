package de.armbrust.planz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class PlanzApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanzApplication.class, args);

    }
}
