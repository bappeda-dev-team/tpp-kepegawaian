package cc.kertaskerja.tppkepegawaian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
@ConfigurationPropertiesScan
public class TppKepegawaianApplication {

	public static void main(String[] args) {
		SpringApplication.run(TppKepegawaianApplication.class, args);
	}

}
