package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.JenisTpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.request.TppRequest;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response.DataTppResponse;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response.DetailTppResponse;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response.RekapTppResponse;
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
     * Get tpp by jenis tpp, NIP, bulan, dan tahun
     * @param jenisTpp jenis TPP
     * @param nip NIP pegawai
     * @param bulan bulan
     * @param tahun tahun
     * @return Tpp object with calculated total
     * url: /tpp/{jenisTpp}/{nip}/{bulan}/{tahun}
     */
    @GetMapping("rekapTppNip/{jenisTpp}/{nip}/{bulan}/{tahun}")
    public ResponseEntity<RekapTppResponse> getByJenisTppNipBulanTahun(
            @PathVariable("jenisTpp") JenisTpp jenisTpp,
            @PathVariable("nip") String nip,
            @PathVariable("bulan") Integer bulan,
            @PathVariable("tahun") Integer tahun) {
        
        // Ambil semua tpp data berdasarkan kode opd, bulan, dan tahun
        Iterable<Tpp> tppList = tppService.listTppByNipBulanTahun(nip, bulan, tahun);

        var perhitunganList = StreamSupport.stream(
                tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(
                        nip,
                        bulan,
                        tahun).spliterator(), false)
                .collect(Collectors.toList());
        
        // Group data tpp berdasarkan nip
        Map<String, List<TppPerhitungan>> perhitunganByJenisTpp = new HashMap<>();
        String employeeName = "";
        
        for (var perhitungan : perhitunganList) {
            String jenisTppKey = perhitungan.jenisTpp().name();
            perhitunganByJenisTpp.computeIfAbsent(jenisTppKey, k -> new ArrayList<>()).add(perhitungan);
            
            if (employeeName.isEmpty() && perhitungan.nama() != null) {
                employeeName = perhitungan.nama();
            }
        }
        
        // cek apakah ada data nama di tpp perhitungan
        if (employeeName.isEmpty()) {
            List<TppPerhitungan> specificJenisTppList = perhitunganByJenisTpp.get(jenisTpp.name());
            if (specificJenisTppList != null && !specificJenisTppList.isEmpty()) {
                TppPerhitungan firstPerhitungan = specificJenisTppList.get(0);
                if (firstPerhitungan.nama() != null) {
                    employeeName = firstPerhitungan.nama();
                }
            }
        }
        
        // Buat list untuk simpan semua data pegawai
        List<DataTppResponse> dataResponses = new ArrayList<>();
        
        // Proses tpp yang ada untuk setiap pegawai
        for (Tpp tpp : tppList) {
            String jenisTppKey = tpp.jenisTpp().name();
            List<TppPerhitungan> perhitunganForTpp = perhitunganByJenisTpp.getOrDefault(jenisTppKey, new ArrayList<>());
            
            // kalkulasi total persentase untuk jenis tpp
            Float totalPersen = 0.0f;
            for (var perhitungan : perhitunganForTpp) {
                if (perhitungan.nilaiPerhitungan() != null) {
                    totalPersen += perhitungan.nilaiPerhitungan();
                }
            }
            
            // Kalkulasi total tpp untuk jenis tpp
            Long totalTpp = (long) Math.round(tpp.maksimumTpp() * (totalPersen / 100.0f));
            
            // Create detail TPP response
            DetailTppResponse detailTpp = new DetailTppResponse(
                    tpp.id(),
                    tpp.jenisTpp().name(),
                    (long) Math.round(tpp.maksimumTpp()),
                    totalPersen,
                    totalTpp);
            
            // Cek jika pegawai sudah ada
            DataTppResponse existingData = null;
            for (DataTppResponse data : dataResponses) {
                if (data.nama().equals(employeeName) && data.opd().equals(tpp.kodeOpd())) {
                    existingData = data;
                    break;
                }
            }
            
            if (existingData != null) {
                // Tambah data yang ada
                List<DetailTppResponse> updatedDetails = new ArrayList<>(existingData.detailTpp());
                updatedDetails.add(detailTpp);
                
                // Kalkilasi total perolehan
                Long newTotalPerolehan = existingData.totalPerolehan() + totalTpp;
                
                // Ganti dengan data yang sudah ada
                dataResponses.remove(existingData);
                dataResponses.add(new DataTppResponse(
                        existingData.id(),
                        existingData.nama(),
                        existingData.opd(),
                        newTotalPerolehan,
                        updatedDetails));
            } else {
                List<DetailTppResponse> details = new ArrayList<>();
                details.add(detailTpp);
                
                dataResponses.add(new DataTppResponse(
                        tpp.id(),
                        employeeName,
                        tpp.kodeOpd(),
                        totalTpp,
                        details));
            }
        }
        
        // Hasil Response
        RekapTppResponse response = new RekapTppResponse(
                bulan,
                tahun,
                nip,
                dataResponses);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get tpp by jenis tpp, kode OPD, bulan, dan tahun
     * @param jenisTpp jenis TPP
     * @param kodeOpd kode OPD
     * @param bulan bulan
     * @param tahun tahun
     * @return List of Tpp objects with calculated totals for all employees in the OPD
     * url: /tpp/rekapTppOpd/{jenisTpp}/{kodeOpd}/{bulan}/{tahun}
     */
    @GetMapping("rekapTppOpd/{jenisTpp}/{kodeOpd}/{bulan}/{tahun}")
    public ResponseEntity<List<RekapTppResponse>> getByJenisTppOpdBulanTahun(
            @PathVariable("jenisTpp") JenisTpp jenisTpp,
            @PathVariable("kodeOpd") String kodeOpd,
            @PathVariable("bulan") Integer bulan,
            @PathVariable("tahun") Integer tahun) {

        // Ambil semua tpp data berdasarkan kode opd, bulan, dan tahun
        Iterable<Tpp> tppList = tppService.listTppByOpdBulanTahun(kodeOpd, bulan, tahun);

        // Group data tpp berdasarkan nip
        Map<String, List<Tpp>> tppByNip = new HashMap<>();
        for (Tpp tpp : tppList) {
            tppByNip.computeIfAbsent(tpp.nip(), k -> new ArrayList<>()).add(tpp);
        }

        List<RekapTppResponse> responses = new ArrayList<>();

        // Proses setiap pegawai (NIP)
        for (Map.Entry<String, List<Tpp>> entry : tppByNip.entrySet()) {
            String nip = entry.getKey();
            List<Tpp> employeeTppList = entry.getValue();

            var perhitunganList = StreamSupport.stream(
                    tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(
                            nip,
                            bulan,
                            tahun).spliterator(), false)
                    .collect(Collectors.toList());

            // Group kalkulasi berdasarkan jenis tpp
            Map<String, List<TppPerhitungan>> perhitunganByJenisTpp = new HashMap<>();
            String employeeName = "";

            for (var perhitungan : perhitunganList) {
                String jenisTppKey = perhitungan.jenisTpp().name();
                perhitunganByJenisTpp.computeIfAbsent(jenisTppKey, k -> new ArrayList<>()).add(perhitungan);

                if (employeeName.isEmpty() && perhitungan.nama() != null) {
                    employeeName = perhitungan.nama();
                }
            }

            // cek apakah ada data nama di tpp perhitungan
            if (employeeName.isEmpty()) {
                List<TppPerhitungan> specificJenisTppList = perhitunganByJenisTpp.get(jenisTpp.name());
                if (specificJenisTppList != null && !specificJenisTppList.isEmpty()) {
                    TppPerhitungan firstPerhitungan = specificJenisTppList.get(0);
                    if (firstPerhitungan.nama() != null) {
                        employeeName = firstPerhitungan.nama();
                    }
                }
            }

            // Buat list untuk simpan semua data pegawai
            List<DataTppResponse> dataResponses = new ArrayList<>();

            // Proses tpp yang ada untuk setiap pegawai
            for (Tpp tpp : employeeTppList) {
                String jenisTppKey = tpp.jenisTpp().name();
                List<TppPerhitungan> perhitunganForTpp = perhitunganByJenisTpp.getOrDefault(jenisTppKey, new ArrayList<>());

                // kalkulasi total persentase untuk jenis tpp
                Float totalPersen = 0.0f;
                for (var perhitungan : perhitunganForTpp) {
                    if (perhitungan.nilaiPerhitungan() != null) {
                        totalPersen += perhitungan.nilaiPerhitungan();
                    }
                }

                // Kalkulasi tpp berdasarkan jenis tpp
                Long totalTpp = (long) Math.round(tpp.maksimumTpp() * (totalPersen / 100.0f));

                // Buat detail Tpp response
                DetailTppResponse detailTpp = new DetailTppResponse(
                        tpp.id(),
                        tpp.jenisTpp().name(),
                        (long) Math.round(tpp.maksimumTpp()),
                        totalPersen,
                        totalTpp);

                // Cek jika sudah ada data di pegawai
                DataTppResponse existingData = null;
                for (DataTppResponse data : dataResponses) {
                    if (data.nama().equals(employeeName) && data.opd().equals(tpp.kodeOpd())) {
                        existingData = data;
                        break;
                    }
                }

                if (existingData != null) {
                    // Tambah data yang ada
                    List<DetailTppResponse> updatedDetails = new ArrayList<>(existingData.detailTpp());
                    updatedDetails.add(detailTpp);

                    // Kalkilasi total perolehan
                    Long newTotalPerolehan = existingData.totalPerolehan() + totalTpp;

                    // Ganti dengan data yang sudah ada
                    dataResponses.remove(existingData);
                    dataResponses.add(new DataTppResponse(
                            existingData.id(),
                            existingData.nama(),
                            existingData.opd(),
                            newTotalPerolehan,
                            updatedDetails));
                } else {
                    List<DetailTppResponse> details = new ArrayList<>();
                    details.add(detailTpp);

                    dataResponses.add(new DataTppResponse(
                            tpp.id(),
                            employeeName,
                            tpp.kodeOpd(),
                            totalTpp,
                            details));
                }
            }

            // Hasil response
            RekapTppResponse response = new RekapTppResponse(
                    bulan,
                    tahun,
                    nip,
                    dataResponses);

            responses.add(response);
        }

        return ResponseEntity.ok(responses);
    }

    /**
     * Update tpp by NIP, bulan, dan tahun
     * @param nip NIP pegawai
     * @param bulan bulan
     * @param tahun tahun
     * @param request tpp update request
     * @return updated Tpp object
     * url: /tpp/update/{nip}/{bulan}/{tahun}
     */
    @PutMapping("update/{nip}/{bulan}/{tahun}")
    public ResponseEntity<TppTotalTppResponse> updateByNipBulanTahun(
            @PathVariable("nip") String nip,
            @PathVariable("bulan") Integer bulan,
            @PathVariable("tahun") Integer tahun,
            @Valid @RequestBody TppRequest request) {
        
        // Temukan data NIP, bulan, dan tahun dari TPP
        var existingTppList = tppService.listTppByNipBulanTahun(nip, bulan, tahun);
        
        // Ambil data pertama TPP (nip, bulan, tahun)
        Tpp existingTpp = existingTppList.iterator().next();
        
        // Create updated TPP object with the existing ID and timestamps
        Tpp updatedTpp = new Tpp(
                existingTpp.id(),
                request.jenisTpp(),
                request.kodeOpd(),
                nip,
                request.kodePemda(),
                request.maksimumTpp(),
                bulan,
                tahun,
                existingTpp.createdDate(),
                Instant.now()
        );
        
        Tpp saved = tppService.ubahTpp(updatedTpp);
        
        // Ambil data perhitungan berdasarkan pada NIP, bulan, dan tahun dari path variables
        var perhitunganList = StreamSupport.stream(
                tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(
                        nip,
                        bulan,
                        tahun).spliterator(), false)
                .collect(Collectors.toList());
        
        // Kalkulasi hasilPerhitungan (total persen) dari perhitungan
        Float hasilPerhitungan = 0.0f;
        for (var perhitungan : perhitunganList) {
            if (perhitungan.nilaiPerhitungan() != null) {
                hasilPerhitungan += perhitungan.nilaiPerhitungan();
            }
        }
        
        // Kalkulasi totaltpp = maksimumTpp * (hasilPerhitungan / 100)
        Float totaltpp = request.maksimumTpp() * (hasilPerhitungan / 100.0f);
        
        // Buat respons dengan nilai terhitung (bulan dan tahun dari path variables)
        TppTotalTppResponse response = new TppTotalTppResponse(
                saved.jenisTpp().name(),
                saved.kodeOpd(),
                saved.nip(),
                saved.kodePemda(),
                saved.maksimumTpp(),
                bulan,
                tahun,
                hasilPerhitungan,
                totaltpp);
        
        return ResponseEntity.ok(response);
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
        var perhitunganList = StreamSupport.stream(
                tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(
                        request.nip(),
                        request.bulan(),
                        request.tahun()).spliterator(), false)
                .collect(Collectors.toList());

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
    @DeleteMapping("delete/{nip}/{bulan}/{tahun}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("nip") String nip,
            @PathVariable("bulan") Integer bulan,
            @PathVariable("tahun") Integer tahun) {
        
        tppService.hapusTppByNipBulanTahun(nip, bulan, tahun);
    }
}

