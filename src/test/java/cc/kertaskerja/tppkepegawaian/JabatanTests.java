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
        void testGetById_found() throws Exception {
                Jabatan jabatan = Jabatan.of(
                                "1234567",
                                "Teknisi Jaringan",
                                "OPD-123",
                                StatusJabatan.UTAMA,
                                JenisJabatan.PELAKSANA,
                                Eselon.ESELON_II,
                                new Date(),
                                new Date());
                Mockito.when(jabatanService.detailJabatan(12L)).thenReturn(jabatan);

                mockMvc.perform(get("/jabatan/12"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.nip").value("1234567"))
                                .andExpect(jsonPath("$.namaJabatan").value("Teknisi Jaringan"))
                                .andExpect(jsonPath("$.kodeOpd").value("OPD-123"))
                                .andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
                                .andExpect(jsonPath("$.jenisJabatan").value("PELAKSANA"))
                                .andExpect(jsonPath("$.eselon").value("ESELON_II"))
                                .andExpect(jsonPath("$.tanggalMulai").isNotEmpty())
                                .andExpect(jsonPath("$.tanggalAkhir").isNotEmpty());
        }

        @Test
        void testGetById_notFound() throws Exception {
                Mockito.when(jabatanService.detailJabatan(13L))
                                .thenThrow(new JabatanNotFoundException(13L));

                mockMvc.perform(get("/jabatan/13"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testPostJabatan_success() throws Exception {
                JabatanRequest request = new JabatanRequest(
                                null,
                                "1234567",
                                "Perencana Ahli Muda",
                                "OPD-345",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date());
                Jabatan jabatan = new Jabatan(
                                12L,
                                "1234567",
                                "Perencana Ahli Muda",
                                "OPD-345",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date(),
                                null,
                                null);
                Mockito.when(jabatanService.tambahJabatan(any(Jabatan.class))).thenReturn(jabatan);

                mockMvc.perform(post("/jabatan")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(12L))
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
        void testPutJabatan() throws Exception {
                JabatanRequest request = new JabatanRequest(
                                12L,
                                "1234567",
                                "Analis Kebijakan Ahli Muda",
                                "OPD-678",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date());
                Jabatan jabatan = new Jabatan(
                                12L,
                                "1234567",
                                "Analis Kebijakan Ahli Muda",
                                "OPD-678",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date(),
                                null,
                                null);
                Mockito.when(jabatanService.ubahJabatan(eq(12L), any(Jabatan.class))).thenReturn(jabatan);

                mockMvc.perform(put("/jabatan/12")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(jsonPath("$.id").value(12L))
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
                                13L,
                                "9999",
                                "Analis Kebijakan Ahli Muda",
                                "OPD-678",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date());
                Mockito.when(jabatanService.ubahJabatan(eq(13L), any(Jabatan.class)))
                                .thenThrow(new JabatanNotFoundException(13L));

                mockMvc.perform(put("/opd/9999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testPutJabatan_idNotFound() throws Exception {
                JabatanRequest request = new JabatanRequest(
                                13L,
                                "1234567",
                                "Perencana Ahli Muda",
                                "OPD-NOT-EXIST",
                                StatusJabatan.UTAMA,
                                JenisJabatan.JABATAN_FUNGSIONAL,
                                Eselon.ESELON_III,
                                new Date(),
                                new Date());
                Mockito.when(jabatanService.ubahJabatan(eq(13L), any(Jabatan.class)))
                                .thenThrow(new JabatanNotFoundException(13L));

                mockMvc.perform(put("/jabatan/13")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        @Test
        void testDeleteJabatan_success() throws Exception {
                mockMvc.perform(delete("/jabatan/1"))
                                .andExpect(status().isNoContent());
                Mockito.verify(jabatanService).hapusJabatan("1");
        }
}
