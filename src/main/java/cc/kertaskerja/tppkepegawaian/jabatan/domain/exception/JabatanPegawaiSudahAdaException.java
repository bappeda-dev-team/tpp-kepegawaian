package cc.kertaskerja.tppkepegawaian.jabatan.domain.exception;

public class JabatanPegawaiSudahAdaException extends RuntimeException {
  public JabatanPegawaiSudahAdaException(String nip) {
    super("Jabatan dengan pegawai nip " + nip + " sudah ada.");
  }
}
