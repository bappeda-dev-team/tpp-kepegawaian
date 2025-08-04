package cc.kertaskerja.tppkepegawaian;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.*;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanController;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanRequest;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JabatanController.class)
public class JabatanTests {
	@Autowired
	private MockMvc mockMvc;

    @MockitoBean
    private JabatanService jabatanService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testGetByNip_found() throws Exception {
        Jabatan jabatan = Jabatan.of(
			                "123456", 
			                "Teknisi Jaringan", 
			                "OPD-123",
			                StatusJabatan.UTAMA, 
			                JenisJabatan.PELAKSANA, 
			                Eselon.ESELON_II,
			                new Date(), 
			                new Date()
        );
        Mockito.when(jabatanService.detailJabatan("123456")).thenReturn(jabatan);

        mockMvc.perform(get("/jabatan/123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nip").value("123456"))
                .andExpect(jsonPath("$.namaJabatan").value("Teknisi Jaringan"))
                .andExpect(jsonPath("$.kodeOpd").value("OPD-123"))
                .andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
                .andExpect(jsonPath("$.jenisJabatan").value("PELAKSANA"))
                .andExpect(jsonPath("$.eselon").value("ESELON_II"))
                .andExpect(jsonPath("$.tanggalMulai").isNotEmpty())
                .andExpect(jsonPath("$.tanggalAkhir").isNotEmpty());
    }
    
    @Test
    void testGetByNip_notFound() throws Exception {
        Mockito.when(jabatanService.detailJabatan("12345"))
                .thenThrow(new JabatanNotFoundException("12345"));

        mockMvc.perform(get("/jabatan/12345"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testPostOpd_success() throws Exception {
    	JabatanRequest request = new JabatanRequest(
    									null,
    									"1234567",
    									"Perencana Ahli Muda",
    									"OPD-345",
    									StatusJabatan.UTAMA,
    									JenisJabatan.JABATAN_FUNGSIONAL,
    									Eselon.ESELON_III,
    									new Date(),
    									new Date()
    	);
    	Jabatan jabatan = new Jabatan(
    						1L,
    						"1234567",
    						"Perencana Ahli Muda",
							"OPD-345",
							StatusJabatan.UTAMA,
							JenisJabatan.JABATAN_FUNGSIONAL,
							Eselon.ESELON_III,
							new Date(),
							new Date(),
							null,
							null
    	);
    	Mockito.when(jabatanService.tambahJabatan(any(Jabatan.class))).thenReturn(jabatan);
    	
    	mockMvc.perform(post("/jabatan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nip").value("1234567"))
                .andExpect(jsonPath("$.namaJabatan").value("Perencana Ahli Muda"))
				.andExpect(jsonPath("$.kodeOpd").value("OPD-345"))
				.andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
				.andExpect(jsonPath("$.jenisJabatan").value("JABATAN_FUNGSIONAL"))
				.andExpect(jsonPath("$.eselon").value("ESELON_III"))
				.andExpect(jsonPath("$.tanggalMulai").isNotEmpty())
				.andExpect(jsonPath("$.tanggalAkhir").isNotEmpty());
    }
    
    @Test
    void testPostJabatan_validationError() throws Exception {
        mockMvc.perform(post("/jabatan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testPostJabatan_kodeOpdNotFound() throws Exception {
        JabatanRequest request = new JabatanRequest(
                null,
                "1234567",
                "Perencana Ahli Muda",
                "OPD-NOT-EXIST",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL,
                Eselon.ESELON_III,
                new Date(),
                new Date()
        );
        Mockito.when(jabatanService.tambahJabatan(any(Jabatan.class)))
                .thenThrow(new OpdNotFoundException("OPD-NOT-EXIST"));

        mockMvc.perform(post("/jabatan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testPutJabatan() throws Exception {
    	JabatanRequest request = new JabatanRequest(
					    			null,
									"1234567",
									"Analis Kebijakan Ahli Muda",
									"OPD-678",
									StatusJabatan.UTAMA,
									JenisJabatan.JABATAN_FUNGSIONAL,
									Eselon.ESELON_III,
									new Date(),
									new Date()
    	);
    	Jabatan jabatan = new Jabatan(
						null,
						"1234567",
						"Analis Kebijakan Ahli Muda",
						"OPD-678",
						StatusJabatan.UTAMA,
						JenisJabatan.JABATAN_FUNGSIONAL,
						Eselon.ESELON_III,
						new Date(),
						new Date(),
						null,
						null
		);
    	Mockito.when(jabatanService.ubahJabatan(eq("1234567"), any(Jabatan.class))).thenReturn(jabatan);
    	
    	mockMvc.perform(put("/jabatan/1234567")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(objectMapper.writeValueAsString(request)))
		        .andExpect(jsonPath("$.nip").value("1234567"))
		        .andExpect(jsonPath("$.namaJabatan").value("Analis Kebijakan Ahli Muda"))
				.andExpect(jsonPath("$.kodeOpd").value("OPD-678"))
				.andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
				.andExpect(jsonPath("$.jenisJabatan").value("JABATAN_FUNGSIONAL"))
				.andExpect(jsonPath("$.eselon").value("ESELON_III"))
				.andExpect(jsonPath("$.tanggalMulai").isNotEmpty())
				.andExpect(jsonPath("$.tanggalAkhir").isNotEmpty());
    }
    
    @Test
    void testPutJabatan_notFound() throws Exception {
        JabatanRequest request = new JabatanRequest(
					        		null,
									"9999",
									"Analis Kebijakan Ahli Muda",
									"OPD-678",
									StatusJabatan.UTAMA,
									JenisJabatan.JABATAN_FUNGSIONAL,
									Eselon.ESELON_III,
									new Date(),
									new Date()
        );
        Mockito.when(jabatanService.ubahJabatan(eq("9999"), any(Jabatan.class)))
               .thenThrow(new JabatanNotFoundException("9999"));

        mockMvc.perform(put("/opd/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testPutJabatan_kodeOpdNotFound() throws Exception {
        JabatanRequest request = new JabatanRequest(
                null,
                "1234567",
                "Perencana Ahli Muda",
                "OPD-NOT-EXIST",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL,
                Eselon.ESELON_III,
                new Date(),
                new Date()
        );
        Mockito.when(jabatanService.ubahJabatan(eq("1234567"), any(Jabatan.class)))
                .thenThrow(new OpdNotFoundException("OPD-NOT-EXIST"));

        mockMvc.perform(put("/jabatan/1234567")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeleteJabatan_success() throws Exception {
        mockMvc.perform(delete("/jabatan/1234567"))
                .andExpect(status().isNoContent());
        Mockito.verify(jabatanService).hapusJabatan("1234567");
    }
}
