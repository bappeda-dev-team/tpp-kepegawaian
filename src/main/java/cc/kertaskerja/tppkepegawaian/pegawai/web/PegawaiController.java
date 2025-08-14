package cc.kertaskerja.tppkepegawaian.pegawai.web;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiService;
import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("pegawai")
public class PegawaiController {
    private final PegawaiService pegawaiService;

    public PegawaiController(PegawaiService pegawaiService) {
        this.pegawaiService = pegawaiService;
    }

    /**
     * Get pegawai by kodeOpd
     * @param kodeOpd
     * @return pegawai object
     */
    @GetMapping
    public Iterable<Pegawai> getPegawaiAktif(@RequestParam("kode_opd") String kodeOpd) {
        return pegawaiService.listPegawaiAktif(kodeOpd);
    }

    /**
     * Get pegawai by nip
     * @param nip
     * @return pegawai object
     */
    @GetMapping("{nip}")
    public Pegawai getByNip(@PathVariable("nip") String nip) {
        return pegawaiService.detailPegawai(nip);
    }
    
    /**
     * Update pegawai by NIP
     * @param nip pegawai NIP
     * @param request pegawai update request
     * @return updated Pegawai object
     * url: /pegawai/{nip}
     */
    @PutMapping("{nip}")
    public Pegawai put(@PathVariable("nip") String nip, @Valid @RequestBody PegawaiRequest request) {
        // Ambil data pegawai yang sudah dibuat
        Pegawai existingPegawai = pegawaiService.detailPegawai(nip);
        
        Pegawai pegawai = new Pegawai(
            request.pegawaiId(),
            request.namaPegawai(),
            nip,
            request.kodeOpd(),
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
     * url: /pegawai/{nip}
     */
    @DeleteMapping("{nip}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("nip") String nip) {
        pegawaiService.hapusPegawai(nip);
    }

}