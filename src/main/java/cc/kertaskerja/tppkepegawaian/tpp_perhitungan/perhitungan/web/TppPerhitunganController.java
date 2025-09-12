package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;

import java.util.List;

@RestController
@RequestMapping("tppPerhitungan")
public class TppPerhitunganController {
    private final TppPerhitunganService tppPerhitunganService;
    
    public TppPerhitunganController(TppPerhitunganService tppPerhitunganService) {
        this.tppPerhitunganService = tppPerhitunganService;
    }
    
    /**
     * Get tpp perhitungan by ID
     * @param id tpp perhitungan ID
     * @return Tpp Perhitungan object
     * url: /tppPerhitungan/{id}
     */
    @GetMapping("detail/{id}")
    public TppPerhitungan getById(@PathVariable("id") Long id) {
        return tppPerhitunganService.detailTppPerhitungan(id);
    }
    
    /**
     * Update tpp perhitungan by ID
     * @param id tpp perhitungan ID
     * @param request tpp perhitungan update request
     * @return updated TppPerhitungan object
     * url: /tppPerhitungan/{id}
     */
    @PutMapping("update/{id}")
    public ResponseEntity<TppPerhitungan> put(@PathVariable("id") Long id, @Valid @RequestBody TppPerhitunganRequest request) {
        // Ambil data tpp perhitungan yang sudah dibuat
        TppPerhitungan existingTppPerhitungan = tppPerhitunganService.detailTppPerhitungan(id);
        
        TppPerhitungan updatedTppPerhitungan = new TppPerhitungan(
                id,
                request.jenisTpp(),
                request.kodeOpd(),
                request.nip(),
                request.nama(),
                request.bulan(),
                request.tahun(),
                request.maksimum(),
                existingTppPerhitungan.namaPerhitungan(),
                existingTppPerhitungan.nilaiPerhitungan(),
                existingTppPerhitungan.createdDate(),
                null
        );
        
        TppPerhitungan saved = tppPerhitunganService.ubahTppPerhitungan(id, updatedTppPerhitungan);

        return ResponseEntity.ok(saved);
    }

    /**
     * Create new tpp perhitungan
     * @param request tpp perhitungan creation request
     * @return created TppPerhitungan objects with location header
     * url: /tppPerhitungan
     */
    @PostMapping
    public ResponseEntity<?> post(@Valid @RequestBody TppPerhitunganRequest request) {
        if (request.perhitungans() != null && !request.perhitungans().isEmpty()) {
            // Ambil data perhitungans dari request
            var totalNilaiMaksimum = request.perhitungans().stream()
                .mapToDouble(PerhitunganRequest::maksimum)
                .sum();

            if (totalNilaiMaksimum > request.maksimum()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Total nilai maksimum yang di input lebih dari total nilai maksimum yang ada");
            }

            var perhitungans = request.perhitungans().stream()
                    .map(p -> TppPerhitungan.of(
                            request.jenisTpp(),
                            request.kodeOpd(),
                            request.nip(),
                            request.nama(),
                            request.bulan(),
                            request.tahun(),
                            p.maksimum(),
                            p.namaPerhitungan(),
                            p.nilaiPerhitungan()
                    ))
                    .map(tppPerhitunganService::tambahTppPerhitungan)
                    .map(saved -> new PerhitunganResponse(
                            saved.namaPerhitungan(),
                            saved.maksimum(),
                            saved.nilaiPerhitungan()
                    ))
                    .toList();

            // Hitung total persen
            var totalPersen = (float) perhitungans.stream()
                .mapToDouble(PerhitunganResponse::nilaiPerhitungan)
                .sum();

            var response = new TppPerhitunganResponse(
                request.jenisTpp().name(),
                request.kodeOpd(),
                request.nip(),
                request.nama(),
                request.kodePemda(),
                request.maksimum(),
                request.bulan(),
                request.tahun(),
                perhitungans,
                totalPersen
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        var response = new TppPerhitunganResponse(
            request.jenisTpp().name(),
            request.kodeOpd(),
            request.nip(),
            request.nama(),
            request.kodePemda(),
            request.maksimum(),
            request.bulan(),
            request.tahun(),
            List.of(),
            0.0f
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Delete tpp perhitungan by ID
     * @param id tpp perhitungan ID
     * url: /tppPerhitungan/{id}
     */
    @DeleteMapping("delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        tppPerhitunganService.hapusTppPerhitungan(id);
    }
}
