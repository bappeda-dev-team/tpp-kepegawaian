
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import java.util.stream.StreamSupport;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganNipBulanTahunNotFoundException;
import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.TppTotalPersenResponse;

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

    public Iterable<TppTotalPersenResponse> listTppByNipBulanTahunWithPerhitungan(String nip, Integer bulan, Integer tahun) {
        var tppList = tppRepository.findByNipAndBulanAndTahun(nip, bulan, tahun);
        
        return StreamSupport.stream(tppList.spliterator(), false)
            .map(tpp -> {
                var perhitunganList = tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun);
                
                // Calculate total persen from perhitungan
                Float totalPersen = StreamSupport.stream(perhitunganList.spliterator(), false)
                    .map(perhitungan -> perhitungan.nilaiPerhitungan())
                    .reduce(0.0f, Float::sum);
                
                return new TppTotalPersenResponse(tpp, totalPersen);
            })
            .toList();
    }

    public Tpp detailTpp(Long id) {
        return tppRepository.findById(id)
        .orElseThrow(() -> new TppNotFoundException(id));
    }
    
    public Tpp ubahTpp(Long id, Tpp tpp) {
        if (!tppRepository.existsById(id)) {
            throw new TppNotFoundException(id);
        }
        
        // hardcode semua jenis tpp = 30
//        if (tpp.totalTpp() > tpp.maksimum()) {
//            throw new TppNilaiInputMelebihiMaksimumException();
//        }

        if (!opdRepository.existsByKodeOpd(tpp.kodeOpd())) {
            throw new OpdNotFoundException(tpp.kodeOpd());
        }

        if (!pegawaiRepository.existsByNip(tpp.nip())) {
            throw new PegawaiNotFoundException(tpp.nip());
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

        if (!pegawaiRepository.existsByNip(tpp.nip())) {
            throw new PegawaiNotFoundException(tpp.nip());
        }

        if (tppRepository.existsByNip(tpp.nip())) {
            throw new TppNipSudahAdaException(tpp.nip());
        }

        // Validasi nip, bulan, tahun harus ada di TppPerhitungan
        if (!tppPerhitunganRepository.existsByNipAndBulanAndTahun(tpp.nip(), tpp.bulan(), tpp.tahun())) {
            throw new TppPerhitunganNipBulanTahunNotFoundException(tpp.nip(), tpp.bulan(), tpp.tahun());
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
