package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.NamaPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.request.TppPerhitunganRequest;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web.request.PerhitunganRequest;

@WebMvcTest(TppPerhitunganController.class)
public class TppPerhitunganControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TppPerhitunganService tppPerhitunganService;

    @Autowired
    private ObjectMapper objectMapper;

    private TppPerhitungan testTppPerhitungan;
    private TppPerhitungan testTppPerhitungan2;
    private TppPerhitunganRequest testTppPerhitunganRequest;
    private PerhitunganRequest testPerhitunganRequest;
    private PerhitunganRequest testPerhitunganRequest2;

    @BeforeEach
    void setUp() {
        testTppPerhitungan = new TppPerhitungan(
            1L,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "PEMDA-001",
            "198001012010011001",
            "John Doe",
            1,
            2024,
            50.0f,
            NamaPerhitungan.KEHADIRAN,
            80.0f,
            Instant.now(),
            Instant.now()
        );

        testTppPerhitungan2 = new TppPerhitungan(
            2L,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "PEMDA-001",
            "198001012010011001",
            "John Doe",
            1,
            2024,
            50.0f,
            NamaPerhitungan.PRODUKTIFITAS_KERJA,
            90.0f,
            Instant.now(),
            Instant.now()
        );

        testPerhitunganRequest = new PerhitunganRequest(
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            NamaPerhitungan.KEHADIRAN,
            1,
            2024,
            50.0f,
            80.0f
        );

        testPerhitunganRequest2 = new PerhitunganRequest(
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            NamaPerhitungan.PRODUKTIFITAS_KERJA,
            1,
            2024,
            50.0f,
            90.0f
        );

        testTppPerhitunganRequest = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            100.0f,
            Arrays.asList(testPerhitunganRequest, testPerhitunganRequest2)
        );
    }

    @Test
    void getByNipAndBulanAndTahun_WhenDataExists_ShouldReturnTppPerhitungan() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan("198001012010011001", 1, 2024)).thenReturn(testTppPerhitungan);
        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Arrays.asList(testTppPerhitungan, testTppPerhitungan2));

        mockMvc.perform(get("/tppPerhitungan/detail/nip/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.jenisTpp").value("KONDISI_KERJA"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$.kodePemda").value("PEMDA-001"))
            .andExpect(jsonPath("$.nip").value("198001012010011001"))
            .andExpect(jsonPath("$.nama").value("John Doe"))
            .andExpect(jsonPath("$.maksimum").value(50.0))
            .andExpect(jsonPath("$.bulan").value(1))
            .andExpect(jsonPath("$.tahun").value(2024))
            .andExpect(jsonPath("$.perhitungans", hasSize(2)))
            .andExpect(jsonPath("$.perhitungans[0].id").value(1))
            .andExpect(jsonPath("$.perhitungans[0].namaPerhitungan").value("KEHADIRAN"))
            .andExpect(jsonPath("$.perhitungans[0].maksimum").value(50.0))
            .andExpect(jsonPath("$.perhitungans[0].nilaiPerhitungan").value(80.0))
            .andExpect(jsonPath("$.perhitungans[1].id").value(2))
            .andExpect(jsonPath("$.perhitungans[1].namaPerhitungan").value("PRODUKTIFITAS_KERJA"))
            .andExpect(jsonPath("$.perhitungans[1].maksimum").value(50.0))
            .andExpect(jsonPath("$.perhitungans[1].nilaiPerhitungan").value(90.0))
            .andExpect(jsonPath("$.totalPersen").value(170.0));
    }

    @Test
    void getByNipAndBulanAndTahun_WhenDataNotExists_ShouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan("999999999999999999", 1, 2024))
            .thenThrow(new TppPerhitunganNipBulanTahunNotFoundException("999999999999999999", 1, 2024));

        mockMvc.perform(get("/tppPerhitungan/detail/nip/{nip}/{bulan}/{tahun}", "999999999999999999", 1, 2024))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Data TPP perhitungan tidak ditemukan untuk parameter yang diberikan"));
    }

    @Test
    void getByKodeOpdBulanTahun_WhenDataExists_ShouldReturnTppPerhitunganList() throws Exception {
        when(tppPerhitunganService.listTppPerhitunganByKodeOpdAndBulanAndTahun("OPD-001", 1, 2024))
            .thenReturn(Arrays.asList(testTppPerhitungan, testTppPerhitungan2));

        mockMvc.perform(get("/tppPerhitungan/detail/opd/{kodeOpd}/{bulan}/{tahun}", "OPD-001", 1, 2024))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].jenisTpp").value("KONDISI_KERJA"))
            .andExpect(jsonPath("$[0].kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$[0].kodePemda").value("PEMDA-001"))
            .andExpect(jsonPath("$[0].nip").value("198001012010011001"))
            .andExpect(jsonPath("$[0].nama").value("John Doe"))
            .andExpect(jsonPath("$[0].maksimum").value(50.0))
            .andExpect(jsonPath("$[0].bulan").value(1))
            .andExpect(jsonPath("$[0].tahun").value(2024))
            .andExpect(jsonPath("$[0].perhitungans", hasSize(2)))
            .andExpect(jsonPath("$[0].perhitungans[0].id").value(1))
            .andExpect(jsonPath("$[0].perhitungans[0].namaPerhitungan").value("KEHADIRAN"))
            .andExpect(jsonPath("$[0].perhitungans[0].maksimum").value(50.0))
            .andExpect(jsonPath("$[0].perhitungans[0].nilaiPerhitungan").value(80.0))
            .andExpect(jsonPath("$[0].perhitungans[1].id").value(2))
            .andExpect(jsonPath("$[0].perhitungans[1].namaPerhitungan").value("PRODUKTIFITAS_KERJA"))
            .andExpect(jsonPath("$[0].perhitungans[1].maksimum").value(50.0))
            .andExpect(jsonPath("$[0].perhitungans[1].nilaiPerhitungan").value(90.0))
            .andExpect(jsonPath("$[0].totalPersen").value(170.0));
    }

    @Test
    void getByKodeOpdBulanTahun_WhenDataNotExists_ShouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.listTppPerhitunganByKodeOpdAndBulanAndTahun("OPD-999", 1, 2024))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tppPerhitungan/detail/opd/{kodeOpd}/{bulan}/{tahun}", "OPD-999", 1, 2024))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Data TPP perhitungan tidak ditemukan untuk parameter yang diberikan"));
    }

    @Test
    void post_WhenValidRequestWithPerhitungans_ShouldCreateTppPerhitungan() throws Exception {
        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Collections.emptyList());
        when(tppPerhitunganService.tambahTppPerhitungan(any(TppPerhitungan.class)))
            .thenReturn(testTppPerhitungan)
            .thenReturn(testTppPerhitungan2);

        mockMvc.perform(post("/tppPerhitungan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTppPerhitunganRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.jenisTpp").value("KONDISI_KERJA"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$.kodePemda").value("PEMDA-001"))
            .andExpect(jsonPath("$.nip").value("198001012010011001"))
            .andExpect(jsonPath("$.nama").value("John Doe"))
            .andExpect(jsonPath("$.maksimum").value(100.0))
            .andExpect(jsonPath("$.bulan").value(1))
            .andExpect(jsonPath("$.tahun").value(2024))
            .andExpect(jsonPath("$.perhitungans", hasSize(2)))
            .andExpect(jsonPath("$.perhitungans[0].id").value(1))
            .andExpect(jsonPath("$.perhitungans[0].namaPerhitungan").value("KEHADIRAN"))
            .andExpect(jsonPath("$.perhitungans[0].maksimum").value(50.0))
            .andExpect(jsonPath("$.perhitungans[0].nilaiPerhitungan").value(80.0))
            .andExpect(jsonPath("$.perhitungans[1].id").value(2))
            .andExpect(jsonPath("$.perhitungans[1].namaPerhitungan").value("PRODUKTIFITAS_KERJA"))
            .andExpect(jsonPath("$.perhitungans[1].maksimum").value(50.0))
            .andExpect(jsonPath("$.perhitungans[1].nilaiPerhitungan").value(90.0))
            .andExpect(jsonPath("$.totalPersen").value(170.0));
    }

    @Test
    void post_WhenValidRequestWithoutPerhitungans_ShouldCreateTppPerhitungan() throws Exception {
        TppPerhitunganRequest requestWithoutPerhitungans = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            100.0f,
            Collections.emptyList()
        );

        mockMvc.perform(post("/tppPerhitungan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestWithoutPerhitungans)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.jenisTpp").value("KONDISI_KERJA"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$.kodePemda").value("PEMDA-001"))
            .andExpect(jsonPath("$.nip").value("198001012010011001"))
            .andExpect(jsonPath("$.nama").value("John Doe"))
            .andExpect(jsonPath("$.maksimum").value(100.0))
            .andExpect(jsonPath("$.bulan").value(1))
            .andExpect(jsonPath("$.tahun").value(2024))
            .andExpect(jsonPath("$.perhitungans", hasSize(0)))
            .andExpect(jsonPath("$.totalPersen").value(0.0));
    }

    @Test
    void post_WhenNipBulanTahunAlreadyExists_ShouldReturnConflict() throws Exception {
        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Arrays.asList(testTppPerhitungan));

        mockMvc.perform(post("/tppPerhitungan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTppPerhitunganRequest)))
            .andExpect(status().isConflict())
            .andExpect(content().string("Tpp Perhitungan dengan nip 198001012010011001 bulan 1 tahun 2024 sudah ada."));
    }

    @Test
    void post_WhenTotalMaksimumExceeds_ShouldReturnBadRequest() throws Exception {
        PerhitunganRequest excessiveRequest = new PerhitunganRequest(
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            NamaPerhitungan.KEHADIRAN,
            1,
            2024,
            150.0f,
            80.0f
        );

        TppPerhitunganRequest requestWithExcessiveMaksimum = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            100.0f,
            Arrays.asList(excessiveRequest)
        );

        mockMvc.perform(post("/tppPerhitungan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestWithExcessiveMaksimum)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Total nilai maksimum yang di input lebih dari total nilai maksimum yang ada"));
    }

    @Test
    void put_WhenValidRequestWithPerhitungans_ShouldUpdateTppPerhitungan() throws Exception {
        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Arrays.asList(testTppPerhitungan, testTppPerhitungan2));
        when(tppPerhitunganService.ubahTppPerhitungan(any(TppPerhitungan.class)))
            .thenReturn(testTppPerhitungan)
            .thenReturn(testTppPerhitungan2);

        mockMvc.perform(put("/tppPerhitungan/update/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTppPerhitunganRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jenisTpp").value("KONDISI_KERJA"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$.kodePemda").value("PEMDA-001"))
            .andExpect(jsonPath("$.nip").value("198001012010011001"))
            .andExpect(jsonPath("$.nama").value("John Doe"))
            .andExpect(jsonPath("$.maksimum").value(100.0))
            .andExpect(jsonPath("$.bulan").value(1))
            .andExpect(jsonPath("$.tahun").value(2024))
            .andExpect(jsonPath("$.perhitungans", hasSize(2)))
            .andExpect(jsonPath("$.perhitungans[0].id").value(1))
            .andExpect(jsonPath("$.perhitungans[0].namaPerhitungan").value("KEHADIRAN"))
            .andExpect(jsonPath("$.perhitungans[0].maksimum").value(50.0))
            .andExpect(jsonPath("$.perhitungans[0].nilaiPerhitungan").value(80.0))
            .andExpect(jsonPath("$.perhitungans[1].id").value(2))
            .andExpect(jsonPath("$.perhitungans[1].namaPerhitungan").value("PRODUKTIFITAS_KERJA"))
            .andExpect(jsonPath("$.perhitungans[1].maksimum").value(50.0))
            .andExpect(jsonPath("$.perhitungans[1].nilaiPerhitungan").value(90.0))
            .andExpect(jsonPath("$.totalPersen").value(170.0));
    }

    @Test
    void put_WhenPathVariablesDoNotMatchRequestBody_ShouldReturnBadRequest() throws Exception {
        TppPerhitunganRequest mismatchedRequest = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "999999999999999999",
            "John Doe",
            "PEMDA-001",
            2,
            2025,
            100.0f,
            Arrays.asList(testPerhitunganRequest)
        );

        mockMvc.perform(put("/tppPerhitungan/update/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mismatchedRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Path variables tidak cocok dengan request body"));
    }

    @Test
    void put_WhenTotalMaksimumExceeds_ShouldReturnBadRequest() throws Exception {
        PerhitunganRequest excessiveRequest = new PerhitunganRequest(
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            NamaPerhitungan.KEHADIRAN,
            1,
            2024,
            150.0f,
            80.0f
        );

        TppPerhitunganRequest requestWithExcessiveMaksimum = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            100.0f,
            Arrays.asList(excessiveRequest)
        );

        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Arrays.asList(testTppPerhitungan));

        mockMvc.perform(put("/tppPerhitungan/update/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestWithExcessiveMaksimum)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Total nilai maksimum yang di input lebih dari total nilai maksimum yang ada"));
    }

    @Test
    void put_WhenValidRequestWithoutPerhitungans_ShouldUpdateTppPerhitungan() throws Exception {
        TppPerhitunganRequest requestWithoutPerhitungans = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            100.0f,
            Collections.emptyList()
        );

        mockMvc.perform(put("/tppPerhitungan/update/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestWithoutPerhitungans)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jenisTpp").value("KONDISI_KERJA"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
            .andExpect(jsonPath("$.kodePemda").value("PEMDA-001"))
            .andExpect(jsonPath("$.nip").value("198001012010011001"))
            .andExpect(jsonPath("$.nama").value("John Doe"))
            .andExpect(jsonPath("$.maksimum").value(100.0))
            .andExpect(jsonPath("$.bulan").value(1))
            .andExpect(jsonPath("$.tahun").value(2024))
            .andExpect(jsonPath("$.perhitungans", hasSize(0)))
            .andExpect(jsonPath("$.totalPersen").value(0.0));
    }

    @Test
    void put_WhenPerhitunganDoesNotExist_ShouldReturnBadRequest() throws Exception {
        PerhitunganRequest newPerhitunganRequest = new PerhitunganRequest(
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            NamaPerhitungan.BELUM_DIATUR,
            1,
            2024,
            60.0f,
            70.0f
        );

        TppPerhitunganRequest requestWithNewPerhitungan = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            100.0f,
            Arrays.asList(newPerhitunganRequest)
        );

        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Arrays.asList(testTppPerhitungan, testTppPerhitungan2));

        mockMvc.perform(put("/tppPerhitungan/update/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestWithNewPerhitungan)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Data perhitungan dengan nama BELUM_DIATUR tidak ditemukan. Tidak dapat membuat data baru saat update. "));
    }

    @Test
    void delete_WhenDataExists_ShouldDeleteTppPerhitungan() throws Exception {
        doNothing().when(tppPerhitunganService).hapusTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024);

        mockMvc.perform(delete("/tppPerhitungan/delete/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024))
            .andExpect(status().isNoContent());

        verify(tppPerhitunganService).hapusTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024);
    }

    @Test
    void delete_WhenDataNotExists_ShouldThrowException() throws Exception {
        doThrow(new TppPerhitunganNipBulanTahunNotFoundException("999999999999999999", 1, 2024))
            .when(tppPerhitunganService).hapusTppPerhitunganByNipBulanTahun("999999999999999999", 1, 2024);

        mockMvc.perform(delete("/tppPerhitungan/delete/{nip}/{bulan}/{tahun}", "999999999999999999", 1, 2024))
            .andExpect(status().isNotFound());

        verify(tppPerhitunganService).hapusTppPerhitunganByNipBulanTahun("999999999999999999", 1, 2024);
    }

    // Additional comprehensive test cases

    @Test
    void getByNipAndBulanAndTahun_WithBelumDiaturEnums_ShouldReturnTppPerhitungan() throws Exception {
        TppPerhitungan tppBelumDiatur = new TppPerhitungan(
            3L,
            JenisTpp.BELUM_DIATUR,
            "OPD-003",
            "PEMDA-001",
            "198001012010011003",
            "Jane Smith",
            1,
            2024,
            60.0f,
            NamaPerhitungan.BELUM_DIATUR,
            0.0f,
            Instant.now(),
            Instant.now()
        );

        when(tppPerhitunganService.detailTppPerhitungan("198001012010011003", 1, 2024)).thenReturn(tppBelumDiatur);
        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011003", 1, 2024))
            .thenReturn(Arrays.asList(tppBelumDiatur));

        mockMvc.perform(get("/tppPerhitungan/detail/nip/{nip}/{bulan}/{tahun}", "198001012010011003", 1, 2024))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jenisTpp").value("BELUM_DIATUR"))
            .andExpect(jsonPath("$.kodeOpd").value("OPD-003"))
            .andExpect(jsonPath("$.nip").value("198001012010011003"))
            .andExpect(jsonPath("$.nama").value("Jane Smith"))
            .andExpect(jsonPath("$.maksimum").value(60.0))
            .andExpect(jsonPath("$.perhitungans", hasSize(1)))
            .andExpect(jsonPath("$.perhitungans[0].namaPerhitungan").value("BELUM_DIATUR"))
            .andExpect(jsonPath("$.perhitungans[0].nilaiPerhitungan").value(0.0))
            .andExpect(jsonPath("$.totalPersen").value(0.0));
    }

    @Test
    void post_WithAllEnumCombinations_ShouldCreateTppPerhitungan() throws Exception {
        // Test with BELUM_DIATUR JenisTpp
        TppPerhitunganRequest requestBelumDiatur = new TppPerhitunganRequest(
            null,
            JenisTpp.BELUM_DIATUR,
            "OPD-004",
            "198001012010011004",
            "Bob Wilson",
            "PEMDA-001",
            6,
            2024,
            75.0f,
            Collections.emptyList()
        );

        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011004", 6, 2024))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/tppPerhitungan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBelumDiatur)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.jenisTpp").value("BELUM_DIATUR"))
            .andExpect(jsonPath("$.perhitungans", hasSize(0)))
            .andExpect(jsonPath("$.totalPersen").value(0.0));
    }

    @Test
    void put_WithEnumChanges_ShouldUpdateTppPerhitungan() throws Exception {
        TppPerhitungan updatedTpp = new TppPerhitungan(
            1L,
            JenisTpp.BELUM_DIATUR,
            "OPD-001",
            "PEMDA-001",
            "198001012010011001",
            "John Doe",
            1,
            2024,
            55.0f,
            NamaPerhitungan.BELUM_DIATUR,
            0.0f,
            Instant.now(),
            Instant.now()
        );

        TppPerhitunganRequest enumChangeRequest = new TppPerhitunganRequest(
            null,
            JenisTpp.BELUM_DIATUR,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            55.0f,
            Collections.emptyList()
        );

        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Arrays.asList(testTppPerhitungan));

        mockMvc.perform(put("/tppPerhitungan/update/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(enumChangeRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jenisTpp").value("BELUM_DIATUR"))
            .andExpect(jsonPath("$.maksimum").value(55.0))
            .andExpect(jsonPath("$.perhitungans", hasSize(0)))
            .andExpect(jsonPath("$.totalPersen").value(0.0));
    }

    @Test
    void getByKodeOpdBulanTahun_WithMultipleNips_ShouldReturnGroupedResponse() throws Exception {
        TppPerhitungan secondUserTpp = new TppPerhitungan(
            4L,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "PEMDA-001",
            "198001012010011002",
            "Alice Johnson",
            1,
            2024,
            45.0f,
            NamaPerhitungan.KEHADIRAN,
            75.0f,
            Instant.now(),
            Instant.now()
        );

        when(tppPerhitunganService.listTppPerhitunganByKodeOpdAndBulanAndTahun("OPD-001", 1, 2024))
            .thenReturn(Arrays.asList(testTppPerhitungan, testTppPerhitungan2, secondUserTpp));

        mockMvc.perform(get("/tppPerhitungan/detail/opd/{kodeOpd}/{bulan}/{tahun}", "OPD-001", 1, 2024))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2))) // Two different NIPs
            .andExpect(jsonPath("$[0].nip").value("198001012010011001"))
            .andExpect(jsonPath("$[1].nip").value("198001012010011002"))
            .andExpect(jsonPath("$[1].perhitungans", hasSize(1)))
            .andExpect(jsonPath("$[1].perhitungans[0].nilaiPerhitungan").value(75.0))
            .andExpect(jsonPath("$[1].totalPersen").value(75.0));
    }

    @Test
    void post_WithZeroAndNegativeValues_ShouldCreateTppPerhitungan() throws Exception {
        TppPerhitungan negativeTppPerhitungan = new TppPerhitungan(
            5L,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "PEMDA-001",
            "198001012010011001",
            "John Doe",
            1,
            2024,
            -1000000.0f,
            NamaPerhitungan.KEHADIRAN,
            -5.0f,
            Instant.now(),
            Instant.now()
        );

        PerhitunganRequest negativePerhitungan = new PerhitunganRequest(
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            NamaPerhitungan.KEHADIRAN,
            1,
            2024,
            -1000000.0f,
            -5.0f
        );

        TppPerhitunganRequest negativeRequest = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            0.0f,
            Arrays.asList(negativePerhitungan)
        );

        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Collections.emptyList());
        when(tppPerhitunganService.tambahTppPerhitungan(any(TppPerhitungan.class)))
            .thenReturn(negativeTppPerhitungan);

        mockMvc.perform(post("/tppPerhitungan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(negativeRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.maksimum").value(0.0))
            .andExpect(jsonPath("$.perhitungans", hasSize(1)))
            .andExpect(jsonPath("$.perhitungans[0].id").value(5))
            .andExpect(jsonPath("$.perhitungans[0].namaPerhitungan").value("KEHADIRAN"))
            .andExpect(jsonPath("$.perhitungans[0].maksimum").value(-1000000.0))
            .andExpect(jsonPath("$.perhitungans[0].nilaiPerhitungan").value(-5.0))
            .andExpect(jsonPath("$.totalPersen").value(-5.0));
    }

    @Test
    void put_WithNonExistentPerhitungan_ShouldReturnBadRequest() throws Exception {
        PerhitunganRequest nonExistentPerhitungan = new PerhitunganRequest(
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            NamaPerhitungan.BELUM_DIATUR,
            1,
            2024,
            50.0f,
            25.0f
        );

        TppPerhitunganRequest requestWithNonExistent = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            100.0f,
            Arrays.asList(nonExistentPerhitungan)
        );

        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Arrays.asList(testTppPerhitungan, testTppPerhitungan2));

        mockMvc.perform(put("/tppPerhitungan/update/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestWithNonExistent)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Data perhitungan dengan nama BELUM_DIATUR tidak ditemukan. Tidak dapat membuat data baru saat update. "));
    }

    @Test
    void getByNipAndBulanAndTahun_WithEmptyResult_ShouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan("198001012010011001", 1, 2024))
            .thenThrow(new TppPerhitunganNipBulanTahunNotFoundException("198001012010011001", 1, 2024));

        mockMvc.perform(get("/tppPerhitungan/detail/nip/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Data TPP perhitungan tidak ditemukan untuk parameter yang diberikan"));
    }

    @Test
    void delete_WithValidParameters_ShouldDeleteSuccessfully() throws Exception {
        doNothing().when(tppPerhitunganService).hapusTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024);

        mockMvc.perform(delete("/tppPerhitungan/delete/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024))
            .andExpect(status().isNoContent());

        verify(tppPerhitunganService).hapusTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024);
    }

    @Test
    void post_WithSpecialCharactersInNama_ShouldCreateTppPerhitungan() throws Exception {
        TppPerhitunganRequest specialCharRequest = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John O'Connor-Niño @#$%^&*()",
            "PEMDA-001",
            1,
            2024,
            100.0f,
            Collections.emptyList()
        );

        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/tppPerhitungan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(specialCharRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nama").value("John O'Connor-Niño @#$%^&*()"));
    }

    @Test
    void post_WithLongStringValues_ShouldCreateTppPerhitungan() throws Exception {
        String longNama = "A".repeat(255);
        String longKodeOpd = "OPD" + "B".repeat(100);
        String longKodePemda = "PEMDA" + "C".repeat(100);

        TppPerhitunganRequest longStringRequest = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            longKodeOpd,
            "198001012010011001",
            longNama,
            longKodePemda,
            1,
            2024,
            100.0f,
            Collections.emptyList()
        );

        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(post("/tppPerhitungan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(longStringRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nama").value(longNama))
            .andExpect(jsonPath("$.kodeOpd").value(longKodeOpd))
            .andExpect(jsonPath("$.kodePemda").value(longKodePemda));
    }

    @Test
    void put_WhenExistingRecordsEmpty_ShouldThrowException() throws Exception {
        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Collections.emptyList());

        mockMvc.perform(put("/tppPerhitungan/update/{nip}/{bulan}/{tahun}", "198001012010011001", 1, 2024)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(testTppPerhitunganRequest)))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void post_WithExactMaksimumLimit_ShouldCreateTppPerhitungan() throws Exception {
        TppPerhitungan exactLimitTppPerhitungan = new TppPerhitungan(
            6L,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "PEMDA-001",
            "198001012010011001",
            "John Doe",
            1,
            2024,
            100.0f,
            NamaPerhitungan.KEHADIRAN,
            80.0f,
            Instant.now(),
            Instant.now()
        );

        PerhitunganRequest exactLimitPerhitungan = new PerhitunganRequest(
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            NamaPerhitungan.KEHADIRAN,
            1,
            2024,
            100.0f,
            80.0f
        );

        TppPerhitunganRequest exactLimitRequest = new TppPerhitunganRequest(
            null,
            JenisTpp.KONDISI_KERJA,
            "OPD-001",
            "198001012010011001",
            "John Doe",
            "PEMDA-001",
            1,
            2024,
            100.0f,
            Arrays.asList(exactLimitPerhitungan)
        );

        when(tppPerhitunganService.listTppPerhitunganByNipBulanTahun("198001012010011001", 1, 2024))
            .thenReturn(Collections.emptyList());
        when(tppPerhitunganService.tambahTppPerhitungan(any(TppPerhitungan.class)))
            .thenReturn(exactLimitTppPerhitungan);

        mockMvc.perform(post("/tppPerhitungan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(exactLimitRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.maksimum").value(100.0))
            .andExpect(jsonPath("$.perhitungans", hasSize(1)))
            .andExpect(jsonPath("$.perhitungans[0].id").value(6))
            .andExpect(jsonPath("$.perhitungans[0].namaPerhitungan").value("KEHADIRAN"))
            .andExpect(jsonPath("$.perhitungans[0].maksimum").value(100.0))
            .andExpect(jsonPath("$.perhitungans[0].nilaiPerhitungan").value(80.0))
            .andExpect(jsonPath("$.totalPersen").value(80.0));
    }
}