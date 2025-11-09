package cc.kertaskerja.tppkepegawaian.role.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PegawaiRepository pegawaiRepository;

    @Mock
    private RoleCacheService roleCacheService;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;
    
    @BeforeEach
    void setUp() {
        testRole = new Role(
                1L,
                "Admin",
                "198001012010011001",
                "1",
                "Y",
                Instant.now(),
                Instant.now()
        );
    }
    
    @Test
    void listRoleAktif_ShouldReturnRoleList() {
        List<Role> roleList = List.of(testRole);
        when(roleRepository.findByNip("198001012010011001")).thenReturn(roleList);
        
        Iterable<Role> result = roleService.listRoleAktif("198001012010011001");
        
        assertThat(result).containsExactly(testRole);
        verify(roleRepository).findByNip("198001012010011001");
    }
    
    @Test
    void detailRole_WhenRoleExists_ShouldReturnRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        
        Role result = roleService.detailRole(1L);
        
        assertThat(result).isEqualTo(testRole);
        verify(roleRepository).findById(1L);
    }
    
    @Test
    void detailRole_WhenRoleNotExists_ShouldThrowException() {
        when(roleRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.detailRole(3L))
                .isInstanceOf(RoleNotFoundException.class);
        verify(roleRepository).findById(3L);
    }
    
    @Test
    void tambahRole_WhenRoleValid_ShouldSaveAndReturnRole() {
        Role newRole = new Role(
                null,
                "User",
                "198001012010011001",
                "3",
                "Y",
                null,
                null
        );

        when(pegawaiRepository.existsByNip(newRole.nip())).thenReturn(true);
        when(roleRepository.save(any(Role.class))).thenReturn(
                new Role(
                        2L,
                        newRole.namaRole(),
                        newRole.nip(),
                        newRole.levelRole(),
                        newRole.isActive(),
                        Instant.now(),
                        Instant.now()
                )
        );

        Role result = roleService.tambahRole(newRole);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.namaRole()).isEqualTo("User");
        assertThat(result.nip()).isEqualTo("198001012010011001");
        assertThat(result.levelRole()).isEqualTo("3");
        assertThat(result.isActive()).isEqualTo("Y");
        verify(pegawaiRepository).existsByNip(newRole.nip());
        verify(roleRepository).save(newRole);
    }
    
    @Test
    void tambahRole_WhenNipNotExists_ShouldThrowException() {
        Role newRole = new Role(
                null,
                "User",
                "200601012010012001",
                "3",
                "Y",
                null,
                null
        );

        when(pegawaiRepository.existsByNip(newRole.nip())).thenReturn(false);

        assertThatThrownBy(() -> roleService.tambahRole(newRole))
                .isInstanceOf(PegawaiNotFoundException.class)
                .hasMessageContaining(newRole.nip());
        verify(pegawaiRepository).existsByNip(newRole.nip());
        verify(pegawaiRepository, never()).save(any());
    }
    
    @Test
    void ubahRole_WhenRoleExistsAndValid_ShouldUpdateAndReturnRole() {
        Long id = 1L;
        Role updatedRole = new Role(
                id,
                "Guest",
                "198001012010011001",
                "4",
                "Y",
                testRole.createdDate(),
                Instant.now()
        );

        when(roleRepository.existsById(id)).thenReturn(true);
        when(pegawaiRepository.existsByNip(updatedRole.nip())).thenReturn(true);
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        Role result = roleService.ubahRole(id, updatedRole);

        assertThat(result).isEqualTo(updatedRole);
        verify(roleRepository).existsById(id);
        verify(pegawaiRepository).existsByNip(updatedRole.nip());
        verify(roleRepository).save(updatedRole);
    }
    
    @Test
    void ubahRole_WhenRoleIdNotExists_ShouldThrowException() {
        Long id = 3L;
        when(roleRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> roleService.ubahRole(id, testRole))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(roleRepository).existsById(id);
        verify(roleRepository, never()).save(any());
    }
    
    @Test
    void ubahRole_WhenNipNotExists_ShouldThrowException() {
        Long id = 1L;
        Role updatedRole = new Role(
                id,
                "Guest",
                "200601012010012001",
                "4",
                "Y",
                testRole.createdDate(),
                Instant.now()
        );

        when(roleRepository.existsById(id)).thenReturn(true);
        when(pegawaiRepository.existsByNip(updatedRole.nip())).thenReturn(false);

        assertThatThrownBy(() -> roleService.ubahRole(id, updatedRole))
                .isInstanceOf(PegawaiNotFoundException.class)
                .hasMessageContaining(updatedRole.nip());
        verify(roleRepository).existsById(id);
        verify(pegawaiRepository).existsByNip(updatedRole.nip());
        verify(roleRepository, never()).save(any());
    }
    
    @Test
    void hapusRole_WhenRoleExists_ShouldDeleteRole() {
        Long id = 1L;
        when(roleRepository.existsById(id)).thenReturn(true);

        roleService.hapusRole(id);

        verify(roleRepository).existsById(id);
        verify(roleRepository).deleteById(id);
    }
    
    @Test
    void hapusRole_WhenIdRoleNotExists_ShouldThrowException() {
        Long id = 3L;
        when(roleRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> roleService.hapusRole(id))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining(id.toString());
        verify(roleRepository).existsById(id);
        verify(roleRepository, never()).deleteById(anyLong());
    }
}
