package cc.kertaskerja.tppkepegawaian.jabatan.domain;

import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.web.PegawaiWithJabatanListResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.web.MasterJabatanByOpdResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class JabatanService {

    private final JabatanRepository jabatanRepository;
    private final OpdRepository opdRepository;
    private final PegawaiRepository pegawaiRepository;

	public JabatanService(JabatanRepository jabatanRepository, OpdRepository opdRepository, PegawaiRepository pegawaiRepository) {
	    this.jabatanRepository = jabatanRepository;
        this.opdRepository = opdRepository;
        this.pegawaiRepository = pegawaiRepository;
    }

   public Iterable<Jabatan> listJabatanByKodeOpd(String kodeOpd) {
        return jabatanRepository.findByKodeOpd(kodeOpd);
   }

   public List<JabatanWithPegawaiResponse> listJabatanByNipWithPegawai(String nip) {
        Iterable<Jabatan> jabatans = jabatanRepository.findAllByNip(nip);
        List<JabatanWithPegawaiResponse> responses = new ArrayList<>();

        for (Jabatan jabatan : jabatans) {
            Pegawai pegawai = pegawaiRepository.findByNip(jabatan.nip())
                .orElse(null); // return null jika pegawai tidak ditemukan

            String namaPegawai = pegawai != null ? pegawai.namaPegawai() : null;

            responses.add(new JabatanWithPegawaiResponse(
                jabatan.id(),
                jabatan.nip(),
                namaPegawai,
                jabatan.namaJabatan(),
                jabatan.kodeOpd(),
                jabatan.statusJabatan(),
                jabatan.jenisJabatan(),
                jabatan.eselon(),
                jabatan.pangkat(),
                jabatan.golongan(),
                jabatan.tanggalMulai(),
                jabatan.tanggalAkhir()
            ));
        }
        return responses;
   }

   public List<JabatanWithPegawaiResponse> listJabatanByKodeOpdWithPegawai(String kodeOpd) {
        Iterable<Jabatan> jabatans = jabatanRepository.findByKodeOpd(kodeOpd);
        List<JabatanWithPegawaiResponse> responses = new ArrayList<>();
        
        for (Jabatan jabatan : jabatans) {
            Pegawai pegawai = pegawaiRepository.findByNip(jabatan.nip())
                .orElse(null); // return null jika pegawai tidak ditemukan
            
            String namaPegawai = pegawai != null ? pegawai.namaPegawai() : null;
            
            responses.add(new JabatanWithPegawaiResponse(
                jabatan.id(),
                jabatan.nip(),
                namaPegawai,
                jabatan.namaJabatan(),
                jabatan.kodeOpd(),
                jabatan.statusJabatan(),
                jabatan.jenisJabatan(),
                jabatan.eselon(),
                jabatan.pangkat(),
                jabatan.golongan(),
                jabatan.tanggalMulai(),
                jabatan.tanggalAkhir()
            ));
        }
        
        return responses;
   }

   public List<PegawaiWithJabatanListResponse> listPegawaiWithJabatanByKodeOpd(String kodeOpd) {
        Iterable<Jabatan> jabatans = jabatanRepository.findByKodeOpd(kodeOpd);

        // Grup jabatan berdasarkan nip
        Map<String, List<Jabatan>> groupedByNip = StreamSupport.stream(jabatans.spliterator(), false)
            .collect(Collectors.groupingBy(Jabatan::nip));

        List<PegawaiWithJabatanListResponse> responses = new ArrayList<>();

        for (Map.Entry<String, List<Jabatan>> entry : groupedByNip.entrySet()) {
            String nip = entry.getKey();
            List<Jabatan> jabatanList = entry.getValue();
            Pegawai pegawai = pegawaiRepository.findByNip(nip).orElse(null);
            String namaPegawai = pegawai != null ? pegawai.namaPegawai() : null;

            Jabatan firstJabatan = jabatanList.get(0);

            List<PegawaiWithJabatanListResponse.JabatanDetail> jabatanDetails = jabatanList.stream()
                .map(jabatan -> new PegawaiWithJabatanListResponse.JabatanDetail(
                    jabatan.namaJabatan(),
                    jabatan.statusJabatan(),
                    jabatan.jenisJabatan(),
                    jabatan.eselon(),
                    jabatan.pangkat(),
                    jabatan.golongan(),
                    jabatan.tanggalMulai(),
                    jabatan.tanggalAkhir()
                ))
                .collect(Collectors.toList());

            responses.add(new PegawaiWithJabatanListResponse(
                firstJabatan.id(),
                nip,
                namaPegawai,
                firstJabatan.kodeOpd(),
                jabatanDetails
            ));
        }

        // Urutkan response berdasarkan prioritas status jabatan
        responses.sort((p1, p2) -> {
            StatusJabatan p1HighestStatus = getHighestPriorityStatus(p1.jabatan());
            StatusJabatan p2HighestStatus = getHighestPriorityStatus(p2.jabatan());

            return compareStatusPriority(p1HighestStatus, p2HighestStatus);
        });

        return responses;
   }

   public List<MasterJabatanByOpdResponse> listMasterJabatanByKodeOpd(String kodeOpd) {
        Iterable<Jabatan> jabatans = jabatanRepository.findByKodeOpd(kodeOpd);

        // Grup jabatan berdasarkan nip
        Map<String, List<Jabatan>> groupedByNip = StreamSupport.stream(jabatans.spliterator(), false)
            .collect(Collectors.groupingBy(Jabatan::nip));

        // Get OPD information
        String namaOpd = opdRepository.findByKodeOpd(kodeOpd)
            .map(opd -> opd.namaOpd())
            .orElse(null);

        List<MasterJabatanByOpdResponse> responses = new ArrayList<>();

        for (Map.Entry<String, List<Jabatan>> entry : groupedByNip.entrySet()) {
            String nip = entry.getKey();
            List<Jabatan> jabatanList = entry.getValue();
            Pegawai pegawai = pegawaiRepository.findByNip(nip).orElse(null);
            String namaPegawai = pegawai != null ? pegawai.namaPegawai() : null;

            List<MasterJabatanByOpdResponse.JabatanDetail> jabatanDetails = jabatanList.stream()
                .map(jabatan -> new MasterJabatanByOpdResponse.JabatanDetail(
                    jabatan.id(),
                    jabatan.namaJabatan(),
                    jabatan.statusJabatan(),
                    jabatan.jenisJabatan(),
                    jabatan.eselon(),
                    jabatan.pangkat(),
                    jabatan.golongan(),
                    jabatan.tanggalMulai(),
                    jabatan.tanggalAkhir()
                ))
                .collect(Collectors.toList());

            responses.add(new MasterJabatanByOpdResponse(
                kodeOpd,
                namaOpd,
                nip,
                namaPegawai,
                jabatanDetails
            ));
        }

        // Urutkan response berdasarkan prioritas status jabatan
        responses.sort((p1, p2) -> {
            StatusJabatan p1HighestStatus = getHighestPriorityStatusNew(p1.jabatan());
            StatusJabatan p2HighestStatus = getHighestPriorityStatusNew(p2.jabatan());

            return compareStatusPriority(p1HighestStatus, p2HighestStatus);
        });

        return responses;
   }

   private StatusJabatan getHighestPriorityStatusNew(List<MasterJabatanByOpdResponse.JabatanDetail> jabatanDetails) {
        return jabatanDetails.stream()
            .map(MasterJabatanByOpdResponse.JabatanDetail::statusJabatan)
            .min(this::compareStatusPriority)
            .orElse(StatusJabatan.UTAMA);
   }

   private StatusJabatan getHighestPriorityStatus(List<PegawaiWithJabatanListResponse.JabatanDetail> jabatanDetails) {
        return jabatanDetails.stream()
            .map(PegawaiWithJabatanListResponse.JabatanDetail::statusJabatan)
            .min(this::compareStatusPriority)
            .orElse(StatusJabatan.UTAMA);
   }

   private int compareStatusPriority(StatusJabatan s1, StatusJabatan s2) {
        if (s1 == StatusJabatan.UTAMA && s2 != StatusJabatan.UTAMA) {
            return -1;
        } else if (s1 != StatusJabatan.UTAMA && s2 == StatusJabatan.UTAMA) {
            return 1;
        } else if (s1 == StatusJabatan.PLT_UTAMA && s2 != StatusJabatan.PLT_UTAMA) {
            return -1;
        } else if (s1 != StatusJabatan.PLT_UTAMA && s2 == StatusJabatan.PLT_UTAMA) {
            return 1;
        } else {
            return 0;
        }
   }

   // Periksa apakah user memiliki salah satu status jabatan
   private boolean isPltJabatan(StatusJabatan statusJabatan) {
       return statusJabatan == StatusJabatan.PLT_UTAMA || statusJabatan == StatusJabatan.PLT_SEMENTARA;
   }

   // Periksa apakah pegawai sudah memiliki jabatan PLT saat tambah data
   private boolean hasPltJabatan(String nip) {
       Iterable<Jabatan> existingJabatans = jabatanRepository.findAllByNip(nip);
       for (Jabatan jabatan : existingJabatans) {
           if (isPltJabatan(jabatan.statusJabatan())) {
               return true;
           }
       }
       return false;
   }

   // Periksa apakah pegawai sudah memiliki jabatan PLT saat update
   private boolean hasPltJabatanExcludingCurrent(String nip, Long currentJabatanId) {
       Iterable<Jabatan> existingJabatans = jabatanRepository.findAllByNip(nip);
       for (Jabatan jabatan : existingJabatans) {
           if (isPltJabatan(jabatan.statusJabatan()) && !jabatan.id().equals(currentJabatanId)) {
               return true;
           }
       }
       return false;
   }

   // Periksa apakah pegawai sudah memiliki jabatan UTAMA saat update
   private boolean hasUtamaJabatanExcludingCurrent(String nip, Long currentJabatanId) {
       Iterable<Jabatan> existingJabatans = jabatanRepository.findAllByNip(nip);
       for (Jabatan jabatan : existingJabatans) {
           if (jabatan.statusJabatan() == StatusJabatan.UTAMA && !jabatan.id().equals(currentJabatanId)) {
               return true;
           }
       }
       return false;
   }

   // Periksa apakah pegawai sudah memiliki jabatan lain (excluding current)
   private boolean hasOtherJabatanExcludingCurrent(String nip, Long currentJabatanId) {
       Iterable<Jabatan> existingJabatans = jabatanRepository.findAllByNip(nip);
       for (Jabatan jabatan : existingJabatans) {
           if (!jabatan.id().equals(currentJabatanId)) {
               return true;
           }
       }
       return false;
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

       if (!pegawaiRepository.existsByNip(jabatan.nip())) {
           throw new PegawaiNotFoundException(jabatan.nip());
       }
       Jabatan currentJabatan = jabatanRepository.findById(id).orElse(null);
       if (currentJabatan == null) {
           throw new JabatanNotFoundException(id);
       }

        return jabatanRepository.save(jabatan);
	}

    public Jabatan tambahJabatan(Jabatan jabatan) {

        if (!opdRepository.existsByKodeOpd(jabatan.kodeOpd())) {
            throw new OpdNotFoundException(jabatan.kodeOpd());
        }

        if (!pegawaiRepository.existsByNip(jabatan.nip())) {
            throw new PegawaiNotFoundException(jabatan.nip());
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
