package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.domain.periode.PeriodeUtils;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithTppPajakResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithTppPajakRequest;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppService;

@Service
public class JabatanService {

    private static final String BASIC_TPP = "BASIC_TPP";
    private static final int DEFAULT_TANGGAL = 1;
    private static final int DEFAULT_BULAN = 1;
    private static final int DEFAULT_TAHUN = 2025;

    private final JabatanRepository jabatanRepository;
    private final PegawaiRepository pegawaiRepository;
    private final TppService tppService;

    public JabatanService(JabatanRepository jabatanRepository, PegawaiRepository pegawaiRepository, TppService tppService) {
        this.jabatanRepository = jabatanRepository;
        this.pegawaiRepository = pegawaiRepository;
        this.tppService = tppService;
    }

    public Iterable<Jabatan> listAllJabatan() {
        return jabatanRepository.findAll();
    }

    public List<JabatanWithTppPajakResponse> listAllJabatanWithTppByBulanTahunKodeOpd(Integer bulan, Integer tahun,
            String kodeOpd) {
        int resolvedBulan = (bulan != null) ? bulan : DEFAULT_BULAN;
        int resolvedTahun = (tahun != null) ? tahun : DEFAULT_TAHUN;

        if (resolvedBulan < 1 || resolvedBulan > 12) {
            throw new IllegalArgumentException("Bulan tidak valid");
        }

        if (resolvedTahun < 2000) {
            throw new IllegalArgumentException("Tahun tidak valid");
        }

        // GET DATA JABATAN BY KODE OPD (ALL)
        Iterable<Jabatan> jabatans = jabatanRepository.findByKodeOpd(kodeOpd);

        // START THE THING
        // Mulai filter dan ambil pegawai serta gabungkan dengan tpp
        List<Jabatan> jabatanList = StreamSupport.stream(jabatans.spliterator(), false)
                .toList();

        // ambil jabatan terbaru per nip
        Map<String, Jabatan> latestJabatanPerNip = PeriodeUtils.latestPerKeyUntil(
                jabatanList,
                bulan,
                tahun,
                Jabatan::nip);

        // ambil nip unik
        List<String> nipPegawais = new ArrayList<>(latestJabatanPerNip.keySet());

        Map<String, Tpp> tppByNip = tppService.detailTppBatchByNip(
                BASIC_TPP,
                nipPegawais,
                resolvedBulan,
                resolvedTahun,
                kodeOpd);

        List<JabatanWithTppPajakResponse> responses = new ArrayList<>();

        for (Jabatan jabatan : latestJabatanPerNip.values()) {
            Tpp tpp = tppByNip.get(jabatan.nip());
            responses.add(mapToJabatanWithTpp(jabatan, tpp));
        }

        return responses;
    }

    // public List<JabatanWithTppPajakResponse> listAllJabatanWithTpp() {
    // Iterable<Jabatan> jabatans = jabatanRepository.findAll();
    // List<JabatanWithTppPajakResponse> responses = new ArrayList<>();
    //
    // for (Jabatan jabatan : jabatans) {
    // responses.add(mapToJabatanWithTpp(jabatan));
    // }
    //
    // return responses;
    // }

    public Iterable<Jabatan> listJabatanByKodeOpd(String kodeOpd) {
        return jabatanRepository.findByKodeOpd(kodeOpd);
    }

    public List<Jabatan> listJabatanByNip(String nip) {
        Iterable<Jabatan> jabatans = jabatanRepository.findAllByNip(nip);
        List<Jabatan> result = new ArrayList<>();
        jabatans.forEach(result::add);
        return result;
    }

    public List<JabatanWithPegawaiResponse> listJabatanByNipWithPegawai(String nip) {
        Iterable<Jabatan> jabatans = jabatanRepository.findAllByNip(nip);
        List<JabatanWithPegawaiResponse> responses = new ArrayList<>();

        for (Jabatan jabatan : jabatans) {
            String namaPegawai = resolveNamaPegawai(jabatan);

            responses.add(new JabatanWithPegawaiResponse(
                    jabatan.id(),
                    jabatan.nip(),
                    namaPegawai,
                    jabatan.namaJabatan(),
                    jabatan.kodeOpd(),
                    jabatan.statusJabatan(),
                    jabatan.jenisJabatan(),
                    jabatan.eselon(),
                    jabatan.pangkat(),
                    jabatan.golongan(),
                    jabatan.basicTpp(),
                    jabatan.tanggalMulai(),
                    jabatan.tanggalAkhir()));
        }

        return responses;
    }

