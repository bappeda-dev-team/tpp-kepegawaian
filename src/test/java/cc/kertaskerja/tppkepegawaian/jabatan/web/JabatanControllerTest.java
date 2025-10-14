package cc.kertaskerja.tppkepegawaian.jabatan.web;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.*;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.web.JabatanWithPegawaiResponse;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Calendar;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JabatanController.class)
public class JabatanControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean   
    private JabatanService jabatanService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Jabatan testJabatan;
    private Jabatan testJabatan2;
    private JabatanRequest testJabatanRequest;
    private JabatanWithPegawaiResponse testJabatanWithPegawaiResponse1;
    private JabatanWithPegawaiResponse testJabatanWithPegawaiResponse2;
    private Calendar tanggalMulai;
    private Calendar tanggalAkhir;
    
    @BeforeEach
    void setUp() {
        tanggalMulai = Calendar.getInstance();
        tanggalMulai.set(2023, Calendar.JANUARY, 1);
        
        tanggalAkhir = Calendar.getInstance();
        tanggalAkhir.set(2025, Calendar.DECEMBER, 31);
        
        testJabatan = new Jabatan(
                1L,
                "198001012010011001",
                "Kepala Dinas",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Junior",
                "Golongan I",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );

        testJabatan2 = new Jabatan(
                2L,
                "199001012015021002",
                "Sekretaris Dinas",
                "OPD-002",
                StatusJabatan.PLT,
                JenisJabatan.JABATAN_ADMINISTRASI,
                Eselon.ESELON_III,
                "Middle",
                "Golongan II",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );
        
        testJabatanRequest = new JabatanRequest(
                null,
                "198001012010011001",
                "Kepala Dinas",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Junior",
                "Golongan I",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime()
        );
        
        testJabatanWithPegawaiResponse1 = new JabatanWithPegawaiResponse(
                1L,
                "198001012010011001",
                "John Doe",
                "Kepala Dinas",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Junior",
                "Golongan I",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime()
        );
        
        testJabatanWithPegawaiResponse2 = new JabatanWithPegawaiResponse(
                2L,
                "199001012015021002",
                "Jane Smith",
                "Sekretaris Dinas",
                "OPD-002",
                StatusJabatan.PLT,
                JenisJabatan.JABATAN_ADMINISTRASI,
                Eselon.ESELON_III,
                "Middle",
                "Golongan II",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime()
        );
    }
    
    @Test
    void detailById_WhenJabatanExists_ShouldReturnJabatan() throws Exception {
        when(jabatanService.detailJabatan(1L)).thenReturn(testJabatan);

        mockMvc.perform(get("/jabatan/detail/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nip").value("198001012010011001"))
                .andExpect(jsonPath("$.namaJabatan").value("Kepala Dinas"))
                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                .andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
                .andExpect(jsonPath("$.jenisJabatan").value("JABATAN_PEMIMPIN_TINGGI"))
                .andExpect(jsonPath("$.eselon").value("ESELON_IV"))
                .andExpect(jsonPath("$.pangkat").value("Junior"))
                .andExpect(jsonPath("$.golongan").value("Golongan I"))
                .andExpect(jsonPath("$.tanggalMulai").value("01-01-2023"))
                .andExpect(jsonPath("$.tanggalAkhir").value("31-12-2025"));
    }
    
    @Test
    void detailById_WhenJabatanNotExists_ShouldReturnNotFound() throws Exception {
        when(jabatanService.detailJabatan(999L)).thenThrow(new JabatanNotFoundException(999L));
        
        mockMvc.perform(get("/jabatan/detail/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMasterByKodeOpd_WhenJabatansExist_ShouldReturnJabatanWithPegawaiList() throws Exception {
        when(jabatanService.listJabatanByKodeOpdWithPegawai("OPD-001")).thenReturn(List.of(testJabatanWithPegawaiResponse1, testJabatanWithPegawaiResponse2));

        mockMvc.perform(get("/jabatan/detail/master/opd/OPD-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nip").value("198001012010011001"))
                .andExpect(jsonPath("$[0].namaPegawai").value("John Doe"))
                .andExpect(jsonPath("$[0].namaJabatan").value("Kepala Dinas"))
                .andExpect(jsonPath("$[0].kodeOpd").value("OPD-001"))
                .andExpect(jsonPath("$[0].statusJabatan").value("UTAMA"))
                .andExpect(jsonPath("$[0].jenisJabatan").value("JABATAN_PEMIMPIN_TINGGI"))
                .andExpect(jsonPath("$[0].eselon").value("ESELON_IV"))
                .andExpect(jsonPath("$[0].pangkat").value("Junior"))
                .andExpect(jsonPath("$[0].golongan").value("Golongan I"))
                .andExpect(jsonPath("$[0].tanggalMulai").value("01-01-2023"))
                .andExpect(jsonPath("$[0].tanggalAkhir").value("31-12-2025"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].nip").value("199001012015021002"))
                .andExpect(jsonPath("$[1].namaPegawai").value("Jane Smith"))
                .andExpect(jsonPath("$[1].namaJabatan").value("Sekretaris Dinas"))
                .andExpect(jsonPath("$[1].kodeOpd").value("OPD-002"))
                .andExpect(jsonPath("$[1].statusJabatan").value("PLT"))
                .andExpect(jsonPath("$[1].jenisJabatan").value("JABATAN_ADMINISTRASI"))
                .andExpect(jsonPath("$[1].eselon").value("ESELON_III"))
                .andExpect(jsonPath("$[1].pangkat").value("Middle"))
                .andExpect(jsonPath("$[1].golongan").value("Golongan II"));
    }

    @Test
    void getMasterByKodeOpd_WhenNoJabatansExist_ShouldReturnEmptyList() throws Exception {
        when(jabatanService.listJabatanByKodeOpdWithPegawai("OPD-999")).thenReturn(List.of());

        mockMvc.perform(get("/jabatan/detail/master/opd/OPD-999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getByNip_WhenJabatansExist_ShouldReturnJabatanWithPegawaiList() throws Exception {
        when(jabatanService.listJabatanByNipWithPegawai("198001012010011001")).thenReturn(List.of(testJabatanWithPegawaiResponse1));

        mockMvc.perform(get("/jabatan/detail/nip/198001012010011001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nip").value("198001012010011001"))
                .andExpect(jsonPath("$[0].namaPegawai").value("John Doe"))
                .andExpect(jsonPath("$[0].namaJabatan").value("Kepala Dinas"))
                .andExpect(jsonPath("$[0].kodeOpd").value("OPD-001"))
                .andExpect(jsonPath("$[0].statusJabatan").value("UTAMA"))
                .andExpect(jsonPath("$[0].jenisJabatan").value("JABATAN_PEMIMPIN_TINGGI"))
                .andExpect(jsonPath("$[0].eselon").value("ESELON_IV"))
                .andExpect(jsonPath("$[0].pangkat").value("Junior"))
                .andExpect(jsonPath("$[0].golongan").value("Golongan I"))
                .andExpect(jsonPath("$[0].tanggalMulai").value("01-01-2023"))
                .andExpect(jsonPath("$[0].tanggalAkhir").value("31-12-2025"));
    }

    @Test
    void getByNip_WhenNoJabatansExist_ShouldReturnEmptyList() throws Exception {
        when(jabatanService.listJabatanByNipWithPegawai("999999999999999999")).thenReturn(List.of());

        mockMvc.perform(get("/jabatan/detail/nip/999999999999999999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
    
    @Test
    void tambah_WhenValidJabatanRequest_ShouldCreateJabatan() throws Exception {
        testJabatanRequest = new JabatanRequest(
                null,
                "201001012010011001",
                "Analis Ahli Utama",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL,
                Eselon.ESELON_III,
                "Senior",
                "Golongan III",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime()
        );
        
        Jabatan createJabatan = new Jabatan(
                2L, 
                "201001012010011001",
                "Analis Ahli Utama",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL,
                Eselon.ESELON_III,
                "Senior",
                "Golongan III",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );
        
        when(jabatanService.tambahJabatan(any(Jabatan.class))).thenReturn(createJabatan);
        
        mockMvc.perform(post("/jabatan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testJabatanRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.nip").value("201001012010011001"))
                .andExpect(jsonPath("$.namaJabatan").value("Analis Ahli Utama"))
                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                .andExpect(jsonPath("$.statusJabatan").value("UTAMA"))
                .andExpect(jsonPath("$.jenisJabatan").value("JABATAN_FUNGSIONAL"))
                .andExpect(jsonPath("$.eselon").value("ESELON_III"))
                .andExpect(jsonPath("$.pangkat").value("Senior"))
                .andExpect(jsonPath("$.golongan").value("Golongan III"))
                .andExpect(jsonPath("$.tanggalMulai").value("01-01-2023"))
                .andExpect(jsonPath("$.tanggalAkhir").value("31-12-2025"));
    }
    
    @Test
    void tambah_WhenInvalidJabatanRequest_ShouldReturn400() throws Exception {
        JabatanRequest request = new JabatanRequest(
                null,
                "",
                "",
                "",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_FUNGSIONAL,
                Eselon.ESELON_III,
                "",
                "",
                null,
                null
        );
        
        mockMvc.perform(post("/jabatan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nip").exists())
                .andExpect(jsonPath("$.namaJabatan").exists())
                .andExpect(jsonPath("$.kodeOpd").exists())
                .andExpect(jsonPath("$.pangkat").exists())
                .andExpect(jsonPath("$.golongan").exists())
                .andExpect(jsonPath("$.tanggalMulai").exists());
    }
    
    @Test
    void post_WhenOpdNotExists_ShouldReturn404() throws Exception {
        when(jabatanService.tambahJabatan(any(Jabatan.class)))
                .thenThrow(new OpdNotFoundException("OPD-002"));
        
        mockMvc.perform(post("/jabatan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testJabatanRequest)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void ubahJabatan_WhenValidRequest_ShouldUpdateJabatan() throws Exception {
        JabatanRequest request = new JabatanRequest(
                1L,
                "200501012010011005",
                "Teknisi Listrik",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_ADMINISTRASI,
                Eselon.ESELON_II,
                "Middle",
                "Golongan II",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime()
        );
        
        Jabatan existingJabatan = new Jabatan(
                1L,
                "198001012010011001",
                "Kepala Dinas",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_PEMIMPIN_TINGGI,
                Eselon.ESELON_IV,
                "Junior",
                "Golongan I",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );
        
        Jabatan updateJabatan = new Jabatan(
                1L,
                "200501012010011005",
                "Teknisi Listrik",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_ADMINISTRASI,
                Eselon.ESELON_II,
                "Middle",
                "Golongan II",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                Instant.now(),
                Instant.now()
        );
        
        when(jabatanService.detailJabatan(1L)).thenReturn(existingJabatan);
        when(jabatanService.ubahJabatan(eq(1L), any(Jabatan.class))).thenReturn(updateJabatan);
        
        mockMvc.perform(put("/jabatan/update/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nip", is("200501012010011005")))
                .andExpect(jsonPath("$.namaJabatan", is("Teknisi Listrik")))
                .andExpect(jsonPath("$.kodeOpd", is("OPD-001")))
                .andExpect(jsonPath("$.statusJabatan", is("UTAMA")))
                .andExpect(jsonPath("$.jenisJabatan", is("JABATAN_ADMINISTRASI")))
                .andExpect(jsonPath("$.eselon", is("ESELON_II")))
                .andExpect(jsonPath("$.pangkat", is("Middle")))
                .andExpect(jsonPath("$.golongan", is("Golongan II")))
                .andExpect(jsonPath("$.tanggalMulai").value("01-01-2023"))
                .andExpect(jsonPath("$.tanggalAkhir").value("31-12-2025"));
        
        verify(jabatanService).detailJabatan(1L);
        verify(jabatanService).ubahJabatan(eq(1L), any(Jabatan.class));
    }
    
    @Test
    void ubahJabatan_WhenIdNotExists_ShouldReturn404() throws Exception {
        JabatanRequest request = new JabatanRequest(
                3L,
                "200501012010011005",
                "Teknisi Listrik",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_ADMINISTRASI,
                Eselon.ESELON_II,
                "Middle",
                "Golongan II",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime()
        );
        
        when(jabatanService.detailJabatan(3L)).thenThrow(new JabatanNotFoundException(3L));
        
        mockMvc.perform(put("/jabatan/update/{id}", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(jabatanService).detailJabatan(3L);
        verify(jabatanService, never()).ubahJabatan(anyLong(), any(Jabatan.class));
    }
    
    @Test
    void ubahJabatan_WhenKodeOpdNotExists_ShouldReturn404() throws Exception {
        JabatanRequest request = new JabatanRequest(
                1L,
                "200501012010011005",
                "Teknisi Listrik",
                "OPD-003",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_ADMINISTRASI,
                Eselon.ESELON_II,
                "Middle",
                "Golongan II",
                tanggalMulai.getTime(),
                tanggalAkhir.getTime()
        );
        
        when(jabatanService.detailJabatan(1L)).thenReturn(testJabatan);
        when(jabatanService.ubahJabatan(eq(1L), any(Jabatan.class)))
                .thenThrow(new OpdNotFoundException("OPD-999"));
        
        mockMvc.perform(put("/jabatan/update/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(jabatanService).detailJabatan(1L);
        verify(jabatanService).ubahJabatan(eq(1L), any(Jabatan.class));
    }
    
    @Test
    void hapusJabatan_WhenJabatanExists_ShouldDeleteJabatan() throws Exception {
        doNothing().when(jabatanService).hapusJabatan(1L);
        
        mockMvc.perform(delete("/jabatan/delete/{id}", "1"))
                .andExpect(status().isNoContent());
        
        verify(jabatanService).hapusJabatan(1L);
    }
    
    @Test
    void hapusJabatan_WhenJabatanNotExists_ShouldReturn404() throws Exception {
        doThrow(new JabatanNotFoundException(3L)).when(jabatanService).hapusJabatan(3L);

        mockMvc.perform(delete("/jabatan/delete/{id}", "3"))
                .andExpect(status().isNotFound());

        verify(jabatanService).hapusJabatan(3L);
    }
}
