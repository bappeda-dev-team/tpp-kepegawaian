package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.stream.Collectors;

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
        
        // Hitung total dari semua nama perhitungan yang dipilih
        Float totalHasil = request.hitungTotalPerhitungan();
        
        // Konversi List ke String untuk data penyimpanan database
        String namaPerhitungan = request.namaPerhitungan().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
                
        String nilaiPerhitungan = request.nilaiPerhitungan().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        TppPerhitungan updatedTppPerhitungan = new TppPerhitungan(
                id,
                request.jenisTpp(),
                request.kodeOpd(),
                request.nip(),
                request.kodePemda(),
                namaPerhitungan,
                nilaiPerhitungan,
                request.maksimum(),
                request.bulan(),
                request.tahun(),
                totalHasil,
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
    public ResponseEntity<TppPerhitungan> post(@Valid @RequestBody TppPerhitunganRequest request) {
        if (tppPerhitunganService.existsByBulanAndTahun(request.bulan(), request.tahun())) {
            throw new TppPerhitunganBulanTahunSudahAdaException(request.bulan(), request.tahun());
        }
        
        // Hitung total dari semua nama perhitungan yang dipilih
        Float totalHasil = request.hitungTotalPerhitungan();
        
        // Konversi List ke String untuk data penyimpanan database
        String namaPerhitungan = request.namaPerhitungan().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
                
        String nilaiPerhitungan = request.nilaiPerhitungan().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        
        TppPerhitungan tppPerhitungan = new TppPerhitungan(
                null,
                request.jenisTpp(),
                request.kodeOpd(),
                request.nip(),
                request.kodePemda(),
                namaPerhitungan,
                nilaiPerhitungan,
                request.maksimum(),
                request.bulan(),
                request.tahun(),
                totalHasil,
                null,
                null
        );
        
        TppPerhitungan saved = tppPerhitunganService.tambahTppPerhitungan(tppPerhitungan);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();

        return ResponseEntity.created(location).body(saved);
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
