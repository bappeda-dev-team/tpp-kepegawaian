package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.request.PerhitunganRequest;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.request.TppPerhitunganRequest;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.response.PerhitunganResponse;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.response.TppPerhitunganResponse;

import java.util.List;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("tppPerhitungan")
public class TppPerhitunganController {
    private final TppPerhitunganService tppPerhitunganService;
    
    public TppPerhitunganController(TppPerhitunganService tppPerhitunganService) {
        this.tppPerhitunganService = tppPerhitunganService;
    }
    
    /**
     * Update tpp perhitungan by NIP, bulan, and tahun
     * @param nip NIP pegawai
     * @param bulan bulan perhitungan
     * @param tahun tahun perhitungan
     * @param request tpp perhitungan update request
     * @return updated TppPerhitunganResponse object
     * url: /tppPerhitungan/update/{nip}/{bulan}/{tahun}
     */
    @PutMapping("update/{nip}/{bulan}/{tahun}")
    public ResponseEntity<?> put(
            @PathVariable("nip") String nip,
            @PathVariable("bulan") Integer bulan,
            @PathVariable("tahun") Integer tahun,
            @Valid @RequestBody TppPerhitunganRequest request) {
        
        // validasi url sesuai atau tidak dengan data yang akan di update
        if (!request.nip().equals(nip) || !request.bulan().equals(bulan) || !request.tahun().equals(tahun)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Path variables tidak cocok dengan request body");
        }

        if (request.perhitungans() != null && !request.perhitungans().isEmpty()) {
            // Validasi total nilai maksimum
            var totalNilaiMaksimum = request.perhitungans().stream()
                .mapToDouble(PerhitunganRequest::maksimum)
                .sum();

            if (totalNilaiMaksimum > request.maksimum()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Total nilai maksimum yang di input lebih dari total nilai maksimum yang ada");
            }

            // Ambil data nip, bulan, dan tahun yang sudah ada untuk proses update
            List<TppPerhitungan> existingRecords = StreamSupport.stream(
                    tppPerhitunganService.getByNipBulanTahun(nip, bulan, tahun).spliterator(), false)
                    .toList();
            
            var perhitungans = request.perhitungans().stream()
                    .map(p -> {
                        // Temukan data yang sudah ada berdasarkan nama perhitungan
                        TppPerhitungan existingRecord = existingRecords.stream()
                                .filter(record -> record.namaPerhitungan().equals(p.namaPerhitungan()))
                                .findFirst()
                                .orElse(null);
                        
                        if (existingRecord != null) {
                            // Update data yang sudah ada
                            TppPerhitungan updatedRecord = new TppPerhitungan(
                                    existingRecord.id(),
                                    request.jenisTpp(),
                                    request.kodeOpd(),
                                    request.nip(),
                                    request.nama(),
                                    request.bulan(),
                                    request.tahun(),
                                    p.maksimum(),
                                    p.namaPerhitungan(),
                                    p.nilaiPerhitungan(),
                                    existingRecord.createdDate(),
                                    null
                            );
                            return tppPerhitunganService.ubahTppPerhitungan(updatedRecord);
                        } else {
                            // Buat data baru jika data yang diubah tidak ada
                            TppPerhitungan newRecord = TppPerhitungan.of(
                                    request.jenisTpp(),
                                    request.kodeOpd(),
                                    request.nip(),
                                    request.nama(),
                                    request.bulan(),
                                    request.tahun(),
                                    p.maksimum(),
                                    p.namaPerhitungan(),
                                    p.nilaiPerhitungan()
                            );
                            return tppPerhitunganService.tambahTppPerhitungan(newRecord);
                        }
                    })
                    .map(saved -> new PerhitunganResponse(
                            saved.namaPerhitungan(),
                            saved.maksimum(),
                            saved.nilaiPerhitungan()
                    ))
                    .toList();

            // Hitung total nilai persen
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
            return ResponseEntity.ok(response);
        }

        // response jika tidak memiliki nilai perhitungan
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
        return ResponseEntity.ok(response);
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
