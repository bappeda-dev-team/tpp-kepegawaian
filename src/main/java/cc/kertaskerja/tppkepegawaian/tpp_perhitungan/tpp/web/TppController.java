package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.request.TppRequest;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response.DataTppResponse;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response.DetailTppResponse;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response.RekapTppResponse;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.response.TppTotalTppResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JabatanRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiService;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("tpp")
@Tag(name = "TPP", description = "Pengelolaan perhitungan dan data TPP")
public class TppController {
    private final TppService tppService;
    private final TppPerhitunganService tppPerhitunganService;
    private final PegawaiService pegawaiService;
    private final JabatanRepository jabatanRepository;

    public TppController(
            TppService tppService,
            TppPerhitunganService tppPerhitunganService,
            PegawaiService pegawaiService,
            JabatanRepository jabatanRepository) {
        this.tppService = tppService;
        this.tppPerhitunganService = tppPerhitunganService;
        this.pegawaiService = pegawaiService;
        this.jabatanRepository = jabatanRepository;
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
            @PathVariable("jenisTpp") String jenisTpp,
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

        for (var perhitungan : perhitunganList) {
            String jenisTppKey = perhitungan.jenisTpp();
            perhitunganByJenisTpp.computeIfAbsent(jenisTppKey, k -> new ArrayList<>()).add(perhitungan);
        }

        // Ambil nama pegawai dari data pegawai yang sudah ada
        String employeeName;
        try {
            employeeName = pegawaiService.detailPegawai(nip).namaPegawai();
        } catch (PegawaiNotFoundException e) {
            employeeName = "";
        }

        // Buat list untuk simpan semua data pegawai
        List<DataTppResponse> dataResponses = new ArrayList<>();

        // Proses tpp yang ada untuk setiap pegawai
        for (Tpp tpp : tppList) {
            String jenisTppKey = tpp.jenisTpp();
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

            // Kalkulasi total pajak = totalTpp * pajak%
            Long totalPajak = (long) Math.round(totalTpp * (tpp.pajak() / 100.0f));

            // Kalkulasi total bpjs = totalTpp * bpjs%
            Long totalBpjs = (long) Math.round(totalTpp * (tpp.bpjs() / 100.0f));

            // Kalkulasi total terima TPP = totalTpp - totalPajak - totalBpjs
            Long totalTerimaTpp = totalTpp - totalPajak - totalBpjs;

            // Create detail TPP response
            DetailTppResponse detailTpp = new DetailTppResponse(
                    tpp.id(),
                    tpp.jenisTpp(),
                    (long) Math.round(tpp.maksimumTpp()),
                    tpp.pajak(),
                    tpp.bpjs(),
                    totalPersen,
                    totalTpp,
                    totalPajak,
                    totalBpjs,
                    totalTerimaTpp);

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

                // Ganti dengan data yang sudah ada
                dataResponses.remove(existingData);
                dataResponses.add(new DataTppResponse(
                        existingData.id(),
                        existingData.nama(),
                        existingData.opd(),
                        updatedDetails));
            } else {
                List<DetailTppResponse> details = new ArrayList<>();
                details.add(detailTpp);

                dataResponses.add(new DataTppResponse(
                        tpp.id(),
                        employeeName,
                        tpp.kodeOpd(),
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
            @PathVariable("jenisTpp") String jenisTpp,
            @PathVariable("kodeOpd") String kodeOpd,
            @PathVariable("bulan") Integer bulan,
            @PathVariable("tahun") Integer tahun) {

        // Ambil semua tpp data berdasarkan kode opd, bulan, dan tahun
        Iterable<Tpp> tppList = tppService.listTppByOpdBulanTahun(jenisTpp, kodeOpd, bulan, tahun);

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

            for (var perhitungan : perhitunganList) {
                String jenisTppKey = perhitungan.jenisTpp();
                perhitunganByJenisTpp.computeIfAbsent(jenisTppKey, k -> new ArrayList<>()).add(perhitungan);
            }

            // Ambil nama pegawai dari data pegawai yang sudah ada
            String employeeName;
            try {
                employeeName = pegawaiService.detailPegawai(nip).namaPegawai();
            } catch (PegawaiNotFoundException e) {
                employeeName = "";
            }

            // Buat list untuk simpan semua data pegawai
            List<DataTppResponse> dataResponses = new ArrayList<>();

            // Proses tpp yang ada untuk setiap pegawai
            for (Tpp tpp : employeeTppList) {
                String jenisTppKey = tpp.jenisTpp();
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

                // Kalkulasi total pajak = totalTpp * pajak%
                Long totalPajak = (long) Math.round(totalTpp * (tpp.pajak() / 100.0f));

                // Kalkulasi total bpjs = totalTpp * bpjs%
                Long totalBpjs = (long) Math.round(totalTpp * (tpp.bpjs() / 100.0f));

                // Kalkulasi total terima TPP = totalTpp - totalPajak - totalBpjs
                Long totalTerimaTpp = totalTpp - totalPajak - totalBpjs;

                // Buat detail Tpp response
                DetailTppResponse detailTpp = new DetailTppResponse(
                        tpp.id(),
                        tpp.jenisTpp(),
                        (long) Math.round(tpp.maksimumTpp()),
                        tpp.pajak(),
                        tpp.bpjs(),
                        totalPersen,
                        totalTpp,
                        totalPajak,
                        totalBpjs,
                        totalTerimaTpp);

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

                    // Ganti dengan data yang sudah ada
                    dataResponses.remove(existingData);
                    dataResponses.add(new DataTppResponse(
                            existingData.id(),
                            existingData.nama(),
                            existingData.opd(),
                            updatedDetails));
                } else {
                    List<DetailTppResponse> details = new ArrayList<>();
                    details.add(detailTpp);

                    dataResponses.add(new DataTppResponse(
                            tpp.id(),
                            employeeName,
                            tpp.kodeOpd(),
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
                request.pajak(),
                request.bpjs(),
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

        // Kalkulasi total pajak = totaltpp * pajak%
        Float totalPajak = (float) Math.round(totaltpp * (request.pajak() / 100.0f));

        // Kalkulasi total bpjs = totaltpp * bpjs%
        Float totalBpjs = (float) Math.round(totaltpp * (request.bpjs() / 100.0f));

        // Kalkulasi total terima TPP = totaltpp - totalPajak - totalBpjs
        Float totalTerimaTpp = totaltpp - totalPajak - totalBpjs;

        // Buat respons dengan nilai terhitung (bulan dan tahun dari path variables)
        TppTotalTppResponse response = new TppTotalTppResponse(
                saved.jenisTpp(),
                saved.kodeOpd(),
                saved.nip(),
                saved.kodePemda(),
                saved.maksimumTpp(),
                saved.pajak(),
                saved.bpjs(),
                bulan,
                tahun,
                hasilPerhitungan,
                totaltpp,
                totalPajak,
                totalBpjs,
                totalTerimaTpp);

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

        Float maksimumTpp = resolveMaksimumTppFromJabatan(request.nip(), request.maksimumTpp());

        Tpp tpp = Tpp.of(
                request.jenisTpp(),
                request.kodeOpd(),
                request.nip(),
                request.kodePemda(),
                maksimumTpp,
                request.pajak(),
                request.bpjs(),
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
        Float totaltpp = maksimumTpp * (hasilPerhitungan / 100.0f);

        // Kalkulasi total pajak = totaltpp * pajak%
        Float totalPajak = (float) Math.round(totaltpp * (request.pajak() / 100.0f));

        // Kalkulasi total bpjs = totaltpp * bpjs%
        Float totalBpjs = (float) Math.round(totaltpp * (request.bpjs() / 100.0f));

        // Kalkulasi total terima TPP = totaltpp - totalPajak - totalBpjs
        Float totalTerimaTpp = totaltpp - totalPajak - totalBpjs;

        // Buat respons dengan nilai terhitung (bulan dan tahun saja dalam respons)
        TppTotalTppResponse response = new TppTotalTppResponse(
                saved.jenisTpp(),
                saved.kodeOpd(),
                saved.nip(),
                saved.kodePemda(),
                saved.maksimumTpp(),
                saved.pajak(),
                saved.bpjs(),
                request.bulan(),
                request.tahun(),
                hasilPerhitungan,
                totaltpp,
                totalPajak,
                totalBpjs,
                totalTerimaTpp);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    private Float resolveMaksimumTppFromJabatan(String nip, Float fallbackMaksimumTpp) {
        Optional<Jabatan> jabatanOpt = jabatanRepository.findByNip(nip);
        return jabatanOpt
                .flatMap(j -> Optional.ofNullable(j.basicTpp()))
                .orElse(fallbackMaksimumTpp);
    }

    /**
     * Delete tpp by nip, bulan, tahun
     * @param nip, bulan, tahun tpp
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
