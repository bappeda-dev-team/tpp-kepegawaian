package cc.kertaskerja.tppkepegawaian;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Eselon;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JenisJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.StatusJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanRequest;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.StatusPegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.web.PegawaiRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/test-data/pegawai-data.sql")
public class PegawaiTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testGetByNip_found() throws Exception {
            mockMvc.perform(get("/pegawai/123456789012345678"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.namaPegawai").value("John Doe"))
                            .andExpect(jsonPath("$.nip").value("123456789012345678"))
                            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                            .andExpect(jsonPath("$.statusPegawai").value("AKTIF"))
                            .andExpect(jsonPath("$.passwordHash").value("hashedpassword123"));
    }
    
    @Test
    void testGetByKodeOpd_found() throws Exception {
        mockMvc.perform(get("/pegawai")
                        .param("kode_opd", "OPD-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nip").value("123456789012345678"))
                .andExpect(jsonPath("$[0].namaPegawai").value("John Doe"))
                .andExpect(jsonPath("$[0].kodeOpd").value("OPD-001"))
                .andExpect(jsonPath("$[0].statusPegawai").value("AKTIF"));
    }
    
    @Test
    void testGetByNip_notFound() throws Exception {
	mockMvc.perform(get("/pegawai/999"))
		.andExpect(status().isNotFound());
    }
    
    @Test
    void testGetByKodeOpd_emptyResult() throws Exception {
        mockMvc.perform(get("/pegawai")
                        .param("kode_opd", "OPD-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
    
    @Test
    void testPostPegawai_success() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
                        null,
                        "Jane Doe",
                        "987654321098765432",
                        "OPD-002",
                        StatusPegawai.CUTI,
                        "hashedpassword789");

        mockMvc.perform(post("/pegawai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.namaPegawai").value("Jane Doe"))
                        .andExpect(jsonPath("$.nip").value("987654321098765432"))
                        .andExpect(jsonPath("$.kodeOpd").value("OPD-002"))
                        .andExpect(jsonPath("$.statusPegawai").value("CUTI"))
                        .andExpect(jsonPath("$.passwordHash").value("hashedpassword789"));
    }
    
    @Test
    void testPostPegawai_validationError() throws Exception {
            mockMvc.perform(post("/pegawai")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                            .andExpect(status().isBadRequest());
    }
    
    @Test
    void testPostPegawai_duplicateNip() throws Exception {
            PegawaiRequest request = new PegawaiRequest(
                            null,
                            "Jane Doe",
                            "123456789012345678",
                            "OPD-002",
                            StatusPegawai.CUTI,
                            "hashedpassword789");

            mockMvc.perform(post("/pegawai")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isUnprocessableEntity());
    }
    
    @Test
    void testPostPegawai_nipLessThan18Character() throws Exception {
            PegawaiRequest request = new PegawaiRequest(
                            null,
                            "Jane Doe",
                            "12345678901234567",
                            "OPD-002",
                            StatusPegawai.AKTIF,
                            "hashedpassword789");

            mockMvc.perform(post("/pegawai")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isBadRequest());
    }
    
    @Test
    void testPutPegawaiByNip_success() throws Exception {
            PegawaiRequest request = new PegawaiRequest(
                            1L,
                            "Agus",
                            "123456789012345678",
                            "OPD-001",
                            StatusPegawai.CUTI,
                            "hashedpassword789");

            mockMvc.perform(put("/pegawai/123456789012345678")
                	    .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.id").exists())
                            .andExpect(jsonPath("$.namaPegawai").value("Agus"))
                            .andExpect(jsonPath("$.nip").value("123456789012345678"))
                            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                            .andExpect(jsonPath("$.statusPegawai").value("CUTI"))
                            .andExpect(jsonPath("$.passwordHash").value("hashedpassword789"));
    }
    
    @Test
    void testPutPegawaiByNip_validationError() throws Exception {
            mockMvc.perform(put("/pegawai/123456789012345678")
                    	.contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                            .andExpect(status().isBadRequest());
    }
    
    @Test
    void testPutPegawaiByNip_nipNotFound() throws Exception {
            PegawaiRequest request = new PegawaiRequest(
                	    1L,
                            "Agus",
                            "999999999999999999",
                            "OPD-001",
                            StatusPegawai.CUTI,
                            "hashedpassword789");

            mockMvc.perform(put("/pegawai/999999999999999999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeletePegawaiByNip_success() throws Exception {
            mockMvc.perform(delete("/pegawai/123456789012345678"))
                    .andExpect(status().isNoContent());
            mockMvc.perform(get("/pegawai/123456789012345678"))
                    .andExpect(status().isNotFound());
    }
    
    @Test
    void testDeletePegawaiByNip_nipNotFound() throws Exception {
            mockMvc.perform(delete("/pegawai/999999999999999999"))
                    .andExpect(status().isNotFound());
    }
}