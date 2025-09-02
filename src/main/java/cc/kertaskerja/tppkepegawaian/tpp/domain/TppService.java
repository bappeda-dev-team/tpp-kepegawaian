
package cc.kertaskerja.tppkepegawaian.tpp.domain;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.TppHasilPerhitunganNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.TppPerhitunganBulanTahunNotFoundException;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.TppPerhitunganRepository;

@Service
public class TppService {
    private final TppRepository tppRepository;
    private final TppPerhitunganRepository tppPerhitunganRepository;
    private final OpdRepository opdRepository;
    private final PegawaiRepository pegawaiRepository;
    
    public TppService(TppRepository tppRepository, TppPerhitunganRepository tppPerhitunganRepository, PegawaiRepository pegawaiRepository, OpdRepository opdRepository) {
        this.tppRepository = tppRepository;
        this.tppPerhitunganRepository = tppPerhitunganRepository;
        this.pegawaiRepository = pegawaiRepository;
        this.opdRepository = opdRepository;
    }
    
    public Iterable<Tpp> listTppByKodeOpd(String kodeOpd) {
        return tppRepository.findByKodeOpd(kodeOpd);
    }

    public Iterable<Tpp> listTppByNip(String nip) {
        return tppRepository.findByNip(nip);
    }

    public Iterable<Tpp> listTppByHasilPerhitungan(Float hasilPerhitungan) {
        return tppRepository.findByHasilPerhitungan(hasilPerhitungan);
    }

    public Tpp detailTpp(Long id) {
        return tppRepository.findById(id)
                .orElseThrow(() -> new TppNotFoundException(id));
    }

    public Tpp ubahTpp(Long id, Tpp tpp) {
        if (!tppRepository.existsById(id)) {
            throw new TppNotFoundException(id);
        }

        if (!opdRepository.existsByKodeOpd(tpp.kodeOpd())) {
            throw new OpdNotFoundException(tpp.kodeOpd());
        }

        if (!pegawaiRepository.existsByNip(tpp.nip())) {
            throw new PegawaiNotFoundException(tpp.nip());
        }

        if (!tppPerhitunganRepository.existsByHasilPerhitungan(tpp.hasilPerhitungan())) {
            throw new TppHasilPerhitunganNotFoundException(tpp.hasilPerhitungan());
        }

        if (!tppPerhitunganRepository.existsByBulanAndTahun(tpp.bulan(), tpp.tahun())) {
            throw new TppPerhitunganBulanTahunNotFoundException(tpp.bulan(), tpp.tahun());
        }

        return tppRepository.save(tpp);
    }

    public Tpp tambahTpp(Tpp tpp) {

        if (!opdRepository.existsByKodeOpd(tpp.kodeOpd())) {
            throw new OpdNotFoundException(tpp.kodeOpd());
        }

        if (!pegawaiRepository.existsByNip(tpp.nip())) {
            throw new PegawaiNotFoundException(tpp.nip());
        }

        if (tppRepository.existsByNip(tpp.nip())) {
            throw new TppNipSudahAdaException(tpp.nip());
        }

        if (!tppPerhitunganRepository.existsByHasilPerhitungan(tpp.hasilPerhitungan())) {
            throw new TppHasilPerhitunganNotFoundException(tpp.hasilPerhitungan());
        }

        if (!tppPerhitunganRepository.existsByBulanAndTahun(tpp.bulan(), tpp.tahun())) {
            throw new TppPerhitunganBulanTahunNotFoundException(tpp.bulan(), tpp.tahun());
        }

        return tppRepository.save(tpp);
    }

    public void hapusTpp(Long id) {
        if (!tppRepository.existsById(id)) {
            throw new TppNotFoundException(id);
        }

        tppRepository.deleteById(id);
    }
}
