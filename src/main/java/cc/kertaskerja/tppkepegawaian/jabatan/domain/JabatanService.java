package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanRequest;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;
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

    public List<JabatanWithPegawaiResponse> listJabatanByNipWithPegawaiBatch(List<String> nipPegawais) {
        List<Jabatan> jabatans = jabatanRepository.findAllByNipIn(nipPegawais);

        return jabatans.stream().map(j -> {
            Tpp tppBasic = tppService.detailTpp("BASIC_TPP", j.nip(), 1, 2025);
            return new JabatanWithPegawaiResponse(
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

    public JabatanWithPegawaiResponse tambahJabatanWithTpp(JabatanRequest request) {

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
        return new JabatanWithPegawaiResponse(
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