    public List<JabatanWithTppPajakResponse> listJabatanByNipWithPegawaiBatch(List<String> nipPegawais, Integer bulan, Integer tahun, String kodeOpd) {

        int resolvedBulan = (bulan != null) ? bulan : DEFAULT_BULAN;
        int resolvedTahun = (tahun != null) ? tahun : DEFAULT_TAHUN;

        if (resolvedBulan < 1 || resolvedBulan > 12) {
            throw new IllegalArgumentException("Bulan tidak valid");
        }

        if (resolvedTahun < 2000) {
            throw new IllegalArgumentException("Tahun tidak valid");
        }

        List<Jabatan> jabatans = jabatanRepository.findAllByNipIn(nipPegawais);

        // ambil jabatan terbaru per nip
        Map<String, Jabatan> latestJabatanPerNip = PeriodeUtils.latestPerKeyUntil(
                jabatans,
                bulan,
                tahun,
                Jabatan::nip);

        Map<String, Tpp> tppByNip = tppService.detailTppBatchByNip(BASIC_TPP, nipPegawais, resolvedBulan, resolvedTahun, kodeOpd);

        return latestJabatanPerNip.values().stream().map( j -> {
            Tpp tppBasic = tppByNip.get(j.nip());

            return mapToJabatanWithTpp(j, tppBasic);
        }).toList();
    }

    public List<JabatanWithPegawaiResponse> listJabatanByKodeOpdWithPegawai(String kodeOpd) {
        Iterable<Jabatan> jabatans = jabatanRepository.findByKodeOpd(kodeOpd);
        List<JabatanWithPegawaiResponse> responses = new ArrayList<>();

        for (Jabatan jabatan : jabatans) {
            String namaPegawai = resolveNamaPegawai(jabatan);

            responses.add(new JabatanWithPegawaiResponse(
                    jabatan.id(),
                    jabatan.nip(),
                    namaPegawai,
                    jabatan.namaJabatan(),
                    jabatan.kodeOpd(),
                    jabatan.statusJabatan(),
                    jabatan.jenisJabatan(),
                    jabatan.eselon(),
                    jabatan.pangkat(),
                    jabatan.golongan(),
                    jabatan.basicTpp(),
                    jabatan.tanggalMulai(),
                    jabatan.tanggalAkhir()));
        }

        return responses;
    }

    public Jabatan detailJabatan(Long id) {
        return jabatanRepository.findById(id)
                .orElseThrow(() -> new JabatanNotFoundException(id));
    }

    public Jabatan ubahJabatan(Long id, Jabatan jabatan) {
        if (!jabatanRepository.existsById(id)) {
            throw new JabatanNotFoundException(id);
        }

        return jabatanRepository.save(jabatan);
    }

    public Jabatan tambahJabatan(Jabatan jabatan) {
        return jabatanRepository.save(jabatan);
    }

    public JabatanWithTppPajakResponse ubahJabatanWithTpp(Long id, JabatanWithTppPajakRequest request) {
        // 5. Default bulan & tahun (bisa disesuaikan dengan kebutuhan)
        // BUAT TPP
        Integer bulan = request.bulan();
        Integer tahun = request.tahun();

        int defaultBulanTpp = (bulan == null) ? DEFAULT_BULAN : bulan;
        int defaultTahunTpp = (tahun == null) ? DEFAULT_TAHUN : tahun;
        if (defaultBulanTpp < 1 || defaultBulanTpp > 12) {
            throw new IllegalArgumentException("Bulan tidak valid");
        }

        if (defaultTahunTpp < 2000) {
            throw new IllegalArgumentException("Tahun tidak valid");
        }
        // 1. Cek apakah jabatan sudah ada
        jabatanRepository.findById(id)
                .orElseThrow(() -> new JabatanNotFoundException(id));

        // 2. Update entity Jabatan - Perhatikan: createdDate dan lastModifiedDate
        Jabatan jabatan = mapToJabatan(request);
        if (request.jabatanId() == null) {
            jabatan = jabatan.withId(id);
        }

        Jabatan updatedJabatan = jabatanRepository.save(jabatan);

        // 3. Validasi penyimpanan
        if (updatedJabatan.id() == null) {
            throw new IllegalStateException("Gagal mengupdate jabatan pegawai");
        }

        // 4. Konversi nilai ke float dengan null safety
        float newPajak = request.pajak() != null ? request.pajak() : 0.0f;
        float newMaxTpp = request.basicTpp() != null ? request.basicTpp() : 0.0f;

        // 6. Buat entity TPP Basic
        // dengan pajak, bpjs, dan max tpp sendiri
        Tpp tpp = Tpp.basicTpp(
                request.kodeOpd(),
                request.nip(),
                "--",
                newMaxTpp,
                newPajak,
                defaultBulanTpp,
                defaultTahunTpp);

        // Save TPP dengan timpa nip dkk
        // buat record baru of Tpp
        Tpp savedTpp = tppService.saveTimpaTpp(tpp);

        if (savedTpp == null || savedTpp.id() == null) {
            throw new IllegalStateException("Gagal menyimpan TPP pegawai");
        }

        // 7. Kembalikan response
        return mapToJabatanWithTpp(updatedJabatan, savedTpp);
    }

