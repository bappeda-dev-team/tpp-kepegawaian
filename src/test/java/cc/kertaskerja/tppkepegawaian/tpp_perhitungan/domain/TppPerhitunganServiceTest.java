package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TppPerhitunganServiceTest {
    @InjectMocks
    private TppPerhitunganService tppPerhitunganService;

    @Mock
    private TppPerhitunganRepository tppPerhitunganRepository;

    @Mock
    private PegawaiRepository pegawaiRepository;

    @Mock
    private OpdRepository opdRepository;

    private TppPerhitungan tppPerhitungan;

    @BeforeEach
    void setUp() {
        tppPerhitungan = new TppPerhitungan(
                1L,
                JenisTpp.BEBAN_KERJA,
                "kodeOpd",
                "nip",
                "kodePemda",
                "namaPerhitungan",
                "22,25",
                30f,
                1,
                2023,
                47f,
                null,
                null
        );
    }

    @Test
    void listTppPerhitunganByKodeOpd() {
        when(tppPerhitunganRepository.findByKodeOpd("kodeOpd")).thenReturn(Collections.singletonList(tppPerhitungan));
        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpd("kodeOpd");
        assertThat(result).contains(tppPerhitungan);
    }

    @Test
    void existsByBulanAndTahun() {
        when(tppPerhitunganRepository.existsByBulanAndTahun(1, 2023)).thenReturn(true);
        boolean result = tppPerhitunganService.existsByBulanAndTahun(1, 2023);
        assertThat(result).isTrue();
    }

    @Test
    void listTppPerhitunganByNip() {
        when(tppPerhitunganRepository.findByNip("nip")).thenReturn(Collections.singletonList(tppPerhitungan));
        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNip("nip");
        assertThat(result).contains(tppPerhitungan);
    }

    @Test
    void listTppPerhitunganByBulanAndTahun() {
        when(tppPerhitunganRepository.findByBulanAndTahun(1, 2023)).thenReturn(Collections.singletonList(tppPerhitungan));
        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByBulanAndTahun(1, 2023);
        assertThat(result).contains(tppPerhitungan);
    }

    @Test
    void detailTppPerhitungan() {
        when(tppPerhitunganRepository.findById(1L)).thenReturn(Optional.of(tppPerhitungan));
        TppPerhitungan result = tppPerhitunganService.detailTppPerhitungan(1L);
        assertThat(result).isEqualTo(tppPerhitungan);
    }

    @Test
    void ubahTppPerhitungan() {
        when(tppPerhitunganRepository.existsById(1L)).thenReturn(true);
        when(opdRepository.existsByKodeOpd("kodeOpd")).thenReturn(true);
        when(pegawaiRepository.existsByNip("nip")).thenReturn(true);
        when(tppPerhitunganRepository.existsByBulanAndTahun(1, 2023)).thenReturn(false);
        when(tppPerhitunganRepository.save(tppPerhitungan)).thenReturn(tppPerhitungan);

        TppPerhitungan result = tppPerhitunganService.ubahTppPerhitungan(1L, tppPerhitungan);
        assertThat(result).isEqualTo(tppPerhitungan);
    }

    @Test
    void tambahTppPerhitungan() {
        when(opdRepository.existsByKodeOpd("kodeOpd")).thenReturn(true);
        when(pegawaiRepository.existsByNip("nip")).thenReturn(true);
        when(tppPerhitunganRepository.existsByNip("nip")).thenReturn(false);
        when(tppPerhitunganRepository.existsByBulanAndTahun(1, 2023)).thenReturn(false);
        when(tppPerhitunganRepository.save(tppPerhitungan)).thenReturn(tppPerhitungan);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppPerhitungan);
        assertThat(result).isEqualTo(tppPerhitungan);
    }

    @Test
    void hapusTppPerhitungan() {
        when(tppPerhitunganRepository.existsById(1L)).thenReturn(true);
        tppPerhitunganService.hapusTppPerhitungan(1L);
    }
}
