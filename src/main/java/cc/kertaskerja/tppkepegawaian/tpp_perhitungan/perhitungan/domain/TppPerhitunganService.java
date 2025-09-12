package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TppPerhitunganService {
    private final TppPerhitunganRepository tppPerhitunganRepository;
    private final PegawaiRepository pegawaiRepository;
    private final OpdRepository opdRepository;
    private static final Float maksimum = 30.0f;
    
    public TppPerhitunganService(TppPerhitunganRepository tppPerhitunganRepository, 
                                PegawaiRepository pegawaiRepository, 
                                OpdRepository opdRepository) {
        this.tppPerhitunganRepository = tppPerhitunganRepository;
        this.pegawaiRepository = pegawaiRepository;
        this.opdRepository = opdRepository;
    }

    public Iterable<TppPerhitungan> listTppPerhitunganByNipAndBulanAndTahun(String nip, Integer bulan, Integer tahun) {
        return tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun);
    }
    
    public Iterable<TppPerhitungan> listTppPerhitunganByKodeOpd(String kodeOpd) {
        return tppPerhitunganRepository.findByKodeOpd(kodeOpd);
    }
    
    public Iterable<TppPerhitungan> listTppPerhitunganByNip(String nip) {
        return tppPerhitunganRepository.findByNip(nip);
    }
    
    public TppPerhitungan detailTppPerhitungan(Long id) {
        return tppPerhitunganRepository.findById(id)
                .orElseThrow(() -> new TppPerhitunganNotFoundException(id));
    }
    
    public TppPerhitungan ubahTppPerhitungan(Long id, TppPerhitungan tppPerhitungan) {
        if (!tppPerhitunganRepository.existsById(id)) {
            throw new TppPerhitunganNotFoundException(id);
        }
        
        if (!opdRepository.existsByKodeOpd(tppPerhitungan.kodeOpd())) {
            throw new OpdNotFoundException(tppPerhitungan.kodeOpd());
        }

        if (!pegawaiRepository.existsByNip(tppPerhitungan.nip())) {
            throw new PegawaiNotFoundException(tppPerhitungan.nip());
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
        
        return tppPerhitunganRepository.save(tppPerhitungan);
    }
    
    public void hapusTppPerhitungan(Long id) {
        if (!tppPerhitunganRepository.existsById(id)) {
            throw new TppPerhitunganNotFoundException(id);
        }
        
        tppPerhitunganRepository.deleteById(id);
    }
}
