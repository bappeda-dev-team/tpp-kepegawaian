package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.pajak.domain;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;

@Service
public class PajakService {
    private final PajakRepository pajakRepository;
    private final PegawaiRepository pegawaiRepository;
    
    public PajakService(PajakRepository pajakRepository, PegawaiRepository pegawaiRepository) {
        this.pajakRepository = pajakRepository;
        this.pegawaiRepository = pegawaiRepository;
    }

    public Pajak detailPajak(String nip) {
        return pajakRepository.findByNip(nip)
                .orElseThrow(() -> new PajakNotFoundException(nip));
    }

    public Pajak ubahPajak(String nip, Pajak pajak) {
        if (!pegawaiRepository.existsByNip(nip)) {
            throw new PegawaiNotFoundException(nip);
        }
        
        if (!pajakRepository.existsByNip(nip)) {
            throw new PajakNotFoundException(nip);
        }

        return pajakRepository.save(pajak);
    }

    public Pajak tambahPajak(Pajak pajak) {
        if (!pegawaiRepository.existsByNip(pajak.nip())) {
            throw new PegawaiNotFoundException(pajak.nip());
        }
        
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
