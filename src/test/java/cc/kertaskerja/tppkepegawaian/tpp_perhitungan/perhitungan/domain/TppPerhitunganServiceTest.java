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
            JenisTpp.KONDISI_KERJA,
            "OPD001",
            "PEMDA001",
            "701301613358689213",
            "Wahyu",
            2,
            2024,
            5000000.0f,
            NamaPerhitungan.KEHADIRAN,
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
        assertEquals(JenisTpp.KONDISI_KERJA, result.jenisTpp());
        assertEquals(NamaPerhitungan.KEHADIRAN, result.namaPerhitungan());
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
        assertEquals(JenisTpp.KONDISI_KERJA, result.jenisTpp());
        assertEquals(NamaPerhitungan.KEHADIRAN, result.namaPerhitungan());
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
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", null, "John Doe", 1, 2024, 5000000.0f, NamaPerhitungan.KEHADIRAN, 27.0f
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
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", "123456789", "John Doe", null, 2024, 5000000.0f, NamaPerhitungan.KEHADIRAN, 27.0f
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
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", "123456789", "John Doe", 1, null, 5000000.0f, NamaPerhitungan.KEHADIRAN, 27.0f
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
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", "123456789", "John Doe", null, null, 5000000.0f, NamaPerhitungan.KEHADIRAN, 27.0f
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
            JenisTpp.BELUM_DIATUR, "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 4000000.0f, NamaPerhitungan.PRODUKTIFITAS_KERJA, 25.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithBelumDiatur.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithBelumDiatur.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithBelumDiatur.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithBelumDiatur)).thenReturn(tppWithBelumDiatur);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithBelumDiatur);

        assertEquals(tppWithBelumDiatur, result);
        assertEquals(JenisTpp.BELUM_DIATUR, result.jenisTpp());
        assertEquals(NamaPerhitungan.PRODUKTIFITAS_KERJA, result.namaPerhitungan());
        verify(tppPerhitunganRepository).save(tppWithBelumDiatur);
    }

    @Test
    void tambahTppPerhitungan_WithNamaPerhitunganBelumDiatur_ShouldWork() {
        TppPerhitungan tppWithBelumDiatur = TppPerhitungan.of(
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 4000000.0f, NamaPerhitungan.BELUM_DIATUR, 25.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithBelumDiatur.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithBelumDiatur.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithBelumDiatur.nama())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithBelumDiatur)).thenReturn(tppWithBelumDiatur);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppWithBelumDiatur);

        assertEquals(tppWithBelumDiatur, result);
        assertEquals(JenisTpp.KONDISI_KERJA, result.jenisTpp());
        assertEquals(NamaPerhitungan.BELUM_DIATUR, result.namaPerhitungan());
        verify(tppPerhitunganRepository).save(tppWithBelumDiatur);
    }

    @Test
    void ubahTppPerhitungan_WithJenisTppBelumDiatur_ShouldWork() {
        TppPerhitungan tppWithBelumDiatur = TppPerhitungan.of(
            JenisTpp.BELUM_DIATUR, "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 4000000.0f, NamaPerhitungan.KEHADIRAN, 25.0f
        );

        when(pegawaiRepository.existsByNip(tppWithBelumDiatur.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai(tppWithBelumDiatur.nama())).thenReturn(true);
        when(opdRepository.existsByKodeOpd(tppWithBelumDiatur.kodeOpd())).thenReturn(true);
        when(tppPerhitunganRepository.save(tppWithBelumDiatur)).thenReturn(tppWithBelumDiatur);

        TppPerhitungan result = tppPerhitunganService.ubahTppPerhitungan(tppWithBelumDiatur);

        assertEquals(tppWithBelumDiatur, result);
        assertEquals(JenisTpp.BELUM_DIATUR, result.jenisTpp());
        verify(tppPerhitunganRepository).save(tppWithBelumDiatur);
    }

    @Test
    void detailTppPerhitungan_WhenMultipleResultsFound_ShouldReturnFirst() {
        String nip = "123456789";
        Integer bulan = 1;
        Integer tahun = 2024;
        TppPerhitungan firstTpp = TppPerhitungan.of(
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", nip, "User1", bulan, tahun, 5000000.0f, NamaPerhitungan.KEHADIRAN, 30.0f
        );
        TppPerhitungan secondTpp = TppPerhitungan.of(
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", nip, "User1", bulan, tahun, 3000000.0f, NamaPerhitungan.PRODUKTIFITAS_KERJA, 20.0f
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
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", "123456789", "Test User", 1, 2024, 0.0f, NamaPerhitungan.KEHADIRAN, 0.0f
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
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", "", "Test User", 1, 2024, 5000000.0f, NamaPerhitungan.KEHADIRAN, 30.0f
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
            JenisTpp.KONDISI_KERJA, "OPD001", "PEMDA001", "123456789", "", 1, 2024, 5000000.0f, NamaPerhitungan.KEHADIRAN, 30.0f
        );

        when(opdRepository.existsByKodeOpd(tppWithEmptyNama.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppWithEmptyNama.nip())).thenReturn(true);
        when(pegawaiRepository.existsByNamaPegawai("")).thenReturn(false);

        assertThrows(NamaPegawaiNotFoundException.class, () -> {
            tppPerhitunganService.tambahTppPerhitungan(tppWithEmptyNama);
        });
    }
}