    public JabatanWithTppPajakResponse tambahJabatanWithTpp(JabatanWithTppPajakRequest request) {
        // 1. Buat entity Jabatan
        Jabatan jabatan = mapToJabatan(request);

        Jabatan newJabatanPegawai = tambahJabatan(jabatan);

        // 2. Validasi penyimpanan
        if (newJabatanPegawai == null || newJabatanPegawai.id() == null) {
            throw new IllegalStateException("Gagal menyimpan jabatan pegawai");
        }

        // 3. Konversi nilai ke float (hindari duplikasi)
        assert request.pajak() != null;
        float pajak = request.pajak();
        assert request.basicTpp() != null;
        float basicTpp = request.basicTpp();

        // 4. Default bulan & tahun
        int defaultBulan = 1;
        int defaultTahun = 2025;

        // 5. Buat entity TPP
        // mestinya dibuat upsert
        // jika bulan, tahun, nip, kodeOpd sama
        Tpp tpp = Tpp.of(
                "BASIC_TPP",
                request.kodeOpd(),
                request.nip(),
                "--",
                basicTpp,
                pajak,
                0.01f,
                defaultBulan,
                defaultTahun);

        Tpp savedTpp = tppService.upsertTpp(tpp);

        if (savedTpp == null || savedTpp.id() == null) {
            throw new IllegalStateException("Gagal menyimpan TPP pegawai");
        }

        // newJabatanPegawai
        // savedTpp
        // 6. Kembalikan response
        return mapToJabatanWithTpp(newJabatanPegawai, savedTpp);
    }

    public void hapusJabatan(Long id) {
        if (!jabatanRepository.existsById(id)) {
            throw new JabatanNotFoundException(id);
        }

        jabatanRepository.deleteById(id);
    }

    private String resolveNamaPegawai(Jabatan jabatan) {
        if (jabatan.namaPegawai() != null && !jabatan.namaPegawai().isBlank()) {
            return jabatan.namaPegawai();
        }

        String nip = jabatan.nip();
        if (nip == null || nip.isBlank()) {
            return null;
        }

        Optional<Pegawai> pegawai = pegawaiRepository.findByNip(nip);
        return pegawai.map(Pegawai::namaPegawai).orElse(null);
    }

    private Jabatan mapToJabatan(JabatanWithTppPajakRequest request) {
        // Set tanggalMulai dan tanggalAkhir dari
        // bulanMulai + tahunMulai
        // bulanBerakhir + tahunBerakhir
        // karena jabatan sebetulnya butuh tanggal
        // tetapi kita skip dulu
        // asumsikan selalu tanggal 1 di DEFAULT_TANGGAL
        // Y M D
        LocalDate tanggalMulai = createTanggal(request.tahunMulai(), request.bulanMulai());
        LocalDate tanggalAkhir = createTanggal(request.tahunBerakhir(), request.bulanBerakhir());
        return new Jabatan(
                request.jabatanId(),
                request.nip(),
                request.namaPegawai(),
                request.namaJabatan(),
                request.kodeOpd(),
                request.statusJabatan(),
                request.jenisJabatan(),
                request.eselon(),
                request.pangkat(),
                request.golongan(),
                request.basicTpp(),
                tanggalMulai,
                tanggalAkhir,
                Instant.now(),
                Instant.now());
    }

    private JabatanWithTppPajakResponse mapToJabatanWithTpp(Jabatan jabatan, Tpp tpp) {
        return new JabatanWithTppPajakResponse(
                jabatan.id(),
                jabatan.nip(),
                jabatan.namaPegawai(),
                jabatan.namaJabatan(),
                jabatan.kodeOpd(),
                jabatan.statusJabatan(),
                jabatan.jenisJabatan(),
                jabatan.eselon(),
                jabatan.pangkat(),
                jabatan.golongan(),
                tpp.maksimumTpp(),
                tpp.pajak(),
                // bulan mulai, tahun mulai
                convertToBulanInteger(jabatan.tanggalMulai()),
                convertToTahunInteger(jabatan.tanggalMulai()),
                // bulan berakhir, tahun berakhir
                convertToBulanInteger(jabatan.tanggalAkhir()),
                convertToTahunInteger(jabatan.tanggalAkhir()),
                tpp.bulan(),
                tpp.tahun());
    }

    // Y M D
    // akomodir tanggalMulai dan tanggalAkhir di Jabatan
    // buat LocalDate dari tahun, bulan, tanggal dari request
    // untuk memudahkan sekaligus guard frontend
    private LocalDate createTanggal(Integer tahun, Integer bulan) {
        if (bulan == null || tahun == null) {
            return null;
        }

        return LocalDate.of(tahun, bulan, JabatanService.DEFAULT_TANGGAL);
    }

    private Integer convertToBulanInteger(LocalDate tanggal) {
        if (tanggal == null) {
            return null;
        }

        return tanggal.getMonthValue();
    }

    private Integer convertToTahunInteger(LocalDate tanggal) {
        if (tanggal == null) {
            return null;
        }

        return tanggal.getYear();
    }
}
