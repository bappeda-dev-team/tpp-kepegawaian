package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanPegawaiSudahAdaException;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.web.PegawaiWithJabatanListResponse;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.StatusPegawai;
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

        Jabatan existingJabatan = new Jabatan(
                1L,
                existingNip,
                "Existing Jabatan",
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

        List<Jabatan> existingJabatans = List.of(existingJabatan);

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
        when(jabatanRepository.findById(id)).thenReturn(Optional.of(testJabatan));
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

    @Test
    void listJabatanByNipWithPegawai_WhenSingleJabatanExists_ShouldReturnSingleResponse() {
        String nip = "198001012010011001";
        Pegawai pegawai = new Pegawai(null, "John Doe", nip, null, null, StatusPegawai.AKTIF, null, null, null);
        
        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(testJabatan));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nip()).isEqualTo(nip);
        assertThat(result.get(0).namaPegawai()).isEqualTo("John Doe");
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Ahli Muda");
        assertThat(result.get(0).statusJabatan()).isEqualTo(StatusJabatan.UTAMA);
        verify(jabatanRepository).findAllByNip(nip);
        verify(pegawaiRepository).findByNip(nip);
    }

    @Test
    void listJabatanByNipWithPegawai_WhenMultipleStatusJabatanExists_ShouldReturnSortedResponses() {
        String nip = "123456789012345678";
        Pegawai pegawai = new Pegawai(null, "Dino", nip, null, null, StatusPegawai.AKTIF, null, null, null);
        
        Jabatan pltJabatan = new Jabatan(
                2L,
                nip,
                "Pelaksana Tugas",
                "OPD-001",
                StatusJabatan.PLT_UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.ESELON_II,
                "Sepuh",
                "Golongan IV",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );

        Jabatan utamaJabatan = new Jabatan(
                1L,
                nip,
                "Analis Kebijakan Industrialisasi",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.ESELON_III,
                "Senior",
                "Golongan III",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );

        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(pltJabatan, utamaJabatan));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).statusJabatan()).isEqualTo(StatusJabatan.UTAMA);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Kebijakan Industrialisasi");
        
        assertThat(result.get(1).statusJabatan()).isEqualTo(StatusJabatan.PLT_UTAMA);
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).namaJabatan()).isEqualTo("Pelaksana Tugas");
        
        assertThat(result.get(0).nip()).isEqualTo(nip);
        assertThat(result.get(0).namaPegawai()).isEqualTo("Dino");
        assertThat(result.get(1).nip()).isEqualTo(nip);
        assertThat(result.get(1).namaPegawai()).isEqualTo("Dino");
        
        verify(jabatanRepository).findAllByNip(nip);
        verify(pegawaiRepository, times(2)).findByNip(nip);
    }

    @Test
    void listJabatanByNipWithPegawai_WhenPegawaiNotFound_ShouldReturnResponseWithNullNamaPegawai() {
        String nip = "198001012010011001";
        
        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(testJabatan));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.empty());

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nip()).isEqualTo(nip);
        assertThat(result.get(0).namaPegawai()).isNull();
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Ahli Muda");
        verify(jabatanRepository).findAllByNip(nip);
        verify(pegawaiRepository).findByNip(nip);
    }

    @Test
    void listJabatanByNipWithPegawai_WhenNoJabatanExists_ShouldReturnEmptyList() {
        String nip = "999999999999999999";
        
        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of());

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).isEmpty();
        verify(jabatanRepository).findAllByNip(nip);
        verify(pegawaiRepository, never()).findByNip(any());
    }

    @Test
    void listJabatanByNipWithPegawai_WhenMultipleStatusIncludingBerakhir_ShouldReturnSortedResponses() {
        String nip = "123456789012345678";
        Pegawai pegawai = new Pegawai(null, "Test User", nip, null, null, StatusPegawai.AKTIF, null, null, null);

        Jabatan berakhirJabatan = new Jabatan(
                3L,
                nip,
                "Jabatan Berakhir",
                "OPD-001",
                StatusJabatan.BERAKHIR,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.ESELON_IV,
                "Junior",
                "Golongan I",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );

        Jabatan pltJabatan = new Jabatan(
                2L,
                nip,
                "Pelaksana Tugas",
                "OPD-001",
                StatusJabatan.PLT_UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.ESELON_II,
                "Sepuh",
                "Golongan IV",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );

        Jabatan utamaJabatan = new Jabatan(
                1L,
                nip,
                "Analis Kebijakan",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.ESELON_III,
                "Senior",
                "Golongan III",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );

        // Mock repository to return unsorted list
        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(berakhirJabatan, pltJabatan, utamaJabatan));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).statusJabatan()).isEqualTo(StatusJabatan.UTAMA);
        assertThat(result.get(1).statusJabatan()).isEqualTo(StatusJabatan.PLT_UTAMA);
        assertThat(result.get(2).statusJabatan()).isEqualTo(StatusJabatan.BERAKHIR);

        verify(jabatanRepository).findAllByNip(nip);
        verify(pegawaiRepository, times(3)).findByNip(nip);
    }

    @Test
    void listJabatanByKodeOpdWithPegawai_WhenJabatansExist_ShouldReturnResponsesWithPegawaiInfo() {
        String kodeOpd = "OPD-001";
        String nip1 = "198001012010011001";
        String nip2 = "199001012010012001";

        Pegawai pegawai1 = new Pegawai(null, "John Doe", nip1, null, null, StatusPegawai.AKTIF, null, null, null);
        Pegawai pegawai2 = new Pegawai(null, "Jane Smith", nip2, null, null, StatusPegawai.AKTIF, null, null, null);

        Jabatan jabatan1 = new Jabatan(
                1L, nip1, "Analis Senior", kodeOpd, StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_III, "Senior", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan jabatan2 = new Jabatan(
                2L, nip2, "Sekretaris", kodeOpd, StatusJabatan.PLT_UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL, Eselon.ESELON_IV, "Middle", "Golongan II",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        when(jabatanRepository.findByKodeOpd(kodeOpd)).thenReturn(List.of(jabatan1, jabatan2));
        when(pegawaiRepository.findByNip(nip1)).thenReturn(Optional.of(pegawai1));
        when(pegawaiRepository.findByNip(nip2)).thenReturn(Optional.of(pegawai2));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByKodeOpdWithPegawai(kodeOpd);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).nip()).isEqualTo(nip1);
        assertThat(result.get(0).namaPegawai()).isEqualTo("John Doe");
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Senior");
        assertThat(result.get(0).statusJabatan()).isEqualTo(StatusJabatan.UTAMA);

        assertThat(result.get(1).nip()).isEqualTo(nip2);
        assertThat(result.get(1).namaPegawai()).isEqualTo("Jane Smith");
        assertThat(result.get(1).namaJabatan()).isEqualTo("Sekretaris");
        assertThat(result.get(1).statusJabatan()).isEqualTo(StatusJabatan.PLT_UTAMA);

        verify(jabatanRepository).findByKodeOpd(kodeOpd);
        verify(pegawaiRepository).findByNip(nip1);
        verify(pegawaiRepository).findByNip(nip2);
    }

    @Test
    void listJabatanByKodeOpdWithPegawai_WhenPegawaiNotFound_ShouldReturnResponseWithNullNama() {
        String kodeOpd = "OPD-001";
        String nip = "198001012010011001";

        Jabatan jabatan = new Jabatan(
                1L, nip, "Analis Senior", kodeOpd, StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_III, "Senior", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        when(jabatanRepository.findByKodeOpd(kodeOpd)).thenReturn(List.of(jabatan));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.empty());

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByKodeOpdWithPegawai(kodeOpd);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nip()).isEqualTo(nip);
        assertThat(result.get(0).namaPegawai()).isNull();
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Senior");

        verify(jabatanRepository).findByKodeOpd(kodeOpd);
        verify(pegawaiRepository).findByNip(nip);
    }

    @Test
    void listPegawaiWithJabatanByKodeOpd_WhenMultiplePegawaiWithMultipleJabatans_ShouldReturnSortedByPriority() {
        String kodeOpd = "OPD-001";
        String nip1 = "198001012010011001";
        String nip2 = "199001012010012001";
        String nip3 = "200001012010013001";

        Pegawai pegawai1 = new Pegawai(null, "John Doe", nip1, null, null, StatusPegawai.AKTIF, null, null, null);
        Pegawai pegawai2 = new Pegawai(null, "Jane Smith", nip2, null, null, StatusPegawai.AKTIF, null, null, null);
        Pegawai pegawai3 = new Pegawai(null, "Bob Wilson", nip3, null, null, StatusPegawai.AKTIF, null, null, null);

        Jabatan jabatan3Utama = new Jabatan(
                5L, nip3, "Kepala Dinas", kodeOpd, StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI, Eselon.ESELON_II, "Senior", "Golongan IV",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan jabatan1Plt = new Jabatan(
                1L, nip1, "Plt Kepala Seksi", kodeOpd, StatusJabatan.PLT_UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL, Eselon.ESELON_III, "Middle", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan jabatan2Regular = new Jabatan(
                3L, nip2, "Staf Analis", kodeOpd, StatusJabatan.BERAKHIR,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_IV, "Junior", "Golongan II",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan jabatan1Additional = new Jabatan(
                2L, nip1, "Analis Muda", kodeOpd, StatusJabatan.BERAKHIR,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_IV, "Junior", "Golongan II",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan jabatan2Additional = new Jabatan(
                4L, nip2, "Jr. Analyst", kodeOpd, StatusJabatan.BERAKHIR,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_IV, "Junior", "Golongan I",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        when(jabatanRepository.findByKodeOpd(kodeOpd)).thenReturn(List.of(jabatan1Additional, jabatan2Regular, jabatan3Utama, jabatan1Plt, jabatan2Additional));
        when(pegawaiRepository.findByNip(nip1)).thenReturn(Optional.of(pegawai1));
        when(pegawaiRepository.findByNip(nip2)).thenReturn(Optional.of(pegawai2));
        when(pegawaiRepository.findByNip(nip3)).thenReturn(Optional.of(pegawai3));

        List<PegawaiWithJabatanListResponse> result = jabatanService.listPegawaiWithJabatanByKodeOpd(kodeOpd);

        assertThat(result).hasSize(3);

        assertThat(result.get(0).nip()).isEqualTo(nip3);
        assertThat(result.get(0).namaPegawai()).isEqualTo("Bob Wilson");
        assertThat(result.get(0).jabatan()).hasSize(1);
        assertThat(result.get(0).jabatan().get(0).statusJabatan()).isEqualTo(StatusJabatan.UTAMA);

        assertThat(result.get(1).nip()).isEqualTo(nip1);
        assertThat(result.get(1).namaPegawai()).isEqualTo("John Doe");
        assertThat(result.get(1).jabatan()).hasSize(2);
        assertThat(result.get(1).jabatan()).anyMatch(j -> j.statusJabatan() == StatusJabatan.PLT_UTAMA);

        assertThat(result.get(2).nip()).isEqualTo(nip2);
        assertThat(result.get(2).namaPegawai()).isEqualTo("Jane Smith");
        assertThat(result.get(2).jabatan()).hasSize(2);

        verify(jabatanRepository).findByKodeOpd(kodeOpd);
        verify(pegawaiRepository).findByNip(nip1);
        verify(pegawaiRepository).findByNip(nip2);
        verify(pegawaiRepository).findByNip(nip3);
    }

    @Test
    void tambahJabatan_WhenUtamaWithExistingPltJabatan_ShouldAllowBoth() {
        String nip = "198001012010011001";
        Jabatan existingPltJabatan = new Jabatan(
                1L, nip, "Plt Jabatan", "OPD-001", StatusJabatan.PLT_UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL, Eselon.ESELON_III, "Middle", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan newUtamaJabatan = new Jabatan(
                null, nip, "Jabatan Utama", "OPD-001", StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_III, "Senior", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), null, null
        );

        when(opdRepository.existsByKodeOpd(newUtamaJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(existingPltJabatan));
        when(jabatanRepository.save(any(Jabatan.class))).thenReturn(
                new Jabatan(2L, newUtamaJabatan.nip(), newUtamaJabatan.namaJabatan(), newUtamaJabatan.kodeOpd(),
                        newUtamaJabatan.statusJabatan(), newUtamaJabatan.jenisJabatan(), newUtamaJabatan.eselon(),
                        newUtamaJabatan.pangkat(), newUtamaJabatan.golongan(), newUtamaJabatan.tanggalMulai(),
                        newUtamaJabatan.tanggalAkhir(), Instant.now(), Instant.now())
        );

        Jabatan result = jabatanService.tambahJabatan(newUtamaJabatan);

        assertThat(result.nip()).isEqualTo(nip);
        assertThat(result.statusJabatan()).isEqualTo(StatusJabatan.UTAMA);
        assertThat(result.namaJabatan()).isEqualTo("Jabatan Utama");

        verify(opdRepository).existsByKodeOpd(newUtamaJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(nip);
        verify(jabatanRepository).findAllByNip(nip);
        verify(jabatanRepository).save(newUtamaJabatan);
    }

    @Test
    void tambahJabatan_WhenPltWithoutExistingJabatan_ShouldSave() {
        String nip = "198001012010011001";
        Jabatan newPltJabatan = new Jabatan(
                null, nip, "Plt Jabatan Baru", "OPD-001", StatusJabatan.PLT_UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL, Eselon.ESELON_III, "Middle", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), null, null
        );

        when(opdRepository.existsByKodeOpd(newPltJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of());
        when(jabatanRepository.save(any(Jabatan.class))).thenReturn(
                new Jabatan(2L, newPltJabatan.nip(), newPltJabatan.namaJabatan(), newPltJabatan.kodeOpd(),
                        newPltJabatan.statusJabatan(), newPltJabatan.jenisJabatan(), newPltJabatan.eselon(),
                        newPltJabatan.pangkat(), newPltJabatan.golongan(), newPltJabatan.tanggalMulai(),
                        newPltJabatan.tanggalAkhir(), Instant.now(), Instant.now())
        );

        Jabatan result = jabatanService.tambahJabatan(newPltJabatan);

        assertThat(result.nip()).isEqualTo(nip);
        assertThat(result.statusJabatan()).isEqualTo(StatusJabatan.PLT_UTAMA);
        assertThat(result.namaJabatan()).isEqualTo("Plt Jabatan Baru");

        verify(opdRepository).existsByKodeOpd(newPltJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(nip);
        verify(jabatanRepository).findAllByNip(nip);
        verify(jabatanRepository).save(newPltJabatan);
    }

    @Test
    void tambahJabatan_WhenPltWithExistingJabatan_ShouldThrowException() {
        String nip = "198001012010011001";
        Jabatan existingJabatan = new Jabatan(
                1L, nip, "Existing Jabatan", "OPD-001", StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_III, "Senior", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan newPltJabatan = new Jabatan(
                null, nip, "Plt Jabatan Baru", "OPD-001", StatusJabatan.PLT_UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL, Eselon.ESELON_III, "Middle", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), null, null
        );

        when(opdRepository.existsByKodeOpd(newPltJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(existingJabatan));

        assertThatThrownBy(() -> jabatanService.tambahJabatan(newPltJabatan))
                .isInstanceOf(JabatanPegawaiSudahAdaException.class)
                .hasMessageContaining(nip);

        verify(opdRepository).existsByKodeOpd(newPltJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(nip);
        verify(jabatanRepository).findAllByNip(nip);
        verify(jabatanRepository, never()).save(any());
    }

    @Test
    void ubahJabatan_WhenUtamaWithExistingPltJabatan_ShouldAllowBoth() {
        Long id = 1L;
        String nip = "198001012010011001";

        Jabatan currentJabatan = new Jabatan(
                id, nip, "Current Jabatan", "OPD-001", StatusJabatan.BERAKHIR,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_IV, "Junior", "Golongan II",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan existingPltJabatan = new Jabatan(
                2L, nip, "Plt Jabatan", "OPD-001", StatusJabatan.PLT_UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL, Eselon.ESELON_III, "Middle", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan updatedUtamaJabatan = new Jabatan(
                id, nip, "Updated Utama Jabatan", "OPD-001", StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_III, "Senior", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), currentJabatan.createdDate(), Instant.now()
        );

        when(jabatanRepository.existsById(id)).thenReturn(true);
        when(jabatanRepository.findById(id)).thenReturn(Optional.of(currentJabatan));
        when(opdRepository.existsByKodeOpd(updatedUtamaJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(existingPltJabatan));
        when(jabatanRepository.save(any(Jabatan.class))).thenReturn(updatedUtamaJabatan);

        Jabatan result = jabatanService.ubahJabatan(id, updatedUtamaJabatan);

        assertThat(result.nip()).isEqualTo(nip);
        assertThat(result.statusJabatan()).isEqualTo(StatusJabatan.UTAMA);
        assertThat(result.namaJabatan()).isEqualTo("Updated Utama Jabatan");

        verify(jabatanRepository).existsById(id);
        verify(jabatanRepository).findById(id);
        verify(opdRepository).existsByKodeOpd(updatedUtamaJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(nip);
        verify(jabatanRepository).save(updatedUtamaJabatan);
    }

    @Test
    void ubahJabatan_WhenPltWithExistingPltJabatan_ShouldThrowException() {
        Long id = 1L;
        String nip = "198001012010011001";

        Jabatan currentJabatan = new Jabatan(
                id, nip, "Current Jabatan", "OPD-001", StatusJabatan.BERAKHIR,
                JenisJabatan.JABATAN_FUNGSIONAL, Eselon.ESELON_IV, "Junior", "Golongan II",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan existingPltJabatan = new Jabatan(
                2L, nip, "Existing Plt Jabatan", "OPD-001", StatusJabatan.PLT_SEMENTARA,
                JenisJabatan.JABATAN_STRUKTURAL, Eselon.ESELON_III, "Middle", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), Instant.now(), Instant.now()
        );

        Jabatan updatedPltJabatan = new Jabatan(
                id, nip, "Updated Plt Jabatan", "OPD-001", StatusJabatan.PLT_UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL, Eselon.ESELON_III, "Middle", "Golongan III",
                tanggalMulai.getTime(), tanggalAkhir.getTime(), currentJabatan.createdDate(), Instant.now()
        );

        when(jabatanRepository.existsById(id)).thenReturn(true);
        when(jabatanRepository.findById(id)).thenReturn(Optional.of(currentJabatan));
        when(opdRepository.existsByKodeOpd(updatedPltJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(existingPltJabatan));

        assertThatThrownBy(() -> jabatanService.ubahJabatan(id, updatedPltJabatan))
                .isInstanceOf(JabatanPegawaiSudahAdaException.class)
                .hasMessageContaining(nip);

        verify(jabatanRepository).existsById(id);
        verify(jabatanRepository).findById(id);
        verify(opdRepository).existsByKodeOpd(updatedPltJabatan.kodeOpd());
        verify(pegawaiRepository).existsByNip(nip);
        verify(jabatanRepository, never()).save(any());
    }
}
