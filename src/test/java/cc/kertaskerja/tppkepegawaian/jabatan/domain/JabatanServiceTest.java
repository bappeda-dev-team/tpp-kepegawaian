package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithTppPajakResponse;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppService;

@ExtendWith(MockitoExtension.class)
public class JabatanServiceTest {
    @Mock
    private JabatanRepository jabatanRepository;

    @Mock
    private PegawaiRepository pegawaiRepository;

//    @Mock
//    private OpdRepository opdRepository;

    @Mock
    private TppService tppService;

    @InjectMocks
    private JabatanService jabatanService;

    private static final float DEFAULT_BASIC_TPP = 1_000_000f;

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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());
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
        assertThat(result.getFirst()).isEqualTo(testJabatan);
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

//    @Test
//    void listAllJabatanWithTpp_WhenTppAvailable_ShouldReturnTppValues() {
//        when(jabatanRepository.findAll()).thenReturn(List.of(testJabatan));
//        Tpp tpp = new Tpp(
//                1L,
//                "BASIC_TPP",
//                testJabatan.kodeOpd(),
//                testJabatan.nip(),
//                "--",
//                200_000f,
//                0.05f,
//                0.01f,
//                1,
//                2025,
//                Instant.now(),
//                Instant.now());
//        when(tppService.detailTpp("BASIC_TPP", testJabatan.nip(), 1, 2025)).thenReturn(tpp);
//
//        List<JabatanWithTppPajakResponse> result = jabatanService.listAllJabatanWithTpp();
//
//        assertThat(result).hasSize(1);
//        JabatanWithTppPajakResponse response = result.get(0);
//        assertThat(response.basicTpp()).isEqualTo(200_000f);
//        assertThat(response.pajak()).isEqualTo(0.05f);
//        assertThat(response.nip()).isEqualTo(testJabatan.nip());
//        verify(jabatanRepository).findAll();
//        verify(tppService).detailTpp("BASIC_TPP", testJabatan.nip(), 1, 2025);
//    }

