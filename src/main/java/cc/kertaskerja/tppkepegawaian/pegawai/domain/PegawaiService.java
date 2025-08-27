package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.role.domain.NamaRoleNotFoundException;
import cc.kertaskerja.tppkepegawaian.role.domain.RoleRepository;

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
    
    public Iterable<Pegawai> listAllPegawaiByKodeOpd(String kodeOpd) {
        if (!opdRepository.existsByKodeOpd(kodeOpd)) {
            throw new OpdNotFoundException(kodeOpd);
        }
        
        return pegawaiRepository.findByKodeOpd(kodeOpd);
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
