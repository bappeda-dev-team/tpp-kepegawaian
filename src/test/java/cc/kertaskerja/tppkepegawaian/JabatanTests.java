package cc.kertaskerja.tppkepegawaian;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.*;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/test-data/jabatan-data.sql")
public class JabatanTests {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testGetById_found() throws Exception {
                mockMvc.perform(get("/jabatan/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nip").value("1234567890123456"))
                                .andExpect(jsonPath("$.namaJabatan").value("Analis Kebijakan Ahli Muda"))
                                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                                .andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
                                .andExpect(jsonPath("$.jenisJabatan").value("JABATAN_FUNGSIONAL"))
                                .andExpect(jsonPath("$.eselon").value("ESELON_II"))
                                .andExpect(jsonPath("$.tanggalMulai").isNotEmpty())
                                .andExpect(jsonPath("$.tanggalAkhir").isNotEmpty());
        }

        @Test
        void testGetById_notFound() throws Exception {
                mockMvc.perform(get("/jabatan/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testPostJabatan_success() throws Exception {
                JabatanRequest request = new JabatanRequest(
                                null,
                                "987654321012345678",
                                "Perencana Ahli Muda",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date());

                mockMvc.perform(post("/jabatan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.nip").value("987654321012345678"))
                                .andExpect(jsonPath("$.namaJabatan").value("Perencana Ahli Muda"))
                                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
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
        void testPutJabatan() throws Exception {
                JabatanRequest request = new JabatanRequest(
                                1L,
                                "1234567890123456",
                                "Analis Kebijakan Ahli Muda Updated",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date());

                mockMvc.perform(put("/jabatan/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                		.andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.nip").value("1234567890123456"))
                                .andExpect(jsonPath("$.namaJabatan").value("Analis Kebijakan Ahli Muda Updated"))
                                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                                .andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
                                .andExpect(jsonPath("$.jenisJabatan").value("JABATAN_FUNGSIONAL"))
                                .andExpect(jsonPath("$.eselon").value("ESELON_III"))
                                .andExpect(jsonPath("$.tanggalMulai").isNotEmpty())
                                .andExpect(jsonPath("$.tanggalAkhir").isNotEmpty());
        }

        @Test
        void testPutJabatan_validationError() throws Exception {
                mockMvc.perform(put("/jabatan/1")
                        	.contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testPutJabatan_idNotFound() throws Exception {
                JabatanRequest request = new JabatanRequest(
                                13L,
                                "1234567890123456",
                                "Analis Kebijakan Ahli Muda Updated",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date());

                mockMvc.perform(put("/jabatan/13")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteJabatan_success() throws Exception {
                mockMvc.perform(delete("/jabatan/1"))
                        .andExpect(status().isNoContent());
                mockMvc.perform(get("/jabatan/1"))
                        .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteJabatan_notFound() throws Exception {
                mockMvc.perform(delete("/jabatan/999"))
                        .andExpect(status().isNotFound());
        }
}