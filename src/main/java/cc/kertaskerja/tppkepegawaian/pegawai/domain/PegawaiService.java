package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;

@Service
public class PegawaiService {
    private final PegawaiRepository pegawaiRepository;
    private final OpdRepository opdRepository;

    public PegawaiService(PegawaiRepository pegawaiRepository, OpdRepository opdRepository) {
	this.pegawaiRepository = pegawaiRepository;
	this.opdRepository = opdRepository;
    }

    public Iterable<Pegawai> listPegawaiAktif(String kodeOpd) {
	return pegawaiRepository.findByKodeOpd(kodeOpd);
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
	    throw new PegawaiNotFoundException(pegawai.kodeOpd());
	}

	return pegawaiRepository.save(pegawai);
    }

    public Pegawai tambahPegawai(Pegawai pegawai) {
	if (pegawaiRepository.existsByNip(pegawai.nip())) {
	    throw new PegawaiSudahAdaException(pegawai.nip());
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
