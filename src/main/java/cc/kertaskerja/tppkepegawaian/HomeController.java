package cc.kertaskerja.tppkepegawaian;

import cc.kertaskerja.tppkepegawaian.config.KertasKerjaProperties;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Status", description = "Endpoint untuk memantau status layanan")
public class HomeController {
    private final KertasKerjaProperties kertasKerjaProperties;

    public HomeController(KertasKerjaProperties kertasKerjaProperties) {
        this.kertasKerjaProperties = kertasKerjaProperties;
    }

    @GetMapping("/")
    public String getStatus() {
        return kertasKerjaProperties.getStatus();
    }
}
