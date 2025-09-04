package cc.kertaskerja.tppkepegawaian.tpp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.TppPerhitunganRepository;

@ExtendWith(MockitoExtension.class)
public class TppServiceTest {
    @Mock
    private TppRepository tppRepository;

    @Mock
    private TppPerhitunganRepository tppPerhitunganRepository;

    @Mock
    private OpdRepository opdRepository;

    @Mock
    private PegawaiRepository pegawaiRepository;

    @InjectMocks
    private TppService tppService;

    private Tpp tpp;

    @BeforeEach
    void setUp() {
        tpp = Tpp.of(
                JenisTpp.BEBAN_KERJA,
                "OPD01",
                "12345",
                "PEMDA01",
                "Keterangan",
                500000.0f,
                120000f,
                47.0f,
                1,
                2023,
                240000.0f);
    }

    @Test
    void testDetailTpp() {
        when(tppRepository.findById(1L)).thenReturn(Optional.of(tpp));

        Tpp result = tppService.detailTpp(1L);

        assertThat(result).isEqualTo(tpp);
    }

    @Test
    void testDetailTppNotFound() {
        when(tppRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TppNotFoundException.class, () -> {
            tppService.detailTpp(1L);
        });
    }

    @Test
    void testTambahTpp() {
        when(opdRepository.existsByKodeOpd(tpp.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tpp.nip())).thenReturn(true);
        when(tppRepository.existsByNip(tpp.nip())).thenReturn(false);
        when(tppPerhitunganRepository.existsByHasilPerhitungan(tpp.hasilPerhitungan())).thenReturn(true);
        when(tppPerhitunganRepository.existsByBulanAndTahun(tpp.bulan(), tpp.tahun())).thenReturn(true);
        when(tppRepository.save(tpp)).thenReturn(tpp);

        Tpp result = tppService.tambahTpp(tpp);

        assertThat(result).isEqualTo(tpp);
    }

    @Test
    void testUbahTpp() {
        Tpp tppToUpdate = new Tpp(1L, tpp.jenisTpp(), tpp.kodeOpd(), tpp.nip(), tpp.kodePemda(), tpp.keterangan(), tpp.nilaiInput(), tpp.maksimum(), tpp.hasilPerhitungan(), tpp.bulan(), tpp.tahun(), tpp.totalTpp(), null, null);

        when(tppRepository.existsById(1L)).thenReturn(true);
        when(opdRepository.existsByKodeOpd(tppToUpdate.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(tppToUpdate.nip())).thenReturn(true);
        when(tppPerhitunganRepository.existsByHasilPerhitungan(tppToUpdate.hasilPerhitungan())).thenReturn(true);
        when(tppPerhitunganRepository.existsByBulanAndTahun(tppToUpdate.bulan(), tppToUpdate.tahun())).thenReturn(true);
        when(tppRepository.save(tppToUpdate)).thenReturn(tppToUpdate);

        Tpp result = tppService.ubahTpp(1L, tppToUpdate);

        assertThat(result).isEqualTo(tppToUpdate);
    }

    @Test
    void testHapusTpp() {
        when(tppRepository.existsById(1L)).thenReturn(true);
        tppService.hapusTpp(1L);
        verify(tppRepository).deleteById(1L);
    }
}
