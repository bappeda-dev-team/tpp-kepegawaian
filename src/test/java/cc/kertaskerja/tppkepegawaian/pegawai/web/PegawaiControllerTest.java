package cc.kertaskerja.tppkepegawaian.pegawai.web;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.*;
import cc.kertaskerja.tppkepegawaian.pegawai.web.PegawaiRequest;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanAndRolesResponse;
import cc.kertaskerja.tppkepegawaian.pegawai.web.response.PegawaiWithJabatanResponse;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.role.domain.Role;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
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
    private PegawaiWithJabatanResponse testPegawaiWithJabatanAndTppResponse;
    private PegawaiWithJabatanResponse testPegawaiWithJabatanWithoutTppResponse;
    private Jabatan testJabatan;
    private Tpp testTpp;
    private List<TppPerhitungan> testPerhitunganList;

    @BeforeEach
    void setUp() {
        testPegawai = new Pegawai(
            1L,
            "John Doe",
            "198001012010011001",
            "OPD-001",
            "Admin",
            "Aktif",
            "hashedpassword",
            Instant.now(),
            Instant.now()
        );

        testJabatan = new Jabatan(
            1L,
            "198001012010011001",
            "Kepala Seksi",
            "OPD-001",
            "AKTIF",
            "STRUKTURAL",
            "III/a",
            "Penata Muda",
            "III/a",
            new java.util.Date(),
            new java.util.Date(),
            Instant.now(),
            Instant.now()
        );

        testTpp = new Tpp(
            1L,
            "TPP_REGULER",
            "OPD-001",
            "198001012010011001",
            "KODE_PEMDA_001",
            5000000.0f,
            10.0f,
            2.0f,
            10,
            2024,
            Instant.now(),
            Instant.now()
        );

        // Create test perhitungan list
        testPerhitunganList = Arrays.asList(
            new TppPerhitungan(
                1L,
                "TPP_REGULER",
                "OPD-001",
                "KODE_PEMDA_001",
                "198001012010011001",
                "John Doe",
                10,
                2024,
                5000000.0f,
                "Kehadiran",
                80.0f,
                Instant.now(),
                Instant.now()
            ),
            new TppPerhitungan(
                2L,
                "TPP_REGULER",
                "OPD-001",
                "KODE_PEMDA_001",
                "198001012010011001",
                "John Doe",
                10,
                2024,
                5000000.0f,
                "Kinerja",
                100.0f,
                Instant.now(),
                Instant.now()
            )
        );

        // Create response with TPP data
        testPegawaiWithJabatanAndTppResponse = PegawaiWithJabatanResponse.from(testPegawai, testJabatan, Optional.of(testTpp), testPerhitunganList);

        // Create response without TPP data
        testPegawaiWithJabatanWithoutTppResponse = PegawaiWithJabatanResponse.from(testPegawai, testJabatan, Optional.empty(), List.of());

        // Keep existing response for backward compatibility test
        testPegawaiWithJabatanResponse = testPegawaiWithJabatanAndTppResponse;
    }

    @Test
    void detailByNip_WhenPegawaiExists_ShouldReturnPegawaiWithJabatanAndTpp() throws Exception {
        when(pegawaiService.detailPegawaiWithJabatan("198001012010011001")).thenReturn(testPegawaiWithJabatanAndTppResponse);

        mockMvc.perform(get("/pegawai/detail/198001012010011001"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.nama_pegawai").value("John Doe"))
            .andExpect(jsonPath("$.nip").value("198001012010011001"))
            .andExpect(jsonPath("$.kode_opd").value("OPD-001"))
            .andExpect(jsonPath("$.nama_role").value("Admin"))
            .andExpect(jsonPath("$.status_pegawai").value("Aktif"))
            .andExpect(jsonPath("$.nama_jabatan").value("Kepala Seksi"))
            .andExpect(jsonPath("$.status_jabatan").value("AKTIF"))
            .andExpect(jsonPath("$.jenis_jabatan").value("STRUKTURAL"))
            .andExpect(jsonPath("$.eselon").value("III/a"))
            .andExpect(jsonPath("$.pangkat").value("Penata Muda"))
            .andExpect(jsonPath("$.golongan").value("III/a"))
            .andExpect(jsonPath("$.jenis_tpp").value("TPP_REGULER"))
            .andExpect(jsonPath("$.bulan").value(10))
            .andExpect(jsonPath("$.tahun").value(2024))
            .andExpect(jsonPath("$.total_terima_tpp").value(7920000L));
    }

    @Test
    void detailByNip_WhenPegawaiNotExists_ShouldReturnNotFound() throws Exception {
        when(pegawaiService.detailPegawaiWithJabatan("999999999999999999")).thenThrow(new PegawaiNotFoundException("999999999999999999"));

        mockMvc.perform(get("/pegawai/detail/999999999999999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void detailByNip_WhenPegawaiExistsWithoutJabatanAndTpp_ShouldReturnPegawaiWithNullJabatanAndTppFields() throws Exception {
        PegawaiWithJabatanResponse responseWithoutJabatanAndTpp = PegawaiWithJabatanResponse.from(testPegawai, null, Optional.empty(), List.of());
        when(pegawaiService.detailPegawaiWithJabatan("198001012010011001")).thenReturn(responseWithoutJabatanAndTpp);

        mockMvc.perform(get("/pegawai/detail/198001012010011001"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.nama_pegawai").value("John Doe"))
            .andExpect(jsonPath("$.nip").value("198001012010011001"))
            .andExpect(jsonPath("$.kode_opd").value("OPD-001"))
            .andExpect(jsonPath("$.nama_role").value("Admin"))
            .andExpect(jsonPath("$.status_pegawai").value("Aktif"))
            .andExpect(jsonPath("$.nama_jabatan").isEmpty())
            .andExpect(jsonPath("$.status_jabatan").isEmpty())
            .andExpect(jsonPath("$.jenis_jabatan").isEmpty())
            .andExpect(jsonPath("$.eselon").isEmpty())
            .andExpect(jsonPath("$.pangkat").isEmpty())
            .andExpect(jsonPath("$.golongan").isEmpty())
            .andExpect(jsonPath("$.jenis_tpp").isEmpty())
            .andExpect(jsonPath("$.bulan").isEmpty())
            .andExpect(jsonPath("$.tahun").isEmpty())
            .andExpect(jsonPath("$.total_terima_tpp").isEmpty());
    }

    @Test
    void getAllPegawaiByKodeOpd_WhenKodeOpdExists_ShouldReturnPegawaiWithRoleList() throws Exception {
        String kodeOpd = "OPD-001";
        Set<Role> roles1 = Set.of(new Role(1L, "Admin", "198001012010011001", null, "Aktif", Instant.now(), Instant.now()));
        Set<Role> roles2 = Set.of(new Role(2L, "User", "201001012010011001", null, "Aktif", Instant.now(), Instant.now()));
        Jabatan jabatan1 = new Jabatan(
            1L,
            "198001012010011001",
            "Analis Kebijakan Industrialisasi",
            "OPD-001",
            "AKTIF",
            "STRUKTURAL",
            "III/a",
            "Senior",
            "III/a",
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
            "AKTIF",
            "STRUKTURAL",
            "III/a",
            "Junior",
            "II/a",
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
            .andExpect(jsonPath("$[0].isActive", is("Aktif")))
            .andExpect(jsonPath("$[0].namaJabatan", is("Analis Kebijakan Industrialisasi")))
            .andExpect(jsonPath("$[0].statusJabatan", is("AKTIF")))
            .andExpect(jsonPath("$[0].jenisJabatan", is("STRUKTURAL")))
            .andExpect(jsonPath("$[0].eselon", is("III/a")))
            .andExpect(jsonPath("$[0].pangkat", is("Senior")))
            .andExpect(jsonPath("$[0].golongan", is("III/a")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].namaPegawai", is("Jane Doe")))
            .andExpect(jsonPath("$[1].nip", is("201001012010011001")))
            .andExpect(jsonPath("$[1].kodeOpd", is("OPD-001")))
            .andExpect(jsonPath("$[1].namaRole", is("User")))
            .andExpect(jsonPath("$[1].isActive", is("Aktif")))
            .andExpect(jsonPath("$[1].namaJabatan", is("Analis Kebijakan")))
            .andExpect(jsonPath("$[1].statusJabatan", is("AKTIF")))
            .andExpect(jsonPath("$[1].jenisJabatan", is("STRUKTURAL")))
            .andExpect(jsonPath("$[1].eselon", is("III/a")))
            .andExpect(jsonPath("$[1].pangkat", is("Junior")))
            .andExpect(jsonPath("$[1].golongan", is("II/a")));

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
            new Pegawai(1L, "John Doe", "198001012010011001", "OPD-001", "Admin", "Aktif", "hashedpassword123", Instant.now(), Instant.now()),
            new Pegawai(2L, "Jane Doe", "201001012010011001", "OPD-002", "Admin", "Aktif", "hashedpassword456", Instant.now(), Instant.now())
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
            "Aktif",
            "hashedpassword123"
        );

        Pegawai createPegawai = new Pegawai(
            2L,
            "Jane Doe",
            "201001012010011001",
            "OPD-001",
            "Admin",
            "Aktif",
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
            .andExpect(jsonPath("$.statusPegawai").value("Aktif"));
    }

    @Test
    void tambah_WhenInvalidPegawaiRequest_ShouldReturn400() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
            null,
            "",
            "",
            "",
            "",
            "Aktif",
            ""
        );

        mockMvc.perform(post("/pegawai")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void tambah_WhenInvalidNipFormat_ShouldReturn400() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
            null,
            "Jane Doe",
            "12345", // Invalid NIP format - should be 18 digits
            "OPD-001",
            "Admin",
            "Aktif",
            "hashedpassword123"
        );

        mockMvc.perform(post("/pegawai")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void tambah_WhenOpdNotExists_ShouldReturn404() throws Exception {
        testPegawaiRequest = new PegawaiRequest(
            null,
            "Test User",
            "201001012010011001",
            "OPD-002",
            "Admin",
            "Aktif",
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
        String nipToUpdate = "198001012010011001";
        PegawaiRequest request = new PegawaiRequest(
            1L,
            "Rohman",
            nipToUpdate,
            "OPD-001",
            "User",
            "CUTI",
            "password213"
        );

        Pegawai existingPegawai = new Pegawai(
            1L,
            "John Doe",
            nipToUpdate,
            "OPD-001",
            "Admin",
            "Aktif",
            "hashedpassword",
            Instant.now(),
            Instant.now()
        );

        Pegawai updatePegawai = new Pegawai(
            1L,
            "Rohman",
            nipToUpdate,
            "OPD-001",
            "User",
            "CUTI",
            "password213",
            existingPegawai.createdDate(), // Keep original created date
            Instant.now()
        );

        when(pegawaiService.detailPegawai(nipToUpdate)).thenReturn(existingPegawai);
        when(pegawaiService.ubahPegawai(eq(nipToUpdate), any(Pegawai.class))).thenReturn(updatePegawai);

        mockMvc.perform(put("/pegawai/update/{nip}", nipToUpdate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.namaPegawai", is("Rohman")))
            .andExpect(jsonPath("$.nip", is(nipToUpdate)))
            .andExpect(jsonPath("$.kodeOpd", is("OPD-001")))
            .andExpect(jsonPath("$.namaRole", is("User")))
            .andExpect(jsonPath("$.statusPegawai", is("CUTI")));

        verify(pegawaiService).detailPegawai(nipToUpdate);
        verify(pegawaiService).ubahPegawai(eq(nipToUpdate), any(Pegawai.class));
    }

    @Test
    void ubahPegawai_WhenNipNotExists_ShouldReturn404() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
            1L,
            "Rohman",
            "198201012010011002",
            "OPD-001",
            "User",
            "CUTI",
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
    void ubahPegawai_WhenInvalidNipFormat_ShouldReturn400() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
            1L,
            "Rohman",
            "12345", // Invalid NIP format - should be 18 digits
            "OPD-001",
            "User",
            "CUTI",
            "password213"
        );

        mockMvc.perform(put("/pegawai/update/{nip}", "198001012010011001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void ubahPegawai_WhenKodeOpdNotExists_ShouldReturn404() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
            1L,
            "Rohman",
            "198001012010011001",
            "OPD-003",
            "User",
            "CUTI",
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

    @Test
    void detailByNip_WhenPegawaiHasTppWithDifferentPerhitungan_ShouldCalculateCorrectTotalTerimaTpp() throws Exception {
        Tpp differentTpp = new Tpp(
            2L,
            "TPP_TUNJANGAN",
            "OPD-001",
            "198001012010011001",
            "KODE_PEMDA_001",
            10000000.0f,
            10.0f,
            3.0f,
            11,
            2024,
            Instant.now(),
            Instant.now()
        );

        // Create perhitungan list with different values
        List<TppPerhitungan> differentPerhitunganList = Arrays.asList(
            new TppPerhitungan(
                3L,
                "TPP_TUNJANGAN",
                "OPD-001",
                "KODE_PEMDA_001",
                "198001012010011001",
                "John Doe",
                11,
                2024,
                10000000.0f,
                "Kehadiran",
                50.0f,
                Instant.now(),
                Instant.now()
            ),
            new TppPerhitungan(
                4L,
                "TPP_TUNJANGAN",
                "OPD-001",
                "KODE_PEMDA_001",
                "198001012010011001",
                "John Doe",
                11,
                2024,
                10000000.0f,
                "Kinerja",
                75.0f,
                Instant.now(),
                Instant.now()
            )
        );

        // Expected calculation:
        // Maximum TPP: 10,000,000
        // Total percentage: 50% + 75% = 125%
        // Total TPP: 10,000,000 * (125/100) = 12,500,000
        // Tax (10%): 1,250,000
        // BPJS (3%): 375,000
        // Total received: 12,500,000 - 1,250,000 - 375,000 = 10,875,000

        PegawaiWithJabatanResponse responseWithDifferentTpp = PegawaiWithJabatanResponse.from(
            testPegawai, testJabatan, Optional.of(differentTpp), differentPerhitunganList);

        when(pegawaiService.detailPegawaiWithJabatan("198001012010011001")).thenReturn(responseWithDifferentTpp);

        mockMvc.perform(get("/pegawai/detail/198001012010011001"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.nama_pegawai").value("John Doe"))
            .andExpect(jsonPath("$.jenis_tpp").value("TPP_TUNJANGAN"))
            .andExpect(jsonPath("$.bulan").value(11))
            .andExpect(jsonPath("$.tahun").value(2024))
            .andExpect(jsonPath("$.total_terima_tpp").value(10875000L));
    }

    @Test
    void detailByNip_WhenPegawaiHasTppWithoutPerhitungan_ShouldReturnZeroTotalTerimaTpp() throws Exception {
        Tpp tppWithoutPerhitungan = new Tpp(
            3L,
            "TPP_LEMBUR",
            "OPD-001",
            "198001012010011001",
            "KODE_PEMDA_001",
            3000000.0f,
            5.0f,
            1.0f,
            12,
            2024,
            Instant.now(),
            Instant.now()
        );

        // Empty perhitungan list
        List<TppPerhitungan> emptyPerhitunganList = List.of();

        PegawaiWithJabatanResponse responseWithoutPerhitungan = PegawaiWithJabatanResponse.from(
            testPegawai, testJabatan, Optional.of(tppWithoutPerhitungan), emptyPerhitunganList);

        when(pegawaiService.detailPegawaiWithJabatan("198001012010011001")).thenReturn(responseWithoutPerhitungan);

        mockMvc.perform(get("/pegawai/detail/198001012010011001"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.jenis_tpp").value("TPP_LEMBUR"))
            .andExpect(jsonPath("$.bulan").value(12))
            .andExpect(jsonPath("$.tahun").value(2024))
            .andExpect(jsonPath("$.total_terima_tpp").value(0));
    }
}