//    @Test
//    void listAllJabatanWithTpp_WhenTppMissing_ShouldFallbackToJabatanValues() {
//        when(jabatanRepository.findAll()).thenReturn(List.of(testJabatan));
//        when(tppService.detailTpp("BASIC_TPP", testJabatan.nip(), 1, 2025))
//                .thenThrow(new TppJenisTppNipBulanTahunNotFoundException("BASIC_TPP", testJabatan.nip(), 1, 2025));
//
//        List<JabatanWithTppPajakResponse> result = jabatanService.listAllJabatanWithTpp();
//
//        assertThat(result).hasSize(1);
//        JabatanWithTppPajakResponse response = result.get(0);
//        assertThat(response.basicTpp()).isEqualTo(testJabatan.basicTpp());
//        assertThat(response.pajak()).isNull();
//        verify(jabatanRepository).findAll();
//        verify(tppService).detailTpp("BASIC_TPP", testJabatan.nip(), 1, 2025);
//    }

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
                DEFAULT_BASIC_TPP,
                testJabatan.tanggalMulai(),
                testJabatan.tanggalAkhir(),
                testJabatan.createdDate(),
                testJabatan.lastModifiedDate());

        when(jabatanRepository.findByKodeOpd("OPD-001")).thenReturn(List.of(jabatanTanpaNama));
        when(pegawaiRepository.findByNip("198001012010011001")).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByKodeOpdWithPegawai("OPD-001");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().nip()).isEqualTo("198001012010011001");
        assertThat(result.getFirst().namaPegawai()).isEqualTo("John Doe");
        assertThat(result.getFirst().namaJabatan()).isEqualTo("Analis Ahli Muda");
        assertThat(result.getFirst().statusJabatan()).isEqualTo("UTAMA");
        assertThat(result.getFirst().basicTpp()).isEqualTo(DEFAULT_BASIC_TPP);
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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());

        when(jabatanRepository.findByKodeOpd("OPD-001")).thenReturn(List.of(jabatanTanpaNama));
        when(pegawaiRepository.findByNip("198001012010011001")).thenReturn(Optional.empty());

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByKodeOpdWithPegawai("OPD-001");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().nip()).isEqualTo("198001012010011001");
        assertThat(result.getFirst().namaPegawai()).isNull();
        assertThat(result.getFirst().namaJabatan()).isEqualTo("Analis Ahli Muda");
        assertThat(result.getFirst().basicTpp()).isEqualTo(DEFAULT_BASIC_TPP);
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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                null,
                null);

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
                        DEFAULT_BASIC_TPP,
                        newJabatan.tanggalMulai(),
                        newJabatan.tanggalAkhir(),
                        Instant.now(),
                        Instant.now()));

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
        verify(jabatanRepository).save(any(Jabatan.class));
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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                testJabatan.createdDate(),
                Instant.now());

        when(jabatanRepository.existsById(id)).thenReturn(true);
        when(jabatanRepository.save(any(Jabatan.class))).thenReturn(updatedJabatan);

        Jabatan result = jabatanService.ubahJabatan(id, updatedJabatan);

        ArgumentCaptor<Jabatan> jabatanCaptor = ArgumentCaptor.forClass(Jabatan.class);

        assertThat(result).isEqualTo(updatedJabatan);
        verify(jabatanRepository).existsById(id);
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
                DEFAULT_BASIC_TPP,
                testJabatan.tanggalMulai(),
                testJabatan.tanggalAkhir(),
                testJabatan.createdDate(),
                testJabatan.lastModifiedDate());

        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(jabatanTanpaNama));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().nip()).isEqualTo(nip);
        assertThat(result.getFirst().namaPegawai()).isEqualTo("John Doe");
        assertThat(result.getFirst().namaJabatan()).isEqualTo("Analis Ahli Muda");
        assertThat(result.getFirst().statusJabatan()).isEqualTo("UTAMA");
        assertThat(result.getFirst().basicTpp()).isEqualTo(DEFAULT_BASIC_TPP);
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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());

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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());

        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(jabatan1, jabatan2));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().statusJabatan()).isEqualTo("UTAMA");
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).namaJabatan()).isEqualTo("Analis Kebijakan Industrialisasi");

        assertThat(result.get(1).statusJabatan()).isEqualTo("PLT_UTAMA");
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).namaJabatan()).isEqualTo("Pelaksana Tugas");

        assertThat(result.get(0).nip()).isEqualTo(nip);
        assertThat(result.get(0).namaPegawai()).isEqualTo("Dino");
        assertThat(result.get(1).nip()).isEqualTo(nip);
        assertThat(result.get(1).namaPegawai()).isEqualTo("Dino");
        assertThat(result)
                .extracting(JabatanWithPegawaiResponse::basicTpp)
                .containsExactly(DEFAULT_BASIC_TPP, DEFAULT_BASIC_TPP);

        verify(jabatanRepository).findAllByNip(nip);
        verify(pegawaiRepository, times(2)).findByNip(nip);
    }

    @Test
    void listJabatanByNipWithPegawaiBatch_WhenJabatansExist_ShouldReturnResponses() {
        String nip1 = "198001012010011001";
        String nip2 = "199001012015021002";

        Jabatan jabatan1 = new Jabatan(
                1L,
                nip1,
                "John Doe",
                "Analis Ahli Muda",
                "OPD-001",
                "UTAMA",
                "JABATAN_STRUKTURAL",
                "ESELON_IV",
                "Junior",
                "Golongan I",
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());

        Jabatan jabatan2 = new Jabatan(
                2L,
                nip2,
                "Jane Smith",
                "Sekretaris Dinas",
                "OPD-002",
                "PLT_UTAMA",
                "JABATAN_ADMINISTRASI",
                "ESELON_III",
                "Middle",
                "Golongan II",
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());

        when(jabatanRepository.findAllByNipIn(List.of(nip1, nip2))).thenReturn(List.of(jabatan1, jabatan2));

        when(tppService.detailTppBatch("BASIC_TPP", List.of(nip1, nip2), 1, 2025, "--"))
                .thenReturn(List.of(
                        new Tpp(null, "BASIC_TPP", "--", nip1, "PEMDA-X", 100_000f, 0.05f, 0.01f, 1, 2025,
                                null, null),
                        new Tpp(null, "BASIC_TPP", "--", nip2, "PEMDA-X", 500_000f, 0.05f, 0.01f, 1, 2025,
                                null, null)));

        List<JabatanWithTppPajakResponse> result = jabatanService.listJabatanByNipWithPegawaiBatch(List.of(nip1, nip2));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(JabatanWithTppPajakResponse::nip).containsExactlyInAnyOrder(nip1, nip2);
        assertThat(result).extracting(JabatanWithTppPajakResponse::namaPegawai).containsExactlyInAnyOrder("John Doe",
                "Jane Smith");
        assertThat(result)
                .anySatisfy(r -> assertThat(r.basicTpp()).isEqualTo(100_000f))
                .anySatisfy(r -> assertThat(r.basicTpp()).isEqualTo(500_000f));

        verify(jabatanRepository).findAllByNipIn(List.of(nip1, nip2));
    }

    @Test
    void listJabatanByNipWithPegawaiBatch_WhenNoJabatanExists_ShouldReturnEmptyList() {
        List<String> nips = List.of("000", "111");
        when(jabatanRepository.findAllByNipIn(nips)).thenReturn(List.of());

        List<JabatanWithTppPajakResponse> result = jabatanService.listJabatanByNipWithPegawaiBatch(nips);

        assertThat(result).isEmpty();
        verify(jabatanRepository).findAllByNipIn(nips);
        verify(pegawaiRepository, never()).findByNip(any());
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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());

        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(jabatanTanpaNama));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.empty());

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().nip()).isEqualTo(nip);
        assertThat(result.getFirst().namaPegawai()).isNull();
        assertThat(result.getFirst().namaJabatan()).isEqualTo("Analis Ahli Muda");
        assertThat(result.getFirst().basicTpp()).isEqualTo(DEFAULT_BASIC_TPP);
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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());

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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());

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
                DEFAULT_BASIC_TPP,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now());

        when(jabatanRepository.findAllByNip(nip)).thenReturn(List.of(jabatan1, jabatan2, jabatan3));
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(pegawai));

        List<JabatanWithPegawaiResponse> result = jabatanService.listJabatanByNipWithPegawai(nip);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).statusJabatan()).isEqualTo("UTAMA");
        assertThat(result.get(1).statusJabatan()).isEqualTo("PLT_UTAMA");
        assertThat(result.get(2).statusJabatan()).isEqualTo("BERAKHIR");
        assertThat(result)
                .extracting(JabatanWithPegawaiResponse::basicTpp)
                .containsExactly(DEFAULT_BASIC_TPP, DEFAULT_BASIC_TPP, DEFAULT_BASIC_TPP);

        verify(jabatanRepository).findAllByNip(nip);
        verify(pegawaiRepository, times(3)).findByNip(nip);
    }
}
