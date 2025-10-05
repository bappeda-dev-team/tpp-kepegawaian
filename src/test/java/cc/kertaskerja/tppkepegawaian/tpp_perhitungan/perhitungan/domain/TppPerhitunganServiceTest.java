package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.NamaPegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TppPerhitunganServiceTest {

    @Mock
    private TppPerhitunganRepository tppPerhitunganRepository;

    @Mock
    private PegawaiRepository pegawaiRepository;

    @Mock
    private OpdRepository opdRepository;

    private TppPerhitunganService tppPerhitunganService;

    private TppPerhitungan sampleTppPerhitungan;

    @BeforeEach
    void setUp() {
        tppPerhitunganService = new TppPerhitunganService(tppPerhitunganRepository, pegawaiRepository, opdRepository);
        sampleTppPerhitungan = new TppPerhitungan(
            1L,
            JenisTpp.ABSENSI,
            "OPD001",
            "PEMDA001",
            "701301613358689213",
            "Wahyu",
            2,
            2024,
            5000000.0f,
            "ABSENSI",
            27.0f,
            Instant.now(),
            Instant.now()
        );
    }

    @Test
    void listTppPerhitunganByNipAndBulanAndTahun_ShouldReturnFromRepository() {
        String nip = "701301613358689213";
        Integer bulan = 2;
        Integer tahun = 2024;
        List<TppPerhitungan> expected = List.of(sampleTppPerhitungan);

        when(tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(expected);

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun);

        assertEquals(expected, result);
        verify(tppPerhitunganRepository).findByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    @Test
    void listTppPerhitunganByKodeOpd_ShouldReturnFromRepository() {
        String kodeOpd = "OPD001";
        List<TppPerhitungan> expected = List.of(sampleTppPerhitungan);

        when(tppPerhitunganRepository.findByKodeOpd(kodeOpd)).thenReturn(expected);

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpd(kodeOpd);

        assertEquals(expected, result);
        verify(tppPerhitunganRepository).findByKodeOpd(kodeOpd);
    }

    @Test
    void listTppPerhitunganByNip_ShouldReturnFromRepository() {
        String nip = "701301613358689213";
        List<TppPerhitungan> expected = List.of(sampleTppPerhitungan);

        when(tppPerhitunganRepository.findByNip(nip)).thenReturn(expected);

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNip(nip);

        assertEquals(expected, result);
        verify(tppPerhitunganRepository).findByNip(nip);
    }

    @Test
    void listTppPerhitunganByNama_ShouldReturnFromRepository() {
        String nama = "Wahyu";
        List<TppPerhitungan> expected = List.of(sampleTppPerhitungan);

        when(tppPerhitunganRepository.findByNama(nama)).thenReturn(expected);

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNama(nama);

        assertEquals(expected, result);
        verify(tppPerhitunganRepository).findByNama(nama);
    }

    @Test
    void listTppPerhitunganByNipBulanTahun_ShouldReturnFromRepository() {
        String nip = "701301613358689213";
        Integer bulan = 2;
        Integer tahun = 2024;
        List<TppPerhitungan> expected = List.of(sampleTppPerhitungan);

        when(tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(expected);

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNipBulanTahun(nip, bulan, tahun);

        assertEquals(expected, result);
        verify(tppPerhitunganRepository).findByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    @Test
    void listTppPerhitunganByKodeOpdAndBulanAndTahun_ShouldReturnFromRepository() {
        String kodeOpd = "OPD001";
        Integer bulan = 2;
        Integer tahun = 2024;
        List<TppPerhitungan> expected = List.of(sampleTppPerhitungan);

        when(tppPerhitunganRepository.findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun)).thenReturn(expected);

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);

        assertEquals(expected, result);
        verify(tppPerhitunganRepository).findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
    }

    @Test
    void detailTppPerhitungan_WhenFound_ShouldReturnTppPerhitungan() {
        String nip = "701301613358689213";
        Integer bulan = 2;
        Integer tahun = 2024;
        List<TppPerhitungan> expected = List.of(sampleTppPerhitungan);

        when(tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(expected);

        TppPerhitungan result = tppPerhitunganService.detailTppPerhitungan(nip, bulan, tahun);

        assertEquals(sampleTppPerhitungan, result);
        verify(tppPerhitunganRepository).findByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    @Test
    void detailTppPerhitungan_WhenNotFound_ShouldThrowException() {
        String nip = "123456789012345678";
        Integer bulan = 1;
        Integer tahun = 2024;

        when(tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(Collections.emptyList());

        assertThrows(TppPerhitunganNipBulanTahunNotFoundException.class, () -> {
            tppPerhitunganService.detailTppPerhitungan(nip, bulan, tahun);
        });
    }

    @Test
    void ubahTppPerhitungan_WhenValid_ShouldSaveAndReturn() {
        when(pegawaiRepository.existsByNip(sampleTppPerhitungan.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(sampleTppPerhitungan.nama())).thenReturn(true);
        when(opdRepository.existsByKodeOpd(sampleTppPerhitungan.kodeOpd())).thenReturn(true);
        when(tppPerhitunganRepository.save(sampleTppPerhitungan)).thenReturn(sampleTppPerhitungan);

        TppPerhitungan result = tppPerhitunganService.ubahTppPerhitungan(sampleTppPerhitungan);

        assertEquals(sampleTppPerhitungan, result);
        verify(tppPerhitunganRepository).save(sampleTppPerhitungan);
    }

    @Test
    void ubahTppPerhitungan_WhenNipNotFound_ShouldThrowException() {
        when(pegawaiRepository.existsByNip(sampleTppPerhitungan.nip())).thenReturn(false);

        assertThrows(PegawaiNotFoundException.class, () -> {
            tppPerhitunganService.ubahTppPerhitungan(sampleTppPerhitungan);
        });
    }

    @Test
    void ubahTppPerhitungan_WhenNamaNotFound_ShouldThrowException() {
        when(pegawaiRepository.existsByNip(sampleTppPerhitungan.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(sampleTppPerhitungan.nama())).thenReturn(false);

        assertThrows(NamaPegawaiNotFoundException.class, () -> {
            tppPerhitunganService.ubahTppPerhitungan(sampleTppPerhitungan);
        });
    }

    @Test
    void ubahTppPerhitungan_WhenOpdNotFound_ShouldThrowException() {
        when(pegawaiRepository.existsByNip(sampleTppPerhitungan.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(sampleTppPerhitungan.nama())).thenReturn(true);
        when(opdRepository.existsByKodeOpd(sampleTppPerhitungan.kodeOpd())).thenReturn(false);

        assertThrows(OpdNotFoundException.class, () -> {
            tppPerhitunganService.ubahTppPerhitungan(sampleTppPerhitungan);
        });
    }

    @Test
    void tambahTppPerhitungan_WhenValid_ShouldSaveAndReturn() {
        when(opdRepository.existsByKodeOpd(sampleTppPerhitungan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(sampleTppPerhitungan.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(sampleTppPerhitungan.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(sampleTppPerhitungan)).thenReturn(sampleTppPerhitungan);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(sampleTppPerhitungan);

        assertEquals(sampleTppPerhitungan, result);
        verify(tppPerhitunganRepository).save(sampleTppPerhitungan);
    }

    @Test
    void tambahTppPerhitungan_WhenNipNull_ShouldThrowException() {
        TppPerhitungan invalidTpp = new TppPerhitungan(
            1L, JenisTpp.ABSENSI, "OPD001", "PEMDA001", null, "John Doe", 1, 2024, 5000000.0f, "ABSENSI", 27.0f, Instant.now(), Instant.now()
        );

        when(opdRepository.existsByKodeOpd(invalidTpp.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(null)).thenThrow(new IllegalArgumentException("NIP tidak boleh null"));

        assertThrows(IllegalArgumentException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(invalidTpp);
        });
    }

    @Test
    void tambahTppPerhitungan_WhenBulanNull_ShouldThrowException() {
        TppPerhitungan invalidTpp = new TppPerhitungan(
            1L, JenisTpp.ABSENSI, "OPD001", "PEMDA001", "123456789", "John Doe", null, 2024, 5000000.0f, "ABSENSI", 27.0f, Instant.now(), Instant.now()
        );

        when(opdRepository.existsByKodeOpd(invalidTpp.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(invalidTpp.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(invalidTpp.nama())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(invalidTpp);
        });
    }

    @Test
    void tambahTppPerhitungan_WhenTahunNull_ShouldThrowException() {
        TppPerhitungan invalidTpp = new TppPerhitungan(
            1L, JenisTpp.ABSENSI, "OPD001", "PEMDA001", "123456789", "John Doe", 1, null, 5000000.0f, "ABSENSI", 27.0f, Instant.now(), Instant.now()
        );

        when(opdRepository.existsByKodeOpd(invalidTpp.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(invalidTpp.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(invalidTpp.nama())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(invalidTpp);
        });
    }

    @Test
    void hapusTppPerhitungan_WhenExists_ShouldDelete() {
        Long id = 1L;
        when(tppPerhitunganRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> {
            tppPerhitunganService.hapusTppPerhitungan(id);
        });

        verify(tppPerhitunganRepository).deleteById(id);
    }

    @Test
    void hapusTppPerhitungan_WhenNotExists_ShouldThrowException() {
        Long id = 1L;
        when(tppPerhitunganRepository.existsById(id)).thenReturn(false);

        assertThrows(TppPerhitunganNotFoundException.class, () -> {
            tppPerhitunganService.hapusTppPerhitungan(id);
        });
    }

    @Test
    void hapusTppPerhitunganByNipBulanTahun_ShouldCallRepository() {
        String nip = "123456789";
        Integer bulan = 1;
        Integer tahun = 2024;

        assertDoesNotThrow(() -> {
            tppPerhitunganService.hapusTppPerhitunganByNipBulanTahun(nip, bulan, tahun);
        });

        verify(tppPerhitunganRepository).deleteByNipAndBulanAndTahun(nip, bulan, tahun);
    }
}
