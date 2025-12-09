package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
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
import org.mockito.ArgumentCaptor;

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
            "John Doe",
            "Analis Ahli Muda",
            "OPD-001",
            "UTAMA",
            "JABATAN_STRUKTURAL",
            "ESELON_IV",
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
    void listJabatanByNip_WhenJabatanExists_ShouldReturnJabatanList() {
        when(jabatanRepository.findAllByNip("198001012010011001")).thenReturn(List.of(testJabatan));

        List<Jabatan> result = jabatanService.listJabatanByNip("198001012010011001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(testJabatan);
        verify(jabatanRepository).findAllByNip("198001012010011001");
    }

    @Test
    void listJabatanByNip_WhenNoJabatanExists_ShouldReturnEmptyList() {
        when(jabatanRepository.findAllByNip("999999999999999999")).thenReturn(List.of());

        List<Jabatan> result = jabatanService.listJabatanByNip("999999999999999999");

        assertThat(result).isEmpty();
        verify(jabatanRepository).findAllByNip("999999999999999999");
    }

    @Test
    void listAllJabatan_ShouldReturnAllJabatan() {
        when(jabatanRepository.findAll()).thenReturn(List.of(testJabatan));

        Iterable<Jabatan> result = jabatanService.listAllJabatan();

        assertThat(result).containsExactly(testJabatan);
        verify(jabatanRepository).findAll();
    }

    @Test
    void listAllJabatan_WhenNoData_ShouldReturnEmptyList() {
        when(jabatanRepository.findAll()).thenReturn(List.of());

        Iterable<Jabatan> result = jabatanService.listAllJabatan();

        assertThat(result).isEmpty();
        verify(jabatanRepository).findAll();
    }

    @Test
    void listJabatanByKodeOpdWithPegawai_WhenJabatanExists_ShouldReturnResponseList() {
        Pegawai pegawai = new Pegawai(null, "John Doe", "198001012010011001", null, null, "AKTIF", null, null, null);

        Jabatan jabatanTanpaNama = new Jabatan(
            testJabatan.id(),
            testJabatan.nip(),
            null,
            testJabatan.namaJabatan(),
            testJabatan.kodeOpd(),
            testJabatan.statusJabatan(),
            testJabatan.jenisJabatan(),
            testJabatan.eselon(),
            testJabatan.pangkat(),
            testJabatan.golongan(),
            testJabatan.tanggalMulai(),
            testJabatan.tanggalAkhir(),
            testJabatan.createdDate(),
            testJabatan.lastModifiedDate()
        );

        when(jabatanRepository.findByKodeOpd("OPD-001")).thenReturn(List.of(jabatanTanpaNama));
        when(pegawaiRepository.findByNip("198001012010011001")).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByKodeOpdWithPegawai("OPD-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nip()).isEqualTo("198001012010011001");
        assertThat(result.get(0).namaPegawai()).isEqualTo("John Doe");
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Ahli Muda");
        assertThat(result.get(0).statusJabatan()).isEqualTo("UTAMA");
        verify(jabatanRepository).findByKodeOpd("OPD-001");
        verify(pegawaiRepository).findByNip("198001012010011001");
    }

    @Test
    void listJabatanByKodeOpdWithPegawai_WhenPegawaiNotFound_ShouldReturnResponseWithNullNamaPegawai() {
        Jabatan jabatanTanpaNama = new Jabatan(
            2L,
            "198001012010011001",
            null,
            "Analis Ahli Muda",
            "OPD-001",
            "UTAMA",
            "JABATAN_STRUKTURAL",
            "ESELON_IV",
            "Junior",
            "Golongan I",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        when(jabatanRepository.findByKodeOpd("OPD-001")).thenReturn(List.of(jabatanTanpaNama));
        when(pegawaiRepository.findByNip("198001012010011001")).thenReturn(Optional.empty());

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByKodeOpdWithPegawai("OPD-001");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nip()).isEqualTo("198001012010011001");
        assertThat(result.get(0).namaPegawai()).isNull();
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Ahli Muda");
        verify(jabatanRepository).findByKodeOpd("OPD-001");
        verify(pegawaiRepository).findByNip("198001012010011001");
    }

    @Test
    void listJabatanByKodeOpdWithPegawai_WhenNoJabatanExists_ShouldReturnEmptyList() {
        when(jabatanRepository.findByKodeOpd("OPD-999")).thenReturn(List.of());

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByKodeOpdWithPegawai("OPD-999");

        assertThat(result).isEmpty();
        verify(jabatanRepository).findByKodeOpd("OPD-999");
        verify(pegawaiRepository, never()).findByNip(any());
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
            "Jane Doe",
            "Sekretaris Dinas",
            "OPD-001",
            "UTAMA",
            "JABATAN_PEMIMPIN_TINGGI",
            "ESELON_IV",
            "Senior",
            "Golongan III",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            null,
            null
        );

        when(opdRepository.existsByKodeOpd(newJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.findByNip(newJabatan.nip())).thenReturn(Optional.of(new Pegawai(
            null,
            newJabatan.namaPegawai(),
            newJabatan.nip(),
            "OPD-001",
            null,
            "AKTIF",
            null,
            null,
            null
        )));
        when(jabatanRepository.save(any(Jabatan.class))).thenReturn(
            new Jabatan(
                2L,
                newJabatan.nip(),
                newJabatan.namaPegawai(),
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
        assertThat(result.statusJabatan()).isEqualTo("UTAMA");
        assertThat(result.jenisJabatan()).isEqualTo("JABATAN_PEMIMPIN_TINGGI");
        assertThat(result.eselon()).isEqualTo("ESELON_IV");
        assertThat(result.pangkat()).isEqualTo("Senior");
        assertThat(result.golongan()).isEqualTo("Golongan III");
        verify(opdRepository).existsByKodeOpd(newJabatan.kodeOpd());
        verify(pegawaiRepository).findByNip(newJabatan.nip());
        verify(jabatanRepository).save(any(Jabatan.class));
    }

    @Test
    void tambahJabatan_WhenOpdNotExists_ShouldThrowException() {
        Jabatan newJabatan = new Jabatan(
            null,
            "200601012010012001",
            "Jane Doe",
            "Sekretaris Dinas",
            "OPD-999",
            "UTAMA",
            "JABATAN_PEMIMPIN_TINGGI",
            "ESELON_IV",
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
            "Jane Doe",
            "Sekretaris Dinas",
            "OPD-001",
            "UTAMA",
            "JABATAN_PEMIMPIN_TINGGI",
            "ESELON_IV",
            "Senior",
            "Golongan III",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            null,
            null
        );

        when(opdRepository.existsByKodeOpd(newJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.findByNip(newJabatan.nip())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jabatanService.tambahJabatan(newJabatan))
            .isInstanceOf(PegawaiNotFoundException.class)
            .hasMessageContaining(newJabatan.nip());
        verify(opdRepository).existsByKodeOpd(newJabatan.kodeOpd());
        verify(pegawaiRepository).findByNip(newJabatan.nip());
        verify(jabatanRepository, never()).save(any());
    }

    @Test
    void ubahJabatan_WhenJabatanExistsAndValid_ShouldUpdateAndReturnJabatan() {
        Long id = 1L;
        Jabatan updatedJabatan = new Jabatan(
            id,
            "201001012010011001",
            "Jane Doe",
            "Sekretaris Kabupaten",
            "OPD-001",
            "UTAMA",
            "JABATAN_PEMIMPIN_TINGGI",
            "ESELON_IV",
            "Middle",
            "Golongan II",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            testJabatan.createdDate(),
            Instant.now()
        );

        when(jabatanRepository.existsById(id)).thenReturn(true);
        when(opdRepository.existsByKodeOpd(updatedJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.findByNip(updatedJabatan.nip())).thenReturn(Optional.of(
            new Pegawai(null, updatedJabatan.namaPegawai(), updatedJabatan.nip(), "OPD-001", null, "AKTIF", null, null, null)
        ));
        when(jabatanRepository.save(any(Jabatan.class))).thenReturn(updatedJabatan);

        Jabatan result = jabatanService.ubahJabatan(id, updatedJabatan);

        ArgumentCaptor<Jabatan> jabatanCaptor = ArgumentCaptor.forClass(Jabatan.class);

        assertThat(result).isEqualTo(updatedJabatan);
        verify(jabatanRepository).existsById(id);
        verify(opdRepository).existsByKodeOpd(updatedJabatan.kodeOpd());
        verify(pegawaiRepository).findByNip(updatedJabatan.nip());
        verify(jabatanRepository).save(jabatanCaptor.capture());

        Jabatan saved = jabatanCaptor.getValue();
        assertThat(saved.id()).isEqualTo(id);
        assertThat(saved.nip()).isEqualTo(updatedJabatan.nip());
        assertThat(saved.namaPegawai()).isEqualTo("Jane Doe");
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
            "Jane Doe",
            "Sekretaris Kabupaten",
            "OPD-999",
            "UTAMA",
            "JABATAN_PEMIMPIN_TINGGI",
            "ESELON_IV",
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
            "Jane Doe",
            "Sekretaris Kabupaten",
            "OPD-001",
            "UTAMA",
            "JABATAN_PEMIMPIN_TINGGI",
            "ESELON_IV",
            "Middle",
            "Golongan II",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            testJabatan.createdDate(),
            Instant.now()
        );

        when(jabatanRepository.existsById(id)).thenReturn(true);
        when(opdRepository.existsByKodeOpd(updatedJabatan.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.findByNip(updatedJabatan.nip())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jabatanService.ubahJabatan(id, updatedJabatan))
            .isInstanceOf(PegawaiNotFoundException.class)
            .hasMessageContaining(updatedJabatan.nip());
        verify(jabatanRepository).existsById(id);
        verify(opdRepository).existsByKodeOpd(updatedJabatan.kodeOpd());
        verify(pegawaiRepository).findByNip(updatedJabatan.nip());
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
        Pegawai pegawai = new Pegawai(null, "John Doe", nip, null, null, "AKTIF", null, null, null);

        Jabatan jabatanTanpaNama = new Jabatan(
            testJabatan.id(),
            testJabatan.nip(),
            null,
            testJabatan.namaJabatan(),
            testJabatan.kodeOpd(),
            testJabatan.statusJabatan(),
            testJabatan.jenisJabatan(),
            testJabatan.eselon(),
            testJabatan.pangkat(),
            testJabatan.golongan(),
            testJabatan.tanggalMulai(),
            testJabatan.tanggalAkhir(),
            testJabatan.createdDate(),
            testJabatan.lastModifiedDate()
        );

        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(jabatanTanpaNama));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nip()).isEqualTo(nip);
        assertThat(result.get(0).namaPegawai()).isEqualTo("John Doe");
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Ahli Muda");
        assertThat(result.get(0).statusJabatan()).isEqualTo("UTAMA");
        verify(jabatanRepository).findAllByNip(nip);
        verify(pegawaiRepository).findByNip(nip);
    }

    @Test
    void listJabatanByNipWithPegawai_WhenMultipleJabatanExists_ShouldReturnResponsesInOrder() {
        String nip = "123456789012345678";
        Pegawai pegawai = new Pegawai(null, "Dino", nip, null, null, "AKTIF", null, null, null);

        Jabatan jabatan1 = new Jabatan(
            1L,
            nip,
            null,
            "Analis Kebijakan Industrialisasi",
            "OPD-001",
            "UTAMA",
            "JABATAN_STRUKTURAL",
            "ESELON_III",
            "Senior",
            "Golongan III",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        Jabatan jabatan2 = new Jabatan(
            2L,
            nip,
            null,
            "Pelaksana Tugas",
            "OPD-001",
            "PLT_UTAMA",
            "JABATAN_STRUKTURAL",
            "ESELON_II",
            "Sepuh",
            "Golongan IV",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(jabatan1, jabatan2));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).statusJabatan()).isEqualTo("UTAMA");
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Kebijakan Industrialisasi");

        assertThat(result.get(1).statusJabatan()).isEqualTo("PLT_UTAMA");
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

        Jabatan jabatanTanpaNama = new Jabatan(
            10L,
            nip,
            null,
            "Analis Ahli Muda",
            "OPD-001",
            "UTAMA",
            "JABATAN_STRUKTURAL",
            "ESELON_IV",
            "Junior",
            "Golongan I",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(jabatanTanpaNama));
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
    void listJabatanByNipWithPegawai_WhenMultipleJabatanWithDifferentStatus_ShouldReturnResponses() {
        String nip = "123456789012345678";
        Pegawai pegawai = new Pegawai(null, "Test User", nip, null, null, "AKTIF", null, null, null);

        Jabatan jabatan1 = new Jabatan(
            1L,
            nip,
            null,
            "Analis Kebijakan",
            "OPD-001",
            "UTAMA",
            "JABATAN_STRUKTURAL",
            "ESELON_III",
            "Senior",
            "Golongan III",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        Jabatan jabatan2 = new Jabatan(
            2L,
            nip,
            null,
            "Pelaksana Tugas",
            "OPD-001",
            "PLT_UTAMA",
            "JABATAN_STRUKTURAL",
            "ESELON_II",
            "Sepuh",
            "Golongan IV",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        Jabatan jabatan3 = new Jabatan(
            3L,
            nip,
            null,
            "Jabatan Berakhir",
            "OPD-001",
            "BERAKHIR",
            "JABATAN_STRUKTURAL",
            "ESELON_IV",
            "Junior",
            "Golongan I",
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(jabatan1, jabatan2, jabatan3));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).statusJabatan()).isEqualTo("UTAMA");
        assertThat(result.get(1).statusJabatan()).isEqualTo("PLT_UTAMA");
        assertThat(result.get(2).statusJabatan()).isEqualTo("BERAKHIR");

        verify(jabatanRepository).findAllByNip(nip);
        verify(pegawaiRepository, times(3)).findByNip(nip);
    }
}
