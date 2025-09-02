
package cc.kertaskerja.tppkepegawaian.tpp.web;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cc.kertaskerja.tppkepegawaian.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp.domain.TppService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("tpp")
public class TppController {
    private final TppService tppService;
    
    public TppController(TppService tppService) {
        this.tppService = tppService;
    }
    
    /**
     * Get tpp by ID
     * @param id tpp ID
     * @return Tpp object
     * url: /tpp/{id}
     */
    @GetMapping("detail/{id}")
    public Tpp getById(@PathVariable("id") Long id) {
        return tppService.detailTpp(id);
    }
    
    /**
     * Update tpp by ID
     * @param id tpp ID
     * @param request tpp update request
     * @return updated Tpp object
     * url: /tpp/{id}
     */
    @PutMapping("update/{id}")
    public Tpp put(@PathVariable("id") Long id, @Valid @RequestBody TppRequest request) {
        // Ambil data tpp yang sudah dibuat
        Tpp existingTpp = tppService.detailTpp(id);

        // Hitung total TPP dari request
        Float calculatedTotal = request.getTotalTppCalculated();

        Tpp tpp = new Tpp(
                id,
                request.jenisTpp(),
                request.kodeOpd(),
                request.nip(),
                request.kodePemda(),
                request.keterangan(),
                request.nilaiInput(),
                request.maksimum(),
                request.hasilPerhitungan(),
                request.bulan(),
                request.tahun(),
                calculatedTotal,
                existingTpp.createdDate(),
                null
        );

        return tppService.ubahTpp(id, tpp);
    }
    
    /**
     * Create new tpp
     * @param request tpp creation request
     * @return created Tpp object with location header
     * url: /tpp
     */
    @PostMapping
    public ResponseEntity<Tpp> post(@Valid @RequestBody TppRequest request) {
        // Hitung total TPP dari request
        Float calculatedTotal = request.getTotalTppCalculated();


        Tpp tpp = Tpp.of(
                request.jenisTpp(),
                request.kodeOpd(),
                request.nip(),
                request.kodePemda(),
                request.keterangan(),
                request.nilaiInput(),
                request.maksimum(),
                request.hasilPerhitungan(),
                request.bulan(),
                request.tahun(),
                calculatedTotal
        );
        Tpp saved = tppService.tambahTpp(tpp);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }
    
    /**
     * Delete tpp by ID
     * @param id tpp ID
     * url: /tpp/{id}
     */
    @DeleteMapping("delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        tppService.hapusTpp(id);
    }
}
