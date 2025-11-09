package cc.kertaskerja.tppkepegawaian.role.domain;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PegawaiRepository pegawaiRepository;
    private final RoleCacheService roleCacheService;
    
    public RoleService(RoleRepository roleRepository, PegawaiRepository pegawaiRepository, RoleCacheService roleCacheService) {
        this.roleRepository = roleRepository;
        this.pegawaiRepository = pegawaiRepository;
        this.roleCacheService = roleCacheService;
    }
    
    public Iterable<Role> listRoleAktif(String nip) {
	    return roleRepository.findByNip(nip);
    }
    
    public Role detailRole(Long id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new RoleNotFoundException(id));
    }

    public Role ubahRole(Long id, Role role) {
        if (!roleRepository.existsById(id)) {
            throw new RoleNotFoundException(id);
        }

        if (!pegawaiRepository.existsByNip(role.nip())) {
            throw new PegawaiNotFoundException(role.nip());
        }
        
        Role updated = roleRepository.save(role);

        return updated;
    }

    public Role tambahRole(Role role) {
        if (!pegawaiRepository.existsByNip(role.nip())) {
            throw new PegawaiNotFoundException(role.nip());
        }

        return roleRepository.save(role);
    }

    public void hapusRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RoleNotFoundException(id);
        }

        roleRepository.deleteById(id);
    }
}
