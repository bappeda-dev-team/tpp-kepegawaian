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
	
	public Iterable<Jabatan> listJabatanByKodeOpd(String kodeOpd) {
        return jabatanRepository.findByKodeOpd(kodeOpd);
    }
	
	public Jabatan detailJabatan(Long id) {
        return jabatanRepository.findById(id)
                .orElseThrow(() -> new JabatanNotFoundException(id));
    }
	
	public Jabatan ubahJabatan(Long id, Jabatan jabatan) {
		if (!jabatanRepository.existsById(id)) {
			throw new JabatanNotFoundException(id);
		}

        if (!opdRepository.existsByKodeOpd(jabatan.kodeOpd())) {
            throw new OpdNotFoundException(jabatan.kodeOpd());
        }
		return jabatanRepository.save(jabatan);
	}
	
	public Jabatan tambahJabatan(Jabatan jabatan) {

        if (!opdRepository.existsByKodeOpd(jabatan.kodeOpd())) {
            throw new OpdNotFoundException(jabatan.kodeOpd());
        }
        return jabatanRepository.save(jabatan);
    }
	
	public void hapusJabatan(Long id) {
        if (!jabatanRepository.existsById(id)) {
            throw new JabatanNotFoundException(id);
        }

        jabatanRepository.deleteById(id);
    }
}
