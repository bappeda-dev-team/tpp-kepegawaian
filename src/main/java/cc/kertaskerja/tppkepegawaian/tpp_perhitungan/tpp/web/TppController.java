
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.request.TppRequest;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response.TppTotalTppResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("tpp")
public class TppController {
    private final TppService tppService;
    private final TppPerhitunganService tppPerhitunganService;

    public TppController(TppService tppService, TppPerhitunganService tppPerhitunganService) {
        this.tppService = tppService;
        this.tppPerhitunganService = tppPerhitunganService;
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
    public TppTotalTppResponse put(@PathVariable("id") Long id, @Valid @RequestBody TppRequest request) {
        // Ambil data tpp yang sudah dibuat
        Tpp existingTpp = tppService.detailTpp(id);

        Tpp tpp = new Tpp(
                id,
                request.jenisTpp(),
                request.kodeOpd(),
                request.nip(),
                request.kodePemda(),
                request.maksimumTpp(),
                request.bulan(),
                request.tahun(),
                existingTpp.createdDate(),
                null
        );
        
        Tpp  updated = tppService.ubahTpp(id, tpp);
        
        // Ambil data perhitungan berdasarkan pada NIP, bulan, dan tahun dari request
        var perhitunganList = tppPerhitunganService.listTppPerhitunganByJenisTppAndNipAndBulanAndTahun(
                request.jenisTpp(),
                request.nip(), 
                request.bulan(), 
                request.tahun());
        
        // Kalkulasi hasilPerhitungan (total persen) dari perhitungan
        Float hasilPerhitungan = 0.0f;
        for (var perhitungan : perhitunganList) {
            if (perhitungan.nilaiPerhitungan() != null) {
                hasilPerhitungan += perhitungan.nilaiPerhitungan();
            }
        }
        
        // Kalkulasi totaltpp = maksimumTpp * (hasilPerhitungan / 100)
        Float totaltpp = request.maksimumTpp() * (hasilPerhitungan / 100.0f);

        // Buat respons dengan nilai terhitung (bulan dan tahun saja dalam respons)
        TppTotalTppResponse response = new TppTotalTppResponse(
                updated.jenisTpp().name(),
                updated.kodeOpd(),
                updated.nip(),
                updated.kodePemda(),
                updated.maksimumTpp(),
                request.bulan(),
                request.tahun(),
                hasilPerhitungan,
                totaltpp);

        return response;
    }
    
    /**
     * Create new tpp
     * @param request tpp creation request
     * @return created Tpp object with location header
     * url: /tpp
     */
    @PostMapping
    public ResponseEntity<TppTotalTppResponse> post(@Valid @RequestBody TppRequest request) {

        Tpp tpp = Tpp.of(
                request.jenisTpp(),
                request.kodeOpd(),
                request.nip(),
                request.kodePemda(),
                request.maksimumTpp(),
                request.bulan(),
                request.tahun());
        Tpp saved = tppService.tambahTpp(tpp);

        // Ambil data perhitungan berdasarkan pada NIP, bulan, dan tahun dari request
        var perhitunganList = tppPerhitunganService.listTppPerhitunganByJenisTppAndNipAndBulanAndTahun(
                request.jenisTpp(),
                request.nip(),
                request.bulan(),
                request.tahun());

        // Kalkulasi hasilPerhitungan (total persen) dari perhitungan
        Float hasilPerhitungan = 0.0f;
        for (var perhitungan : perhitunganList) {
            if (perhitungan.nilaiPerhitungan() != null) {
                hasilPerhitungan += perhitungan.nilaiPerhitungan();
            }
        }

        // Kalkulasi totaltpp = maksimumTpp * (hasilPerhitungan / 100)
        Float totaltpp = request.maksimumTpp() * (hasilPerhitungan / 100.0f);

        // Buat respons dengan nilai terhitung (bulan dan tahun saja dalam respons)
        TppTotalTppResponse response = new TppTotalTppResponse(
                saved.jenisTpp().name(),
                saved.kodeOpd(),
                saved.nip(),
                saved.kodePemda(),
                saved.maksimumTpp(),
                request.bulan(),
                request.tahun(),
                hasilPerhitungan,
                totaltpp);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
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
