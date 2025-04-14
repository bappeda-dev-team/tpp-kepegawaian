package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class PegawaiService {
    private final PegawaiRepository pegawaiRepository;

    public PegawaiService(PegawaiRepository pegawaiRepository) {
        this.pegawaiRepository = pegawaiRepository;
    }

    public Page<Pegawai> listPegawaiAktif(String kodeOpd, Integer tahun, Integer bulan) {
        return pegawaiRepository.listPegawaiOpdByTahunBulan(kodeOpd, tahun, bulan);
    }

    public Pegawai detailPegawai(String nip) {
        return pegawaiRepository.findByNip(nip)
                .orElseThrow(() -> new PegawaiNotFoundException(nip));
    }

    public Pegawai tambahPegawai(Pegawai pegawai) {
        if (pegawaiRepository.nipSudahAda(pegawai.nip())) {
            throw new PegawaiSudahAdaException(pegawai.nip());
        }
        return pegawaiRepository.save(pegawai);
    }

    public void hapusPegawai(String nip) {
        pegawaiRepository.deleteByNip(nip);
    }
}
