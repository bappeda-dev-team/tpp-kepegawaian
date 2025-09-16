package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganJenisTppNipBulanTahunSudahAdaException;

import org.springframework.stereotype.Service;

@Service
public class TppPerhitunganService {
    private final TppPerhitunganRepository tppPerhitunganRepository;
    private final PegawaiRepository pegawaiRepository;
    private final OpdRepository opdRepository;
    
    public TppPerhitunganService(TppPerhitunganRepository tppPerhitunganRepository, PegawaiRepository pegawaiRepository,
                                OpdRepository opdRepository) {
        this.tppPerhitunganRepository = tppPerhitunganRepository;
        this.pegawaiRepository = pegawaiRepository;
        this.opdRepository = opdRepository;
    }

    public Iterable<TppPerhitungan> listTppPerhitunganByJenisTppAndNipAndBulanAndTahun(JenisTpp jenisTpp, String nip, Integer bulan, Integer tahun) {
        return tppPerhitunganRepository.findByJenisTppAndNipAndBulanAndTahun(jenisTpp, nip, bulan, tahun);
    }
    
    public Iterable<TppPerhitungan> listTppPerhitunganByKodeOpd(String kodeOpd) {
        return tppPerhitunganRepository.findByKodeOpd(kodeOpd);
    }
    
    public Iterable<TppPerhitungan> listTppPerhitunganByNip(String nip) {
        return tppPerhitunganRepository.findByNip(nip);
    }
    
    public Iterable<TppPerhitungan> getByNipBulanTahun(String nip, Integer bulan, Integer tahun) {
        return tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun);
    }
    
    public TppPerhitungan ubahTppPerhitungan(TppPerhitungan tppPerhitungan) {

        if (!pegawaiRepository.existsByNip(tppPerhitungan.nip())) {
            throw new PegawaiNotFoundException(tppPerhitungan.nip());
        }
        
        if (!opdRepository.existsByKodeOpd(tppPerhitungan.kodeOpd())) {
            throw new OpdNotFoundException(tppPerhitungan.kodeOpd());
        }
        
        return tppPerhitunganRepository.save(tppPerhitungan);
    }
    
    public TppPerhitungan tambahTppPerhitungan(TppPerhitungan tppPerhitungan) {
        
        if (!opdRepository.existsByKodeOpd(tppPerhitungan.kodeOpd())) {
            throw new OpdNotFoundException(tppPerhitungan.kodeOpd());
        }
        
        if (!pegawaiRepository.existsByNip(tppPerhitungan.nip())) {
            throw new PegawaiNotFoundException(tppPerhitungan.nip());
        }

        if (tppPerhitunganRepository.findByJenisTppAndNipAndBulanAndTahun(
                tppPerhitungan.jenisTpp(), 
                tppPerhitungan.nip(), 
                tppPerhitungan.bulan(), 
                tppPerhitungan.tahun()).iterator().hasNext()) {
            throw new TppPerhitunganJenisTppNipBulanTahunSudahAdaException(
                tppPerhitungan.jenisTpp(), 
                tppPerhitungan.nip(), 
                tppPerhitungan.bulan(), 
                tppPerhitungan.tahun());
        }
        
        return tppPerhitunganRepository.save(tppPerhitungan);
    }
    
    public void hapusTppPerhitungan(Long id) {
        if (!tppPerhitunganRepository.existsById(id)) {
            throw new TppPerhitunganNotFoundException(id);
        }
        
        tppPerhitunganRepository.deleteById(id);
    }
    
    public void hapusTppPerhitunganByNipBulanTahun(String nip, Integer bulan, Integer tahun) {
        tppPerhitunganRepository.deleteByNipAndBulanAndTahun(nip, bulan, tahun);
    }
}
