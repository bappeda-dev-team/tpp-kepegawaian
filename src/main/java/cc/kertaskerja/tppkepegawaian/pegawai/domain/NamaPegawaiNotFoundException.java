package cc.kertaskerja.tppkepegawaian.pegawai.domain;

public class NamaPegawaiNotFoundException extends RuntimeException {
    public NamaPegawaiNotFoundException(String namaPegawai, String nip) {
        super("Pegawai dengan nama " + namaPegawai + " dengan" + " nip " + nip + " tidak ditemukan.");
    }
}
