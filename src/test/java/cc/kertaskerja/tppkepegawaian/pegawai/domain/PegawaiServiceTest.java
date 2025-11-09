package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import java.time.Instant;
import java.util.*;

import cc.kertaskerja.tppkepegawaian.role.domain.*;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JabatanRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanResponse;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanAndRolesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PegawaiServiceTest {
    @Mock
    private PegawaiRepository pegawaiRepository;
    
    @Mock
    private JabatanRepository jabatanRepository;

    @Mock
    private OpdRepository opdRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private TppRepository tppRepository;

    @Mock
    private TppPerhitunganService tppPerhitunganService;

    @InjectMocks
    private PegawaiService pegawaiService;

    private Pegawai testPegawai;
    private Tpp testTpp;
    private Jabatan testJabatan;
    private TppPerhitungan testTppPerhitungan;

    @BeforeEach
    void setUp() {
        testPegawai = new Pegawai(
            1L,
            "John Doe",
            "198001012010011001",
            "OPD-001",
            "Admin",
            "AKTIF",
            "hashedpassword",
            Instant.now(),
            Instant.now()
        );

        testTpp = new Tpp(
            1L,
            "TPP_REGULER",
            "OPD-001",
            "198001012010011001",
            "KODE_PEMDA_001",
            5000000.0f,
            500000.0f,
            200000.0f,
            10,
            2024,
            Instant.now(),
            Instant.now()
        );

        testJabatan = new Jabatan(
            1L,
            "198001012010011001",
            "Kepala Seksi",
            "OPD-001",
            "AKTIF",
            "STRUKTURAL",
            "III/a",
            "Penata Muda",
            "III/a",
            new java.util.Date(),
            new java.util.Date(),
            Instant.now(),
            Instant.now()
        );

        testTppPerhitungan = new TppPerhitungan(
            1L,
            "TPP_REGULER",
            "OPD-001",
            "KODE_PEMDA_001",
            "198001012010011001",
            "John Doe",
            10,
            2024,
            5000000.0f,
            "TUNJANGAN_KINERJA",
            2000000.0f,
            Instant.now(),
            Instant.now()
        );
    }

    @Test
    void detailPegawai_WhenPegawaiExists_ShouldReturnPegawai() {
        String nip = "198001012010011001";
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(testPegawai));

        Pegawai result = pegawaiService.detailPegawai(nip);

        assertThat(result).isEqualTo(testPegawai);
        verify(pegawaiRepository).findByNip(nip);
    }

    @Test
    void detailPegawai_WhenPegawaiNotExists_ShouldThrowException() {
        String nip = "999999999999999999";
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pegawaiService.detailPegawai(nip))
            .isInstanceOf(PegawaiNotFoundException.class)
            .hasMessageContaining(nip);
        verify(pegawaiRepository).findByNip(nip);
    }

    @Test
    void listPegawaiAktif_WhenOpdExists_ShouldReturnPegawaiWithRoleList() {
        String kodeOpd = "OPD-001";
        Set<Role> rolePegawai = Set.of(new Role(1L, "admin", "200601012010012001", "level 1",
            "AKTIF", Instant.now(), Instant.now() ));
        Pegawai pegawai = new Pegawai(
            2L,
            "Jane Doe",
            "200601012010012001",
            "OPD-001",
            "User",
            "AKTIF",
            "hashedpassword123",
            Instant.now(),
            Instant.now()
        );

        List<Pegawai> pegawaiList = List.of(pegawai);

        List<PegawaiWithRoles> expected = List.of(
            PegawaiWithRoles.of(pegawai, rolePegawai)
        );

        when(opdRepository.existsByKodeOpd(kodeOpd)).thenReturn(true);
        when(pegawaiRepository.findByKodeOpd(kodeOpd)).thenReturn(pegawaiList);
        when(roleRepository.findByNip("200601012010012001")).thenReturn(new ArrayList<>(rolePegawai));

        List<PegawaiWithRoles> result = pegawaiService.listAllPegawaiByKodeOpd(kodeOpd);

        assertThat(result).containsExactlyElementsOf(expected);

        verify(opdRepository).existsByKodeOpd(kodeOpd);
        verify(pegawaiRepository).findByKodeOpd(kodeOpd);
        verify(roleRepository).findByNip("200601012010012001");
    }

    @Test
    void listPegawaiAktif_WhenOpdNotExists_ShouldThrowException() {
        String kodeOpd = "OPD-9999";
        when(opdRepository.existsByKodeOpd(kodeOpd)).thenReturn(false);

        assertThatThrownBy(() -> pegawaiService.listAllPegawaiByKodeOpd(kodeOpd))
            .isInstanceOf(OpdNotFoundException.class)
            .hasMessageContaining(kodeOpd);

        verify(opdRepository).existsByKodeOpd(kodeOpd);
        verify(pegawaiRepository, never()).findByKodeOpd(any());
    }

    @Test
    void listAllPegawaiByRole_WhenRoleExists_ShouldReturnPegawaiList() {
        String namaRole = "Admin";
        List<Pegawai> pegawaiList = Arrays.asList(
            testPegawai,
            new Pegawai(
                2L,
                "Jane Doe",
                "200601012010012001",
                "OPD-001",
                "Admin",
                "AKTIF",
                "hashedpassword123",
                Instant.now(),
                Instant.now()
            )
        );

        when(pegawaiRepository.findByNamaRole(namaRole)).thenReturn(pegawaiList);

        Iterable<Pegawai> result = pegawaiService.listAllPegawaiByRole(namaRole);

        assertThat(result).isEqualTo(pegawaiList);
        verify(pegawaiRepository).findByNamaRole(namaRole);
    }

    @Test
    void listAllPegawaiByRole_WhenRoleNotExists_ShouldReturnEmptyList() {
        String namaRole = "Salah";
        when(pegawaiRepository.findByNamaRole(namaRole)).thenReturn(List.of());

        Iterable<Pegawai> result = pegawaiService.listAllPegawaiByRole(namaRole);

        assertThat(result).isEmpty();
        verify(pegawaiRepository).findByNamaRole(namaRole);
    }

    @Test
    void tambahPegawai_WhenPegawaiValid_ShouldSaveAndReturnPegawai() {
        Pegawai newPegawai = new Pegawai(
            null,
            "Jane Doe",
            "200601012010012001",
            "OPD-001",
            "Admin",
            "AKTIF",
            "hashedpassword123",
            null,
            null
        );

        when(pegawaiRepository.existsByNip(newPegawai.nip())).thenReturn(false);
        when(opdRepository.existsByKodeOpd(newPegawai.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.save(any(Pegawai.class))).thenReturn(
            new Pegawai(
                2L,
                newPegawai.namaPegawai(),
                newPegawai.nip(),
                newPegawai.kodeOpd(),
                newPegawai.namaRole(),
                newPegawai.statusPegawai(),
                newPegawai.passwordHash(),
                Instant.now(),
                Instant.now()
            )
        );

        Pegawai result = pegawaiService.tambahPegawai(newPegawai);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.namaPegawai()).isEqualTo("Jane Doe");
        assertThat(result.nip()).isEqualTo("200601012010012001");
        assertThat(result.namaRole()).isEqualTo("Admin");
        assertThat(result.passwordHash()).isEqualTo("hashedpassword123");
        verify(pegawaiRepository).existsByNip(newPegawai.nip());
        verify(opdRepository).existsByKodeOpd(newPegawai.kodeOpd());
        verify(pegawaiRepository).save(newPegawai);
    }

    @Test
    void tambahPegawai_WhenPegawaiAlreadyExists_ShouldThrowException() {
        when(pegawaiRepository.existsByNip(testPegawai.nip())).thenReturn(true);

        assertThatThrownBy(() -> pegawaiService.tambahPegawai(testPegawai))
            .isInstanceOf(PegawaiSudahAdaException.class)
            .hasMessageContaining(testPegawai.nip());
        verify(pegawaiRepository).existsByNip(testPegawai.nip());
        verify(pegawaiRepository, never()).save(any());
    }

    @Test
    void tambahPegawai_WhenOpdNotExists_ShouldThrowException() {
        Pegawai newPegawai = new Pegawai(
            null,
            "Jane Doe",
            "198001012010011001",
            "OPD-9999",
            "Admin",
            "AKTIF",
            "hashedpassword123",
            null,
            null);

        when(pegawaiRepository.existsByNip(newPegawai.nip())).thenReturn(false);
        when(opdRepository.existsByKodeOpd("OPD-9999")).thenReturn(false);

        assertThatThrownBy(() -> pegawaiService.tambahPegawai(newPegawai))
            .isInstanceOf(OpdNotFoundException.class)
            .hasMessageContaining("OPD-9999");

        verify(pegawaiRepository).existsByNip(newPegawai.nip());
        verify(opdRepository).existsByKodeOpd("OPD-9999");
        verify(pegawaiRepository, never()).save(any());
    }

    @Test
    void ubahPegawai_WhenPegawaiExistsAndValid_ShouldUpdateAndReturnPegawai() {
        String nip = "198001012010011001";
        Pegawai updatedPegawai = new Pegawai(
            1L,
            "Anthony",
            nip,
            "OPD-001",
            "User",
            "CUTI",
            "newhash123",
            testPegawai.createdDate(),
            Instant.now()
        );

        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(opdRepository.existsByKodeOpd("OPD-001")).thenReturn(true);
        when(roleRepository.existsByNamaRole("User")).thenReturn(true);
        when(pegawaiRepository.save(any(Pegawai.class))).thenReturn(updatedPegawai);

        Pegawai result = pegawaiService.ubahPegawai(nip, updatedPegawai);

        assertThat(result).isEqualTo(updatedPegawai);
        verify(pegawaiRepository).existsByNip(nip);
        verify(opdRepository).existsByKodeOpd("OPD-001");
        verify(roleRepository).existsByNamaRole("User");
        verify(pegawaiRepository).save(updatedPegawai);
    }

    @Test
    void ubahPegawai_WhenPegawaiNotExists_ShouldThrowException() {
        String nip = "999999999999999999";
        when(pegawaiRepository.existsByNip(nip)).thenReturn(false);

        assertThatThrownBy(() -> pegawaiService.ubahPegawai(nip, testPegawai))
            .isInstanceOf(PegawaiNotFoundException.class)
            .hasMessageContaining(nip);
        verify(pegawaiRepository).existsByNip(nip);
        verify(pegawaiRepository, never()).save(any());
    }

    @Test
    void ubahPegawai_WhenOpdNotExists_ShouldThrowException() {
        String nip = "198001012010011001";
        Pegawai updatedPegawai = new Pegawai(
            1L,
            "Anthony",
            nip,
            "OPD-9999",
            "User",
            "CUTI",
            "newhash123",
            testPegawai.createdDate(),
            Instant.now());

        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(opdRepository.existsByKodeOpd("OPD-9999")).thenReturn(false);

        assertThatThrownBy(() -> pegawaiService.ubahPegawai(nip, updatedPegawai))
            .isInstanceOf(OpdNotFoundException.class)
            .hasMessageContaining("OPD-9999");

        verify(pegawaiRepository).existsByNip(nip);
        verify(opdRepository).existsByKodeOpd("OPD-9999");
        verify(pegawaiRepository, never()).save(any());
    }

    @Test
    void ubahPegawai_WhenRoleNotExists_ShouldThrowException() {
        String nip = "198001012010011001";
        Pegawai updatedPegawai = new Pegawai(
            1L,
            "Anthony",
            nip,
            "OPD-001",
            "Salah",
            "CUTI",
            "newhash123",
            testPegawai.createdDate(),
            Instant.now());

        when(pegawaiRepository.existsByNip("198001012010011001")).thenReturn(true);
        when(opdRepository.existsByKodeOpd("OPD-001")).thenReturn(true);
        when(roleRepository.existsByNamaRole("Salah")).thenReturn(false);

        assertThatThrownBy(() -> pegawaiService.ubahPegawai(nip, updatedPegawai))
            .isInstanceOf(NamaRoleNotFoundException.class)
            .hasMessageContaining("Salah");

        verify(pegawaiRepository).existsByNip(nip);
        verify(opdRepository).existsByKodeOpd("OPD-001");
        verify(roleRepository).existsByNamaRole("Salah");
        verify(pegawaiRepository, never()).save(any());
    }

    @Test
    void hapusPegawai_WhenPegawaiExists_ShouldDeletePegawai() {
        String nip = "198001012010011001";
        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        doNothing().when(pegawaiRepository).deleteByNip(nip);

        pegawaiService.hapusPegawai(nip);

        verify(pegawaiRepository).existsByNip(nip);
        verify(pegawaiRepository).deleteByNip(nip);
    }

    @Test
    void hapusPegawai_WhenPegawaiNotExists_ShouldThrowException() {
        String nip = "999999999999999999";
        when(pegawaiRepository.existsByNip(nip)).thenReturn(false);

        assertThatThrownBy(() -> pegawaiService.hapusPegawai(nip))
            .isInstanceOf(PegawaiNotFoundException.class)
            .hasMessageContaining(nip);
        verify(pegawaiRepository).existsByNip(nip);
        verify(pegawaiRepository, never()).deleteByNip(any());
    }

    @Test
    void listAllPegawaiWithJabatanByKodeOpd_WhenOpdExists_ShouldReturnPegawaiWithJabatanAndRolesList() {
        String kodeOpd = "OPD-001";
        Set<Role> rolePegawai = Set.of(new Role(1L, "admin", "200601012010012001", "level 1",
            "AKTIF", Instant.now(), Instant.now() ));
        Pegawai pegawai = new Pegawai(
            2L,
            "Jane Doe",
            "200601012010012001",
            "OPD-001",
            "User",
            "AKTIF",
            "hashedpassword123",
            Instant.now(),
            Instant.now()
        );

        List<Pegawai> pegawaiList = List.of(pegawai);

        List<PegawaiWithJabatanAndRolesResponse> expected = List.of(
            PegawaiWithJabatanAndRolesResponse.of(
                pegawai.id(),
                pegawai.namaPegawai(),
                pegawai.nip(),
                pegawai.kodeOpd(),
                rolePegawai,
                testJabatan
            )
        );

        when(opdRepository.existsByKodeOpd(kodeOpd)).thenReturn(true);
        when(pegawaiRepository.findByKodeOpd(kodeOpd)).thenReturn(pegawaiList);
        when(roleRepository.findByNip("200601012010012001")).thenReturn(new ArrayList<>(rolePegawai));
        when(jabatanRepository.findByNip("200601012010012001")).thenReturn(Optional.of(testJabatan));

        List<PegawaiWithJabatanAndRolesResponse> result = pegawaiService.listAllPegawaiWithJabatanByKodeOpd(kodeOpd);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(pegawai.id());
        assertThat(result.get(0).namaPegawai()).isEqualTo(pegawai.namaPegawai());
        assertThat(result.get(0).nip()).isEqualTo(pegawai.nip());
        assertThat(result.get(0).kodeOpd()).isEqualTo(pegawai.kodeOpd());
        assertThat(result.get(0).namaRole()).isEqualTo("admin");
        assertThat(result.get(0).isActive()).isEqualTo("AKTIF");
        assertThat(result.get(0).idJabatan()).isEqualTo(testJabatan.id());
        assertThat(result.get(0).namaJabatan()).isEqualTo(testJabatan.namaJabatan());

        verify(opdRepository).existsByKodeOpd(kodeOpd);
        verify(pegawaiRepository).findByKodeOpd(kodeOpd);
        verify(roleRepository).findByNip("200601012010012001");
        verify(jabatanRepository).findByNip("200601012010012001");
    }

    @Test
    void listAllPegawaiWithJabatanByKodeOpd_WhenOpdExistsWithoutJabatanAndRoles_ShouldReturnPegawaiWithNullJabatanAndRoles() {
        String kodeOpd = "OPD-001";
        Pegawai pegawai = new Pegawai(
            2L,
            "Jane Doe",
            "200601012010012001",
            "OPD-001",
            "User",
            "AKTIF",
            "hashedpassword123",
            Instant.now(),
            Instant.now()
        );

        List<Pegawai> pegawaiList = List.of(pegawai);

        when(opdRepository.existsByKodeOpd(kodeOpd)).thenReturn(true);
        when(pegawaiRepository.findByKodeOpd(kodeOpd)).thenReturn(pegawaiList);
        when(roleRepository.findByNip("200601012010012001")).thenReturn(new ArrayList<>());
        when(jabatanRepository.findByNip("200601012010012001")).thenReturn(Optional.empty());

        List<PegawaiWithJabatanAndRolesResponse> result = pegawaiService.listAllPegawaiWithJabatanByKodeOpd(kodeOpd);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(pegawai.id());
        assertThat(result.get(0).namaPegawai()).isEqualTo(pegawai.namaPegawai());
        assertThat(result.get(0).nip()).isEqualTo(pegawai.nip());
        assertThat(result.get(0).kodeOpd()).isEqualTo(pegawai.kodeOpd());
        assertThat(result.get(0).namaRole()).isNull();
        assertThat(result.get(0).isActive()).isNull();
        assertThat(result.get(0).idJabatan()).isNull();
        assertThat(result.get(0).namaJabatan()).isNull();

        verify(opdRepository).existsByKodeOpd(kodeOpd);
        verify(pegawaiRepository).findByKodeOpd(kodeOpd);
        verify(roleRepository).findByNip("200601012010012001");
        verify(jabatanRepository).findByNip("200601012010012001");
    }

    @Test
    void listAllPegawaiWithJabatanByKodeOpd_WhenOpdNotExists_ShouldThrowException() {
        String kodeOpd = "OPD-9999";
        when(opdRepository.existsByKodeOpd(kodeOpd)).thenReturn(false);

        assertThatThrownBy(() -> pegawaiService.listAllPegawaiWithJabatanByKodeOpd(kodeOpd))
            .isInstanceOf(OpdNotFoundException.class)
            .hasMessageContaining(kodeOpd);

        verify(opdRepository).existsByKodeOpd(kodeOpd);
        verify(pegawaiRepository, never()).findByKodeOpd(any());
        verify(jabatanRepository, never()).findByNip(any());
        verify(roleRepository, never()).findByNip(any());
    }

    @Test
    void getRolesByNip_WhenRolesExist_ShouldReturnRolesSet() {
        String nip = "198001012010011001";
        Set<Role> expectedRoles = Set.of(
            new Role(1L, "admin", nip, "level 1", "AKTIF", Instant.now(), Instant.now()),
            new Role(2L, "user", nip, "level 2", "AKTIF", Instant.now(), Instant.now())
        );

        when(roleRepository.findByNip(nip)).thenReturn(new ArrayList<>(expectedRoles));

        Set<Role> result = pegawaiService.getRolesByNip(nip);

        assertThat(result).hasSize(2);
        assertThat(result).containsAll(expectedRoles);
        verify(roleRepository).findByNip(nip);
    }

    @Test
    void getRolesByNip_WhenNoRolesExist_ShouldReturnEmptySet() {
        String nip = "198001012010011001";
        when(roleRepository.findByNip(nip)).thenReturn(new ArrayList<>());

        Set<Role> result = pegawaiService.getRolesByNip(nip);

        assertThat(result).isEmpty();
        verify(roleRepository).findByNip(nip);
    }

    @Test
    void detailPegawaiWithJabatan_WhenPegawaiExistsWithCurrentTppAndJabatan_ShouldReturnPegawaiWithJabatanAndTppResponse() {
        String nip = "198001012010011001";

        Tpp currentTpp = new Tpp(
            1L,
            "TPP_REGULER",
            "OPD-001",
            nip,
            "KODE_PEMDA_001",
            5000000.0f,
            500000.0f,
            200000.0f,
            java.time.LocalDate.now().getMonthValue(),
            java.time.LocalDate.now().getYear(),
            Instant.now(),
            Instant.now()
        );

        List<TppPerhitungan> perhitunganList = List.of(testTppPerhitungan);

        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(testPegawai));
        when(jabatanRepository.findByNip(nip)).thenReturn(Optional.of(testJabatan));
        when(tppRepository.findByNipAndBulanAndTahun(nip, currentTpp.bulan(), currentTpp.tahun()))
            .thenReturn(List.of(currentTpp));
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, currentTpp.bulan(), currentTpp.tahun()))
            .thenReturn(perhitunganList);

        PegawaiWithJabatanResponse result = pegawaiService.detailPegawaiWithJabatan(nip);

        assertThat(result.id()).isEqualTo(testPegawai.id());
        assertThat(result.namaPegawai()).isEqualTo(testPegawai.namaPegawai());
        assertThat(result.nip()).isEqualTo(testPegawai.nip());
        assertThat(result.kodeOpd()).isEqualTo(testPegawai.kodeOpd());
        assertThat(result.namaRole()).isEqualTo(testPegawai.namaRole());
        assertThat(result.statusPegawai()).isEqualTo(testPegawai.statusPegawai());
        assertThat(result.namaJabatan()).isEqualTo(testJabatan.namaJabatan());
        assertThat(result.statusJabatan()).isEqualTo(testJabatan.statusJabatan());
        assertThat(result.jenisJabatan()).isEqualTo(testJabatan.jenisJabatan());
        assertThat(result.eselon()).isEqualTo(testJabatan.eselon());
        assertThat(result.pangkat()).isEqualTo(testJabatan.pangkat());
        assertThat(result.golongan()).isEqualTo(testJabatan.golongan());
        assertThat(result.jenisTpp()).isEqualTo(currentTpp.jenisTpp());
        assertThat(result.bulan()).isEqualTo(currentTpp.bulan());
        assertThat(result.tahun()).isEqualTo(currentTpp.tahun());
        assertThat(result.createdDate()).isEqualTo(testPegawai.createdDate());
        assertThat(result.lastModifiedDate()).isEqualTo(testPegawai.lastModifiedDate());

        verify(pegawaiRepository).findByNip(nip);
        verify(jabatanRepository).findByNip(nip);
        verify(tppRepository).findByNipAndBulanAndTahun(nip, currentTpp.bulan(), currentTpp.tahun());
        verify(tppPerhitunganService).listTppPerhitunganByNipAndBulanAndTahun(nip, currentTpp.bulan(), currentTpp.tahun());
    }

    @Test
    void detailPegawaiWithJabatan_WhenPegawaiExistsWithoutTppAndJabatan_ShouldReturnPegawaiWithNullTppAndJabatan() {
        String nip = "198001012010011001";

        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(testPegawai));
        when(jabatanRepository.findByNip(nip)).thenReturn(Optional.empty());
        when(tppRepository.findByNipAndBulanAndTahun(eq(nip), anyInt(), anyInt())).thenReturn(List.of());
        when(tppRepository.findByNip(nip)).thenReturn(List.of());

        PegawaiWithJabatanResponse result = pegawaiService.detailPegawaiWithJabatan(nip);

        assertThat(result.id()).isEqualTo(testPegawai.id());
        assertThat(result.namaPegawai()).isEqualTo(testPegawai.namaPegawai());
        assertThat(result.nip()).isEqualTo(testPegawai.nip());
        assertThat(result.kodeOpd()).isEqualTo(testPegawai.kodeOpd());
        assertThat(result.namaRole()).isEqualTo(testPegawai.namaRole());
        assertThat(result.statusPegawai()).isEqualTo(testPegawai.statusPegawai());
        assertThat(result.namaJabatan()).isNull();
        assertThat(result.statusJabatan()).isNull();
        assertThat(result.jenisJabatan()).isNull();
        assertThat(result.eselon()).isNull();
        assertThat(result.pangkat()).isNull();
        assertThat(result.golongan()).isNull();
        assertThat(result.jenisTpp()).isNull();
        assertThat(result.bulan()).isNull();
        assertThat(result.tahun()).isNull();
        assertThat(result.createdDate()).isEqualTo(testPegawai.createdDate());
        assertThat(result.lastModifiedDate()).isEqualTo(testPegawai.lastModifiedDate());

        verify(pegawaiRepository).findByNip(nip);
        verify(jabatanRepository).findByNip(nip);
        verify(tppRepository).findByNipAndBulanAndTahun(eq(nip), anyInt(), anyInt());
        verify(tppRepository).findByNip(nip);
        verifyNoInteractions(tppPerhitunganService);
    }

    @Test
    void detailPegawaiWithJabatan_WhenPegawaiExistsWithOldTpp_ShouldReturnPegawaiWithFirstAvailableTpp() {
        String nip = "198001012010011001";

        // Create old TPP (not current month/year)
        Tpp oldTpp = new Tpp(
            1L,
            "TPP_REGULER",
            "OPD-001",
            nip,
            "KODE_PEMDA_001",
            5000000.0f,
            500000.0f,
            200000.0f,
            1,
            2023,
            Instant.now(),
            Instant.now()
        );

        List<TppPerhitungan> perhitunganList = List.of(testTppPerhitungan);

        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(testPegawai));
        when(jabatanRepository.findByNip(nip)).thenReturn(Optional.of(testJabatan));
        when(tppRepository.findByNipAndBulanAndTahun(eq(nip), anyInt(), anyInt())).thenReturn(List.of());
        when(tppRepository.findByNip(nip)).thenReturn(List.of(oldTpp));
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, oldTpp.bulan(), oldTpp.tahun()))
            .thenReturn(perhitunganList);

        PegawaiWithJabatanResponse result = pegawaiService.detailPegawaiWithJabatan(nip);

        assertThat(result.id()).isEqualTo(testPegawai.id());
        assertThat(result.namaPegawai()).isEqualTo(testPegawai.namaPegawai());
        assertThat(result.jenisTpp()).isEqualTo(oldTpp.jenisTpp());
        assertThat(result.bulan()).isEqualTo(oldTpp.bulan());
        assertThat(result.tahun()).isEqualTo(oldTpp.tahun());

        verify(pegawaiRepository).findByNip(nip);
        verify(jabatanRepository).findByNip(nip);
        verify(tppRepository).findByNipAndBulanAndTahun(eq(nip), anyInt(), anyInt());
        verify(tppRepository).findByNip(nip);
        verify(tppPerhitunganService).listTppPerhitunganByNipAndBulanAndTahun(nip, oldTpp.bulan(), oldTpp.tahun());
    }

    @Test
    void detailPegawaiWithJabatan_WhenPegawaiNotExists_ShouldThrowException() {
        String nip = "999999999999999999";
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pegawaiService.detailPegawaiWithJabatan(nip))
            .isInstanceOf(PegawaiNotFoundException.class)
            .hasMessageContaining(nip);

        verify(pegawaiRepository).findByNip(nip);
        verify(jabatanRepository, never()).findByNip(any());
        verify(tppRepository, never()).findByNipAndBulanAndTahun(any(), anyInt(), anyInt());
        verify(tppRepository, never()).findByNip(any());
        verifyNoInteractions(tppPerhitunganService);
    }

    @Test
    void detailPegawaiWithJabatan_WhenPegawaiExistsWithCurrentTppNoJabatan_ShouldReturnPegawaiWithTppAndNullJabatan() {
        String nip = "198001012010011001";

        // Create TPP that matches current month/year
        Tpp currentTpp = new Tpp(
            1L,
            "TPP_REGULER",
            "OPD-001",
            nip,
            "KODE_PEMDA_001",
            5000000.0f,
            500000.0f,
            200000.0f,
            java.time.LocalDate.now().getMonthValue(),
            java.time.LocalDate.now().getYear(),
            Instant.now(),
            Instant.now()
        );

        List<TppPerhitungan> perhitunganList = List.of(testTppPerhitungan);

        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(testPegawai));
        when(jabatanRepository.findByNip(nip)).thenReturn(Optional.empty());
        when(tppRepository.findByNipAndBulanAndTahun(nip, currentTpp.bulan(), currentTpp.tahun()))
            .thenReturn(List.of(currentTpp));
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, currentTpp.bulan(), currentTpp.tahun()))
            .thenReturn(perhitunganList);

        PegawaiWithJabatanResponse result = pegawaiService.detailPegawaiWithJabatan(nip);

        assertThat(result.id()).isEqualTo(testPegawai.id());
        assertThat(result.namaPegawai()).isEqualTo(testPegawai.namaPegawai());
        assertThat(result.nip()).isEqualTo(testPegawai.nip());
        assertThat(result.kodeOpd()).isEqualTo(testPegawai.kodeOpd());
        assertThat(result.namaRole()).isEqualTo(testPegawai.namaRole());
        assertThat(result.statusPegawai()).isEqualTo(testPegawai.statusPegawai());
        assertThat(result.namaJabatan()).isNull();
        assertThat(result.statusJabatan()).isNull();
        assertThat(result.jenisJabatan()).isNull();
        assertThat(result.eselon()).isNull();
        assertThat(result.pangkat()).isNull();
        assertThat(result.golongan()).isNull();
        assertThat(result.jenisTpp()).isEqualTo(currentTpp.jenisTpp());
        assertThat(result.bulan()).isEqualTo(currentTpp.bulan());
        assertThat(result.tahun()).isEqualTo(currentTpp.tahun());
        assertThat(result.createdDate()).isEqualTo(testPegawai.createdDate());
        assertThat(result.lastModifiedDate()).isEqualTo(testPegawai.lastModifiedDate());

        verify(pegawaiRepository).findByNip(nip);
        verify(jabatanRepository).findByNip(nip);
        verify(tppRepository).findByNipAndBulanAndTahun(nip, currentTpp.bulan(), currentTpp.tahun());
        verify(tppPerhitunganService).listTppPerhitunganByNipAndBulanAndTahun(nip, currentTpp.bulan(), currentTpp.tahun());
    }

    @Test
    void detailPegawaiWithJabatan_WhenPegawaiExistsWithJabatanNoTpp_ShouldReturnPegawaiWithJabatanAndNullTpp() {
        String nip = "198001012010011001";

        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(testPegawai));
        when(jabatanRepository.findByNip(nip)).thenReturn(Optional.of(testJabatan));
        when(tppRepository.findByNipAndBulanAndTahun(eq(nip), anyInt(), anyInt())).thenReturn(List.of());
        when(tppRepository.findByNip(nip)).thenReturn(List.of());

        PegawaiWithJabatanResponse result = pegawaiService.detailPegawaiWithJabatan(nip);

        assertThat(result.id()).isEqualTo(testPegawai.id());
        assertThat(result.namaPegawai()).isEqualTo(testPegawai.namaPegawai());
        assertThat(result.nip()).isEqualTo(testPegawai.nip());
        assertThat(result.kodeOpd()).isEqualTo(testPegawai.kodeOpd());
        assertThat(result.namaRole()).isEqualTo(testPegawai.namaRole());
        assertThat(result.statusPegawai()).isEqualTo(testPegawai.statusPegawai());
        assertThat(result.namaJabatan()).isEqualTo(testJabatan.namaJabatan());
        assertThat(result.statusJabatan()).isEqualTo(testJabatan.statusJabatan());
        assertThat(result.jenisJabatan()).isEqualTo(testJabatan.jenisJabatan());
        assertThat(result.eselon()).isEqualTo(testJabatan.eselon());
        assertThat(result.pangkat()).isEqualTo(testJabatan.pangkat());
        assertThat(result.golongan()).isEqualTo(testJabatan.golongan());
        assertThat(result.jenisTpp()).isNull();
        assertThat(result.bulan()).isNull();
        assertThat(result.tahun()).isNull();
        assertThat(result.createdDate()).isEqualTo(testPegawai.createdDate());
        assertThat(result.lastModifiedDate()).isEqualTo(testPegawai.lastModifiedDate());

        verify(pegawaiRepository).findByNip(nip);
        verify(jabatanRepository).findByNip(nip);
        verify(tppRepository).findByNipAndBulanAndTahun(eq(nip), anyInt(), anyInt());
        verify(tppRepository).findByNip(nip);
        verifyNoInteractions(tppPerhitunganService);
    }
}
