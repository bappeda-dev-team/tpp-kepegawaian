
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganJenisTppNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganJenisTppNipBulanTahunSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppNipSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppNotFoundException;

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

    public Iterable<Tpp> listTppByNipBulanTahun(String nip, Integer bulan, Integer tahun) {
        return tppRepository.findByNipAndBulanAndTahun(nip, bulan, tahun);
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
        
        if (!tppPerhitunganRepository.existsByJenisTppAndNipAndBulanAndTahun(tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun())) {
            throw new TppPerhitunganJenisTppNipBulanTahunNotFoundException(tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun());
        }

        if (tppPerhitunganRepository.findByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), 
                tpp.nip(), 
                tpp.bulan(), 
                tpp.tahun()).iterator().hasNext()) {
            throw new TppPerhitunganJenisTppNipBulanTahunSudahAdaException(
                tpp.jenisTpp(), 
                tpp.nip(), 
                tpp.bulan(), 
                tpp.tahun());
        }

        return tppRepository.save(tpp);
    }

    public Tpp tambahTpp(Tpp tpp) {

        if (!opdRepository.existsByKodeOpd(tpp.kodeOpd())) {
            throw new OpdNotFoundException(tpp.kodeOpd());
        }

        if (!tppPerhitunganRepository.existsByJenisTppAndNipAndBulanAndTahun(tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun())) {
            throw new TppPerhitunganJenisTppNipBulanTahunNotFoundException(tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun());
        }

        if (tppPerhitunganRepository.findByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), 
                tpp.nip(), 
                tpp.bulan(), 
                tpp.tahun()).iterator().hasNext()) {
            throw new TppPerhitunganJenisTppNipBulanTahunSudahAdaException(
                tpp.jenisTpp(), 
                tpp.nip(), 
                tpp.bulan(), 
                tpp.tahun());
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
