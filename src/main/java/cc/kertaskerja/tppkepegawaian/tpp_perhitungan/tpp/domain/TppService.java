
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppNipBulanTahunSudahAdaException;

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
    
    public Iterable<Tpp> listTppByOpdBulanTahun(String kodeOpd, Integer bulan, Integer tahun) {
        return tppRepository.findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
    }

    public Tpp detailTpp(JenisTpp jenisTpp, String nip, Integer bulan, Integer tahun) {
        return tppRepository.findByJenisTppAndNipAndBulanAndTahun(jenisTpp, nip, bulan, tahun)
        .orElseThrow(() -> new TppJenisTppNipBulanTahunNotFoundException(jenisTpp, nip, bulan, tahun));
    }
    
    public Tpp ubahTpp(Tpp tpp) {
        if (!opdRepository.existsByKodeOpd(tpp.kodeOpd())) {
            throw new OpdNotFoundException(tpp.kodeOpd());
        }
        
        if (!tppPerhitunganRepository.existsByNipAndBulanAndTahun(tpp.nip(), tpp.bulan(), tpp.tahun())) {
            throw new TppPerhitunganNipBulanTahunNotFoundException(tpp.nip(), tpp.bulan(), tpp.tahun());
        }

        return tppRepository.save(tpp);
    }

    public Tpp tambahTpp(Tpp tpp) {

        if (!opdRepository.existsByKodeOpd(tpp.kodeOpd())) {
            throw new OpdNotFoundException(tpp.kodeOpd());
        }

        if (!tppPerhitunganRepository.existsByNipAndBulanAndTahun(tpp.nip(), tpp.bulan(), tpp.tahun())) {
            throw new TppPerhitunganNipBulanTahunNotFoundException(tpp.nip(), tpp.bulan(), tpp.tahun());
        }

        if (tppRepository.existsByJenisTppAndNipAndBulanAndTahun(tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun())) {
            throw new TppJenisTppNipBulanTahunSudahAdaException(tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun());
        }

        return tppRepository.save(tpp);
    }

    public void hapusTppByNipBulanTahun(String nip, Integer bulan, Integer tahun) {
        tppRepository.deleteByNipAndBulanAndTahun(nip, bulan, tahun);
    }
}
