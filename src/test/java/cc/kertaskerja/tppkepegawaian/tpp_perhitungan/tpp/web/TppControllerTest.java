package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitungan;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.JenisTpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.request.TppRequest;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TppController.class)
class TppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp convertJenisTpp(JenisTpp jenisTpp) {
        return switch (jenisTpp) {
            case BEBAN_KERJA -> cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp.BEBAN_KERJA;
            case PRESTASI_KERJA -> cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp.PRESTASI_KERJA;
            case ABSENSI -> cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp.ABSENSI;
            case BELUM_DIATUR -> cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp.BELUM_DIATUR;
        };
    }

    @MockitoBean
    private TppService tppService;

    @MockitoBean
    private TppPerhitunganService tppPerhitunganService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetRekapTppNip_Success() throws Exception {
        String nip = "201001012010011001";
        Integer bulan = 1;
        Integer tahun = 2024;
        JenisTpp jenisTpp = JenisTpp.BEBAN_KERJA;

        Tpp tpp = new Tpp(1L, jenisTpp, "OPD001", nip, "PEMDA001", 5000000.0f, bulan, tahun, Instant.now(), Instant.now());
        List<Tpp> tppList = Arrays.asList(tpp);

        // Create perhitungan with the same jenisTpp enum that the controller expects
        TppPerhitungan perhitungan = new TppPerhitungan(1L, convertJenisTpp(jenisTpp), "OPD001", nip, "John Doe", bulan, tahun, 100.0f, "ABSENSI", 80.0f, Instant.now(), Instant.now());
        List<TppPerhitungan> perhitunganList = Arrays.asList(perhitungan);

        when(tppService.listTppByNipBulanTahun(nip, bulan, tahun)).thenReturn(tppList);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(perhitunganList);

        mockMvc.perform(get("/tpp/rekapTppNip/{jenisTpp}/{nip}/{bulan}/{tahun}", jenisTpp, nip, bulan, tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bulan").value(bulan))
                .andExpect(jsonPath("$.tahun").value(tahun))
                .andExpect(jsonPath("$.nip").value(nip))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].nama").value("John Doe"))
                .andExpect(jsonPath("$.data[0].opd").value("OPD001"))
                .andExpect(jsonPath("$.data[0].detailTpp[0].jenisTpp").value("BEBAN_KERJA"));
    }

    @Test
    void testGetRekapTppOpd_MultipleEmployees() throws Exception {
        String kodeOpd = "OPD001";
        Integer bulan = 1;
        Integer tahun = 2024;
        JenisTpp jenisTpp = JenisTpp.BEBAN_KERJA;
        String nip1 = "201001012010011001";
        String nip2 = "198001012010011001";

        Tpp tpp1 = new Tpp(1L, jenisTpp, kodeOpd, nip1, "PEMDA001", 5000000.0f, bulan, tahun, Instant.now(), Instant.now());
        Tpp tpp2 = new Tpp(2L, jenisTpp, kodeOpd, nip2, "PEMDA001", 5000000.0f, bulan, tahun, Instant.now(), Instant.now());
        List<Tpp> tppList = Arrays.asList(tpp1, tpp2);

        TppPerhitungan perhitungan1 = new TppPerhitungan(1L, convertJenisTpp(jenisTpp), kodeOpd, nip1, "John Doe", bulan, tahun, 100.0f, "KINERJA", 80.0f, Instant.now(), Instant.now());
        TppPerhitungan perhitungan2 = new TppPerhitungan(2L, convertJenisTpp(jenisTpp), kodeOpd, nip2, "Jane Smith", bulan, tahun, 100.0f, "KINERJA", 90.0f, Instant.now(), Instant.now());

        when(tppService.listTppByOpdBulanTahun(kodeOpd, bulan, tahun)).thenReturn(tppList);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip1, bulan, tahun)).thenReturn(Arrays.asList(perhitungan1));
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip2, bulan, tahun)).thenReturn(Arrays.asList(perhitungan2));

        mockMvc.perform(get("/tpp/rekapTppOpd/{jenisTpp}/{kodeOpd}/{bulan}/{tahun}", jenisTpp, kodeOpd, bulan, tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetRekapTppNip_NoDataFound() throws Exception {
        String nip = "1234567890";
        Integer bulan = 1;
        Integer tahun = 2024;
        JenisTpp jenisTpp = JenisTpp.BEBAN_KERJA;

        when(tppService.listTppByNipBulanTahun(nip, bulan, tahun)).thenReturn(Collections.emptyList());
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tpp/rekapTppNip/{jenisTpp}/{nip}/{bulan}/{tahun}", jenisTpp, nip, bulan, tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bulan").value(bulan))
                .andExpect(jsonPath("$.tahun").value(tahun))
                .andExpect(jsonPath("$.nip").value(nip))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetRekapTppOpd_Success() throws Exception {
        String kodeOpd = "OPD001";
        Integer bulan = 1;
        Integer tahun = 2024;
        JenisTpp jenisTpp = JenisTpp.BEBAN_KERJA;
        String nip = "1234567890";

        Tpp tpp = new Tpp(1L, jenisTpp, kodeOpd, nip, "PEMDA001", 5000000.0f, bulan, tahun, Instant.now(), Instant.now());
        List<Tpp> tppList = Arrays.asList(tpp);

        TppPerhitungan perhitungan = new TppPerhitungan(1L, convertJenisTpp(jenisTpp), kodeOpd, nip, "John Doe", bulan, tahun, 100.0f, "KINERJA", 80.0f, Instant.now(), Instant.now());
        List<TppPerhitungan> perhitunganList = Arrays.asList(perhitungan);

        when(tppService.listTppByOpdBulanTahun(kodeOpd, bulan, tahun)).thenReturn(tppList);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(perhitunganList);

        mockMvc.perform(get("/tpp/rekapTppOpd/{jenisTpp}/{kodeOpd}/{bulan}/{tahun}", jenisTpp, kodeOpd, bulan, tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bulan").value(bulan))
                .andExpect(jsonPath("$[0].tahun").value(tahun))
                .andExpect(jsonPath("$[0].nip").value(nip))
                .andExpect(jsonPath("$[0].data[0].nama").value("John Doe"))
                .andExpect(jsonPath("$[0].data[0].opd").value(kodeOpd));
    }

    @Test
    void testGetRekapTppOpd_NoDataFound() throws Exception {
        String kodeOpd = "OPD999";
        Integer bulan = 1;
        Integer tahun = 2024;
        JenisTpp jenisTpp = JenisTpp.BEBAN_KERJA;

        when(tppService.listTppByOpdBulanTahun(kodeOpd, bulan, tahun)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tpp/rekapTppOpd/{jenisTpp}/{kodeOpd}/{bulan}/{tahun}", jenisTpp, kodeOpd, bulan, tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetRekapTppNip_InvalidJenisTpp() throws Exception {
        String nip = "1234567890";
        Integer bulan = 1;
        Integer tahun = 2024;

        mockMvc.perform(get("/tpp/rekapTppNip/INVALID/{nip}/{bulan}/{tahun}", nip, bulan, tahun))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTpp_Success() throws Exception {
        String nip = "127863215400899674";
        Integer bulan = 1;
        Integer tahun = 2024;

        Tpp existingTpp = new Tpp(1L, JenisTpp.BEBAN_KERJA, "OPD001", nip, "PEMDA001", 5000000.0f, bulan, tahun, Instant.now(), Instant.now());
        List<Tpp> existingTppList = Arrays.asList(existingTpp);

        TppPerhitungan perhitungan = new TppPerhitungan(1L, cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp.BEBAN_KERJA, "OPD001", nip, "John Doe", bulan, tahun, 100.0f, "KINERJA", 80.0f, Instant.now(), Instant.now());
        List<TppPerhitungan> perhitunganList = Arrays.asList(perhitungan);

        Tpp updatedTpp = new Tpp(1L, JenisTpp.PRESTASI_KERJA, "OPD002", nip, "PEMDA001", 6000000.0f, bulan, tahun, existingTpp.createdDate(), Instant.now());

        TppRequest request = new TppRequest(null, JenisTpp.PRESTASI_KERJA, "OPD002", nip, "PEMDA001", 6000000.0f, bulan, tahun);

        when(tppService.listTppByNipBulanTahun(nip, bulan, tahun)).thenReturn(existingTppList);
        when(tppService.ubahTpp(any(Tpp.class))).thenReturn(updatedTpp);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(perhitunganList);

        mockMvc.perform(put("/tpp/update/{nip}/{bulan}/{tahun}", nip, bulan, tahun)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jenisTpp").value("PRESTASI_KERJA"))
                .andExpect(jsonPath("$.kodeOpd").value("OPD002"))
                .andExpect(jsonPath("$.nip").value(nip))
                .andExpect(jsonPath("$.maksimumTpp").value(6000000.0))
                .andExpect(jsonPath("$.bulan").value(bulan))
                .andExpect(jsonPath("$.tahun").value(tahun))
                .andExpect(jsonPath("$.hasilPerhitungan").value(80.0))
                .andExpect(jsonPath("$.totaltpp").value(4800000.0));
    }

    @Test
    void testUpdateTpp_InvalidRequest() throws Exception {
        String nip = "1234567890";
        Integer bulan = 1;
        Integer tahun = 2024;

        TppRequest request = new TppRequest(null, null, null, null, null, null, null, null);

        mockMvc.perform(put("/tpp/update/{nip}/{bulan}/{tahun}", nip, bulan, tahun)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTpp_Success() throws Exception {
        String nip = "201001012010011001";
        Integer bulan = 1;
        Integer tahun = 2024;

        TppRequest request = new TppRequest(null, JenisTpp.BEBAN_KERJA, "OPD001", nip, "PEMDA001", 5000000.0f, bulan, tahun);
        Tpp createdTpp = new Tpp(1L, JenisTpp.BEBAN_KERJA, "OPD001", nip, "PEMDA001", 5000000.0f, bulan, tahun, Instant.now(), Instant.now());

        TppPerhitungan perhitungan = new TppPerhitungan(1L, cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.JenisTpp.BEBAN_KERJA, "OPD001", nip, "John Doe", bulan, tahun, 100.0f, "KINERJA", 80.0f, Instant.now(), Instant.now());
        List<TppPerhitungan> perhitunganList = Arrays.asList(perhitungan);

        when(tppService.tambahTpp(any(Tpp.class))).thenReturn(createdTpp);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(perhitunganList);

        mockMvc.perform(post("/tpp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jenisTpp").value("BEBAN_KERJA"))
                .andExpect(jsonPath("$.kodeOpd").value("OPD001"))
                .andExpect(jsonPath("$.nip").value(nip))
                .andExpect(jsonPath("$.maksimumTpp").value(5000000.0))
                .andExpect(jsonPath("$.bulan").value(bulan))
                .andExpect(jsonPath("$.tahun").value(tahun))
                .andExpect(jsonPath("$.hasilPerhitungan").value(80.0))
                .andExpect(jsonPath("$.totaltpp").value(4000000.0));
    }

    @Test
    void testCreateTpp_InvalidRequest() throws Exception {
        TppRequest request = new TppRequest(null, null, null, null, null, null, null, null);

        mockMvc.perform(post("/tpp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteTpp_Success() throws Exception {
        String nip = "201001012010011001";
        Integer bulan = 1;
        Integer tahun = 2024;

        doNothing().when(tppService).hapusTppByNipBulanTahun(nip, bulan, tahun);

        mockMvc.perform(delete("/tpp/delete/{nip}/{bulan}/{tahun}", nip, bulan, tahun))
                .andExpect(status().isNoContent());
    }

}