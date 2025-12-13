package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.Instant;

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

    private final JabatanRepository jabatanRepository;
    private final OpdRepository opdRepository;
    private final PegawaiRepository pegawaiRepository;
    private final TppService tppService;

    public JabatanService(JabatanRepository jabatanRepository, OpdRepository opdRepository,
            PegawaiRepository pegawaiRepository, TppService tppService) {
        this.jabatanRepository = jabatanRepository;
        this.opdRepository = opdRepository;
        this.pegawaiRepository = pegawaiRepository;
        this.tppService = tppService;
    }

    public Iterable<Jabatan> listAllJabatan() {
        return jabatanRepository.findAll();
    }

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

        return jabatans.stream().map(j -> {
            Tpp tppBasic = tppService.detailTpp("BASIC_TPP", j.nip(), 1, 2025);
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
                    j.tanggalAkhir());
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

    public List<JabatanWithTppPajakResponse> listAllJabatanWithTppPajak() {
        List<Jabatan> jabatans = jabatanRepository.findAll();

        return jabatans.stream().map(j -> {
            Tpp tppBasic = tppService.detailTpp("BASIC_TPP", j.nip(), 1, 2025);

            // Gunakan Float.valueOf() dengan parameter float yang eksplisit
            Float basicTpp;
            if (tppBasic != null && tppBasic.maksimumTpp() != null) {
                basicTpp = tppBasic.maksimumTpp();
            } else if (j.basicTpp() != null) {
                basicTpp = j.basicTpp();
            } else {
                basicTpp = Float.valueOf(0.0f); // Eksplisit menggunakan Float.valueOf()
            }

            Float pajak;
            if (tppBasic != null && tppBasic.pajak() != null) {
                pajak = tppBasic.pajak();
            } else {
                pajak = Float.valueOf(0.0f); // Eksplisit menggunakan Float.valueOf()
            }

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
                basicTpp,
                pajak,
                j.tanggalMulai(),
                j.tanggalAkhir()
            );
        }).toList();
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
        // 1. Cek apakah jabatan sudah ada
        Jabatan existingJabatan = jabatanRepository.findById(id)
                .orElseThrow(() -> new JabatanNotFoundException(id));

        // 2. Update entity Jabatan - Perhatikan: createdDate dan lastModifiedDate adalah Instant
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
        if (updatedJabatan == null || updatedJabatan.id() == null) {
            throw new IllegalStateException("Gagal mengupdate jabatan pegawai");
        }

        // 4. Konversi nilai ke float dengan null safety
        float pajak = request.pajak() != null ? request.pajak().floatValue() : 0.0f;
        float basicTpp = request.basicTpp() != null ? request.basicTpp().floatValue() : 0.0f;

        // 5. Default bulan & tahun (bisa disesuaikan dengan kebutuhan)
        int defaultBulan = 1;
        int defaultTahun = 2025;

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

        Tpp savedTpp = tppService.upsertTpp(tpp);

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
                updatedJabatan.tanggalAkhir());
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
        float pajak = request.pajak().floatValue();
        float basicTpp = request.basicTpp().floatValue();

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
                newJabatanPegawai.tanggalAkhir());
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

    private Jabatan attachNamaPegawai(Jabatan jabatan, Pegawai pegawai) {
        return new Jabatan(
                jabatan.id(),
                jabatan.nip(),
                pegawai.namaPegawai(),
                jabatan.namaJabatan(),
                jabatan.kodeOpd(),
                jabatan.statusJabatan(),
                jabatan.jenisJabatan(),
                jabatan.eselon(),
                jabatan.pangkat(),
                jabatan.golongan(),
                jabatan.basicTpp(),
                jabatan.tanggalMulai(),
                jabatan.tanggalAkhir(),
                jabatan.createdDate(),
                jabatan.lastModifiedDate());
    }
}
