package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.JenisTpp;

public class TppJenisTppNipBulanTahunNotFoundException extends RuntimeException {
    public TppJenisTppNipBulanTahunNotFoundException(JenisTpp jenisTpp, String nip, Integer bulan, Integer tahun) {
        super("Tpp dengan jenis Tpp " + jenisTpp + " nip " +  nip + " bulan " + bulan + " tahun " + tahun + " tidak ditemukan.");
    }
}
