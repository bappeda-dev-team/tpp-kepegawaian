package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithTppPajakResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithTppPajakRequest;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppService;

@Service
public class JabatanService {

    private static final String BASIC_TPP = "BASIC_TPP";
    private static final int DEFAULT_BULAN = 1;
    private static final int DEFAULT_TAHUN = 2025;

    private final JabatanRepository jabatanRepository;
    private final PegawaiRepository pegawaiRepository;
    private final TppService tppService;

    public JabatanService(JabatanRepository jabatanRepository, OpdRepository opdRepository,
            PegawaiRepository pegawaiRepository, TppService tppService) {
        this.jabatanRepository = jabatanRepository;
        this.pegawaiRepository = pegawaiRepository;
        this.tppService = tppService;
    }

    public Iterable<Jabatan> listAllJabatan() {
        return jabatanRepository.findAll();
    }

    public List<JabatanWithTppPajakResponse> listAllJabatanWithTppByBulanTahunKodeOpd(Integer bulan, Integer tahun, String kodeOpd) {
        int resolvedBulan = (bulan != null) ? bulan : DEFAULT_BULAN;
        int resolvedTahun = (tahun != null) ? tahun : DEFAULT_TAHUN;

        if (resolvedBulan < 1 || resolvedBulan > 12) {
            throw new IllegalArgumentException("Bulan tidak valid");
        }

        if (resolvedTahun < 2000) {
            throw new IllegalArgumentException("Tahun tidak valid");
        }
        Iterable<Jabatan> jabatans = jabatanRepository.findByKodeOpd(kodeOpd);

        List<String> nipPegawais = StreamSupport.stream(jabatans.spliterator(), false)
                .map(Jabatan::nip)
                .toList();

        List<Tpp> tppBasics = tppService.detailTppBatch(BASIC_TPP, nipPegawais, resolvedBulan, resolvedTahun, kodeOpd);

        Map<String, Tpp> tppByNip = tppBasics.stream()
                .collect(Collectors.toMap(Tpp::nip, Function.identity(),
                        (a, b) -> a));

        List<JabatanWithTppPajakResponse> responses = new ArrayList<>();

        for (Jabatan jabatan : jabatans) {
            Tpp tpp = tppByNip.get(jabatan.nip());
            responses.add(mapToJabatanWithTpp(jabatan, tpp));
        }

        return responses;
    }

//    public List<JabatanWithTppPajakResponse> listAllJabatanWithTpp() {
//        Iterable<Jabatan> jabatans = jabatanRepository.findAll();
//        List<JabatanWithTppPajakResponse> responses = new ArrayList<>();
//
//        for (Jabatan jabatan : jabatans) {
//            responses.add(mapToJabatanWithTpp(jabatan));
//        }
//
//        return responses;
//    }

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

