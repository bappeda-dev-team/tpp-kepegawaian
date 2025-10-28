package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import java.time.Instant;
import java.util.*;

import cc.kertaskerja.tppkepegawaian.role.domain.*;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.*;
import cc.kertaskerja.tppkepegawaian.opd.domain.Opd;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanResponse;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.MasterPegawaiByOpdResponse;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PegawaiServiceTest {
    @Mock
    private PegawaiRepository pegawaiRepository;

    @Mock
    private OpdRepository opdRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JabatanRepository jabatanRepository;

    @InjectMocks
    private PegawaiService pegawaiService;

    private Pegawai testPegawai;

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
        Set<Role> rolePegawai = Set.of(new Role(1L, "admin", "200601012010012001",
                "1", "AKTIF", Instant.now(), Instant.now() ));
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

        Iterable<PegawaiWithRoles> result = pegawaiService.listAllPegawaiByKodeOpd(kodeOpd);

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

    // Test cases for new methods

    @Test
    void detailPegawaiWithJabatan_WhenPegawaiExistsAndHasJabatan_ShouldReturnPegawaiWithJabatanResponse() {
        String nip = "198001012010011001";
        Date currentDate = new Date();
        Jabatan jabatan = new Jabatan(
                1L,
                nip,
                "Kepala Seksi",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.III_A,
                "Pembina",
                "IV/a",
                currentDate,
                currentDate,
                Instant.now(),
                Instant.now()
        );

        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(testPegawai));
        when(jabatanRepository.findByNip(nip)).thenReturn(Optional.of(jabatan));

        PegawaiWithJabatanResponse result = pegawaiService.detailPegawaiWithJabatan(nip);

        assertThat(result.id()).isEqualTo(testPegawai.id());
        assertThat(result.namaPegawai()).isEqualTo(testPegawai.namaPegawai());
        assertThat(result.nip()).isEqualTo(testPegawai.nip());
        assertThat(result.namaJabatan()).isEqualTo("Kepala Seksi");
        assertThat(result.statusJabatan()).isEqualTo(StatusJabatan.UTAMA);
        assertThat(result.jenisJabatan()).isEqualTo(JenisJabatan.JABATAN_STRUKTURAL);
        assertThat(result.eselon()).isEqualTo(Eselon.III_A);
        assertThat(result.pangkat()).isEqualTo("Pembina");
        assertThat(result.golongan()).isEqualTo("IV/a");
        verify(pegawaiRepository).findByNip(nip);
        verify(jabatanRepository).findByNip(nip);
    }

    @Test
    void detailPegawaiWithJabatan_WhenPegawaiExistsAndNoJabatan_ShouldReturnPegawaiWithNullJabatan() {
        String nip = "198001012010011001";

        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(testPegawai));
        when(jabatanRepository.findByNip(nip)).thenReturn(Optional.empty());

        PegawaiWithJabatanResponse result = pegawaiService.detailPegawaiWithJabatan(nip);

        assertThat(result.id()).isEqualTo(testPegawai.id());
        assertThat(result.namaPegawai()).isEqualTo(testPegawai.namaPegawai());
        assertThat(result.nip()).isEqualTo(testPegawai.nip());
        assertThat(result.namaJabatan()).isNull();
        assertThat(result.statusJabatan()).isNull();
        assertThat(result.jenisJabatan()).isNull();
        assertThat(result.eselon()).isNull();
        assertThat(result.pangkat()).isNull();
        assertThat(result.golongan()).isNull();
        verify(pegawaiRepository).findByNip(nip);
        verify(jabatanRepository).findByNip(nip);
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
    }

    @Test
    void listAllPegawaiWithJabatanByKodeOpd_WhenOpdExists_ShouldReturnMasterPegawaiByOpdResponse() {
        String kodeOpd = "OPD-001";
        Opd opd = Opd.of(kodeOpd, "Dinas Komunikasi dan Informatika");

        Pegawai pegawai1 = new Pegawai(
                1L,
                "John Doe",
                "198001012010011001",
                kodeOpd,
                "Admin",
                StatusPegawai.AKTIF,
                "hashedpassword1",
                Instant.now(),
                Instant.now()
        );

        Pegawai pegawai2 = new Pegawai(
                2L,
                "Jane Smith",
                "199001012010012001",
                kodeOpd,
                "User",
                StatusPegawai.AKTIF,
                "hashedpassword2",
                Instant.now(),
                Instant.now()
        );

        Date currentDate = new Date();
        Jabatan jabatan1 = new Jabatan(
                1L,
                "198001012010011001",
                "Kepala Bidang",
                kodeOpd,
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.IV_A,
                "Pembina",
                "IV/a",
                currentDate,
                currentDate,
                Instant.now(),
                Instant.now()
        );

        Set<Role> roles1 = Set.of(new Role(1L, "admin", "198001012010011001",
                LevelRole.LEVEL_1, IsActive.AKTIF, Instant.now(), Instant.now()));

        List<Pegawai> pegawaiList = List.of(pegawai1, pegawai2);

        when(opdRepository.findByKodeOpd(kodeOpd)).thenReturn(Optional.of(opd));
        when(pegawaiRepository.findByKodeOpd(kodeOpd)).thenReturn(pegawaiList);
        when(jabatanRepository.findByNip("198001012010011001")).thenReturn(Optional.of(jabatan1));
        when(jabatanRepository.findByNip("199001012010012001")).thenReturn(Optional.empty());
        when(roleRepository.findByNip("198001012010011001")).thenReturn(new ArrayList<>(roles1));
        when(roleRepository.findByNip("199001012010012001")).thenReturn(List.of());

        MasterPegawaiByOpdResponse result = pegawaiService.listAllPegawaiWithJabatanByKodeOpd(kodeOpd);

        assertThat(result.kodeOpd()).isEqualTo(kodeOpd);
        assertThat(result.namaOPD()).isEqualTo("Dinas Komunikasi dan Informatika");
        assertThat(result.pegawai()).hasSize(2);

        MasterPegawaiByOpdResponse.PegawaiItem item1 = result.pegawai().get(0);
        assertThat(item1.namaPegawai()).isEqualTo("John Doe");
        assertThat(item1.nip()).isEqualTo("198001012010011001");
        assertThat(item1.namaJabatan()).isEqualTo("Kepala Bidang");
        assertThat(item1.namaRole()).isEqualTo("admin");
        assertThat(item1.isActive()).isEqualTo(IsActive.AKTIF);

        MasterPegawaiByOpdResponse.PegawaiItem item2 = result.pegawai().get(1);
        assertThat(item2.namaPegawai()).isEqualTo("Jane Smith");
        assertThat(item2.nip()).isEqualTo("199001012010012001");
        assertThat(item2.namaJabatan()).isNull();
        assertThat(item2.namaRole()).isNull();
        assertThat(item2.isActive()).isNull();

        verify(opdRepository).findByKodeOpd(kodeOpd);
        verify(pegawaiRepository).findByKodeOpd(kodeOpd);
        verify(jabatanRepository).findByNip("198001012010011001");
        verify(jabatanRepository).findByNip("199001012010012001");
        verify(roleRepository).findByNip("198001012010011001");
        verify(roleRepository).findByNip("199001012010012001");
    }

    @Test
    void listAllPegawaiWithJabatanByKodeOpd_WhenOpdNotExists_ShouldThrowException() {
        String kodeOpd = "OPD-9999";
        when(opdRepository.findByKodeOpd(kodeOpd)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pegawaiService.listAllPegawaiWithJabatanByKodeOpd(kodeOpd))
                .isInstanceOf(OpdNotFoundException.class)
                .hasMessageContaining(kodeOpd);

        verify(opdRepository).findByKodeOpd(kodeOpd);
        verify(pegawaiRepository, never()).findByKodeOpd(any());
        verify(jabatanRepository, never()).findByNip(any());
        verify(roleRepository, never()).findByNip(any());
    }

    @Test
    void getRolesByNip_WhenRolesExist_ShouldReturnRoles() {
        String nip = "198001012010011001";
        Set<Role> expectedRoles = Set.of(
                new Role(1L, "admin", nip, LevelRole.LEVEL_1, IsActive.AKTIF, Instant.now(), Instant.now()),
                new Role(2L, "user", nip, LevelRole.LEVEL_2, IsActive.AKTIF, Instant.now(), Instant.now())
        );

        when(roleRepository.findByNip(nip)).thenReturn(new ArrayList<>(expectedRoles));

        Set<Role> result = pegawaiService.getRolesByNip(nip);

        assertThat(result).isEqualTo(expectedRoles);
        verify(roleRepository).findByNip(nip);
    }

    @Test
    void getRolesByNip_WhenNoRolesExist_ShouldReturnEmptySet() {
        String nip = "198001012010011001";
        when(roleRepository.findByNip(nip)).thenReturn(List.of());

        Set<Role> result = pegawaiService.getRolesByNip(nip);

        assertThat(result).isEmpty();
        verify(roleRepository).findByNip(nip);
    }
}
