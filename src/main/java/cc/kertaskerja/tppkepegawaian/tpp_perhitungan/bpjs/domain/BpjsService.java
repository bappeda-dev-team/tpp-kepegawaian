package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain;

import org.springframework.stereotype.Service;

@Service
public class BpjsService {
    private final BpjsRepository bpjsRepository;

    public BpjsService(BpjsRepository bpjsRepository) {
        this.bpjsRepository = bpjsRepository;
    }

    public Bpjs detailBpjs(String nip) {
        return bpjsRepository.findByNip(nip)
                .orElseThrow(() -> new BpjsNotFoundException(nip));
    }

    public Bpjs ubahBpjs(String nip, Bpjs bpjs) {
        if (!bpjsRepository.existsByNip(nip)) {
            throw new BpjsNotFoundException(nip);
        }

        return bpjsRepository.save(bpjs);
    }

    public Bpjs tambahBpjs(Bpjs bpjs) {
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
