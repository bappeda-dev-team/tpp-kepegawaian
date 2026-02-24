
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppKodeOpdBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppNipBulanTahunSudahAdaException;

@Service
public class TppService {
    private final TppRepository tppRepository;
    // private final TppPerhitunganRepository tppPerhitunganRepository;
    private final OpdRepository opdRepository;
    private final PegawaiRepository pegawaiRepository;

    public TppService(TppRepository tppRepository, PegawaiRepository pegawaiRepository,
        OpdRepository opdRepository) {
        this.tppRepository = tppRepository;
        this.pegawaiRepository = pegawaiRepository;
        this.opdRepository = opdRepository;
    }

    public Iterable<Tpp> listTppByKodeOpd(String kodeOpd) {
        if (!opdRepository.existsByKodeOpd(kodeOpd)) {
            throw new OpdNotFoundException(kodeOpd);
        }

        return tppRepository.findByKodeOpd(kodeOpd);
    }

    public Iterable<Tpp> listTppByNip(String nip) {
        if (!pegawaiRepository.existsByNip(nip)) {
            throw new PegawaiNotFoundException(nip);
        }

        return tppRepository.findByNip(nip);
    }

    public Iterable<Tpp> listTppByNipBulanTahun(String nip, Integer bulan, Integer tahun) {

        return tppRepository.findByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    public Iterable<Tpp> listTppByOpdBulanTahun(String jenisTpp, String kodeOpd, Integer bulan, Integer tahun) {

        Iterable<Tpp> tppList = tppRepository.findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
        if (!tppList.iterator().hasNext()) {
            throw new TppJenisTppKodeOpdBulanTahunNotFoundException(jenisTpp, kodeOpd, bulan, tahun);
        }

        return tppList;
    }

    public Tpp detailTpp(String jenisTpp, String nip, Integer bulan, Integer tahun) {
        return tppRepository.findByJenisTppAndNipAndBulanAndTahun(jenisTpp, nip, bulan, tahun)
                .orElseThrow(() -> new TppJenisTppNipBulanTahunNotFoundException(jenisTpp, nip, bulan, tahun));
    }

    public List<Tpp> detailTppBatch(String jenisTpp, List<String> nipPegawais, Integer bulan, Integer tahun,
            String kodeOpd) {
        if (nipPegawais == null || nipPegawais.isEmpty()) {
            throw new IllegalArgumentException("nipPegawais tidak boleh kosong");
        }

        // ambil seluruh tpp tanpa filter periode
        List<Tpp> all = tppRepository.findAllByJenisTppAndNipInAndKodeOpd(jenisTpp, nipPegawais, kodeOpd);

        // untuk filter periode tpp
        Comparator<Tpp> periodeTppComparator = Comparator.comparing(Tpp::bulan)
                .thenComparing(Tpp::tahun);

        // filter <= periode target agar menncari tpp yang mendekati
        // periode tersebut
        List<Tpp> filteredTpp = all.stream()
                .filter(t -> t.tahun() < tahun ||
                        (t.tahun().equals(tahun) && t.bulan() <= bulan))
                .toList();

        // group by nip ambil latest
        Map<String, Tpp> latestPerNip = filteredTpp.stream()
                .collect(Collectors.toMap(
                        Tpp::nip,
                        Function.identity(),
                        (t1, t2) -> periodeTppComparator.compare(t1, t2) > 0 ? t1 : t2));

        return nipPegawais.stream()
                .map(nip -> latestPerNip.getOrDefault(
                        nip,
                        Tpp.zero(
                                jenisTpp,
                                kodeOpd,
                                nip,
                                "--",
                                bulan,
                                tahun)))
                .toList();
    }

    public Tpp ubahTpp(Tpp tpp) {

        if (!tppRepository.existsByJenisTppAndNipAndBulanAndTahun(tpp.jenisTpp(), tpp.nip(), tpp.bulan(),
                tpp.tahun())) {
            throw new TppJenisTppNipBulanTahunNotFoundException(tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun());
        }

        return tppRepository.save(tpp);
    }

    public Tpp tambahTpp(Tpp tpp) {

        if (tppRepository.existsByJenisTppAndNipAndBulanAndTahun(tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun())) {
            throw new TppJenisTppNipBulanTahunSudahAdaException(tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun());
        }

        return tppRepository.save(tpp);
    }

    public void hapusTppByNipBulanTahun(String nip, Integer bulan, Integer tahun) {
        tppRepository.deleteByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    @Transactional
    public Tpp saveTimpaTpp(Tpp tpp) {
        return tppRepository.save(tpp);
    }

    @Transactional
    public Tpp upsertTpp(Tpp tpp) {
        return tppRepository
                .findByJenisTppAndNipAndBulanAndTahun(
                        tpp.jenisTpp(),
                        tpp.nip(),
                        tpp.bulan(),
                        tpp.tahun())
                .map(existing -> {
                    Tpp updated = existing.updateFrom(tpp);
                    return tppRepository.save(updated);
                })
                .orElseGet(() -> tppRepository.save(tpp));
    }

    // saat user ganti nip jabatan, data nip tpp juga ganti
    @Transactional
    public Tpp upsertTppReplacingOldNip(Tpp tpp, String nip) {
        String nipBaru = (nip != null && !nip.isBlank()) ? nip : tpp.nip();

        return tppRepository
                .findByJenisTppAndNipAndBulanAndTahun(
                        tpp.jenisTpp(),
                        nipBaru,
                        tpp.bulan(),
                        tpp.tahun())
                .map(existing -> {
                    Tpp updated = new Tpp(
                            existing.id(),
                            existing.jenisTpp(),
                            tpp.kodeOpd(),
                            tpp.nip(),
                            tpp.kodePemda(),
                            tpp.maksimumTpp(),
                            tpp.pajak(),
                            tpp.bpjs(),
                            existing.bulan(),
                            existing.tahun(),
                            existing.createdDate(),
                            Instant.now());
                    return tppRepository.save(updated);
                })
                .orElseGet(() -> tppRepository.save(tpp));
    }
}
