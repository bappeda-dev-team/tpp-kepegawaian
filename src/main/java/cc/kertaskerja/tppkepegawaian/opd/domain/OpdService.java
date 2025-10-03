package cc.kertaskerja.tppkepegawaian.opd.domain;

import org.springframework.stereotype.Service;

@Service
public class OpdService {
	private final OpdRepository opdRepository;
	
	public OpdService(OpdRepository opdRepository) {
		this.opdRepository = opdRepository;
	}
	
	public Opd detailOpd(String kodeOpd) {
		return opdRepository.findByKodeOpd(kodeOpd)
				.orElseThrow(() -> new OpdNotFoundException(kodeOpd));
	}
	
	public Iterable<Opd> listAllOpd() {
	    return opdRepository.findAll();
	}

	public Iterable<Opd> getDataMasterOpd() {
	    return opdRepository.findAll();
	}
	
	public Opd tambahOpd(Opd opd) {
        if (opdRepository.existsByKodeOpd(opd.kodeOpd())) {
            throw new OpdSudahAdaException(opd.kodeOpd());
        }
        return opdRepository.save(opd);
    }
	
	public Opd ubahOpd(String kodeOpd, Opd opd) {
		if (!opdRepository.existsByKodeOpd(kodeOpd)) {
			throw new OpdNotFoundException(kodeOpd);
		}
		return opdRepository.save(opd);
	}
	
	public void hapusOpd(String kodeOpd) {
	if (!opdRepository.existsByKodeOpd(kodeOpd)) {
	    throw new OpdNotFoundException(kodeOpd);
	} 
	   
        opdRepository.deleteByKodeOpd(kodeOpd);
    }
}
