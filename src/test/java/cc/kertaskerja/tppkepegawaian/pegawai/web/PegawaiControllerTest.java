package cc.kertaskerja.tppkepegawaian.pegawai.web;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.*;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanAndRolesResponse;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Eselon;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JenisJabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.StatusJabatan;
import cc.kertaskerja.tppkepegawaian.role.domain.IsActive;
import cc.kertaskerja.tppkepegawaian.role.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.ObjectMapper;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PegawaiController.class)
public class PegawaiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private PegawaiService pegawaiService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Pegawai testPegawai;
    private PegawaiRequest testPegawaiRequest;
    private PegawaiWithJabatanResponse testPegawaiWithJabatanResponse;
    private Jabatan testJabatan;
    
    @BeforeEach
    void setUp() {
        testPegawai = new Pegawai(
                1L,
                "John Doe",
                "198001012010011001",
                "OPD-001",
                "Admin",
                StatusPegawai.AKTIF,
                "hashedpassword",
                Instant.now(),
                Instant.now()
        );
        
        testJabatan = new Jabatan(
                1L,
                "198001012010011001",
                "Kepala Seksi",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.ESELON_III,
                "Penata Muda",
                "III/a",
                new java.util.Date(),
                new java.util.Date(),
                Instant.now(),
                Instant.now()
        );
        
        testPegawaiWithJabatanResponse = PegawaiWithJabatanResponse.from(testPegawai, testJabatan);
    }
    
    @Test
    void detailByNip_WhenPegawaiExists_ShouldReturnPegawaiWithJabatan() throws Exception {
        when(pegawaiService.detailPegawaiWithJabatan("198001012010011001")).thenReturn(testPegawaiWithJabatanResponse);
        
        mockMvc.perform(get("/pegawai/detail/198001012010011001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nama_pegawai").value("John Doe"))
                .andExpect(jsonPath("$.nip").value("198001012010011001"))
                .andExpect(jsonPath("$.kode_opd").value("OPD-001"))
                .andExpect(jsonPath("$.nama_role").value("Admin"))
                .andExpect(jsonPath("$.status_pegawai").value("AKTIF"))
                .andExpect(jsonPath("$.nama_jabatan").value("Kepala Seksi"))
                .andExpect(jsonPath("$.status_jabatan").value("UTAMA"))
                .andExpect(jsonPath("$.jenis_jabatan").value("JABATAN_STRUKTURAL"))
                .andExpect(jsonPath("$.eselon").value("ESELON_III"))
                .andExpect(jsonPath("$.pangkat").value("Penata Muda"))
                .andExpect(jsonPath("$.golongan").value("III/a"));
    }
    
    @Test
    void detailByNip_WhenPegawaiNotExists_ShouldReturnNotFound() throws Exception {
        when(pegawaiService.detailPegawaiWithJabatan("999999999999999999")).thenThrow(new PegawaiNotFoundException("999999999999999999"));
        
        mockMvc.perform(get("/pegawai/detail/999999999999999999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void detailByNip_WhenPegawaiExistsWithoutJabatan_ShouldReturnPegawaiWithNullJabatanFields() throws Exception {
        PegawaiWithJabatanResponse responseWithoutJabatan = PegawaiWithJabatanResponse.from(testPegawai, null);
        when(pegawaiService.detailPegawaiWithJabatan("198001012010011001")).thenReturn(responseWithoutJabatan);
        
        mockMvc.perform(get("/pegawai/detail/198001012010011001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nama_pegawai").value("John Doe"))
                .andExpect(jsonPath("$.nip").value("198001012010011001"))
                .andExpect(jsonPath("$.kode_opd").value("OPD-001"))
                .andExpect(jsonPath("$.nama_role").value("Admin"))
                .andExpect(jsonPath("$.status_pegawai").value("AKTIF"))
                .andExpect(jsonPath("$.nama_jabatan").isEmpty())
                .andExpect(jsonPath("$.status_jabatan").isEmpty())
                .andExpect(jsonPath("$.jenis_jabatan").isEmpty())
                .andExpect(jsonPath("$.eselon").isEmpty())
                .andExpect(jsonPath("$.pangkat").isEmpty())
                .andExpect(jsonPath("$.golongan").isEmpty());
    }
    
    @Test
    void getAllPegawaiByKodeOpd_WhenKodeOpdExists_ShouldReturnPegawaiWithRoleList() throws Exception {
        String kodeOpd = "OPD-001";
        Set<Role> roles1 = Set.of(new Role(1L, "Admin", "198001012010011001", null, IsActive.AKTIF, Instant.now(), Instant.now()));
        Set<Role> roles2 = Set.of(new Role(2L, "User", "201001012010011001", null, IsActive.AKTIF, Instant.now(), Instant.now()));
        Jabatan jabatan1 = new Jabatan(
                1L,
                "198001012010011001",
                "Analis Kebijakan Industrialisasi",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.ESELON_III,
                "Senior",
                "Golongan III",
                new java.util.Date(),
                new java.util.Date(),
                Instant.now(),
                Instant.now()
        );
        Jabatan jabatan2 = new Jabatan(
                2L,
                "201001012010011001",
                "Analis Kebijakan",
                "OPD-001",
                StatusJabatan.UTAMA,
                JenisJabatan.JABATAN_STRUKTURAL,
                Eselon.ESELON_IV,
                "Junior",
                "Golongan II",
                new java.util.Date(),
                new java.util.Date(),
                Instant.now(),
                Instant.now()
        );

        List<PegawaiWithJabatanAndRolesResponse> pegawaiList = Arrays.asList(
                PegawaiWithJabatanAndRolesResponse.of(1L, "John Doe", "198001012010011001", "OPD-001", roles1, jabatan1),
                PegawaiWithJabatanAndRolesResponse.of(2L, "Jane Doe", "201001012010011001", "OPD-001", roles2, jabatan2)
        );

        when(pegawaiService.listAllPegawaiWithJabatanByKodeOpd(kodeOpd)).thenReturn(pegawaiList);

        mockMvc.perform(get("/pegawai/detail/master/opd/{kodeOpd}", kodeOpd))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].namaPegawai", is("John Doe")))
                .andExpect(jsonPath("$[0].nip", is("198001012010011001")))
                .andExpect(jsonPath("$[0].kodeOpd", is("OPD-001")))
                .andExpect(jsonPath("$[0].namaRole", is("Admin")))
                .andExpect(jsonPath("$[0].isActive", is("AKTIF")))
                .andExpect(jsonPath("$[0].namaJabatan", is("Analis Kebijakan Industrialisasi")))
                .andExpect(jsonPath("$[0].statusJabatan", is("UTAMA")))
                .andExpect(jsonPath("$[0].jenisJabatan", is("JABATAN_STRUKTURAL")))
                .andExpect(jsonPath("$[0].eselon", is("ESELON_III")))
                .andExpect(jsonPath("$[0].pangkat", is("Senior")))
                .andExpect(jsonPath("$[0].golongan", is("Golongan III")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].namaPegawai", is("Jane Doe")))
                .andExpect(jsonPath("$[1].nip", is("201001012010011001")))
                .andExpect(jsonPath("$[1].kodeOpd", is("OPD-001")))
                .andExpect(jsonPath("$[1].namaRole", is("User")))
                .andExpect(jsonPath("$[1].isActive", is("AKTIF")))
                .andExpect(jsonPath("$[1].namaJabatan", is("Analis Kebijakan")))
                .andExpect(jsonPath("$[1].statusJabatan", is("UTAMA")))
                .andExpect(jsonPath("$[1].jenisJabatan", is("JABATAN_STRUKTURAL")))
                .andExpect(jsonPath("$[1].eselon", is("ESELON_IV")))
                .andExpect(jsonPath("$[1].pangkat", is("Junior")))
                .andExpect(jsonPath("$[1].golongan", is("Golongan II")));

        verify(pegawaiService).listAllPegawaiWithJabatanByKodeOpd(kodeOpd);
    }
    
    @Test
    void getAllPegawaiByKodeOpd_WhenKodeOpdNotExists_ShouldReturnEmptyList() throws Exception {
        String kodeOpd = "OPD-999";
        List<PegawaiWithJabatanAndRolesResponse> emptyList = Collections.emptyList();

        when(pegawaiService.listAllPegawaiWithJabatanByKodeOpd(kodeOpd)).thenReturn(emptyList);

        mockMvc.perform(get("/pegawai/detail/master/opd/{kodeOpd}", kodeOpd))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(pegawaiService).listAllPegawaiWithJabatanByKodeOpd(kodeOpd);
    }
    
    @Test
    void getAllPegawaiByRole_WhenRoleExists_ShouldReturnPegawaiList() throws Exception {
        String namaRole = "Admin";
        List<Pegawai> pegawaiList = Arrays.asList(
                new Pegawai(1L, "John Doe", "198001012010011001", "OPD-001", "Admin", StatusPegawai.AKTIF, "hashedpassword123", Instant.now(), Instant.now()),
                new Pegawai(2L, "Jane Doe", "201001012010011001", "OPD-002", "Admin", StatusPegawai.AKTIF, "hashedpassword456", Instant.now(), Instant.now())
        );

        when(pegawaiService.listAllPegawaiByRole(namaRole)).thenReturn(pegawaiList);

        mockMvc.perform(get("/pegawai/role/{namaRole}", namaRole))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nip", is("198001012010011001")))
                .andExpect(jsonPath("$[0].kodeOpd", is("OPD-001")))
                .andExpect(jsonPath("$[0].namaRole", is("Admin")))
                .andExpect(jsonPath("$[1].nip", is("201001012010011001")))
                .andExpect(jsonPath("$[1].kodeOpd", is("OPD-002")))
                .andExpect(jsonPath("$[1].namaRole", is("Admin")));

        verify(pegawaiService).listAllPegawaiByRole(namaRole);
    }
    
    @Test
    void getAllPegawaiByRole_WhenRoleNotExists_ShouldReturnEmptyList() throws Exception {
        String namaRole = "Salah";
        when(pegawaiService.listAllPegawaiByRole(namaRole)).thenReturn(List.of());
        
        mockMvc.perform(get("/pegawai/role/{namaRole}", namaRole))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
        
        verify(pegawaiService).listAllPegawaiByRole(namaRole);
    }
    
    @Test
    void tambah_WhenValidPegawaiRequest_ShouldCreatePegawai() throws Exception {
        testPegawaiRequest = new PegawaiRequest(
                null,
                "Jane Doe",
                "201001012010011001",
                "OPD-001",
                "Admin",
                StatusPegawai.AKTIF,
                "hashedpassword123"
        );
        
        Pegawai createPegawai = new Pegawai(
                2L,
                "Jane Doe",
                "201001012010011001",
                "OPD-001",
                "Admin",
                StatusPegawai.AKTIF,
                "hashedpassword123",
                Instant.now(),
                Instant.now()
        );
        
        when(pegawaiService.tambahPegawai(any(Pegawai.class))).thenReturn(createPegawai);
        
        mockMvc.perform(post("/pegawai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPegawaiRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.namaPegawai").value("Jane Doe"))
                .andExpect(jsonPath("$.nip").value("201001012010011001"))
                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                .andExpect(jsonPath("$.namaRole").value("Admin"))
                .andExpect(jsonPath("$.statusPegawai").value("AKTIF"));
    }
    
    @Test
    void tambah_WhenInvalidPegawaiRequest_ShouldReturn400() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
                null,
                "",
                "",
                "",
                "",
                StatusPegawai.AKTIF,
                ""
        );
        
        mockMvc.perform(post("/pegawai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void tambah_WhenPegawaiNotExists_ShouldReturn404() throws Exception {
        testPegawaiRequest = new PegawaiRequest(
                null,
                "Test User",
                "201001012010011001",
                "OPD-002",
                "Admin",
                StatusPegawai.AKTIF,
                "hashedpassword123"
        );

        when(pegawaiService.tambahPegawai(any(Pegawai.class)))
                .thenThrow(new PegawaiNotFoundException("OPD-002"));

        mockMvc.perform(post("/pegawai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPegawaiRequest)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void ubahPegawai_WhenValidRequest_ShouldUpdatePegawai() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
                1L,
                "Rohman",
                "199501012012011003",
                "OPD-001",
                "User",
                StatusPegawai.CUTI,
                "password213"
        );
        
        Pegawai existingPegawai = new Pegawai(
                1L,
                "John Doe",
                "198001012010011001",
                "OPD-001",
                "Admin",
                StatusPegawai.AKTIF,
                "hashedpassword",
                Instant.now(),
                Instant.now()
        );
        
        Pegawai updatePegawai = new Pegawai(
                1L,
                "Rohman",
                "199501012012011003",
                "OPD-001",
                "User",
                StatusPegawai.CUTI,
                "password213",
                Instant.now(),
                Instant.now()
        );
        
        when(pegawaiService.detailPegawai("199501012012011003")).thenReturn(existingPegawai);
        when(pegawaiService.ubahPegawai(eq("199501012012011003"), any(Pegawai.class))).thenReturn(updatePegawai);
        
        mockMvc.perform(put("/pegawai/update/{nip}", "199501012012011003")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.namaPegawai", is("Rohman")))
                .andExpect(jsonPath("$.nip", is("199501012012011003")))
                .andExpect(jsonPath("$.kodeOpd", is("OPD-001")))
                .andExpect(jsonPath("$.namaRole", is("User")))
                .andExpect(jsonPath("$.statusPegawai", is("CUTI")));
        
        verify(pegawaiService).detailPegawai("199501012012011003");
        verify(pegawaiService).ubahPegawai(eq("199501012012011003"), any(Pegawai.class));
    } 
    
    @Test
    void ubahPegawai_WhenNipNotExists_ShouldReturn404() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
                1L,
                "Rohman",
                "198201012010011002",
                "OPD-001",
                "User",
                StatusPegawai.CUTI,
                "password213"
        );
        
        when(pegawaiService.detailPegawai("198201012010011002")).thenThrow(new PegawaiNotFoundException("198201012010011002"));

        mockMvc.perform(put("/pegawai/update/{nip}", "198201012010011002")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(pegawaiService).detailPegawai("198201012010011002");
        verify(pegawaiService, never()).ubahPegawai(anyString(), any(Pegawai.class));
    }
    
    @Test
    void ubahPegawai_WhenKodeOpdNotExists_ShouldReturn404() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
                1L,
                "Rohman",
                "198001012010011001",
                "OPD-003",
                "User",
                StatusPegawai.CUTI,
                "password213"
        );
        
        when(pegawaiService.detailPegawai("198001012010011001")).thenReturn(testPegawai);
        when(pegawaiService.ubahPegawai(eq("198001012010011001"), any(Pegawai.class)))
                .thenThrow(new OpdNotFoundException("OPD-003"));
        
        mockMvc.perform(put("/pegawai/update/{nip}", "198001012010011001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(pegawaiService).detailPegawai("198001012010011001");
        verify(pegawaiService).ubahPegawai(eq("198001012010011001"), any(Pegawai.class));
    }
    
    @Test
    void hapusPegawai_WhenJabatanExists_ShouldDeletePegawai() throws Exception {
        doNothing().when(pegawaiService).hapusPegawai("198001012010011001");
        
        mockMvc.perform(delete("/pegawai/delete/{nip}", "198001012010011001"))
                .andExpect(status().isNoContent());
        
        verify(pegawaiService).hapusPegawai("198001012010011001");
    }
    
    @Test
    void hapusPegawai_WhenPegawaiNotExists_ShouldReturn404() throws Exception {
        doThrow(new PegawaiNotFoundException("198001012010011001")).when(pegawaiService).hapusPegawai("198001012010011001");
        
        mockMvc.perform(delete("/pegawai/delete/{nip}", "198001012010011001"))
                .andExpect(status().isNotFound());
        
        verify(pegawaiService).hapusPegawai("198001012010011001");
    }
}
