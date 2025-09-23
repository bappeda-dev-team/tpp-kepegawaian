package cc.kertaskerja.tppkepegawaian.role.web;

import cc.kertaskerja.tppkepegawaian.role.domain.Role;
import cc.kertaskerja.tppkepegawaian.role.domain.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("role")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Get role by ID
     * @param id role ID
     * @return Role object
     * url: /role/{id}
     */
    @GetMapping("detail/{id}")
    public Role getById(@PathVariable("id") Long id) {
        return roleService.detailRole(id);
    }

    /**
     * Update role by ID
     * @param id role ID
     * @param request role update request
     * @return updated Jabatan object
     * url: /role/{id}
     */
    @PutMapping("update/{id}")
    public Role put(@PathVariable("id") Long id, @Valid @RequestBody RoleRequest request) {
        // Ambil data role yang sudah dibuat
        Role existingRole = roleService.detailRole(id);

        Role role = new Role(
                id,
                request.namaRole(),
                request.nip(),
                request.levelRole(),
                request.isActive(),
                existingRole.createdDate(),
                null
        );

        return roleService.ubahRole(id, role);
    }

    /**
     * Create new role
     * @param request role creation request
     * @return created Role object with location header
     * url: /role
     */
    @PostMapping
    public ResponseEntity<Role> post(@Valid @RequestBody RoleRequest request) {
        Role role = Role.of(
                request.namaRole(),
                request.nip(),
                request.levelRole(),
                request.isActive()
        );
        Role saved = roleService.tambahRole(role);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    /**
     * Delete role by ID
     * @param id role ID
     * url: /role/{id}
     */
    @DeleteMapping("delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        roleService.hapusRole(id);
    }
}
