package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import java.time.Instant;
import java.util.*;

import cc.kertaskerja.tppkepegawaian.role.domain.*;
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
}