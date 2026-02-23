package cc.kertaskerja.tppkepegawaian.jabatan.web;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.Jabatan;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.JabatanService;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanPegawaiSudahAdaException;

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
import java.util.TimeZone;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JabatanController.class)
@SuppressWarnings("unused")
public class JabatanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JabatanService jabatanService;

    @Autowired
    private ObjectMapper objectMapper;

    private Jabatan testJabatan;
    private JabatanRequest testJabatanRequest;
    private JabatanWithPegawaiResponse testJabatanWithPegawaiResponse1;
    private JabatanWithPegawaiResponse testJabatanWithPegawaiResponse2;
    private JabatanWithTppPajakResponse testJabatanWithTppPajakResponse1;
    private JabatanWithTppPajakResponse testJabatanWithTppPajakResponse2;
    private Calendar tanggalMulai;
    private Calendar tanggalAkhir;

    @BeforeEach
    void setUp() {
        tanggalMulai = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        tanggalMulai.set(2023, Calendar.JANUARY, 1);

        tanggalAkhir = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        tanggalAkhir.set(2025, Calendar.DECEMBER, 31);

        testJabatan = new Jabatan(
            1L,
            "198001012010011001",
            "John Doe",
            "Kepala Dinas",
            "OPD-001",
            "Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            5000000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        Jabatan testJabatan2 = new Jabatan(
            2L,
            "199001012015021002",
            "Jane Smith",
            "Sekretaris Dinas",
            "OPD-002",
            "PLT Utama",
            "Jabatan Administrasi",
            "Eselon III",
            "Middle",
            "Golongan II",
            4500000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        testJabatanRequest = new JabatanRequest(
            null,
            "198001012010011001",
            "John Doe",
            "Kepala Dinas",
            "OPD-001",
            "Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            5500000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        testJabatanWithPegawaiResponse1 = new JabatanWithPegawaiResponse(
            1L,
            "198001012010011001",
            "John Doe",
            "Kepala Dinas",
            "OPD-001",
            "Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            5000000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        testJabatanWithPegawaiResponse2 = new JabatanWithPegawaiResponse(
            2L,
            "199001012015021002",
            "Jane Smith",
            "Sekretaris Dinas",
            "OPD-002",
            "PLT Utama",
            "Jabatan Administrasi",
            "Eselon III",
            "Middle",
            "Golongan II",
            4500000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        testJabatanWithTppPajakResponse1 = new JabatanWithTppPajakResponse(
                1L,
                "198001012010011001",
                "John Doe",
                "Kepala Dinas",
                "OPD-001",
                "Utama",
                "Jabatan Pemimpin Tinggi",
                "Eselon IV",
                "Junior",
                "Golongan I",
                5000000.0f,
                0.05f,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                1,
                2025
        );

        testJabatanWithTppPajakResponse2 = new JabatanWithTppPajakResponse(
                2L,
                "199001012015021002",
                "Jane Smith",
                "Sekretaris Dinas",
                "OPD-002",
                "PLT Utama",
                "Jabatan Administrasi",
                "Eselon III",
                "Middle",
                "Golongan II",
                4500000.0f,
                0.05f,
                tanggalMulai.getTime(),
                tanggalAkhir.getTime(),
                1,
                2025
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
            .andExpect(jsonPath("$.namaPegawai").value("John Doe"))
            .andExpect(jsonPath("$.namaJabatan").value("Kepala Dinas"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$.statusJabatan").value("Utama"))
            .andExpect(jsonPath("$.jenisJabatan").value("Jabatan Pemimpin Tinggi"))
            .andExpect(jsonPath("$.eselon").value("Eselon IV"))
            .andExpect(jsonPath("$.pangkat").value("Junior"))
            .andExpect(jsonPath("$.golongan").value("Golongan I"))
            .andExpect(jsonPath("$.basicTpp").value(5000000.0))
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
            .andExpect(jsonPath("$[0].statusJabatan").value("Utama"))
            .andExpect(jsonPath("$[0].jenisJabatan").value("Jabatan Pemimpin Tinggi"))
            .andExpect(jsonPath("$[0].eselon").value("Eselon IV"))
            .andExpect(jsonPath("$[0].pangkat").value("Junior"))
            .andExpect(jsonPath("$[0].golongan").value("Golongan I"))
            .andExpect(jsonPath("$[0].basicTpp").value(5000000.0))
            .andExpect(jsonPath("$[0].tanggalMulai").value("01-01-2023"))
            .andExpect(jsonPath("$[0].tanggalAkhir").value("31-12-2025"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].nip").value("199001012015021002"))
            .andExpect(jsonPath("$[1].namaPegawai").value("Jane Smith"))
            .andExpect(jsonPath("$[1].namaJabatan").value("Sekretaris Dinas"))
            .andExpect(jsonPath("$[1].kodeOpd").value("OPD-002"))
            .andExpect(jsonPath("$[1].statusJabatan").value("PLT Utama"))
            .andExpect(jsonPath("$[1].jenisJabatan").value("Jabatan Administrasi"))
            .andExpect(jsonPath("$[1].eselon").value("Eselon III"))
            .andExpect(jsonPath("$[1].pangkat").value("Middle"))
            .andExpect(jsonPath("$[1].golongan").value("Golongan II"))
            .andExpect(jsonPath("$[1].basicTpp").value(4500000.0))
            .andExpect(jsonPath("$[1].tanggalMulai").value("01-01-2023"))
            .andExpect(jsonPath("$[1].tanggalAkhir").value("31-12-2025"));
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
            .andExpect(jsonPath("$[0].statusJabatan").value("Utama"))
            .andExpect(jsonPath("$[0].jenisJabatan").value("Jabatan Pemimpin Tinggi"))
            .andExpect(jsonPath("$[0].eselon").value("Eselon IV"))
            .andExpect(jsonPath("$[0].pangkat").value("Junior"))
            .andExpect(jsonPath("$[0].golongan").value("Golongan I"))
            .andExpect(jsonPath("$[0].basicTpp").value(5000000.0))
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
    void getByNip_WhenMultipleStatusJabatanExists_ShouldReturnSortedJabatanWithPegawaiList() throws Exception {
        JabatanWithPegawaiResponse pltJabatanResponse = new JabatanWithPegawaiResponse(
            2L,
            "123456789012345678",
            "Dino",
            "Pelaksana Tugas",
            "OPD-001",
            "PLT Utama",
            "Jabatan Struktural",
            "Eselon II",
            "Sepuh",
            "Golongan IV",
            2000000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        JabatanWithPegawaiResponse utamaJabatanResponse = new JabatanWithPegawaiResponse(
            1L,
            "123456789012345678",
            "Dino",
            "Analis Kebijakan Industrialisasi",
            "OPD-001",
            "Utama",
            "Jabatan Struktural",
            "Eselon III",
            "Senior",
            "Golongan III",
            3000000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        when(jabatanService.listJabatanByNipWithPegawai("123456789012345678"))
            .thenReturn(List.of(utamaJabatanResponse, pltJabatanResponse));

        mockMvc.perform(get("/jabatan/detail/nip/123456789012345678"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].nip").value("123456789012345678"))
            .andExpect(jsonPath("$[0].namaPegawai").value("Dino"))
            .andExpect(jsonPath("$[0].namaJabatan").value("Analis Kebijakan Industrialisasi"))
            .andExpect(jsonPath("$[0].kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$[0].statusJabatan").value("Utama"))
            .andExpect(jsonPath("$[0].jenisJabatan").value("Jabatan Struktural"))
            .andExpect(jsonPath("$[0].eselon").value("Eselon III"))
            .andExpect(jsonPath("$[0].pangkat").value("Senior"))
            .andExpect(jsonPath("$[0].golongan").value("Golongan III"))
            .andExpect(jsonPath("$[0].basicTpp").value(3000000.0))
            .andExpect(jsonPath("$[0].tanggalMulai").value("01-01-2023"))
            .andExpect(jsonPath("$[0].tanggalAkhir").value("31-12-2025"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].nip").value("123456789012345678"))
            .andExpect(jsonPath("$[1].namaPegawai").value("Dino"))
            .andExpect(jsonPath("$[1].namaJabatan").value("Pelaksana Tugas"))
            .andExpect(jsonPath("$[1].kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$[1].statusJabatan").value("PLT Utama"))
            .andExpect(jsonPath("$[1].jenisJabatan").value("Jabatan Struktural"))
            .andExpect(jsonPath("$[1].eselon").value("Eselon II"))
            .andExpect(jsonPath("$[1].pangkat").value("Sepuh"))
            .andExpect(jsonPath("$[1].golongan").value("Golongan IV"))
            .andExpect(jsonPath("$[1].basicTpp").value(2000000.0));
    }

    @Test
    void getByNipBatch_WhenJabatansExist_ShouldReturnJabatanWithPegawaiList() throws Exception {
        NipBatchRequest request = new NipBatchRequest();
        request.setNipPegawais(List.of("198001012010011001", "199001012015021002"));

        when(jabatanService.listJabatanByNipWithPegawaiBatch(request.getNipPegawais()))
            .thenReturn(List.of(testJabatanWithTppPajakResponse1, testJabatanWithTppPajakResponse2));

        mockMvc.perform(post("/jabatan/detail/by-nip-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].nip").value("198001012010011001"))
            .andExpect(jsonPath("$[0].namaPegawai").value("John Doe"))
            .andExpect(jsonPath("$[0].namaJabatan").value("Kepala Dinas"))
            .andExpect(jsonPath("$[0].kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$[0].statusJabatan").value("Utama"))
            .andExpect(jsonPath("$[0].jenisJabatan").value("Jabatan Pemimpin Tinggi"))
            .andExpect(jsonPath("$[0].eselon").value("Eselon IV"))
            .andExpect(jsonPath("$[0].pangkat").value("Junior"))
            .andExpect(jsonPath("$[0].golongan").value("Golongan I"))
            .andExpect(jsonPath("$[0].basicTpp").value(5000000.0))
            .andExpect(jsonPath("$[0].tanggalMulai").value("01-01-2023"))
            .andExpect(jsonPath("$[0].tanggalAkhir").value("31-12-2025"))
            .andExpect(jsonPath("$[1].id").value(2L))
            .andExpect(jsonPath("$[1].nip").value("199001012015021002"))
            .andExpect(jsonPath("$[1].namaPegawai").value("Jane Smith"))
            .andExpect(jsonPath("$[1].namaJabatan").value("Sekretaris Dinas"))
            .andExpect(jsonPath("$[1].kodeOpd").value("OPD-002"))
            .andExpect(jsonPath("$[1].statusJabatan").value("PLT Utama"))
            .andExpect(jsonPath("$[1].jenisJabatan").value("Jabatan Administrasi"))
            .andExpect(jsonPath("$[1].eselon").value("Eselon III"))
            .andExpect(jsonPath("$[1].pangkat").value("Middle"))
            .andExpect(jsonPath("$[1].golongan").value("Golongan II"))
            .andExpect(jsonPath("$[1].basicTpp").value(4500000.0))
            .andExpect(jsonPath("$[1].tanggalMulai").value("01-01-2023"))
            .andExpect(jsonPath("$[1].tanggalAkhir").value("31-12-2025"));

        verify(jabatanService).listJabatanByNipWithPegawaiBatch(request.getNipPegawais());
    }

    @Test
    void getByNipBatch_WhenRequestEmpty_ShouldReturnBadRequest() throws Exception {
        NipBatchRequest request = new NipBatchRequest();
        request.setNipPegawais(List.of());

        mockMvc.perform(post("/jabatan/detail/by-nip-batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.nipPegawais").value("nip_pegawais tidak boleh kosong"));

        verifyNoInteractions(jabatanService);
    }

 //   @Test
//    void getAll_WhenJabatansExist_ShouldReturnJabatanList() throws Exception {
//        JabatanWithTppPajakResponse response1 = new JabatanWithTppPajakResponse(
//            testJabatan.id(),
//            testJabatan.nip(),
//            testJabatan.namaPegawai(),
//            testJabatan.namaJabatan(),
//            testJabatan.kodeOpd(),
//            testJabatan.statusJabatan(),
//            testJabatan.jenisJabatan(),
//            testJabatan.eselon(),
//            testJabatan.pangkat(),
//            testJabatan.golongan(),
//            5000000.0f,
//            5.0f,
//            testJabatan.tanggalMulai(),
//            testJabatan.tanggalAkhir(),
//                1,
//                2025
//        );
//
//        JabatanWithTppPajakResponse response2 = new JabatanWithTppPajakResponse(
//            2L,
//            "199001012015021002",
//            "Jane Smith",
//            "Sekretaris Dinas",
//            "OPD-002",
//            "PLT Utama",
//            "Jabatan Administrasi",
//            "Eselon III",
//            "Middle",
//            "Golongan II",
//            4500000.0f,
//            15.0f,
//            tanggalMulai.getTime(),
//            tanggalAkhir.getTime(),
//                1,
//                2025
//        );
//
//        when(jabatanService.listAllJabatanWithTpp()).thenReturn(List.of(response1, response2));
//
//        mockMvc.perform(get("/jabatan/detail/findall"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$").isArray())
//            .andExpect(jsonPath("$.length()").value(2))
//            .andExpect(jsonPath("$[0].id").value(response1.id()))
//            .andExpect(jsonPath("$[0].nip").value(response1.nip()))
//            .andExpect(jsonPath("$[0].namaPegawai").value(response1.namaPegawai()))
//            .andExpect(jsonPath("$[0].namaJabatan").value(response1.namaJabatan()))
//            .andExpect(jsonPath("$[0].kodeOpd").value(response1.kodeOpd()))
//            .andExpect(jsonPath("$[0].statusJabatan").value(response1.statusJabatan()))
//            .andExpect(jsonPath("$[0].jenisJabatan").value(response1.jenisJabatan()))
//            .andExpect(jsonPath("$[0].eselon").value(response1.eselon()))
//            .andExpect(jsonPath("$[0].pangkat").value(response1.pangkat()))
//            .andExpect(jsonPath("$[0].golongan").value(response1.golongan()))
//            .andExpect(jsonPath("$[0].basicTpp").value(response1.basicTpp()))
//            .andExpect(jsonPath("$[0].pajak").value(response1.pajak()))
//            .andExpect(jsonPath("$[1].id").value(response2.id()))
//            .andExpect(jsonPath("$[1].nip").value(response2.nip()))
//            .andExpect(jsonPath("$[1].namaPegawai").value(response2.namaPegawai()))
//            .andExpect(jsonPath("$[1].namaJabatan").value(response2.namaJabatan()))
//            .andExpect(jsonPath("$[1].kodeOpd").value(response2.kodeOpd()))
//            .andExpect(jsonPath("$[1].statusJabatan").value(response2.statusJabatan()))
//            .andExpect(jsonPath("$[1].jenisJabatan").value(response2.jenisJabatan()))
//            .andExpect(jsonPath("$[1].eselon").value(response2.eselon()))
//            .andExpect(jsonPath("$[1].pangkat").value(response2.pangkat()))
//            .andExpect(jsonPath("$[1].golongan").value(response2.golongan()))
//            .andExpect(jsonPath("$[1].basicTpp").value(response2.basicTpp()))
//            .andExpect(jsonPath("$[1].pajak").value(response2.pajak()));
//
//        verify(jabatanService).listAllJabatanWithTpp();
//    }

//    @Test
//    void getAll_WhenNoJabatansExist_ShouldReturnEmptyList() throws Exception {
//        when(jabatanService.listAllJabatanWithTpp()).thenReturn(List.of());
//
//        mockMvc.perform(get("/jabatan/detail/findall"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$").isArray())
//            .andExpect(jsonPath("$.length()").value(0));
//
//        verify(jabatanService).listAllJabatanWithTpp();
//    }

    @Test
    void tambah_WhenValidJabatanRequest_ShouldCreateJabatan() throws Exception {
        testJabatanRequest = new JabatanRequest(
            null,
            "201001012010011001",
            "Alice Doe",
            "Analis Ahli Utama",
            "OPD-001",
            "Utama",
            "Jabatan Fungsional",
            "Eselon III",
            "Senior",
            "Golongan III",
            6000000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        Jabatan createJabatan = new Jabatan(
            2L,
            "201001012010011001",
            "Alice Doe",
            "Analis Ahli Utama",
            "OPD-001",
            "Utama",
            "Jabatan Fungsional",
            "Eselon III",
            "Senior",
            "Golongan III",
            6000000.0f,
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
            .andExpect(jsonPath("$.namaPegawai").value("Alice Doe"))
            .andExpect(jsonPath("$.namaJabatan").value("Analis Ahli Utama"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$.statusJabatan").value("Utama"))
            .andExpect(jsonPath("$.jenisJabatan").value("Jabatan Fungsional"))
            .andExpect(jsonPath("$.eselon").value("Eselon III"))
            .andExpect(jsonPath("$.pangkat").value("Senior"))
            .andExpect(jsonPath("$.golongan").value("Golongan III"))
            .andExpect(jsonPath("$.basicTpp").value(6000000.0))
            .andExpect(jsonPath("$.tanggalMulai").value("01-01-2023"))
            .andExpect(jsonPath("$.tanggalAkhir").value("31-12-2025"));
    }

    @Test
    void tambah_WhenPltJabatanWithoutExistingJabatan_ShouldCreatePltJabatan() throws Exception {
        JabatanRequest pltRequest = new JabatanRequest(
            null,
            "200001012010011003",
            "Budi",
            "Plt Kepala Dinas",
            "OPD-002",
            "PLT Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            3500000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        Jabatan pltJabatan = new Jabatan(
            3L,
            "200001012010011003",
            "Budi",
            "Plt Kepala Dinas",
            "OPD-002",
            "PLT Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            3500000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        when(jabatanService.tambahJabatan(any(Jabatan.class))).thenReturn(pltJabatan);

        mockMvc.perform(post("/jabatan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pltRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(3L))
            .andExpect(jsonPath("$.nip").value("200001012010011003"))
            .andExpect(jsonPath("$.namaPegawai").value("Budi"))
            .andExpect(jsonPath("$.namaJabatan").value("Plt Kepala Dinas"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-002"))
            .andExpect(jsonPath("$.statusJabatan").value("PLT Utama"))
            .andExpect(jsonPath("$.jenisJabatan").value("Jabatan Pemimpin Tinggi"))
            .andExpect(jsonPath("$.basicTpp").value(3500000.0));
    }

    @Test
    void tambah_WhenUtamaJabatanWithExistingPlt_ShouldCreateUtamaJabatan() throws Exception {
        JabatanRequest utamaRequest = new JabatanRequest(
            null,
            "199001012015021002",
            "Jane Smith",
            "Kepala Dinas Utama",
            "OPD-001",
            "Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            7500000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        Jabatan utamaJabatan = new Jabatan(
            3L,
            "199001012015021002",
            "Jane Smith",
            "Kepala Dinas Utama",
            "OPD-001",
            "Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            7500000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        when(jabatanService.tambahJabatan(any(Jabatan.class))).thenReturn(utamaJabatan);

        mockMvc.perform(post("/jabatan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(utamaRequest)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(3L))
            .andExpect(jsonPath("$.nip").value("199001012015021002"))
            .andExpect(jsonPath("$.namaPegawai").value("Jane Smith"))
            .andExpect(jsonPath("$.namaJabatan").value("Kepala Dinas Utama"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$.statusJabatan").value("Utama"))
            .andExpect(jsonPath("$.jenisJabatan").value("Jabatan Pemimpin Tinggi"))
            .andExpect(jsonPath("$.basicTpp").value(7500000.0));
    }

    @Test
    void tambah_WhenNipAndKodeOpdEmpty_ShouldCreateJabatan() throws Exception {
        JabatanRequest request = new JabatanRequest(
            null,
            null,
            "Tanpa NIP",
            "Kepala Bidang",
            null,
            "Utama",
            "Jabatan Fungsional",
            "Eselon IV",
            "Senior",
            "Golongan III",
            2750000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        Jabatan savedJabatan = new Jabatan(
            5L,
            null,
            "Tanpa NIP",
            "Kepala Bidang",
            null,
            "Utama",
            "Jabatan Fungsional",
            "Eselon IV",
            "Senior",
            "Golongan III",
            2750000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        when(jabatanService.tambahJabatan(any(Jabatan.class))).thenReturn(savedJabatan);

        mockMvc.perform(post("/jabatan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(5L))
            .andExpect(jsonPath("$.nip").value(nullValue()))
            .andExpect(jsonPath("$.namaPegawai").value("Tanpa NIP"))
            .andExpect(jsonPath("$.namaJabatan").value("Kepala Bidang"))
            .andExpect(jsonPath("$.kodeOpd").value(nullValue()))
            .andExpect(jsonPath("$.statusJabatan").value("Utama"))
            .andExpect(jsonPath("$.jenisJabatan").value("Jabatan Fungsional"))
            .andExpect(jsonPath("$.basicTpp").value(2_750_000.0));
    }

    @Test
    void tambah_WhenPltJabatanWithExistingJabatan_ShouldReturn422() throws Exception {
        JabatanRequest pltRequest = new JabatanRequest(
            null,
            "198001012010011001",
            "John Doe",
            "Plt Kepala Dinas",
            "OPD-002",
            "PLT Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            3500000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        when(jabatanService.tambahJabatan(any(Jabatan.class)))
            .thenThrow(new JabatanPegawaiSudahAdaException("198001012010011001"));

        mockMvc.perform(post("/jabatan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pltRequest)))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().string("Jabatan dengan pegawai nip 198001012010011001 sudah ada."));
    }

    @Test
    void ubahJabatan_WhenChangingToUtamaWithExistingPlt_ShouldUpdateJabatan() throws Exception {
        JabatanRequest request = new JabatanRequest(
            1L,
            "199001012015021002",
            "Jane Smith",
            "Kepala Dinas Utama",
            "OPD-001",
            "Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            8000000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime()
        );

        Jabatan existingJabatan = new Jabatan(
            1L,
            "199001012015021002",
            "Jane Smith",
            "Plt Sekretaris",
            "OPD-002",
            "PLT Utama",
            "Jabatan Administrasi",
            "Eselon III",
            "Middle",
            "Golongan II",
            3000000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        Jabatan updatedJabatan = new Jabatan(
            1L,
            "199001012015021002",
            "Jane Smith",
            "Kepala Dinas Utama",
            "OPD-001",
            "Utama",
            "Jabatan Pemimpin Tinggi",
            "Eselon IV",
            "Junior",
            "Golongan I",
            8000000.0f,
            tanggalMulai.getTime(),
            tanggalAkhir.getTime(),
            Instant.now(),
            Instant.now()
        );

        when(jabatanService.detailJabatan(1L)).thenReturn(existingJabatan);
        when(jabatanService.ubahJabatan(eq(1L), any(Jabatan.class))).thenReturn(updatedJabatan);

        mockMvc.perform(put("/jabatan/update/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nip", is("199001012015021002")))
            .andExpect(jsonPath("$.namaPegawai", is("Jane Smith")))
            .andExpect(jsonPath("$.namaJabatan", is("Kepala Dinas Utama")))
            .andExpect(jsonPath("$.kodeOpd", is("OPD-001")))
            .andExpect(jsonPath("$.statusJabatan", is("Utama")))
            .andExpect(jsonPath("$.jenisJabatan", is("Jabatan Pemimpin Tinggi")))
            .andExpect(jsonPath("$.basicTpp", is(8_000_000.0)));
    }

    @Test
    void hapusJabatan_WhenJabatanExists_ShouldDeleteJabatan() throws Exception {
        doNothing().when(jabatanService).hapusJabatan(1L);

        mockMvc.perform(delete("/jabatan/delete/{id}", "1"))
            .andExpect(status().isOk())
            .andExpect(content().string("data sudah dihapus"));

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
