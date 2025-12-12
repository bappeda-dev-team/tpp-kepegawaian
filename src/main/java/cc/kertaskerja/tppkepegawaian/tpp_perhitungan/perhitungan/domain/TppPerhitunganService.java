package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.NamaPegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNotFoundException;
import java.util.stream.StreamSupport;

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

    public Iterable<TppPerhitungan> listTppPerhitunganByNipAndBulanAndTahun(String nip, Integer bulan, Integer tahun) {
        return tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    public Iterable<TppPerhitungan> listTppPerhitunganByKodeOpd(String kodeOpd) {
        return tppPerhitunganRepository.findByKodeOpd(kodeOpd);
    }

    public Iterable<TppPerhitungan> listTppPerhitunganByNip(String nip) {
        return tppPerhitunganRepository.findByNip(nip);
    }

    public Iterable<TppPerhitungan> listTppPerhitunganByNama(String nama) {
        return tppPerhitunganRepository.findByNama(nama);
    }

    public Iterable<TppPerhitungan> listTppPerhitunganByNipBulanTahun(String nip, Integer bulan, Integer tahun) {
        return tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    public Iterable<TppPerhitungan> listTppPerhitunganByKodeOpdAndBulanAndTahun(String kodeOpd, Integer bulan, Integer tahun) {
        return tppPerhitunganRepository.findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
    }

    public TppPerhitungan detailTppPerhitungan(String nip, Integer bulan, Integer tahun) {
        Iterable<TppPerhitungan> result = tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun);
        return StreamSupport.stream(result.spliterator(), false)
                .findFirst()
                .orElseThrow(() -> new TppPerhitunganNipBulanTahunNotFoundException(nip, bulan, tahun));
    }

    public TppPerhitungan ubahTppPerhitungan(TppPerhitungan tppPerhitungan) {

        if (!opdRepository.existsByKodeOpd(tppPerhitungan.kodeOpd())) {
            throw new OpdNotFoundException(tppPerhitungan.kodeOpd());
        }

        return tppPerhitunganRepository.save(tppPerhitungan);
    }

    public TppPerhitungan tambahTppPerhitungan(TppPerhitungan tppPerhitungan) {

        if (tppPerhitungan.nip() == null || tppPerhitungan.bulan() == null || tppPerhitungan.tahun() == null) {
            throw new IllegalArgumentException("NIP, Bulan, dan Tahun tidak boleh null.");
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
