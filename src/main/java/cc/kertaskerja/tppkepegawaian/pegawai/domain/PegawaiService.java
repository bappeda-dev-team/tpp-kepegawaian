package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JabatanRepository;
import cc.kertaskerja.tppkepegawaian.role.domain.Role;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanAndRolesResponse;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanResponse;
import cc.kertaskerja.tppkepegawaian.role.domain.NamaRoleNotFoundException;
import cc.kertaskerja.tppkepegawaian.role.domain.RoleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PegawaiService {
    private final PegawaiRepository pegawaiRepository;
    private final OpdRepository opdRepository;
    private final RoleRepository roleRepository;
    private final JabatanRepository jabatanRepository;
    private final TppRepository tppRepository;
    private final TppPerhitunganService tppPerhitunganService;

    public PegawaiService(PegawaiRepository pegawaiRepository, OpdRepository opdRepository, RoleRepository roleRepository, JabatanRepository jabatanRepository, TppRepository tppRepository, TppPerhitunganService tppPerhitunganService) {
        this.pegawaiRepository = pegawaiRepository;
        this.opdRepository = opdRepository;
        this.roleRepository = roleRepository;
        this.jabatanRepository = jabatanRepository;
        this.tppRepository = tppRepository;
        this.tppPerhitunganService = tppPerhitunganService;
    }

    public List<PegawaiWithRoles> listAllPegawaiByKodeOpd(String kodeOpd) {
        if (!opdRepository.existsByKodeOpd(kodeOpd)) {
            throw new OpdNotFoundException(kodeOpd);
        }
        Iterable<Pegawai> pegawais = pegawaiRepository.findByKodeOpd(kodeOpd);

        return StreamSupport.stream(pegawais.spliterator(), false)
            .map(p -> PegawaiWithRoles.of(p, getRolesByNip(p.nip())))
            .toList();
    }

    public List<PegawaiWithJabatanAndRolesResponse> listAllPegawaiWithJabatanByKodeOpd(String kodeOpd) {
        if (!opdRepository.existsByKodeOpd(kodeOpd)) {
            throw new OpdNotFoundException(kodeOpd);

        }
        Iterable<Pegawai> pegawais = pegawaiRepository.findByKodeOpd(kodeOpd);

        return StreamSupport.stream(pegawais.spliterator(), false)
            .map(p -> {
                Set<Role> roles = getRolesByNip(p.nip());
                Jabatan jabatan = jabatanRepository.findByNip(p.nip()).orElse(null);
                return PegawaiWithJabatanAndRolesResponse.of(
                    p.id(),
                    p.namaPegawai(),
                    p.nip(),
                    p.kodeOpd(),
                    roles,
                    jabatan
                );
            })
            .toList();
    }

    @Cacheable(value = "rolesByNip", key = "#nip")
    public Set<Role> getRolesByNip(String nip) {
        Iterable<Role> roles = roleRepository.findByNip(nip);
        return StreamSupport.stream(roles.spliterator(), false)
            .collect(Collectors.toSet());
    }

    public Iterable<Pegawai> listAllPegawaiByRole(String namaRole) {
        return pegawaiRepository.findByNamaRole(namaRole);
    }

    public Pegawai detailPegawai(String nip) {
        return pegawaiRepository.findByNip(nip)
            .orElseThrow(() -> new PegawaiNotFoundException(nip));
    }

    public PegawaiWithJabatanResponse detailPegawaiWithJabatan(String nip) {
        Pegawai pegawai = detailPegawai(nip);
        Jabatan jabatan = jabatanRepository.findByNip(nip).orElse(null);

        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        Iterable<Tpp> currentTppRecords = tppRepository.findByNipAndBulanAndTahun(nip, currentMonth, currentYear);
        Optional<Tpp> tpp = StreamSupport.stream(currentTppRecords.spliterator(), false).findFirst();

        if (tpp.isEmpty()) {
            Iterable<Tpp> allTppRecords = tppRepository.findByNip(nip);
            tpp = StreamSupport.stream(allTppRecords.spliterator(), false).findFirst();
        }

        List<TppPerhitungan> perhitunganList = new ArrayList<>();
        if (tpp.isPresent()) {
            Tpp foundTpp = tpp.get();

            perhitunganList = StreamSupport.stream(
                    tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, foundTpp.bulan(), foundTpp.tahun()).spliterator(), false)
                    .collect(Collectors.toList());
        }

        return PegawaiWithJabatanResponse.from(pegawai, jabatan, tpp, perhitunganList);
    }

    public Pegawai ubahPegawai(String nip, Pegawai pegawai) {
        if (!pegawaiRepository.existsByNip(nip)) {
            throw new PegawaiNotFoundException(nip);
        }

        if (!opdRepository.existsByKodeOpd(pegawai.kodeOpd())) {
            throw new OpdNotFoundException(pegawai.kodeOpd());
        }

        assert pegawai.namaRole() != null;
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