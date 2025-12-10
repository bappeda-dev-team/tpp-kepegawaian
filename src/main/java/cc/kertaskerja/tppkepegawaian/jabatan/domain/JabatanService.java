package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JabatanService {

    private final JabatanRepository jabatanRepository;
    private final OpdRepository opdRepository;
    private final PegawaiRepository pegawaiRepository;

    public JabatanService(JabatanRepository jabatanRepository, OpdRepository opdRepository, PegawaiRepository pegawaiRepository) {
        this.jabatanRepository = jabatanRepository;
        this.opdRepository = opdRepository;
        this.pegawaiRepository = pegawaiRepository;
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
                jabatan.tanggalMulai(),
                jabatan.tanggalAkhir()
            ));
        }

        return responses;
    }

    public List<JabatanWithPegawaiResponse> listJabatanByNipWithPegawaiBatch(List<String> nipPegawais) {
        List<Jabatan> jabatans = jabatanRepository.findAllByNipIn(nipPegawais);
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
                jabatan.tanggalMulai(),
                jabatan.tanggalAkhir()
            ));
        }

        return responses;
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
                jabatan.tanggalMulai(),
                jabatan.tanggalAkhir()
            ));
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

        Optional<Pegawai> pegawai = pegawaiRepository.findByNip(jabatan.nip());
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
            jabatan.tanggalMulai(),
            jabatan.tanggalAkhir(),
            jabatan.createdDate(),
            jabatan.lastModifiedDate()
        );
    }
}
