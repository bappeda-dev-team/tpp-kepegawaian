package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain.exception.BpjsNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain.exception.BpjsSudahAdaException;
import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;

@Service
public class BpjsService {
    private final BpjsRepository bpjsRepository;
    private final PegawaiRepository pegawaiRepository;

    public BpjsService(BpjsRepository bpjsRepository, PegawaiRepository pegawaiRepository) {
        this.bpjsRepository = bpjsRepository;
        this.pegawaiRepository = pegawaiRepository;
    }

    public Bpjs detailBpjs(String nip) {
        return bpjsRepository.findByNip(nip)
                .orElseThrow(() -> new BpjsNotFoundException(nip));
    }

    public Bpjs ubahBpjs(String nip, Bpjs bpjs) {
        if (!pegawaiRepository.existsByNip(nip)) {
            throw new PegawaiNotFoundException(nip);
        }
               
        if (!bpjsRepository.existsByNip(nip)) {
            throw new BpjsNotFoundException(nip);
        }

        return bpjsRepository.save(bpjs);
    }

    public Bpjs tambahBpjs(Bpjs bpjs) {
        if (!pegawaiRepository.existsByNip(bpjs.nip())) {
            throw new PegawaiNotFoundException(bpjs.nip());
        }
        
        if (bpjsRepository.existsByNip(bpjs.nip())) {
            throw new BpjsSudahAdaException(bpjs.nip());
        }

        return bpjsRepository.save(bpjs);
    }

    public void hapusPajak(String nip) {
        if (!bpjsRepository.existsByNip(nip)) {
            throw new BpjsNotFoundException(nip);
        }

        bpjsRepository.deleteByNip(nip);
    }
}
