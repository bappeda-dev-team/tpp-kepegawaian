package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp;

public class TppPerhitunganJenisTppNipBulanTahunSudahAdaException extends RuntimeException {
    public TppPerhitunganJenisTppNipBulanTahunSudahAdaException(JenisTpp jenisTpp, String nip, Integer bulan, Integer tahun) {
        super("Tpp Perhitungan dengan jenis tpp " + jenisTpp + " nip " + nip + " bulan " + bulan + " tahun " + tahun + " sudah ada .");
    }
}
