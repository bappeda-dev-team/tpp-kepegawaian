package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanPegawaiSudahAdaException;
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

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JabatanServiceTest {
    @Mock
    private JabatanRepository jabatanRepository;

    @Mock
    private PegawaiRepository pegawaiRepository;

    @Mock
    private OpdRepository opdRepository;

    @InjectMocks
    private JabatanService jabatanService;

    private Jabatan testJabatan;
    private Calendar tanggalMulai;
    private Calendar tanggalAkhir;
    
    @BeforeEach
    void setUp() {
        tanggalMulai = Calendar.getInstance();
        tanggalMulai.set(2023, Calendar.JANUARY, 1);
        
        tanggalAkhir = Calendar.getInstance();
        tanggalAkhir.set(2025, Calendar.DECEMBER, 31);
        
        testJabatan = new Jabatan(
                1L,
                "198001012010011001",
                "Analis Ahli Muda",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL,
                Eselon.ESELON_III,
                "Junior",
                "Golongan I",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );
    }
    
    @Test
    void listJabatanByKodeOpd_WhenKodeOpdExists_ShouldReturnJabatanList() {
        List<Jabatan> jabatanList = List.of(testJabatan);
        when(jabatanRepository.findByKodeOpd("OPD-001")).thenReturn(jabatanList);

        Iterable<Jabatan> result = jabatanService.listJabatanByKodeOpd("OPD-001");

        assertThat(result).containsExactly(testJabatan);
        verify(jabatanRepository).findByKodeOpd("OPD-001");
    }

    @Test
    void detailJabatan_WhenJabatanExists_ShouldReturnJabatan() {
        when(jabatanRepository.findById(1L)).thenReturn(Optional.of(testJabatan));

        Jabatan result = jabatanService.detailJabatan(1L);

        assertThat(result).isEqualTo(testJabatan);
        verify(jabatanRepository).findById(1L);
    }
    
    @Test
    void detailJabatan_WhenJabatanNotExists_ShouldThrowException() {
        when(jabatanRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jabatanService.detailJabatan(999L))
                .isInstanceOf(JabatanNotFoundException.class);
        verify(jabatanRepository).findById(999L);
    }
    
    @Test
    void tambahJabatan_WhenJabatanValid_ShouldSaveAndReturnJabatan() {
        Jabatan newJabatan = new Jabatan(
                null,
                "200601012010012001",
                "Sekretaris Dinas",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Senior",
                "Golongan III",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                null,
                null
        );

        when(opdRepository.existsByKodeOpd(newJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(newJabatan.nip())).thenReturn(true);
        when(jabatanRepository.findAllByNip(newJabatan.nip())).thenReturn(List.of());
        when(jabatanRepository.save(any(Jabatan.class))).thenReturn(
                new Jabatan(
                        2L,
                        newJabatan.nip(),
                        newJabatan.namaJabatan(),
                        newJabatan.kodeOpd(),
                        newJabatan.statusJabatan(),
                        newJabatan.jenisJabatan(),
                        newJabatan.eselon(),
                        newJabatan.pangkat(),
                        newJabatan.golongan(),
                        newJabatan.tanggalMulai(),
                        newJabatan.tanggalAkhir(),
                        Instant.now(),
                        Instant.now()
                )
        );

        Jabatan result = jabatanService.tambahJabatan(newJabatan);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.nip()).isEqualTo("200601012010012001");
        assertThat(result.namaJabatan()).isEqualTo("Sekretaris Dinas");
        assertThat(result.kodeOpd()).isEqualTo("OPD-001");
        assertThat(result.statusJabatan()).isEqualTo(StatusJabatan.UTAMA);
        assertThat(result.jenisJabatan()).isEqualTo(JenisJabatan.JABATAN_PEMIMPIN_TINGGI);
        assertThat(result.eselon()).isEqualTo(Eselon.ESELON_IV);
        assertThat(result.pangkat()).isEqualTo("Senior");
        assertThat(result.golongan()).isEqualTo("Golongan III");
        verify(opdRepository).existsByKodeOpd(newJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(newJabatan.nip());
        verify(jabatanRepository).findAllByNip(newJabatan.nip());
        verify(jabatanRepository).save(newJabatan);
    }
    
    @Test
    void tambahJabatan_WhenOpdNotExists_ShouldThrowException() {
        Jabatan newJabatan = new Jabatan(
                null,
                "200601012010012001",
                "Sekretaris Dinas",
                "OPD-999",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Senior",
                "Golongan III",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                null,
                null
        );

        when(opdRepository.existsByKodeOpd(newJabatan.kodeOpd())).thenReturn(false);

        assertThatThrownBy(() -> jabatanService.tambahJabatan(newJabatan))
                .isInstanceOf(OpdNotFoundException.class)
                .hasMessageContaining(newJabatan.kodeOpd());
        verify(opdRepository).existsByKodeOpd(newJabatan.kodeOpd());
        verify(jabatanRepository, never()).save(any());
    }
    
    @Test
    void tambahJabatan_WhenPegawaiNotExists_ShouldThrowException() {
        Jabatan newJabatan = new Jabatan(
                null,
                "200601012010012001",
                "Sekretaris Dinas",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Senior",
                "Golongan III",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                null,
                null
        );

        when(opdRepository.existsByKodeOpd(newJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(newJabatan.nip())).thenReturn(false);

        assertThatThrownBy(() -> jabatanService.tambahJabatan(newJabatan))
                .isInstanceOf(PegawaiNotFoundException.class)
                .hasMessageContaining(newJabatan.nip());
        verify(opdRepository).existsByKodeOpd(newJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(newJabatan.nip());
        verify(jabatanRepository, never()).save(any());
    }

    @Test
    void tambahJabatan_WhenPegawaiAlreadyHasJabatan_ShouldThrowException() {
        String existingNip = "200601012010012001";
        Jabatan newJabatan = new Jabatan(
                null,
                existingNip,
                "Sekretaris Dinas",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Senior",
                "Golongan III",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                null,
                null
        );

        List<Jabatan> existingJabatans = List.of(testJabatan);

        when(opdRepository.existsByKodeOpd(newJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(newJabatan.nip())).thenReturn(true);
        when(jabatanRepository.findAllByNip(existingNip)).thenReturn(existingJabatans);

        assertThatThrownBy(() -> jabatanService.tambahJabatan(newJabatan))
                .isInstanceOf(JabatanPegawaiSudahAdaException.class)
                .hasMessageContaining(existingNip);
        verify(opdRepository).existsByKodeOpd(newJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(newJabatan.nip());
        verify(jabatanRepository).findAllByNip(existingNip);
        verify(jabatanRepository, never()).save(any());
    }
    
    @Test
    void ubahJabatan_WhenJabatanExistsAndValid_ShouldUpdateAndReturnJabatan() {
        Long id = 1L;
        Jabatan updatedJabatan = new Jabatan(
                id,
                "201001012010011001",
                "Sekretaris Kabupaten",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Middle",
                "Golongan II",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                testJabatan.createdDate(),
                Instant.now()
        );

        when(jabatanRepository.existsById(id)).thenReturn(true);
        when(opdRepository.existsByKodeOpd(updatedJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(updatedJabatan.nip())).thenReturn(true);
        when(jabatanRepository.save(any(Jabatan.class))).thenReturn(updatedJabatan);

        Jabatan result = jabatanService.ubahJabatan(id, updatedJabatan);

        assertThat(result).isEqualTo(updatedJabatan);
        verify(jabatanRepository).existsById(id);
        verify(opdRepository).existsByKodeOpd(updatedJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(updatedJabatan.nip());
        verify(jabatanRepository).save(updatedJabatan);
    }
    
    @Test
    void ubahJabatan_WhenJabatanIdNotExists_ShouldThrowException() {
        Long id = 999L;
        when(jabatanRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> jabatanService.ubahJabatan(id, testJabatan))
                .isInstanceOf(JabatanNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(jabatanRepository).existsById(id);
        verify(jabatanRepository, never()).save(any());
    }
    
    @Test
    void ubahJabatan_WhenOpdNotExists_ShouldThrowException() {
        Long id = 1L;
        Jabatan updatedJabatan = new Jabatan(
                id,
                "201001012010011001",
                "Sekretaris Kabupaten",
                "OPD-999",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Middle",
                "Golongan II",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                testJabatan.createdDate(),
                Instant.now()
        );

        when(jabatanRepository.existsById(id)).thenReturn(true);
        when(opdRepository.existsByKodeOpd(updatedJabatan.kodeOpd())).thenReturn(false);

        assertThatThrownBy(() -> jabatanService.ubahJabatan(id, updatedJabatan))
                .isInstanceOf(OpdNotFoundException.class)
                .hasMessageContaining(updatedJabatan.kodeOpd());
        verify(jabatanRepository).existsById(id);
        verify(opdRepository).existsByKodeOpd(updatedJabatan.kodeOpd());
        verify(jabatanRepository, never()).save(any());
    }
    
    @Test
    void ubahJabatan_WhenPegawaiNotExists_ShouldThrowException() {
        Long id = 1L;
        Jabatan updatedJabatan = new Jabatan(
                id,
                "201001012010011001",
                "Sekretaris Kabupaten",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Middle",
                "Golongan II",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                testJabatan.createdDate(),
                Instant.now()
        );

        when(jabatanRepository.existsById(id)).thenReturn(true);
        when(opdRepository.existsByKodeOpd(updatedJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(updatedJabatan.nip())).thenReturn(false);

        assertThatThrownBy(() -> jabatanService.ubahJabatan(id, updatedJabatan))
                .isInstanceOf(PegawaiNotFoundException.class)
                .hasMessageContaining(updatedJabatan.nip());
        verify(jabatanRepository).existsById(id);
        verify(opdRepository).existsByKodeOpd(updatedJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(updatedJabatan.nip());
        verify(jabatanRepository, never()).save(any());
    }
    
    @Test
    void hapusJabatan_WhenJabatanExists_ShouldDeleteJabatan() {
        Long id = 1L;
        when(jabatanRepository.existsById(id)).thenReturn(true);

        jabatanService.hapusJabatan(id);

        verify(jabatanRepository).existsById(id);
        verify(jabatanRepository).deleteById(id);
    }
    
    @Test
    void hapusJabatan_WhenIdJabatanNotExists_ShouldThrowException() {
        Long id = 999L;
        when(jabatanRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> jabatanService.hapusJabatan(id))
                .isInstanceOf(JabatanNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(jabatanRepository).existsById(id);
        verify(jabatanRepository, never()).deleteById(anyLong());
    }
}
