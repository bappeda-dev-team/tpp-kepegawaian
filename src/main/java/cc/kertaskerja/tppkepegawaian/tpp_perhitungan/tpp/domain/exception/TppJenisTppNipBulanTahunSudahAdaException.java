package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception;

public class TppJenisTppNipBulanTahunSudahAdaException extends RuntimeException {
    public TppJenisTppNipBulanTahunSudahAdaException(String jenisTpp, String nip, Integer bulan, Integer tahun) {
        super("Tpp dengan jenis Tpp " + jenisTpp + " nip " +  nip + " bulan " + bulan + " tahun " + tahun + " sudah ada.");
    }
}
