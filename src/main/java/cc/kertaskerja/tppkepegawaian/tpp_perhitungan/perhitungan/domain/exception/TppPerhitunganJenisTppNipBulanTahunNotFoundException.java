package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp;

public class TppPerhitunganJenisTppNipBulanTahunNotFoundException extends RuntimeException {
    public TppPerhitunganJenisTppNipBulanTahunNotFoundException(JenisTpp jenisTpp, String nip, Integer bulan, Integer tahun) {
        super("Data TPP Perhitungan untuk pegawai dengan jenis tpp " + jenisTpp + " nip " +  nip + " bulan " + bulan + " dan tahun " + tahun + " tidak ditemukan.");
    }
}
