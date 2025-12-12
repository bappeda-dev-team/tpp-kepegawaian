package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
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
    void listTppPerhitunganByNipAndBulanAndTahun_returnsResultsFromRepository() {
        when(tppPerhitunganRepository.findByNipAndBulanAndTahun("701301613358689213", 2, 2024))
            .thenReturn(List.of(sampleTppPerhitungan));

        Iterable<TppPerhitungan> result =
            tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun("701301613358689213", 2, 2024);

        assertEquals(List.of(sampleTppPerhitungan), result);
        verify(tppPerhitunganRepository).findByNipAndBulanAndTahun("701301613358689213", 2, 2024);
    }

    @Test
    void listTppPerhitunganByKodeOpd_returnsResultsFromRepository() {
        when(tppPerhitunganRepository.findByKodeOpd("OPD001")).thenReturn(List.of(sampleTppPerhitungan));

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpd("OPD001");

        assertEquals(List.of(sampleTppPerhitungan), result);
        verify(tppPerhitunganRepository).findByKodeOpd("OPD001");
    }

    @Test
    void listTppPerhitunganByNip_returnsResultsFromRepository() {
        when(tppPerhitunganRepository.findByNip("701301613358689213")).thenReturn(List.of(sampleTppPerhitungan));

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNip("701301613358689213");

        assertEquals(List.of(sampleTppPerhitungan), result);
        verify(tppPerhitunganRepository).findByNip("701301613358689213");
    }

    @Test
    void listTppPerhitunganByNama_returnsResultsFromRepository() {
        when(tppPerhitunganRepository.findByNama("Wahyu")).thenReturn(List.of(sampleTppPerhitungan));

        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNama("Wahyu");

        assertEquals(List.of(sampleTppPerhitungan), result);
        verify(tppPerhitunganRepository).findByNama("Wahyu");
    }

    @Test
    void listTppPerhitunganByNipBulanTahun_returnsResultsFromRepository() {
        when(tppPerhitunganRepository.findByNipAndBulanAndTahun("701301613358689213", 2, 2024))
            .thenReturn(List.of(sampleTppPerhitungan));

        Iterable<TppPerhitungan> result =
            tppPerhitunganService.listTppPerhitunganByNipBulanTahun("701301613358689213", 2, 2024);

        assertEquals(List.of(sampleTppPerhitungan), result);
        verify(tppPerhitunganRepository).findByNipAndBulanAndTahun("701301613358689213", 2, 2024);
    }

    @Test
    void listTppPerhitunganByKodeOpdAndBulanAndTahun_returnsResultsFromRepository() {
        when(tppPerhitunganRepository.findByKodeOpdAndBulanAndTahun("OPD001", 2, 2024))
            .thenReturn(List.of(sampleTppPerhitungan));

        Iterable<TppPerhitungan> result =
            tppPerhitunganService.listTppPerhitunganByKodeOpdAndBulanAndTahun("OPD001", 2, 2024);

        assertEquals(List.of(sampleTppPerhitungan), result);
        verify(tppPerhitunganRepository).findByKodeOpdAndBulanAndTahun("OPD001", 2, 2024);
    }

    @Test
    void detailTppPerhitungan_whenDataExists_returnsFirstResult() {
        when(tppPerhitunganRepository.findByNipAndBulanAndTahun("701301613358689213", 2, 2024))
            .thenReturn(List.of(sampleTppPerhitungan));

        TppPerhitungan result = tppPerhitunganService.detailTppPerhitungan("701301613358689213", 2, 2024);

        assertEquals(sampleTppPerhitungan, result);
        verify(tppPerhitunganRepository).findByNipAndBulanAndTahun("701301613358689213", 2, 2024);
    }

    @Test
    void detailTppPerhitungan_whenDataMissing_throwsException() {
        when(tppPerhitunganRepository.findByNipAndBulanAndTahun("missing", 1, 2024)).thenReturn(Collections.emptyList());

        assertThrows(
            TppPerhitunganNipBulanTahunNotFoundException.class,
            () -> tppPerhitunganService.detailTppPerhitungan("missing", 1, 2024)
        );
    }

    @Test
    void ubahTppPerhitungan_whenOpdExists_savesEntity() {
        when(opdRepository.existsByKodeOpd(sampleTppPerhitungan.kodeOpd())).thenReturn(true);
        when(tppPerhitunganRepository.save(sampleTppPerhitungan)).thenReturn(sampleTppPerhitungan);

        TppPerhitungan result = tppPerhitunganService.ubahTppPerhitungan(sampleTppPerhitungan);

        assertEquals(sampleTppPerhitungan, result);
        verify(opdRepository).existsByKodeOpd("OPD001");
        verify(tppPerhitunganRepository).save(sampleTppPerhitungan);
        verifyNoInteractions(pegawaiRepository);
    }

    @Test
    void ubahTppPerhitungan_whenOpdMissing_throwsException() {
        when(opdRepository.existsByKodeOpd(sampleTppPerhitungan.kodeOpd())).thenReturn(false);

        assertThrows(OpdNotFoundException.class, () -> tppPerhitunganService.ubahTppPerhitungan(sampleTppPerhitungan));
        verify(opdRepository).existsByKodeOpd("OPD001");
        verifyNoInteractions(pegawaiRepository);
        verifyNoInteractions(tppPerhitunganRepository);
    }

    @Test
    void tambahTppPerhitungan_whenValid_savesEntity() {
        when(tppPerhitunganRepository.save(sampleTppPerhitungan)).thenReturn(sampleTppPerhitungan);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(sampleTppPerhitungan);

        assertEquals(sampleTppPerhitungan, result);
        verify(tppPerhitunganRepository).save(sampleTppPerhitungan);
        verifyNoInteractions(opdRepository);
        verifyNoInteractions(pegawaiRepository);
    }

    @Test
    void tambahTppPerhitungan_whenNipNull_throwsIllegalArgumentException() {
        TppPerhitungan invalid = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", null, "Wahyu", 2, 2024, 5000000.0f, "Absensi", 27.0f
        );

        IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> tppPerhitunganService.tambahTppPerhitungan(invalid));

        assertEquals("NIP, Bulan, dan Tahun tidak boleh null.", exception.getMessage());
        verifyNoInteractions(tppPerhitunganRepository);
    }

    @Test
    void tambahTppPerhitungan_whenBulanNull_throwsIllegalArgumentException() {
        TppPerhitungan invalid = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "701301613358689213", "Wahyu", null, 2024, 5000000.0f, "Absensi", 27.0f
        );

        IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> tppPerhitunganService.tambahTppPerhitungan(invalid));

        assertEquals("NIP, Bulan, dan Tahun tidak boleh null.", exception.getMessage());
        verifyNoInteractions(tppPerhitunganRepository);
    }

    @Test
    void tambahTppPerhitungan_whenTahunNull_throwsIllegalArgumentException() {
        TppPerhitungan invalid = TppPerhitungan.of(
            "Kondisi Kerja", "OPD001", "PEMDA001", "701301613358689213", "Wahyu", 2, null, 5000000.0f, "Absensi", 27.0f
        );

        IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> tppPerhitunganService.tambahTppPerhitungan(invalid));

        assertEquals("NIP, Bulan, dan Tahun tidak boleh null.", exception.getMessage());
        verifyNoInteractions(tppPerhitunganRepository);
    }

    @Test
    void hapusTppPerhitungan_whenDataExists_deletesEntity() {
        when(tppPerhitunganRepository.existsById(1L)).thenReturn(true);

        tppPerhitunganService.hapusTppPerhitungan(1L);

        verify(tppPerhitunganRepository).existsById(1L);
        verify(tppPerhitunganRepository).deleteById(1L);
    }

    @Test
    void hapusTppPerhitungan_whenDataMissing_throwsException() {
        when(tppPerhitunganRepository.existsById(1L)).thenReturn(false);

        assertThrows(TppPerhitunganNotFoundException.class, () -> tppPerhitunganService.hapusTppPerhitungan(1L));
        verify(tppPerhitunganRepository).existsById(1L);
        verify(tppPerhitunganRepository, never()).deleteById(anyLong());
    }

    @Test
    void hapusTppPerhitunganByNipBulanTahun_deletesByCompositeKey() {
        tppPerhitunganService.hapusTppPerhitunganByNipBulanTahun("701301613358689213", 2, 2024);

        verify(tppPerhitunganRepository).deleteByNipAndBulanAndTahun("701301613358689213", 2, 2024);
        verifyNoInteractions(opdRepository);
        verifyNoInteractions(pegawaiRepository);
    }
}
