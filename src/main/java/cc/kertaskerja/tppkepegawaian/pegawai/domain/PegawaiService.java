package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import cc.kertaskerja.tppkepegawaian.role.domain.Role;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.role.domain.NamaRoleNotFoundException;
import cc.kertaskerja.tppkepegawaian.role.domain.RoleRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PegawaiService {
    private final PegawaiRepository pegawaiRepository;
    private final OpdRepository opdRepository;
    private final RoleRepository roleRepository;

    public PegawaiService(PegawaiRepository pegawaiRepository, OpdRepository opdRepository, RoleRepository roleRepository) {
	this.pegawaiRepository = pegawaiRepository;
	this.opdRepository = opdRepository;
	this.roleRepository = roleRepository;
    }
    
    public List<PegawaiWithRoles> listAllPegawaiByKodeOpd(String kodeOpd) {
        if (!opdRepository.existsByKodeOpd(kodeOpd)) {
            throw new OpdNotFoundException(kodeOpd);
        }
        Iterable<Pegawai> pegawais = pegawaiRepository.findByKodeOpd(kodeOpd);

        return StreamSupport.stream(pegawais.spliterator(), false)
                .map(p -> PegawaiWithRoles.of(p, getRolesByNip(p.nip())))
                .toList();
    }

    @Cacheable(value = "rolesByNip", key = "#nip")
    public Set<Role> getRolesByNip(String nip) {
        Iterable<Role> roles = roleRepository.findByNip(nip);
        return StreamSupport.stream(roles.spliterator(), false)
                        .collect(Collectors.toSet());
    }

    public Iterable<Pegawai> listAllPegawaiByRole(String namaRole) {
        return pegawaiRepository.findByNamaRole(namaRole);
    }

    public Pegawai detailPegawai(String nip) {
	return pegawaiRepository.findByNip(nip)
		.orElseThrow(() -> new PegawaiNotFoundException(nip));
    }

    public Pegawai ubahPegawai(String nip, Pegawai pegawai) {
	if (!pegawaiRepository.existsByNip(nip)) {
	    throw new PegawaiNotFoundException(nip);
	}

	if (!opdRepository.existsByKodeOpd(pegawai.kodeOpd())) {
        throw new OpdNotFoundException(pegawai.kodeOpd());
    }
	
	if (!roleRepository.existsByNamaRole(pegawai.namaRole())) {
        throw new NamaRoleNotFoundException(pegawai.namaRole());
    }

	return pegawaiRepository.save(pegawai);
    }

    public Pegawai tambahPegawai(Pegawai pegawai) {
	if (pegawaiRepository.existsByNip(pegawai.nip())) {
	    throw new PegawaiSudahAdaException(pegawai.nip());
	}

	if (!opdRepository.existsByKodeOpd(pegawai.kodeOpd())) {
        throw new OpdNotFoundException(pegawai.kodeOpd());
    }

	return pegawaiRepository.save(pegawai);
    }

    public void hapusPegawai(String nip) {
	if (!pegawaiRepository.existsByNip(nip)) {
	    throw new PegawaiNotFoundException(nip);
	}

	pegawaiRepository.deleteByNip(nip);
    }
}