    public List<JabatanWithTppPajakResponse> listJabatanByNipWithPegawaiBatch(List<String> nipPegawais) {
        List<Jabatan> jabatans = jabatanRepository.findAllByNipIn(nipPegawais);
        List<Tpp> tppBasics = tppService.detailTppBatch(BASIC_TPP, nipPegawais, DEFAULT_BULAN, DEFAULT_TAHUN, "");

        // Gabungkan
        Map<String, Tpp> tppByNip = tppBasics.stream()
                .collect(Collectors.toMap(
                        Tpp::nip,
                        Function.identity(),
                        (a, b) -> a));

        return jabatans.stream().map(j -> {
            Tpp tppBasic = tppByNip.get(j.nip());

            return new JabatanWithTppPajakResponse(
                    j.id(),
                    j.nip(),
                    j.namaPegawai(),
                    j.namaJabatan(),
                    j.kodeOpd(),
                    j.statusJabatan(),
                    j.jenisJabatan(),
                    j.eselon(),
                    j.pangkat(),
                    j.golongan(),
                    tppBasic.maksimumTpp(),
                    tppBasic.pajak(),
                    j.tanggalMulai(),
                    j.tanggalAkhir(),
                    tppBasic.bulan(),
                    tppBasic.tahun());
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
        Integer bulan = request.bulan();
        Integer tahun = request.tahun();

        int defaultBulan = (bulan != null) ? bulan : DEFAULT_BULAN;
        int defaultTahun = (tahun != null) ? tahun : DEFAULT_TAHUN;
        if (defaultBulan < 1 || defaultBulan > 12) {
            throw new IllegalArgumentException("Bulan tidak valid");
        }

        if (defaultTahun < 2000) {
            throw new IllegalArgumentException("Tahun tidak valid");
        }
        // 1. Cek apakah jabatan sudah ada
        Jabatan existingJabatan = jabatanRepository.findById(id)
                .orElseThrow(() -> new JabatanNotFoundException(id));

        // 2. Update entity Jabatan - Perhatikan: createdDate dan lastModifiedDate
        // adalah Instant
        Jabatan jabatan = new Jabatan(
                id,
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
                request.tanggalMulai(),
                request.tanggalAkhir(),
                existingJabatan.createdDate(),
                Instant.now());

        Jabatan updatedJabatan = jabatanRepository.save(jabatan);

        // 3. Validasi penyimpanan
        if (updatedJabatan.id() == null) {
            throw new IllegalStateException("Gagal mengupdate jabatan pegawai");
        }

        // 4. Konversi nilai ke float dengan null safety
        float pajak = request.pajak() != null ? request.pajak() : 0.0f;
        float basicTpp = request.basicTpp() != null ? request.basicTpp() : 0.0f;

        // 6. Buat atau update entity TPP
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

        Tpp savedTpp;
        if (!Objects.equals(existingJabatan.nip(), request.nip())) {
            savedTpp = tppService.upsertTppReplacingOldNip(tpp, existingJabatan.nip());
        } else {
            savedTpp = tppService.upsertTpp(tpp);
        }

        if (savedTpp == null || savedTpp.id() == null) {
            throw new IllegalStateException("Gagal menyimpan TPP pegawai");
        }

        // 7. Kembalikan response
        return new JabatanWithTppPajakResponse(
                updatedJabatan.id(),
                updatedJabatan.nip(),
                updatedJabatan.namaPegawai(),
                updatedJabatan.namaJabatan(),
                updatedJabatan.kodeOpd(),
                updatedJabatan.statusJabatan(),
                updatedJabatan.jenisJabatan(),
                updatedJabatan.eselon(),
                updatedJabatan.pangkat(),
                updatedJabatan.golongan(),
                savedTpp.maksimumTpp(),
                savedTpp.pajak(),
                updatedJabatan.tanggalMulai(),
                updatedJabatan.tanggalAkhir(),
                savedTpp.bulan(),
                savedTpp.tahun());
    }

    public JabatanWithTppPajakResponse tambahJabatanWithTpp(JabatanWithTppPajakRequest request) {
        // 1. Buat entity Jabatan
        Jabatan jabatan = Jabatan.of(
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
                request.tanggalMulai(),
                request.tanggalAkhir());

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

        // 6. Kembalikan response
        return new JabatanWithTppPajakResponse(
                newJabatanPegawai.id(),
                newJabatanPegawai.nip(),
                newJabatanPegawai.namaPegawai(),
                newJabatanPegawai.namaJabatan(),
                newJabatanPegawai.kodeOpd(),
                newJabatanPegawai.statusJabatan(),
                newJabatanPegawai.jenisJabatan(),
                newJabatanPegawai.eselon(),
                newJabatanPegawai.pangkat(),
                newJabatanPegawai.golongan(),
                savedTpp.maksimumTpp(),
                savedTpp.pajak(),
                newJabatanPegawai.tanggalMulai(),
                newJabatanPegawai.tanggalAkhir(),
                defaultBulan,
                defaultTahun);
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

//    private Jabatan attachNamaPegawai(Jabatan jabatan, Pegawai pegawai) {
//        return new Jabatan(
//                jabatan.id(),
//                jabatan.nip(),
//                pegawai.namaPegawai(),
//                jabatan.namaJabatan(),
//                jabatan.kodeOpd(),
//                jabatan.statusJabatan(),
//                jabatan.jenisJabatan(),
//                jabatan.eselon(),
//                jabatan.pangkat(),
//                jabatan.golongan(),
//                jabatan.basicTpp(),
//                jabatan.tanggalMulai(),
//                jabatan.tanggalAkhir(),
//                jabatan.createdDate(),
//                jabatan.lastModifiedDate());
//    }

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
                jabatan.tanggalMulai(),
                jabatan.tanggalAkhir(),
                tpp.bulan(),
                tpp.tahun());
    }
}
