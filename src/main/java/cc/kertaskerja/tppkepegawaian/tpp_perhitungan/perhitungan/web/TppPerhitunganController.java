package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNipBulanTahunSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.request.PerhitunganRequest;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.request.TppPerhitunganRequest;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.response.PerhitunganResponse;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.response.TppPerhitunganResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("tppPerhitungan")
public class TppPerhitunganController {
    private final TppPerhitunganService tppPerhitunganService;
    
    public TppPerhitunganController(TppPerhitunganService tppPerhitunganService) {
        this.tppPerhitunganService = tppPerhitunganService;
    }

    /**
     * Get tpp perhitungan by nip, bulan, and tahun
     *
     * @param nip NIP pegawai
     * @param bulan bulan perhitungan
     * @param tahun tahun perhitungan
     * @return list of TppPerhitunganResponse objects url:
     * /tppPerhitungan/rekapTppNip/{nip}/{bulan}/{tahun}
     */
    @GetMapping("detail/nip/{nip}/{bulan}/{tahun}")
    public ResponseEntity<?> getByNipAndBulanAndTahun(
            @PathVariable("nip") String nip,
            @PathVariable("bulan") Integer bulan,
            @PathVariable("tahun") Integer tahun) {

        try {
            TppPerhitungan tppPerhitungan = tppPerhitunganService.detailTppPerhitungan(nip, bulan, tahun);

            // Ambil semua data untuk nip, bulan, tahun yang sama
            Iterable<TppPerhitungan> tppPerhitungans = tppPerhitunganService.listTppPerhitunganByNipBulanTahun(nip, bulan, tahun);

            // Konversi menjadi response object
            var perhitungans = StreamSupport.stream(tppPerhitungans.spliterator(), false)
                    .map(tpp -> new PerhitunganResponse(
                            tpp.namaPerhitungan(),
                            tpp.maksimum(),
                            tpp.nilaiPerhitungan()
                    ))
                    .toList();

            var totalPersen = (float) perhitungans.stream()
                    .mapToDouble(PerhitunganResponse::nilaiPerhitungan)
                    .sum();

            var response = new TppPerhitunganResponse(
                    tppPerhitungan.jenisTpp().name(),
                    tppPerhitungan.kodeOpd(),
                    tppPerhitungan.nip(),
                    tppPerhitungan.nama(),
                    tppPerhitungan.kodePemda(),
                    tppPerhitungan.maksimum(),
                    tppPerhitungan.bulan(),
                    tppPerhitungan.tahun(),
                    perhitungans,
                    totalPersen
            );

            return ResponseEntity.ok(response);

        } catch (TppPerhitunganNipBulanTahunNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Data TPP perhitungan tidak ditemukan untuk parameter yang diberikan");
        }
    }

    /**
     * Get tpp perhitungan by kode opd, bulan, and tahun
     * @param kodeOpd Kode OPD
     * @param bulan bulan perhitungan
     * @param tahun tahun perhitungan
     * @return list of TppPerhitunganResponse objects
     * url: /tppPerhitungan/{kodeOpd}/{bulan}/{tahun}
     */
    @GetMapping("detail/opd/{kodeOpd}/{bulan}/{tahun}")
    public ResponseEntity<?> getByKodeOpdBulanTahun(
            @PathVariable("kodeOpd") String kodeOpd,
            @PathVariable("bulan") Integer bulan,
            @PathVariable("tahun") Integer tahun) {

        // Ambil semua data untuk kode opd, bulan, tahun yang sama
        Iterable<TppPerhitungan> tppPerhitungans = tppPerhitunganService.listTppPerhitunganByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);

        // Cek apakah data ada
        if (!tppPerhitungans.iterator().hasNext()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Data TPP perhitungan tidak ditemukan untuk parameter yang diberikan");
        }

        // Ambil record pertama sebagai referensi
        TppPerhitungan firstRecord = StreamSupport.stream(tppPerhitungans.spliterator(), false)
                .findFirst()
                .orElseThrow();

        // Konversi menjadi response object
        var responses = StreamSupport.stream(tppPerhitungans.spliterator(), false)
                .collect(Collectors.groupingBy(
                        tpp -> tpp.nip(),
                        Collectors.toList()
                ))
                .values()
                .stream()
                .map(group -> {
                    TppPerhitungan first = group.get(0);
                    var perhitungans = group.stream()
                            .map(tpp -> new PerhitunganResponse(
                                    tpp.namaPerhitungan(),
                                    tpp.maksimum(),
                                    tpp.nilaiPerhitungan()
                            ))
                            .toList();

                    var totalPersen = (float) perhitungans.stream()
                            .mapToDouble(PerhitunganResponse::nilaiPerhitungan)
                            .sum();

                    return new TppPerhitunganResponse(
                            first.jenisTpp().name(),
                            first.kodeOpd(),
                            first.nip(),
                            first.nama(),
                            first.kodePemda(),
                            first.maksimum(),
                            first.bulan(),
                            first.tahun(),
                            perhitungans,
                            totalPersen
                    );
                })
                .toList();

        return ResponseEntity.ok(responses);
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
                    tppPerhitunganService.listTppPerhitunganByNipBulanTahun(nip, bulan, tahun).spliterator(), false)
                    .toList();

            if (existingRecords.isEmpty()) {
                // Jika tidak ada record yang sama untuk nip, bulan, tahun, maka ini adalah operasi create, bukan update.
                throw new TppPerhitunganNipBulanTahunSudahAdaException(nip, bulan, tahun);
            }
            
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
                                    request.kodePemda(),
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
                            // Buat data baru jika data yang diubah tidak ada, tetapi pastikan ini masih dalam konteks update
                            // (yaitu, setidaknya satu record untuk nip/bulan/tahun sudah ada)
                            TppPerhitungan newRecord = TppPerhitungan.of(
                                    request.jenisTpp(),
                                    request.kodeOpd(),
                                    request.kodePemda(),
                                    request.nip(),
                                    request.nama(),
                                    request.bulan(),
                                    request.tahun(),
                                    p.maksimum(),
                                    p.namaPerhitungan(),
                                    p.nilaiPerhitungan()
                            );
                            // Tambahkan item baru ke set yang sudah ada.
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
            // Cek nip, bulan, tahun sudah ada
            if (tppPerhitunganService.listTppPerhitunganByNipBulanTahun(request.nip(), request.bulan(), request.tahun()).iterator().hasNext()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Tpp Perhitungan dengan nip " + request.nip() + " bulan " + request.bulan() + " tahun " + request.tahun() + " sudah ada.");
            }

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
                            request.kodePemda(),
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
     * Delete tpp perhitungan by NIP, bulan, and tahun
     * @param nip NIP pegawai
     * @param bulan bulan perhitungan
     * @param tahun tahun perhitungan
     * url: /tppPerhitungan/delete/{nip}/{bulan}/{tahun}
     */
    @DeleteMapping("delete/{nip}/{bulan}/{tahun}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("nip") String nip,
            @PathVariable("bulan") Integer bulan,
            @PathVariable("tahun") Integer tahun) {
        tppPerhitunganService.hapusTppPerhitunganByNipBulanTahun(nip, bulan, tahun);
    }
}
