package cc.kertaskerja.tppkepegawaian;

import cc.kertaskerja.tppkepegawaian.config.KertasKerjaProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
