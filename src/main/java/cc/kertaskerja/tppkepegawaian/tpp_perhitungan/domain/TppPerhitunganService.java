package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiSudahAdaException;
import org.springframework.stereotype.Service;

@Service
public class TppPerhitunganService {
    private final TppPerhitunganRepository tppPerhitunganRepository;
    private final PegawaiRepository pegawaiRepository;
    private final OpdRepository opdRepository;
    
    public TppPerhitunganService(TppPerhitunganRepository tppPerhitunganRepository, 
                                PegawaiRepository pegawaiRepository, 
                                OpdRepository opdRepository) {
        this.tppPerhitunganRepository = tppPerhitunganRepository;
        this.pegawaiRepository = pegawaiRepository;
        this.opdRepository = opdRepository;
    }
    
    public Iterable<TppPerhitungan> listTppPerhitunganByKodeOpd(String kodeOpd) {
        return tppPerhitunganRepository.findByKodeOpd(kodeOpd);
    }
    
    public boolean existsByBulanAndTahun(Integer bulan, Integer tahun) {
        return tppPerhitunganRepository.existsByBulanAndTahun(bulan, tahun);
    }
    
    public Iterable<TppPerhitungan> listTppPerhitunganByNip(String nip) {
        return tppPerhitunganRepository.findByNip(nip);
    }
    
    public Iterable<TppPerhitungan> listTppPerhitunganByBulanAndTahun(Integer bulan, Integer tahun) {
        return tppPerhitunganRepository.findByBulanAndTahun(bulan, tahun);
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

        if (tppPerhitunganRepository.existsByBulanAndTahun(tppPerhitungan.bulan(), tppPerhitungan.tahun())) {
            throw new TppPerhitunganBulanTahunNotFoundException(tppPerhitungan.bulan(), tppPerhitungan.tahun());
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

        if (tppPerhitunganRepository.existsByNip(tppPerhitungan.nip())) {
            throw new PegawaiSudahAdaException(tppPerhitungan.nip());
        }

        if (tppPerhitunganRepository.existsByBulanAndTahun(tppPerhitungan.bulan(), tppPerhitungan.tahun())) {
            throw new TppPerhitunganBulanTahunSudahAdaException(tppPerhitungan.bulan(), tppPerhitungan.tahun());
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
