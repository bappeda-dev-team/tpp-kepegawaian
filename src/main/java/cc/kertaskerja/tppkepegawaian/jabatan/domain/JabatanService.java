package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;

@Service
public class JabatanService {
	private final JabatanRepository jabatanRepository;
	private final OpdRepository opdRepository;
	
	public JabatanService(JabatanRepository jabatanRepository, OpdRepository opdRepository) {
        this.jabatanRepository = jabatanRepository;
        this.opdRepository = opdRepository;
    }
	
	public Iterable<Jabatan> listJabatanAktif(String kodeOpd) {
        return jabatanRepository.findByKodeOpd(kodeOpd);
    }
	
	public Jabatan detailJabatan(String nip) {
        return jabatanRepository.findByNip(nip)
                .orElseThrow(() -> new JabatanNotFoundException(nip));
    }
	
	public Jabatan ubahJabatan(String nip, Jabatan jabatan) {
		if (!jabatanRepository.existsByNip(nip)) {
			throw new OpdNotFoundException(nip);
		}
		
		if (!opdRepository.existsByKodeOpd(jabatan.kodeOpd())) {
            throw new OpdNotFoundException(jabatan.kodeOpd());
        }
		return jabatanRepository.save(jabatan);
	}
	
	public Jabatan tambahJabatan(Jabatan jabatan) {
        if (jabatanRepository.existsByNip(jabatan.nip())) {
            throw new JabatanSudahAdaException(jabatan.nip());
        }
        
        if (!opdRepository.existsByKodeOpd(jabatan.kodeOpd())) {
            throw new OpdNotFoundException(jabatan.kodeOpd());
        }
        return jabatanRepository.save(jabatan);
    }
	
	public void hapusJabatan(String nip) {
        jabatanRepository.deleteByNip(nip);
    }
}
