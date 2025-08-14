package cc.kertaskerja.tppkepegawaian;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Eselon;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JabatanService;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JenisJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.StatusJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class JabatanTests {
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private JabatanService jabatanService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        void testPostJabatan_success() throws Exception {
                JabatanRequest request = new JabatanRequest(
                                null,
                                "198765432109876543",
                                "Kepala Bidang Perencanaan",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_STRUKTURAL,
                                Eselon.ESELON_II,
                                new Date(),
                                new Date());

                Jabatan jabatan = new Jabatan(
                                1L,
                                "198765432109876543",
                                "Kepala Bidang Perencanaan",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_STRUKTURAL,
                                Eselon.ESELON_II,
                                new Date(),
                                new Date(),
                                null,
                                null);
                Mockito.when(jabatanService.tambahJabatan(any(Jabatan.class))).thenReturn(jabatan);

                mockMvc.perform(post("/jabatan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.nip").value("198765432109876543"))
                                .andExpect(jsonPath("$.namaJabatan").value("Kepala Bidang Perencanaan"))
                                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                                .andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
                                .andExpect(jsonPath("$.jenisJabatan").value("JABATAN_STRUKTURAL"))
                                .andExpect(jsonPath("$.eselon").value("ESELON_II"))
                                .andExpect(jsonPath("$.tanggalMulai").isNotEmpty())
                                .andExpect(jsonPath("$.tanggalAkhir").isNotEmpty());
        }

        @Test
        void testPostJabatan_invalidData() throws Exception {
                JabatanRequest request = new JabatanRequest(
                                null,
                                "",
                                "",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_STRUKTURAL,
                                Eselon.ESELON_II,
                                new Date(),
                                new Date());

                mockMvc.perform(post("/jabatan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testPutJabatan_success() throws Exception {
                JabatanRequest request = new JabatanRequest(
                                1L,
                                "198765432109876543",
                                "Kepala Bidang Perencanaan",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_STRUKTURAL,
                                Eselon.ESELON_II,
                                new Date(),
                                new Date());

                Jabatan existingJabatan = new Jabatan(
                                1L,
                                "198765432109876543",
                                "Analis Kebijakan Ahli Muda",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_STRUKTURAL,
                                Eselon.ESELON_II,
                                new Date(),
                                new Date(),
                                java.time.Instant.now(),
                                null);
                Mockito.when(jabatanService.detailJabatan(1L)).thenReturn(existingJabatan);

                Jabatan updatedJabatan = new Jabatan(
                                1L,
                                "200065432109876543",
                                "Analis Kebijakan Ahli Muda",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date(),
                                existingJabatan.createdDate(),
                                java.time.Instant.now());
                Mockito.when(jabatanService.ubahJabatan(eq(1L), any(Jabatan.class))).thenReturn(updatedJabatan);

                mockMvc.perform(put("/jabatan/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.nip").value("200065432109876543"))
                                .andExpect(jsonPath("$.namaJabatan").value("Analis Kebijakan Ahli Muda"))
                                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                                .andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
                                .andExpect(jsonPath("$.jenisJabatan").value("JABATAN_FUNGSIONAL"))
                                .andExpect(jsonPath("$.eselon").value("ESELON_III"))
                                .andExpect(jsonPath("$.tanggalMulai").isNotEmpty())
                                .andExpect(jsonPath("$.tanggalAkhir").isNotEmpty())
                                .andExpect(jsonPath("$.createdDate").isNotEmpty())
                                .andExpect(jsonPath("$.lastModifiedDate").isNotEmpty());;
        }

        @Test
        void testPutJabatan_idNotFound() throws Exception {
                JabatanRequest request = new JabatanRequest(
                               2L,
                                "200065432109876543",
                                "Analis Kebijakan Ahli Muda",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date());
                 Mockito.when(jabatanService.detailJabatan(2L)).thenThrow(new JabatanNotFoundException(2L));

                mockMvc.perform(put("/jabatan/2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testGetJabatanById_success() throws Exception {
                Jabatan jabatan = Jabatan.of(
                                "200065432109876543",
                                "Analis Kebijakan Ahli Muda",
                                "OPD-001",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date());
                Mockito.when(jabatanService.detailJabatan(1L)).thenReturn(jabatan);

                mockMvc.perform(get("/jabatan/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nip").exists())
                                .andExpect(jsonPath("$.namaJabatan").exists())
                                .andExpect(jsonPath("$.kodeOpd").exists())
                                .andExpect(jsonPath("$.statusJabatan").exists())
                                .andExpect(jsonPath("$.jenisJabatan").exists())
                                .andExpect(jsonPath("$.eselon").exists())
                                .andExpect(jsonPath("$.tanggalMulai").isNotEmpty())
                                .andExpect(jsonPath("$.tanggalAkhir").isNotEmpty());
        }

        @Test
        void testGetJabatanById_notFound() throws Exception {
                Mockito.when(jabatanService.detailJabatan(999L))
                                .thenThrow(new JabatanNotFoundException(999L));

                mockMvc.perform(get("/jabatan/999"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteJabatan_success() throws Exception {
                mockMvc.perform(delete("/jabatan/1"))
                                .andExpect(status().isNoContent());
                Mockito.verify(jabatanService).hapusJabatan(1L);
        }

        @Test
        void testDeleteJabatan_notFound() throws Exception {
                Mockito.doThrow(new JabatanNotFoundException(999L))
                                .when(jabatanService).hapusJabatan(999L);

                mockMvc.perform(delete("/jabatan/999"))
                                .andExpect(status().isNotFound());
        }
}