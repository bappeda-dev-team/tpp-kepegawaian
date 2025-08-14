package cc.kertaskerja.tppkepegawaian;

import cc.kertaskerja.tppkepegawaian.opd.domain.Opd;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdService;
import cc.kertaskerja.tppkepegawaian.opd.web.OpdRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/test-data/opd-data.sql")
public class OpdTests {
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testGetByKodeOpd_found() throws Exception {
        mockMvc.perform(get("/opd/OPD-001"))
               	.andExpect(status().isOk())
                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                .andExpect(jsonPath("$.namaOpd").value("Badan Perencanaan Pembangunan Daerah"));
    }
    
    @Test
    void testGetByKodeOpd_notFound() throws Exception {

        mockMvc.perform(get("/opd/OPD-999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testPostOpd_success() throws Exception {
	// gunakan milisecond untuk membuat data test yang unique
	String uniqueCode = "TEST-OPD-" + System.currentTimeMillis();
	OpdRequest request = new OpdRequest(null, uniqueCode, "Badan Perencanaan Pembangunan Daerah");

        mockMvc.perform(post("/opd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.kodeOpd").value(uniqueCode))
                .andExpect(jsonPath("$.namaOpd").value("Badan Perencanaan Pembangunan Daerah"));
    }
    
    @Test
    void testPostOpd_validationError() throws Exception {
        mockMvc.perform(post("/opd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testPostOpd_duplicateKodeOpd() throws Exception {
        OpdRequest request = new OpdRequest(null, "OPD-001", "Badan Perencanaan Pembangunan Daerah");

        mockMvc.perform(post("/opd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity());
    }
    
    @Test
    void testPutOpd_success() throws Exception {
        OpdRequest request = new OpdRequest(1L, "OPD-001", "Badan Perencanaan Pembangunan Daerah Madiun");

        mockMvc.perform(put("/opd/OPD-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                .andExpect(jsonPath("$.namaOpd").value("Badan Perencanaan Pembangunan Daerah Madiun"));
    }
    
    @Test
    void testPutOpd_validationError() throws Exception {
        mockMvc.perform(put("/opd/001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testPutOpd_notFound() throws Exception {
        OpdRequest request = new OpdRequest(13L, "OPD-999", "Badan Perencanaan Pembangunan Daerah Madiun");

        mockMvc.perform(put("/opd/OPD-999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeleteOpd_success() throws Exception {
        mockMvc.perform(delete("/opd/OPD-001"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/opd/OPD-001"))
        	.andExpect(status().isNotFound());
    }
    
    @Test
    void testDeleteOpd_notFound() throws Exception {
            mockMvc.perform(delete("/opd/OPD-999"))
                    .andExpect(status().isNotFound());
    }
}
