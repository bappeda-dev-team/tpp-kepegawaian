package cc.kertaskerja.tppkepegawaian;

import cc.kertaskerja.tppkepegawaian.opd.domain.Opd;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdService;
import cc.kertaskerja.tppkepegawaian.opd.web.OpdController;
import cc.kertaskerja.tppkepegawaian.opd.web.OpdRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OpdController.class)
public class OpdTests {
	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpdService opdService;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testGetByKodeOpd_found() throws Exception {
        Opd opd = new Opd(null, "OPD-123", "Pemda", null, null);
        Mockito.when(opdService.detailOpd("OPD-123")).thenReturn(opd);

        mockMvc.perform(get("/opd/OPD-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kodeOpd").value("OPD-123"))
                .andExpect(jsonPath("$.namaOpd").value("Pemda"));
    }
    
    @Test
    void testGetByKodeOpd_notFound() throws Exception {
        Mockito.when(opdService.detailOpd("OPD-1")).thenThrow(new OpdNotFoundException("OPD-1"));

        mockMvc.perform(get("/opd/OPD-1"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testPostOpd_success() throws Exception {
        OpdRequest request = new OpdRequest(null, "OPD-123", "Pemda");
        Opd opd = new Opd(1L, "OPD-123", "Pemda", null, null);
        Mockito.when(opdService.tambahOpd(any(Opd.class))).thenReturn(opd);

        mockMvc.perform(post("/opd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.kodeOpd").value("OPD-123"))
                .andExpect(jsonPath("$.namaOpd").value("Pemda"));
    }
    
    @Test
    void testPostOpd_validationError() throws Exception {
        mockMvc.perform(post("/opd")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testPutOpd_success() throws Exception {
        OpdRequest request = new OpdRequest(null, "OPD-124", "Dinas");
        Opd opd = new Opd(null, "OPD-124", "Dinas", null, null);
        Mockito.when(opdService.ubahOpd(eq("OPD-124"), any(Opd.class))).thenReturn(opd);

        mockMvc.perform(put("/opd/OPD-124")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kodeOpd").value("OPD-124"))
                .andExpect(jsonPath("$.namaOpd").value("Dinas"));
    }
    
    @Test
    void testPutOpd_notFound() throws Exception {
        OpdRequest request = new OpdRequest(null, "OPD-999", "Dinas");
        Mockito.when(opdService.ubahOpd(eq("OPD-999"), any(Opd.class)))
               .thenThrow(new OpdNotFoundException("OPD-999"));

        mockMvc.perform(put("/opd/OPD-999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeleteOpd_success() throws Exception {
        mockMvc.perform(delete("/opd/OPD-124"))
                .andExpect(status().isNoContent());
        Mockito.verify(opdService).hapusOpd("OPD-124");
    }
}