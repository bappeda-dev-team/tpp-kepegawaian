package cc.kertaskerja.tppkepegawaian.jabatan.web;

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

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JabatanService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("jabatan")
public class JabatanController {
	private final JabatanService jabatanService;
	
	public JabatanController(JabatanService jabatanService) {
		this.jabatanService = jabatanService;
	}
	
	/**
	 * 
	 * @param nip
	 * url: /jabatan/{nip}
	 */
	@GetMapping("{nip}")
    public Jabatan getByNip(@PathVariable("nip") String nip) {
        return jabatanService.detailJabatan(nip);
    }
	
	/**
	 * 
	 * @param nip
	 * url: /jabatan/{nip}
	 */
	@PutMapping("{nip}")
	public Jabatan put(@PathVariable("nip") String nip, @Valid @RequestBody JabatanRequest request) {
		Jabatan jabatan = new Jabatan(
	            request.jabatanId(),
	            nip,
	            request.namaJabatan(),
	            request.kodeOpd(),
	            request.statusJabatan(),
	            request.jenisJabatan(),
	            request.eselon(),
	            request.tanggalMulai(),
	            request.tanggalBerakhir(),
	            null,
	            null
	    );
	    return jabatanService.ubahJabatan(nip, jabatan);
	}
	
	@PostMapping
	public ResponseEntity<Jabatan> post(@Valid @RequestBody JabatanRequest request) {
        Jabatan jabatan = Jabatan.of(
				        		request.nip(), 
				        		request.namaJabatan(),
				        		request.kodeOpd(),
				        		request.statusJabatan(),
				        		request.jenisJabatan(),
				        		request.eselon(),
				        		request.tanggalMulai(),
				        		request.tanggalBerakhir()
				        	);
        Jabatan saved = jabatanService.tambahJabatan(jabatan);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{nip}")
                .buildAndExpand(saved.nip())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }
	
	/**
	 * 
	 * @param nip
	 * url: /jabatan/{nip}
	 */
	@DeleteMapping("{nip}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("nip") String nip) {
		jabatanService.hapusJabatan(nip);
	}
}
