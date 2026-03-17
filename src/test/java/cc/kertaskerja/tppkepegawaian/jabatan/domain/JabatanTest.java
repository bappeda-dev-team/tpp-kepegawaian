package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Instant;

import org.junit.jupiter.api.Test;

public class JabatanTest {

    private static final int DEFAULT_TANGGAL = 1;

    private Jabatan createJabatan(String status) {
        LocalDate tanggalMulai = createTanggal(2025, 1);
        LocalDate tanggalAkhir = createTanggal(2025, 8);
        return new Jabatan(
                1L,
                "123_456",
                "PEGAWAI TELADAN",
                "KEPALA XX",
                "123",
                status,
                "JABATAN_PIMPINAN_TINGGI_PRATAMA",
                "ESELON II",
                "IV C",
                "IV",
                null,
                tanggalMulai,
                tanggalAkhir,
                Instant.now(),
                Instant.now());
    }

    // Y M D
    // akomodir tanggalMulai dan tanggalAkhir di Jabatan
    // buat LocalDate dari tahun, bulan, tanggal dari request
    // untuk memudahkan sekaligus guard frontend
    private LocalDate createTanggal(Integer tahun, Integer bulan) {
        if (bulan == null || tahun == null) {
            return null;
        }

        return LocalDate.of(tahun, bulan, DEFAULT_TANGGAL);
    }

    @Test
    void shouldBeActiveAt_whenStatusNotBerakhir() {

        var jabatan = createJabatan("AKTIF");
        var tanggal = createTanggal(2025, 3);

        assertThat(jabatan.isActiveAt(tanggal)).isTrue();
    }

    @Test
    void shouldNotBeActiveAt_whenBeyondTanggalBerakhir() {

        var jabatan = createJabatan("AKTIF");
        var tanggal = createTanggal(2025, 9);

        assertThat(jabatan.isActiveAt(tanggal)).isFalse();
    }

    @Test
    void shouldNotBeIsKepalaAt_whenBeyondTanggalBerakhir() {
        var jabatan = createJabatan("AKTIF");
        var tanggal = createTanggal(2025, 9);

        assertThat(jabatan.isKepalaAt(tanggal)).isFalse();
    }

    @Test
    void shouldBeIsKepalaAt_whenBeyondBeforeTanggalBerakhir() {
        var jabatan = createJabatan("AKTIF");
        var tanggal = createTanggal(2025, 2);

        assertThat(jabatan.isKepalaAt(tanggal)).isFalse();
    }

}
