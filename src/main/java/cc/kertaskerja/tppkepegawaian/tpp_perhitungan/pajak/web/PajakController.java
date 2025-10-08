package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.web;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.domain.Pajak;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.domain.PajakService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("pajak")
public class PajakController {
    private final PajakService pajakService;

    public PajakController(PajakService pajakService) { this.pajakService = pajakService; }

    /**
     * Get detail pajak by nip
     * @param nip
     * url: /pajak/detail/{nip}
     */
    @GetMapping("detail/{nip}")
    public Pajak getByNip(@PathVariable("nip") String nip) {
        return pajakService.detailPajak(nip);
    }
    
    /**
     * Update pajak by nip
     * @param nip
     * url: /pajak/update/{nip}
     */
    @PutMapping("update/{nip}")
    public Pajak put(@PathVariable("nip") String nip, @Valid @RequestBody PajakRequest request) {
        // Ambil data pajak yang sudah dibuat
        Pajak existingPajak = pajakService.detailPajak(nip);

        Pajak pajak = new Pajak(
                existingPajak.id(),
                request.nip(),
                request.namaPajak(),
                request.dasarHukum(),
                request.komponenPajak(),
                request.nilaiPajak(),
                existingPajak.createdDate(),
                null
        );
        
        return pajakService.ubahPajak(nip, pajak);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Pajak> post(@Valid @RequestBody PajakRequest request) {
        Pajak pajak = Pajak.of(
                request.nip(),
                request.namaPajak(),
                request.dasarHukum(),
                request.komponenPajak(),
                request.nilaiPajak()
        );

        Pajak saved = pajakService.tambahPajak(pajak);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{nip}")
                .buildAndExpand(saved.nip())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    /**
     * delete pajak
     * @param nip
     * url: /pajak/delete/{nip}
     */ 
    @DeleteMapping("delete/{nip}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("nip") String nip) {
        pajakService.hapusPajak(nip);
    }
}
