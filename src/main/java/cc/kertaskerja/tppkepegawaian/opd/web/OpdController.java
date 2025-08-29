package cc.kertaskerja.tppkepegawaian.opd.web;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import cc.kertaskerja.tppkepegawaian.opd.domain.Opd;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("opd")
public class OpdController {
	private final OpdService opdService;
	
	public OpdController(OpdService opdService) {
		this.opdService = opdService;
	}
	
	/**
     * 
     * @param kodeOpd
     * url: /opd/detail/{kodeOpd}
     */
    @GetMapping("detail/{kodeOpd}")
    public Opd getByKodeOpd(@PathVariable("kodeOpd") String kodeOpd) {
        return opdService.detailOpd(kodeOpd);
    }
	
	/**
     * Get all OPD
     * @return list of all OPD
     * url: /opd/detail/allOpd
     */
    @GetMapping("detail/allOpd")
    public Iterable<Opd> getAllOpd() {
        return opdService.listAllOpd();
    }
	
	/**
     * Update Kode OPD
     * @param kodeOpd
     * url: /opd/update/{kodeOpd}
     */
    @PutMapping("update/{kodeOpd}")
    public Opd put(@PathVariable("kodeOpd") String kodeOpd, @Valid @RequestBody OpdRequest request) {
        	// Ambil data opd yang sudah dibuat
        	Opd existingOpd = opdService.detailOpd(kodeOpd);
        
        Opd opd = new Opd(
                existingOpd.id(),
                kodeOpd,
                request.namaOpd(),
                // saat update data ambil data createdDate dari opd yang sudah dibuat
                existingOpd.createdDate(),
                null
        );
        return opdService.ubahOpd(kodeOpd, opd);
    }
	
	/**
     * Tambah Kode OPD
     * url: /opd
     */
	@PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Opd> post(@Valid @RequestBody OpdRequest request) {
        Opd opd = Opd.of(request.kodeOpd(), request.namaOpd());
        Opd saved = opdService.tambahOpd(opd);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/detail/{kodeOpd}")
                .buildAndExpand(saved.kodeOpd())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }
	
	/**
     * delete Kode OPD
     * @param kodeOpd
     * url: /opd/delete/{kodeOpd}
     */
    @DeleteMapping("delete/{kodeOpd}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("kodeOpd") String kodeOpd) {
        opdService.hapusOpd(kodeOpd);
    }
}
