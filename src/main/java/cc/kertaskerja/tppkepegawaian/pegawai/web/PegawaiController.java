package cc.kertaskerja.tppkepegawaian.pegawai.web;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiService;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiWithRoles;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanAndRolesResponse;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanResponse;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("pegawai")
@Tag(name = "Pegawai", description = "Manajemen data pegawai")
public class PegawaiController {
    private final PegawaiService pegawaiService;

    public PegawaiController(PegawaiService pegawaiService) {
        this.pegawaiService = pegawaiService;
    }

    /**
     * Get pegawai by nip
     * @param nip
     * nip asn
     * @return pegawai object with jabatan information
     * url: /pegawai/detail/{nip}
     */
    @GetMapping("detail/{nip}")
    public PegawaiWithJabatanResponse getByNip(@PathVariable("nip") String nip) {
        return pegawaiService.detailPegawaiWithJabatan(nip);
    }

    /**
     * Get master pegawai by kodeOpd
     * @param kodeOpd
     * kodeOpd: OPD unique code 1.23.4.56.7.89.1.0000
     * @return list of all pegawai in the OPD with jabatan information
     * url: /pegawai/detail/master/opd/{kodeOpd}
     */
    @GetMapping("detail/master/opd/{kodeOpd}")
    public List<PegawaiWithJabatanAndRolesResponse> getMasterByKodeOpd(@PathVariable("kodeOpd") String kodeOpd) {
        return pegawaiService.listAllPegawaiWithJabatanByKodeOpd(kodeOpd);
    }

    /**
     * Get all pegawai by role name
     * @param namaRole role name
     * @return list of all pegawai with the specified role
     * url: /pegawai/role/{namaRole}
     */
    @GetMapping("role/{namaRole}")
    public Iterable<Pegawai> getAllPegawaiByRole(@PathVariable("namaRole") String namaRole) {
        return pegawaiService.listAllPegawaiByRole(namaRole);
    }

    /**
     * Get all pegawai
     * @return list of all pegawai
     * url: /pegawai
     */
    @GetMapping("detail/findall")
    public Iterable<Pegawai> getAllPegawai() {
        return pegawaiService.listAllPegawai();
    }

    /**
     * Update pegawai by NIP
     * @param nip pegawai NIP
     * @param request pegawai update request
     * @return updated Pegawai object
     * url: /pegawai/update/{nip}
     */
    @PutMapping("update/{nip}")
    public Pegawai put(@PathVariable("nip") String nip, @Valid @RequestBody PegawaiRequest request) {
        // Ambil data pegawai yang sudah dibuat
        Pegawai existingPegawai = pegawaiService.detailPegawai(nip);

        Pegawai pegawai = new Pegawai(
            request.pegawaiId(),
            request.namaPegawai(),
            nip,
            request.kodeOpd(),
            request.namaRole(),
            request.statusPegawai(),
            request.passwordHash(),
            // saat update data ambil data createdDate dari pegawai yang sudah dibuat
            existingPegawai.createdDate(),
            null
        );
        return pegawaiService.ubahPegawai(nip, pegawai);
    }

    /**
     * Create new pegawai
     * @param request pegawai creation request
     * @return created Pegawai object with location header
     * url: /pegawai
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Pegawai> post(@Valid @RequestBody PegawaiRequest request) {
        Pegawai pegawai = Pegawai.of(
            request.namaPegawai(),
            request.nip(),
            request.kodeOpd(),
            request.namaRole(),
            request.statusPegawai(),
            request.passwordHash()
        );
        Pegawai saved = pegawaiService.tambahPegawai(pegawai);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(saved.id())
            .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /**
     * Delete pegawai by nip
     * @param nip pegawai
     * url: /pegawai/delete/{nip}
     */
    @DeleteMapping("delete/{nip}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("nip") String nip) {
        pegawaiService.hapusPegawai(nip);
    }

}
