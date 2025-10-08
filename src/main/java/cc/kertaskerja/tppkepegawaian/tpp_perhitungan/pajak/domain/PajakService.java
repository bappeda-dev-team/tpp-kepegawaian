package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.domain;

import org.springframework.stereotype.Service;

@Service
public class PajakService {
    private final PajakRepository pajakRepository;
    
    public PajakService(PajakRepository pajakRepository) {
        this.pajakRepository = pajakRepository;
    }

    public Pajak detailPajak(String nip) {
        return pajakRepository.findByNip(nip)
                .orElseThrow(() -> new PajakNotFoundException(nip));
    }

    public Pajak ubahPajak(String nip, Pajak pajak) {
        if (!pajakRepository.existsByNip(nip)) {
            throw new PajakNotFoundException(nip);
        }

        return pajakRepository.save(pajak);
    }

    public Pajak tambahPajak(Pajak pajak) {
        if (pajakRepository.existsByNip(pajak.nip())) {
            throw new PajakSudahAdaException(pajak.nip());
        }

        return pajakRepository.save(pajak);
    }

    public void hapusPajak(String nip) {
        if (!pajakRepository.existsByNip(nip)) {
            throw new PajakNotFoundException(nip);
        }

        pajakRepository.deleteByNip(nip);
    }
}
