package cc.kertaskerja.tppkepegawaian.pegawai.domain;

public class PegawaiNotFoundException extends RuntimeException {
    public PegawaiNotFoundException(String nip) {
        super("Pegawai dengan NIP " + nip + " tidak ditemukan.");
    }
}
