package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    void shouldReturnListWhenFindingByKodeOpd() {
        when(tppPerhitunganRepository.findByKodeOpd("kodeOpd")).thenReturn(Collections.singletonList(tppPerhitungan));
        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpd("kodeOpd");
        assertThat(result).contains(tppPerhitungan);
    }

    @Test
    void shouldReturnEmptyListWhenFindingByKodeOpdAndNoneExist() {
        when(tppPerhitunganRepository.findByKodeOpd("kodeOpd_tidak_ada")).thenReturn(Collections.emptyList());
        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByKodeOpd("kodeOpd_tidak_ada");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnListWhenFindingByNip() {
        when(tppPerhitunganRepository.findByNip("nip")).thenReturn(Collections.singletonList(tppPerhitungan));
        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNip("nip");
        assertThat(result).contains(tppPerhitungan);
    }

    @Test
    void shouldReturnEmptyListWhenFindingByNipAndNoneExist() {
        when(tppPerhitunganRepository.findByNip("nip_tidak_ada")).thenReturn(Collections.emptyList());
        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByNip("nip_tidak_ada");
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldReturnListWhenFindingByMonthAndYear() {
        when(tppPerhitunganRepository.findByBulanAndTahun(1, 2023)).thenReturn(Collections.singletonList(tppPerhitungan));
        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByBulanAndTahun(1, 2023);
        assertThat(result).contains(tppPerhitungan);
    }
    
    @Test
    void shouldReturnEmptyListWhenFindingByMonthAndYearAndNoneExist() {
        when(tppPerhitunganRepository.findByBulanAndTahun(99, 2099)).thenReturn(Collections.emptyList());
        Iterable<TppPerhitungan> result = tppPerhitunganService.listTppPerhitunganByBulanAndTahun(99, 2099);
        assertThat(result).isEmpty();
    }
    
    @Test
    void shouldReturnTppPerhitunganWhenFindingById() {
        when(tppPerhitunganRepository.findById(1L)).thenReturn(Optional.of(tppPerhitungan));
        TppPerhitungan result = tppPerhitunganService.detailTppPerhitungan(1L);
        assertThat(result).isEqualTo(tppPerhitungan);
    }
    
    @Test
    void shouldThrowExceptionWhenFindingByIdAndItDoesNotExist() {
        when(tppPerhitunganRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> tppPerhitunganService.detailTppPerhitungan(999L))
            .isInstanceOf(TppPerhitunganNotFoundException.class)
            .hasMessageContaining("Tpp Perhitungan dengan Id 999 tidak ditemukan.");
    }
    
    @Test
    void shouldUpdateAndReturnTppPerhitungan() {
        when(tppPerhitunganRepository.existsById(1L)).thenReturn(true);
        when(opdRepository.existsByKodeOpd("kodeOpd")).thenReturn(true);
        when(pegawaiRepository.existsByNip("nip")).thenReturn(true);
        when(tppPerhitunganRepository.existsByBulanAndTahun(1, 2023)).thenReturn(false);
        when(tppPerhitunganRepository.save(tppPerhitungan)).thenReturn(tppPerhitungan);

        TppPerhitungan result = tppPerhitunganService.ubahTppPerhitungan(1L, tppPerhitungan);
        assertThat(result).isEqualTo(tppPerhitungan);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndIdDoesNotExist() {
        when(tppPerhitunganRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> tppPerhitunganService.ubahTppPerhitungan(999L, tppPerhitungan))
                .isInstanceOf(TppPerhitunganNotFoundException.class)
                .hasMessageContaining("Tpp Perhitungan dengan Id 999 tidak ditemukan.");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndKodeOpdDoesNotExist() {
        when(tppPerhitunganRepository.existsById(1L)).thenReturn(true);
        when(opdRepository.existsByKodeOpd("kodeOpd")).thenReturn(false);

        assertThatThrownBy(() -> tppPerhitunganService.ubahTppPerhitungan(1L, tppPerhitungan))
                .isInstanceOf(OpdNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndNipDoesNotExist() {
        when(tppPerhitunganRepository.existsById(1L)).thenReturn(true);
        when(opdRepository.existsByKodeOpd("kodeOpd")).thenReturn(true);
        when(pegawaiRepository.existsByNip("nip")).thenReturn(false);

        assertThatThrownBy(() -> tppPerhitunganService.ubahTppPerhitungan(1L, tppPerhitungan))
                .isInstanceOf(PegawaiNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingAndMonthAndYearAlreadyExist() {
        when(tppPerhitunganRepository.existsById(1L)).thenReturn(true);
        when(opdRepository.existsByKodeOpd("kodeOpd")).thenReturn(true);
        when(pegawaiRepository.existsByNip("nip")).thenReturn(true);
        when(tppPerhitunganRepository.existsByBulanAndTahun(1, 2023)).thenReturn(true);

        assertThatThrownBy(() -> tppPerhitunganService.ubahTppPerhitungan(1L, tppPerhitungan))
                .isInstanceOf(TppPerhitunganBulanTahunNotFoundException.class);
    }

    @Test
    void shouldAddAndReturnTppPerhitungan() {
        when(opdRepository.existsByKodeOpd("kodeOpd")).thenReturn(true);
        when(pegawaiRepository.existsByNip("nip")).thenReturn(true);
        when(tppPerhitunganRepository.existsByNip("nip")).thenReturn(false);
        when(tppPerhitunganRepository.existsByBulanAndTahun(1, 2023)).thenReturn(false);
        when(tppPerhitunganRepository.save(tppPerhitungan)).thenReturn(tppPerhitungan);

        TppPerhitungan result = tppPerhitunganService.tambahTppPerhitungan(tppPerhitungan);
        assertThat(result).isEqualTo(tppPerhitungan);
    }

    @Test
    void shouldThrowExceptionWhenAddingAndKodeOpdDoesNotExist() {
        when(opdRepository.existsByKodeOpd("kodeOpd")).thenReturn(false);

        assertThatThrownBy(() -> tppPerhitunganService.tambahTppPerhitungan(tppPerhitungan))
                .isInstanceOf(OpdNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenAddingAndNipDoesNotExist() {
        when(opdRepository.existsByKodeOpd("kodeOpd")).thenReturn(true);
        when(pegawaiRepository.existsByNip("nip")).thenReturn(false);

        assertThatThrownBy(() -> tppPerhitunganService.tambahTppPerhitungan(tppPerhitungan))
                .isInstanceOf(PegawaiNotFoundException.class);
    }

    @Test
    void shouldDeleteTppPerhitungan() {
        when(tppPerhitunganRepository.existsById(1L)).thenReturn(true);
        tppPerhitunganService.hapusTppPerhitungan(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingAndIdDoesNotExist() {
        when(tppPerhitunganRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> tppPerhitunganService.hapusTppPerhitungan(999L))
                .isInstanceOf(TppPerhitunganNotFoundException.class)
                .hasMessageContaining("Tpp Perhitungan dengan Id 999 tidak ditemukan.");
    }
}
