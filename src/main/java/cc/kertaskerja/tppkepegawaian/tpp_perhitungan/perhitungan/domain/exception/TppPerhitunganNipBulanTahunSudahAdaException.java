package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception;

public class TppPerhitunganNipBulanTahunSudahAdaException extends RuntimeException {
    public TppPerhitunganNipBulanTahunSudahAdaException(String nip, Integer bulan, Integer tahun) {
        super("Tpp Perhitungan dengan nip " + nip + " bulan " + bulan + " tahun " + tahun + " sudah ada .");
    }
}
