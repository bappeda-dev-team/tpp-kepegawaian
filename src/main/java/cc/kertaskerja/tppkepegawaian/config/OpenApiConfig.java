package cc.kertaskerja.tppkepegawaian.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tppKepegawaianOpenAPI() {
        Info info = new Info()
                .title("TPP Kepegawaian API")
                .version("v1")
                .description("Dokumentasi OpenAPI untuk layanan pengelolaan data kepegawaian dan perhitungan TPP.");

        return new OpenAPI()
                .info(info)
                .servers(List.of(
                        new Server().url("/").description("Server default mengikuti base path aplikasi")));
    }
}
