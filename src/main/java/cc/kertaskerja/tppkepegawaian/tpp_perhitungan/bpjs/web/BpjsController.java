package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.web;

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

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain.Bpjs;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.bpjs.domain.BpjsService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("bpjs")
public class BpjsController {
    private final BpjsService bpjsService;
    
    public BpjsController(BpjsService bpjsService) {
        this.bpjsService = bpjsService;
    }
    
    /**
     * Get detail bpjs by nip
     * @param nip
     * url: /bpjs/detail/{nip}
     */
    @GetMapping("detail/{nip}")
    public Bpjs getByNip(@PathVariable("nip") String nip) {
        return bpjsService.detailBpjs(nip);
    }
    
    /**
     * Update bpjs by nip
     * @param nip
     * url: /bpjs/update/{nip}
     */
    @PutMapping("update/{nip}")
    public Bpjs put(@PathVariable("nip") String nip, @Valid @RequestBody BpjsRequest request) {
        // Ambil data bpjs yang sudah dibuat
        Bpjs existingBpjs = bpjsService.detailBpjs(nip);
        
        Bpjs bpjs = new Bpjs(
                existingBpjs.id(),
                request.nip(),
                request.namaBpjs(),
                request.komponenIuran(),
                request.nilaiBpjs(),
                existingBpjs.createdDate(),
                null
        );
        
        return bpjsService.ubahBpjs(nip, bpjs);
    }
    
    /**
     * Add bpjs
     * url: /bpjs
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Bpjs> post(@Valid @RequestBody BpjsRequest request) {
        Bpjs bpjs = Bpjs.of(
                request.nip(), 
                request.namaBpjs(), 
                request.komponenIuran(), 
                request.nilaiBpjs()
        );
        
        Bpjs saved = bpjsService.tambahBpjs(bpjs);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{nip}")
                .buildAndExpand(saved.nip())
                .toUri();
        
        return ResponseEntity.created(location).body(saved);
    }
    
    /**
     * Delete bpjs
     * @param nip
     * url: /bpjs/delete/{nip}
     */
    @DeleteMapping("delete/{nip}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("nip") String nip) {
        bpjsService.hapusPajak(nip);
    }
}
