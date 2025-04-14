package cc.kertaskerja.tppkepegawaian.pegawai.web;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pegawais")
public class PegawaiController {
    private final PegawaiService pegawaiService;

    public PegawaiController(PegawaiService pegawaiService) {
        this.pegawaiService = pegawaiService;
    }

    @GetMapping
    public Iterable<Pegawai> getPegawaiAktif(
            @RequestParam("kode_opd") String kodeOpd,
            @RequestParam(value = "tahun", required = false) Integer tahun,
            @RequestParam(value = "bulan", required = false) Integer bulan
    ) {
        return pegawaiService.listPegawaiAktif(kodeOpd, tahun, bulan);
    }

    @GetMapping("{nip}")
    public Pegawai getByNip(@PathVariable String nip) {
        return pegawaiService.detailPegawai(nip);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pegawai post(@Valid @RequestBody Pegawai pegawai) {
        return pegawaiService.tambahPegawai(pegawai);
    }

    @DeleteMapping("{nip}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String nip) {
        pegawaiService.hapusPegawai(nip);
    }

}
