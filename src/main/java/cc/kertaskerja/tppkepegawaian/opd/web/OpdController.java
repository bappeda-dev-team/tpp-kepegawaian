package cc.kertaskerja.tppkepegawaian.opd.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
	 * url: /opd/{kodeOpd}
	 */
	@GetMapping("{kodeOpd}")
    public Opd getByKodeOpd(@PathVariable("kodeOpd") String kodeOpd) {
        return opdService.detailOpd(kodeOpd);
    }
	
	/**
	 * 
	 * @param kodeOpd
	 * url: /opd/{kodeOpd}
	 */
	@PutMapping("{kodeOpd}")
	public Opd put(@PathVariable("kodeOpd") String kodeOpd, @Valid @RequestBody Opd opd) {
	    return opdService.ubahOpd(kodeOpd, opd);
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Opd post(@Valid @RequestBody Opd opd) {
		return opdService.tambahOpd(opd);
	}
	
	/**
	 * 
	 * @param kodeOpd
	 * url: /opd/{kodeOpd}
	 */
	@DeleteMapping("{kodeOpd}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("kodeOpd") String kodeOpd) {
		opdService.hapusOpd(kodeOpd);
	}
}
