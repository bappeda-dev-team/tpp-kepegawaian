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
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("jabatan")
public class JabatanController {
	private final JabatanService jabatanService;
	
	public JabatanController(JabatanService jabatanService) {
		this.jabatanService = jabatanService;
	}

	/**
	 * Get jabatan by ID
	 * @param id jabatan ID
	 * @return Jabatan object
	 * url: /jabatan/detail/{id}
	 */
	@GetMapping("detail/{id}")
	public ResponseEntity<Jabatan> getById(@PathVariable("id") Long id) {
		return ResponseEntity.ok(jabatanService.detailJabatan(id));
	}

	/**
	 * Get master jabatan by kode OPD
	 * @param kodeOpd OPD code
	 * @return List of JabatanWithPegawaiResponse objects (includes nama pegawai)
	 * url: /jabatan/master/{kodeOpd}
	 */
	@GetMapping("detail/master/opd/{kodeOpd}")
	public ResponseEntity<List<JabatanWithPegawaiResponse>> getMasterByKodeOpd(@PathVariable("kodeOpd") String kodeOpd) {
		return ResponseEntity.ok(jabatanService.listJabatanByKodeOpdWithPegawai(kodeOpd));
	}

	/**
	 * Update jabatan by ID
	 * @param id jabatan ID
	 * @param request jabatan update request
	 * @return updated Jabatan object
	 * url: /jabatan/update/{id}
	 */
	@PutMapping("update/{id}")
	public ResponseEntity<Jabatan> put(@PathVariable("id") Long id, @Valid @RequestBody JabatanRequest request) {
		// Ambil data jabatan yang sudah dibuat
		Jabatan existingJabatan = jabatanService.detailJabatan(id);

		Jabatan jabatan = new Jabatan(
	            id,
	            request.nip(),
	            request.namaJabatan(),
	            request.kodeOpd(),
	            request.statusJabatan(),
	            request.jenisJabatan(),
	            request.eselon(),
				request.pangkat(),
				request.golongan(),
	            request.tanggalMulai(),
	            request.tanggalAkhir(),
	            // saat update data ambil data createdDate dari jabatan yang sudah dibuat
	            existingJabatan.createdDate(),
	            null
	    );
	    Jabatan updatedJabatan = jabatanService.ubahJabatan(id, jabatan);
	    return ResponseEntity.ok(updatedJabatan);
	}

	/**
	 * Create new jabatan
	 * @param request jabatan creation request
	 * @return created Jabatan object with location header
	 * url: /jabatan
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Jabatan> post(@Valid @RequestBody JabatanRequest request) {
	    Jabatan jabatan = Jabatan.of(
		    request.nip(), 
		    request.namaJabatan(),
		    request.kodeOpd(),
		    request.statusJabatan(),
		    request.jenisJabatan(),
		    request.eselon(),
		    request.pangkat(),
		    request.golongan(),
		    request.tanggalMulai(),
		    request.tanggalAkhir()
		    );
	    Jabatan saved = jabatanService.tambahJabatan(jabatan);
	    URI location = ServletUriComponentsBuilder
		    .fromCurrentRequest()
		    .path("/{id}")
		    .buildAndExpand(saved.id())
		    .toUri();
	    return ResponseEntity.created(location).body(saved);
	}

	/**
	 * Delete jabatan by ID
	 * @param id jabatan ID
	 * url: /jabatan/delete/{id}
	 */
	@DeleteMapping("delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) {
		jabatanService.hapusJabatan(id);
	}
}
