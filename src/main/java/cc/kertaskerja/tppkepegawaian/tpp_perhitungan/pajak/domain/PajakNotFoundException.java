package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.domain;

public class PajakNotFoundException extends RuntimeException {
    public PajakNotFoundException(String nip) {
        super("Pajak dengan nip " + nip + " tidak ditemukan.");
    }
}
