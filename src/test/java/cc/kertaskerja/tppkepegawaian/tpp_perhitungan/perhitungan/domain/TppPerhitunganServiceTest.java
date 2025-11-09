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

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        sampleTppPerhitungan = TppPerhitungan.of(
            "Kondisi Kerja",
            "OPD001",
            "PEMDA001",
            "701301613358689213",
            "Wahyu",
            2,
            2024,
            5000000.0f,
            "Absensi",
            27.0f
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
    void listTppPerhitunganByNipBulanTahun_ShouldBehaveIdenticallyToListTppPerhitunganByNipAndBulanAndTahun() {
        String nip = "701301613358689213";
        Integer bulan = 2;
        Integer tahun = 2024;
        List<TppPerhitungan> expected = List.of(sampleTppPerhitungan);

        when(tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(expected);

        Iterable<TppPerhitungan> result1 = tppPerhitunganService.listTppPerhitunganByNipBulanTahun(nip, bulan, tahun);
        Iterable<TppPerhitungan> result2 = tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun);

        assertEquals(result1, result2);
        verify(tppPerhitunganRepository, times(2)).findByNipAndBulanAndTahun(nip, bulan, tahun);
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
        assertEquals("701301613358689213", result.nip());
        assertEquals("Wahyu", result.nama());
        assertEquals("Kondisi Kerja", result.jenisTpp());
        assertEquals("Absensi", result.namaPerhitungan());
        assertEquals(2, result.bulan());
        assertEquals(2024, result.tahun());
        assertEquals(5000000.0f, result.maksimum());
        assertEquals(27.0f, result.nilaiPerhitungan());
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
        assertEquals("701301613358689213", result.nip());
        assertEquals("Wahyu", result.nama());
        assertEquals("Kondisi Kerja", result.jenisTpp());
        assertEquals("Absensi", result.namaPerhitungan());
        assertEquals(2, result.bulan());
        assertEquals(2024, result.tahun());
        assertEquals(5000000.0f, result.maksimum());
        assertEquals(27.0f, result.nilaiPerhitungan());
        assertEquals("OPD001", result.kodeOpd());
        assertEquals("PEMDA001", result.kodePemda());
        verify(tppPerhitunganRepository).save(sampleTppPerhitungan);
    }

    @Test
    void tambahTppPerhitungan_WhenNipNull_ShouldThrowPegawaiNotFoundException() {
        TppPerhitungan invalidTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", null, "John Doe", 1, 2024, 5000000.0f, "Absensi", 27.0f
        );

        when(opdRepository.existsByKodeOpd(invalidTpp.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(null)).thenReturn(false);

        assertThrows(PegawaiNotFoundException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(invalidTpp);
        });
    }

    @Test
    void tambahTppPerhitungan_WhenBulanNull_ShouldThrowException() {
        TppPerhitungan invalidTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "John Doe", null, 2024, 5000000.0f, "Absensi", 27.0f
        );

        when(opdRepository.existsByKodeOpd(invalidTpp.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(invalidTpp.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(invalidTpp.nama())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(invalidTpp);
        });

        assertEquals("NIP, Bulan, dan Tahun tidak boleh null.", exception.getMessage());
    }

    @Test
    void tambahTppPerhitungan_WhenTahunNull_ShouldThrowException() {
        TppPerhitungan invalidTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "John Doe", 1, null, 5000000.0f, "Absensi", 27.0f
        );

        when(opdRepository.existsByKodeOpd(invalidTpp.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(invalidTpp.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(invalidTpp.nama())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(invalidTpp);
        });

        assertEquals("NIP, Bulan, dan Tahun tidak boleh null.", exception.getMessage());
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

    @Test
    void tambahTppPerhitungan_WhenOpdNotFound_ShouldThrowException() {
        when(opdRepository.existsByKodeOpd(sampleTppPerhitungan.kodeOpd())).thenReturn(false);

        assertThrows(OpdNotFoundException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(sampleTppPerhitungan);
        });
    }

    @Test
    void tambahTppPerhitungan_WhenPegawaiNotFound_ShouldThrowException() {
        when(opdRepository.existsByKodeOpd(sampleTppPerhitungan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(sampleTppPerhitungan.nip())).thenReturn(false);

        assertThrows(PegawaiNotFoundException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(sampleTppPerhitungan);
        });
    }

    @Test
    void tambahTppPerhitungan_WhenNamaPegawaiNotFound_ShouldThrowException() {
        when(opdRepository.existsByKodeOpd(sampleTppPerhitungan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(sampleTppPerhitungan.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(sampleTppPerhitungan.nama())).thenReturn(false);

        assertThrows(NamaPegawaiNotFoundException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(sampleTppPerhitungan);
        });
    }

    @Test
    void tambahTppPerhitungan_WhenNipBulanTahunNull_ShouldThrowIllegalArgumentException() {
        TppPerhitungan invalidTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "John Doe", null, null, 5000000.0f, "Absensi", 27.0f
        );

        when(opdRepository.existsByKodeOpd(invalidTpp.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(invalidTpp.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(invalidTpp.nama())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(invalidTpp);
        });

        assertEquals("NIP, Bulan, dan Tahun tidak boleh null.", exception.getMessage());
    }

    @Test
    void listTppPerhitunganByNip_WhenEmptyList_ShouldReturnEmptyIterable() {
        String nip = "123456789";
        when(tppPerhitunganRepository.findByNip(nip)).thenReturn(Collections.emptyList());

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNip(nip);

        assertFalse(result.iterator().hasNext());
        verify(tppPerhitunganRepository).findByNip(nip);
    }

    @Test
    void listTppPerhitunganByNama_WhenEmptyList_ShouldReturnEmptyIterable() {
        String nama = "NonExistent";
        when(tppPerhitunganRepository.findByNama(nama)).thenReturn(Collections.emptyList());

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNama(nama);

        assertFalse(result.iterator().hasNext());
        verify(tppPerhitunganRepository).findByNama(nama);
    }

    @Test
    void listTppPerhitunganByKodeOpd_WhenEmptyList_ShouldReturnEmptyIterable() {
        String kodeOpd = "NONEXISTENT";
        when(tppPerhitunganRepository.findByKodeOpd(kodeOpd)).thenReturn(Collections.emptyList());

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpd(kodeOpd);

        assertFalse(result.iterator().hasNext());
        verify(tppPerhitunganRepository).findByKodeOpd(kodeOpd);
    }

    @Test
    void listTppPerhitunganByNipAndBulanAndTahun_WhenEmptyList_ShouldReturnEmptyIterable() {
        String nip = "123456789";
        Integer bulan = 1;
        Integer tahun = 2024;
        when(tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(Collections.emptyList());

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun);

        assertFalse(result.iterator().hasNext());
        verify(tppPerhitunganRepository).findByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    @Test
    void tambahTppPerhitungan_WithJenisTppBelumDiatur_ShouldWork() {
        TppPerhitungan tppWithBelumDiatur = TppPerhitungan.of(
            "Belum Diatur", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 4000000.0f, "Produktifitas Kerja", 25.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithBelumDiatur.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithBelumDiatur.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithBelumDiatur.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithBelumDiatur)).thenReturn(tppWithBelumDiatur);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithBelumDiatur);

        assertEquals(tppWithBelumDiatur, result);
        assertEquals("Belum Diatur", result.jenisTpp());
        assertEquals("Produktifitas Kerja", result.namaPerhitungan());
        verify(tppPerhitunganRepository).save(tppWithBelumDiatur);
    }

    @Test
    void tambahTppPerhitungan_WithNamaPerhitunganBelumDiatur_ShouldWork() {
        TppPerhitungan tppWithBelumDiatur = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 4000000.0f, "Belum Diatur", 25.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithBelumDiatur.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithBelumDiatur.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithBelumDiatur.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithBelumDiatur)).thenReturn(tppWithBelumDiatur);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithBelumDiatur);

        assertEquals(tppWithBelumDiatur, result);
        assertEquals("Kondisi Kerja", result.jenisTpp());
        assertEquals("Belum Diatur", result.namaPerhitungan());
        verify(tppPerhitunganRepository).save(tppWithBelumDiatur);
    }

    @Test
    void ubahTppPerhitungan_WithJenisTppBelumDiatur_ShouldWork() {
        TppPerhitungan tppWithBelumDiatur = TppPerhitungan.of(
            "Belum Diatur", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 4000000.0f, "Absensi", 25.0f
        );

        when(pegawaiRepository.existsByNip(tppWithBelumDiatur.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithBelumDiatur.nama())).thenReturn(true);
        when(opdRepository.existsByKodeOpd(tppWithBelumDiatur.kodeOpd())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithBelumDiatur)).thenReturn(tppWithBelumDiatur);

        TppPerhitungan result = tppPerhitunganService.ubahTppPerhitungan(tppWithBelumDiatur);

        assertEquals(tppWithBelumDiatur, result);
        assertEquals("Belum Diatur", result.jenisTpp());
        verify(tppPerhitunganRepository).save(tppWithBelumDiatur);
    }

    @Test
    void detailTppPerhitungan_WhenMultipleResultsFound_ShouldReturnFirst() {
        String nip = "123456789";
        Integer bulan = 1;
        Integer tahun = 2024;
        TppPerhitungan firstTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", nip, "User1", bulan, tahun, 5000000.0f, "Absensi", 30.0f
        );
        TppPerhitungan secondTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", nip, "User1", bulan, tahun, 3000000.0f, "Produktifitas Kerja", 20.0f
        );
        List<TppPerhitungan> multipleResults = List.of(firstTpp, secondTpp);

        when(tppPerhitunganRepository.findByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(multipleResults);

        TppPerhitungan result = tppPerhitunganService.detailTppPerhitungan(nip, bulan, tahun);

        assertEquals(firstTpp, result);
        verify(tppPerhitunganRepository).findByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    @Test
    void tambahTppPerhitungan_WhenMaksimumZero_ShouldWork() {
        TppPerhitungan tppWithZeroMaksimum = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 0.0f, "Absensi", 0.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithZeroMaksimum.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithZeroMaksimum.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithZeroMaksimum.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithZeroMaksimum)).thenReturn(tppWithZeroMaksimum);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithZeroMaksimum);

        assertEquals(tppWithZeroMaksimum, result);
        assertEquals(0.0f, result.maksimum());
        assertEquals(0.0f, result.nilaiPerhitungan());
        verify(tppPerhitunganRepository).save(tppWithZeroMaksimum);
    }

    @Test
    void tambahTppPerhitungan_WhenEmptyNip_ShouldThrowPegawaiNotFoundException() {
        TppPerhitungan tppWithEmptyNip = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "", "Test User", 1, 2024, 5000000.0f, "Absensi", 30.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithEmptyNip.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip("")).thenReturn(false);

        assertThrows(PegawaiNotFoundException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(tppWithEmptyNip);
        });
    }

    @Test
    void tambahTppPerhitungan_WhenEmptyNama_ShouldThrowNamaPegawaiNotFoundException() {
        TppPerhitungan tppWithEmptyNama = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "", 1, 2024, 5000000.0f, "Absensi", 30.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithEmptyNama.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithEmptyNama.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai("")).thenReturn(false);

        assertThrows(NamaPegawaiNotFoundException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(tppWithEmptyNama);
        });
    }

    // Additional comprehensive test cases

    @Test
    void tambahTppPerhitungan_WhenNegativeMaksimum_ShouldWork() {
        TppPerhitungan tppWithNegativeMaksimum = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, -1000000.0f, "Absensi", 30.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithNegativeMaksimum.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithNegativeMaksimum.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithNegativeMaksimum.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithNegativeMaksimum)).thenReturn(tppWithNegativeMaksimum);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithNegativeMaksimum);

        assertEquals(tppWithNegativeMaksimum, result);
        assertEquals(-1000000.0f, result.maksimum());
        verify(tppPerhitunganRepository).save(tppWithNegativeMaksimum);
    }

    @Test
    void tambahTppPerhitungan_WhenNegativeNilaiPerhitungan_ShouldWork() {
        TppPerhitungan tppWithNegativeNilai = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 5000000.0f, "Absensi", -5.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithNegativeNilai.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithNegativeNilai.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithNegativeNilai.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithNegativeNilai)).thenReturn(tppWithNegativeNilai);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithNegativeNilai);

        assertEquals(tppWithNegativeNilai, result);
        assertEquals(-5.0f, result.nilaiPerhitungan());
        verify(tppPerhitunganRepository).save(tppWithNegativeNilai);
    }

    @Test
    void tambahTppPerhitungan_WithMaximumBoundaryValues_ShouldWork() {
        TppPerhitungan tppWithMaxValues = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 12, 9999, 100.0f, "Absensi", 100.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithMaxValues.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithMaxValues.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithMaxValues.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithMaxValues)).thenReturn(tppWithMaxValues);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithMaxValues);

        assertEquals(tppWithMaxValues, result);
        assertEquals(100.0f, result.maksimum());
        assertEquals(100.0f, result.nilaiPerhitungan());
        assertEquals(12, result.bulan());
        assertEquals(9999, result.tahun());
        verify(tppPerhitunganRepository).save(tppWithMaxValues);
    }

    @Test
    void tambahTppPerhitungan_WithMinimumBoundaryValues_ShouldWork() {
        TppPerhitungan tppWithMinValues = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 1, 1900, Float.MIN_VALUE, "Absensi", Float.MIN_VALUE
        );

        when(opdRepository.existsByKodeOpd(tppWithMinValues.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithMinValues.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithMinValues.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithMinValues)).thenReturn(tppWithMinValues);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithMinValues);

        assertEquals(tppWithMinValues, result);
        assertEquals(Float.MIN_VALUE, result.maksimum());
        assertEquals(Float.MIN_VALUE, result.nilaiPerhitungan());
        assertEquals(1, result.bulan());
        assertEquals(1900, result.tahun());
        verify(tppPerhitunganRepository).save(tppWithMinValues);
    }

    @Test
    void tambahTppPerhitungan_WithAllEnumCombinations_ShouldWork() {
        // Test KONDISI_KERJA + PRODUKTIFITAS_KERJA
        TppPerhitungan tpp1 = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User 1", 1, 2024, 5000000.0f, "Produktifitas Kerja", 25.0f
        );

        // Test BELUM_DIATUR + KEHADIRAN
        TppPerhitungan tpp2 = TppPerhitungan.of(
            "Belum Diatur", "OPD002", "PEMDA001", "987654321", "Test User 2", 2, 2024, 4000000.0f, "Absensi", 20.0f
        );

        // Test BELUM_DIATUR + PRODUKTIFITAS_KERJA
        TppPerhitungan tpp3 = TppPerhitungan.of(
            "Belum Diatur", "OPD003", "PEMDA001", "555666777", "Test User 3", 3, 2024, 3000000.0f, "Produktifitas Kerja", 15.0f
        );

        // Test KONDISI_KERJA + BELUM_DIATUR
        TppPerhitungan tpp4 = TppPerhitungan.of(
            "Kondisi Kerja", "OPD004", "PEMDA001", "111222333", "Test User 4", 4, 2024, 6000000.0f, "Belum Diatur", 35.0f
        );

        // Test BELUM_DIATUR + BELUM_DIATUR
        TppPerhitungan tpp5 = TppPerhitungan.of(
            "Belum Diatur", "OPD005", "PEMDA001", "999888777", "Test User 5", 5, 2024, 2000000.0f, "Belum Diatur", 10.0f
        );

        when(opdRepository.existsByKodeOpd(anyString())).thenReturn(true);
        when(pegawaiRepository.existsByNip(anyString())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(anyString())).thenReturn(true);
        when(tppPerhitunganRepository.save(any(TppPerhitungan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TppPerhitungan result1 = tppPerhitunganService.tambahTppPerhitungan(tpp1);
        TppPerhitungan result2 = tppPerhitunganService.tambahTppPerhitungan(tpp2);
        TppPerhitungan result3 = tppPerhitunganService.tambahTppPerhitungan(tpp3);
        TppPerhitungan result4 = tppPerhitunganService.tambahTppPerhitungan(tpp4);
        TppPerhitungan result5 = tppPerhitunganService.tambahTppPerhitungan(tpp5);

        // Verify all combinations work
        assertEquals("Kondisi Kerja", result1.jenisTpp());
        assertEquals("Produktifitas Kerja", result1.namaPerhitungan());

        assertEquals("Belum Diatur", result2.jenisTpp());
        assertEquals("Absensi", result2.namaPerhitungan());

        assertEquals("Belum Diatur", result3.jenisTpp());
        assertEquals("Produktifitas Kerja", result3.namaPerhitungan());

        assertEquals("Kondisi Kerja", result4.jenisTpp());
        assertEquals("Belum Diatur", result4.namaPerhitungan());

        assertEquals("Belum Diatur", result5.jenisTpp());
        assertEquals("Belum Diatur", result5.namaPerhitungan());

        verify(tppPerhitunganRepository, times(5)).save(any(TppPerhitungan.class));
    }

    @Test
    void tambahTppPerhitungan_WhenEmptyKodeOpd_ShouldThrowOpdNotFoundException() {
        TppPerhitungan tppWithEmptyKodeOpd = TppPerhitungan.of(
            "Kondisi Kerja", "", "PEMDA001", "123456789", "Test User", 1, 2024, 5000000.0f, "Absensi", 30.0f
        );

        when(opdRepository.existsByKodeOpd("")).thenReturn(false);

        assertThrows(OpdNotFoundException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(tppWithEmptyKodeOpd);
        });
    }

    @Test
    void tambahTppPerhitungan_WhenNullKodeOpd_ShouldThrowNullPointerException() {
        TppPerhitungan tppWithNullKodeOpd = TppPerhitungan.of(
            "Kondisi Kerja", null, "PEMDA001", "123456789", "Test User", 1, 2024, 5000000.0f, "Absensi", 30.0f
        );

        when(opdRepository.existsByKodeOpd(null)).thenReturn(false);

        assertThrows(OpdNotFoundException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(tppWithNullKodeOpd);
        });
    }

    @Test
    void listTppPerhitunganByKodeOpd_WithNullKodeOpd_ShouldWork() {
        when(tppPerhitunganRepository.findByKodeOpd(null)).thenReturn(Collections.emptyList());

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpd(null);

        assertFalse(result.iterator().hasNext());
        verify(tppPerhitunganRepository).findByKodeOpd(null);
    }

    @Test
    void listTppPerhitunganByKodeOpd_WithEmptyKodeOpd_ShouldWork() {
        when(tppPerhitunganRepository.findByKodeOpd("")).thenReturn(Collections.emptyList());

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpd("");

        assertFalse(result.iterator().hasNext());
        verify(tppPerhitunganRepository).findByKodeOpd("");
    }

    @Test
    void detailTppPerhitungan_WithNullParameters_ShouldThrowException() {
        assertThrows(TppPerhitunganNipBulanTahunNotFoundException.class, () -> {
            tppPerhitunganService.detailTppPerhitungan(null, null, null);
        });
    }

    @Test
    void ubahTppPerhitungan_WhenMaksimumChanged_ShouldSaveWithNewValue() {
        TppPerhitungan originalTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 5000000.0f, "Absensi", 30.0f
        );
        TppPerhitungan updatedTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 7000000.0f, "Absensi", 30.0f
        );

        when(pegawaiRepository.existsByNip(updatedTpp.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(updatedTpp.nama())).thenReturn(true);
        when(opdRepository.existsByKodeOpd(updatedTpp.kodeOpd())).thenReturn(true);
        when(tppPerhitunganRepository.save(updatedTpp)).thenReturn(updatedTpp);

        TppPerhitungan result = tppPerhitunganService.ubahTppPerhitungan(updatedTpp);

        assertEquals(updatedTpp, result);
        assertEquals(7000000.0f, result.maksimum());
        verify(tppPerhitunganRepository).save(updatedTpp);
    }

    @Test
    void ubahTppPerhitungan_WhenNilaiPerhitunganChanged_ShouldSaveWithNewValue() {
        TppPerhitungan originalTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 5000000.0f, "Absensi", 30.0f
        );
        TppPerhitungan updatedTpp = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 5000000.0f, "Absensi", 45.0f
        );

        when(pegawaiRepository.existsByNip(updatedTpp.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(updatedTpp.nama())).thenReturn(true);
        when(opdRepository.existsByKodeOpd(updatedTpp.kodeOpd())).thenReturn(true);
        when(tppPerhitunganRepository.save(updatedTpp)).thenReturn(updatedTpp);

        TppPerhitungan result = tppPerhitunganService.ubahTppPerhitungan(updatedTpp);

        assertEquals(updatedTpp, result);
        assertEquals(45.0f, result.nilaiPerhitungan());
        verify(tppPerhitunganRepository).save(updatedTpp);
    }

    @Test
    void listTppPerhitunganByKodeOpdAndBulanAndTahun_WhenEmptyList_ShouldReturnEmptyIterable() {
        String kodeOpd = "NONEXISTENT";
        Integer bulan = 13;
        Integer tahun = 2025;
        when(tppPerhitunganRepository.findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun)).thenReturn(Collections.emptyList());

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);

        assertFalse(result.iterator().hasNext());
        verify(tppPerhitunganRepository).findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
    }

    @Test
    void listTppPerhitunganByKodeOpdAndBulanAndTahun_WithBoundaryMonth_ShouldWork() {
        String kodeOpd = "OPD001";

        // Test month 1
        when(tppPerhitunganRepository.findByKodeOpdAndBulanAndTahun(kodeOpd, 1, 2024)).thenReturn(List.of(sampleTppPerhitungan));
        Iterable<TppPerhitungan> resultJan = tppPerhitunganService.listTppPerhitunganByKodeOpdAndBulanAndTahun(kodeOpd, 1, 2024);
        assertEquals(List.of(sampleTppPerhitungan), resultJan);

        // Test month 12
        when(tppPerhitunganRepository.findByKodeOpdAndBulanAndTahun(kodeOpd, 12, 2024)).thenReturn(List.of(sampleTppPerhitungan));
        Iterable<TppPerhitungan> resultDec = tppPerhitunganService.listTppPerhitunganByKodeOpdAndBulanAndTahun(kodeOpd, 12, 2024);
        assertEquals(List.of(sampleTppPerhitungan), resultDec);

        verify(tppPerhitunganRepository).findByKodeOpdAndBulanAndTahun(kodeOpd, 1, 2024);
        verify(tppPerhitunganRepository).findByKodeOpdAndBulanAndTahun(kodeOpd, 12, 2024);
    }

    @Test
    void hapusTppPerhitunganByNipBulanTahun_WithNullParameters_ShouldCallRepository() {
        assertDoesNotThrow(() -> {
            tppPerhitunganService.hapusTppPerhitunganByNipBulanTahun(null, null, null);
        });

        verify(tppPerhitunganRepository).deleteByNipAndBulanAndTahun(null, null, null);
    }

    @Test
    void tambahTppPerhitungan_WithLongStrings_ShouldWork() {
        String longNip = "12345678901234567890";
        String longNama = "A".repeat(255);
        String longKodeOpd = "OPD" + "A".repeat(100);
        String longKodePemda = "PEMDA" + "B".repeat(100);

        TppPerhitungan tppWithLongStrings = TppPerhitungan.of(
            "Kondisi Kerja", longKodeOpd, longKodePemda, longNip, longNama, 1, 2024, 5000000.0f, "Absensi", 30.0f
        );

        when(opdRepository.existsByKodeOpd(longKodeOpd)).thenReturn(true);
        when(pegawaiRepository.existsByNip(longNip)).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(longNama)).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithLongStrings)).thenReturn(tppWithLongStrings);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithLongStrings);

        assertEquals(tppWithLongStrings, result);
        assertEquals(longNip, result.nip());
        assertEquals(longNama, result.nama());
        assertEquals(longKodeOpd, result.kodeOpd());
        assertEquals(longKodePemda, result.kodePemda());
        verify(tppPerhitunganRepository).save(tppWithLongStrings);
    }

    @Test
    void tambahTppPerhitungan_WithSpecialCharactersInNama_ShouldWork() {
        String namaWithSpecialChars = "John O'Connor-Niño @#$%^&*()";

        TppPerhitungan tppWithSpecialChars = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "123456789", namaWithSpecialChars, 1, 2024, 5000000.0f, "Absensi", 30.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithSpecialChars.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithSpecialChars.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(namaWithSpecialChars)).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithSpecialChars)).thenReturn(tppWithSpecialChars);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithSpecialChars);

        assertEquals(tppWithSpecialChars, result);
        assertEquals(namaWithSpecialChars, result.nama());
        verify(tppPerhitunganRepository).save(tppWithSpecialChars);
    }

    @Test
    void listTppPerhitunganByNip_WithVeryLongNip_ShouldCallRepository() {
        String veryLongNip = "1".repeat(100);

        when(tppPerhitunganRepository.findByNip(veryLongNip)).thenReturn(Collections.emptyList());

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNip(veryLongNip);

        assertFalse(result.iterator().hasNext());
        verify(tppPerhitunganRepository).findByNip(veryLongNip);
    }
}
