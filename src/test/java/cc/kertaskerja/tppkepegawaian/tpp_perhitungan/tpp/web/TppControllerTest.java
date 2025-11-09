package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppService;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web.request.TppRequest;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiService;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;

@WebMvcTest(TppController.class)
class TppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TppService tppService;

    @MockitoBean
    private TppPerhitunganService tppPerhitunganService;

    @MockitoBean
    private PegawaiService pegawaiService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetRekapTppNip_Success() throws Exception {
        String nip = "201001012010011001";
        Integer bulan = 1;
        Integer tahun = 2024;
        String jenisTpp = "Kondisi Kerja";

        Tpp tpp = new Tpp(1L, jenisTpp, "OPD001", nip, "PEMDA001", 5000000.0f, 10.0f, 8.0f, bulan, tahun, Instant.now(), Instant.now());
        List<Tpp> tppList = Arrays.asList(tpp);

        TppPerhitungan perhitungan = new TppPerhitungan(1L, "Kondisi Kerja", "OPD001", "Pemda-001", "152020342189755645", "John Doe", bulan, tahun, 100.0f, "Absensi", 80.0f, Instant.now(), Instant.now());
        List<TppPerhitungan> perhitunganList = Arrays.asList(perhitungan);

        // Create mock pegawai
        Pegawai pegawai = new Pegawai(1L, "John Doe", nip, "OPD001", "Admin", "Aktif", "hashedPassword", Instant.now(), Instant.now());

        when(tppService.listTppByNipBulanTahun(nip, bulan, tahun)).thenReturn(tppList);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(perhitunganList);
        when(pegawaiService.detailPegawai(nip)).thenReturn(pegawai);

        mockMvc.perform(get("/tpp/rekapTppNip/{jenisTpp}/{nip}/{bulan}/{tahun}", jenisTpp, nip, bulan, tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bulan").value(bulan))
                .andExpect(jsonPath("$.tahun").value(tahun))
                .andExpect(jsonPath("$.nip").value(nip))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].nama").value("John Doe"))
                .andExpect(jsonPath("$.data[0].opd").value("OPD001"))
                .andExpect(jsonPath("$.data[0].detailTpp[0].jenisTpp").value("Kondisi Kerja"))
                .andExpect(jsonPath("$.data[0].detailTpp[0].totalPajak").value(400000))
                .andExpect(jsonPath("$.data[0].detailTpp[0].totalBpjs").value(320000));
    }

    @Test
    void testGetRekapTppOpd_MultipleEmployees() throws Exception {
        String kodeOpd = "OPD001";
        Integer bulan = 1;
        Integer tahun = 2024;
        String jenisTpp = "Kondisi Kerja";
        String nip1 = "201001012010011001";
        String nip2 = "198001012010011001";

        Tpp tpp1 = new Tpp(1L, jenisTpp, kodeOpd, nip1, "PEMDA001", 5000000.0f, 10.0f, 8.0f, bulan, tahun, Instant.now(), Instant.now());
        Tpp tpp2 = new Tpp(2L, jenisTpp, kodeOpd, nip2, "PEMDA001", 5000000.0f, 10.0f, 8.0f, bulan, tahun, Instant.now(), Instant.now());
        List<Tpp> tppList = Arrays.asList(tpp1, tpp2);

        TppPerhitungan perhitungan1 = new TppPerhitungan(1L, "Kondisi Kerja", kodeOpd, "PEMDA001", "123456789012345678", "John Doe",  bulan, tahun, 100.0f, "Absensi", 80.0f, Instant.now(), Instant.now());
        TppPerhitungan perhitungan2 = new TppPerhitungan(2L, "Kondisi Kerja", kodeOpd, "PEMDA001", "152020342189755645", "Jane Smith", bulan, tahun, 100.0f, "Absensi", 90.0f, Instant.now(), Instant.now());

        // Create mock pegawai objects
        Pegawai pegawai1 = new Pegawai(1L, "John Doe", nip1, kodeOpd, "Admin", "Aktif", "hashedPassword", Instant.now(), Instant.now());
        Pegawai pegawai2 = new Pegawai(2L, "Jane Smith", nip2, kodeOpd, "Admin", "Aktif", "hashedPassword", Instant.now(), Instant.now());

        when(tppService.listTppByOpdBulanTahun(jenisTpp, kodeOpd, bulan, tahun)).thenReturn(tppList);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip1, bulan, tahun)).thenReturn(Arrays.asList(perhitungan1));
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip2, bulan, tahun)).thenReturn(Arrays.asList(perhitungan2));
        when(pegawaiService.detailPegawai(nip1)).thenReturn(pegawai1);
        when(pegawaiService.detailPegawai(nip2)).thenReturn(pegawai2);

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
        String jenisTpp = "Kondisi Kerja";

        when(tppService.listTppByNipBulanTahun(nip, bulan, tahun)).thenReturn(Collections.emptyList());
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(Collections.emptyList());
        doThrow(new PegawaiNotFoundException(nip)).when(pegawaiService).detailPegawai(nip);

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
        String jenisTpp = "Kondisi Kerja";
        String nip = "1234567890";

        Tpp tpp = new Tpp(1L, jenisTpp, kodeOpd, nip, "PEMDA001", 5000000.0f, 10.0f, 8.0f, bulan, tahun, Instant.now(), Instant.now());
        List<Tpp> tppList = Arrays.asList(tpp);

        TppPerhitungan perhitungan = new TppPerhitungan(1L, jenisTpp, kodeOpd, "PEMDA001", nip, "John Doe", bulan, tahun, 100.0f, "Absensi", 80.0f, Instant.now(), Instant.now());
        List<TppPerhitungan> perhitunganList = Arrays.asList(perhitungan);

        // Create mock pegawai
        Pegawai pegawai = new Pegawai(1L, "John Doe", nip, kodeOpd, "Admin", "Aktif", "hashedPassword", Instant.now(), Instant.now());

        when(tppService.listTppByOpdBulanTahun(jenisTpp, kodeOpd, bulan, tahun)).thenReturn(tppList);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(perhitunganList);
        when(pegawaiService.detailPegawai(nip)).thenReturn(pegawai);

        mockMvc.perform(get("/tpp/rekapTppOpd/{jenisTpp}/{kodeOpd}/{bulan}/{tahun}", jenisTpp, kodeOpd, bulan, tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bulan").value(bulan))
                .andExpect(jsonPath("$[0].tahun").value(tahun))
                .andExpect(jsonPath("$[0].nip").value(nip))
                .andExpect(jsonPath("$[0].data[0].nama").value("John Doe"))
                .andExpect(jsonPath("$[0].data[0].opd").value(kodeOpd))
                .andExpect(jsonPath("$[0].data[0].detailTpp[0].totalPajak").value(400000))
                .andExpect(jsonPath("$[0].data[0].detailTpp[0].totalBpjs").value(320000));
    }

    @Test
    void testGetRekapTppOpd_NoDataFound() throws Exception {
        String kodeOpd = "OPD999";
        Integer bulan = 1;
        Integer tahun = 2024;
        String jenisTpp = "Kondisi Kerja";

        when(tppService.listTppByOpdBulanTahun(jenisTpp, kodeOpd, bulan, tahun)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/tpp/rekapTppOpd/{jenisTpp}/{kodeOpd}/{bulan}/{tahun}", jenisTpp, kodeOpd, bulan, tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetRekapTppNip_InvalidJenisTpp() throws Exception {
        String jenisTpp = "invalid";
        String nip = "1234567890";
        Integer bulan = 1;
        Integer tahun = 2024;

        when(tppService.listTppByNipBulanTahun(nip, bulan, tahun)).thenReturn(Collections.emptyList());
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(Collections.emptyList());

        when(pegawaiService.detailPegawai(nip)).thenThrow(new PegawaiNotFoundException(nip));

        mockMvc.perform(get("/tpp/rekapTppNip/{jenisTpp}/{nip}/{bulan}/{tahun}", jenisTpp, nip, bulan, tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bulan").value(bulan))
                .andExpect(jsonPath("$.tahun").value(tahun))
                .andExpect(jsonPath("$.nip").value(nip))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testUpdateTpp_Success() throws Exception {
        String nip = "127863215400899674";
        Integer bulan = 1;
        Integer tahun = 2024;

        Tpp existingTpp = new Tpp(1L, "Kondisi Kerja", "OPD001", nip, "PEMDA001", 5000000.0f, 10.0f, 8.0f, bulan, tahun, Instant.now(), Instant.now());
        List<Tpp> existingTppList = Arrays.asList(existingTpp);

        TppPerhitungan perhitungan = new TppPerhitungan(1L, "Kondisi Kerja", "OPD001", "PEMDA001", nip, "John Doe", bulan, tahun, 100.0f, "Produktifitas Kerja", 80.0f, Instant.now(), Instant.now());
        List<TppPerhitungan> perhitunganList = Arrays.asList(perhitungan);

        Tpp updatedTpp = new Tpp(1L, "Kondisi Kerja", "OPD002", nip, "PEMDA001", 6000000.0f, 10.0f, 8.0f, bulan, tahun, existingTpp.createdDate(), Instant.now());

        TppRequest request = new TppRequest(null, "Kondisi Kerja", "OPD002", nip, "PEMDA001", 6000000.0f, 10.0f, 8.0f, bulan, tahun);

        when(tppService.listTppByNipBulanTahun(nip, bulan, tahun)).thenReturn(existingTppList);
        when(tppService.ubahTpp(any(Tpp.class))).thenReturn(updatedTpp);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(perhitunganList);

        mockMvc.perform(put("/tpp/update/{nip}/{bulan}/{tahun}", nip, bulan, tahun)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jenisTpp").value("Kondisi Kerja"))
                .andExpect(jsonPath("$.kodeOpd").value("OPD002"))
                .andExpect(jsonPath("$.nip").value(nip))
                .andExpect(jsonPath("$.maksimumTpp").value(6000000.0))
                .andExpect(jsonPath("$.bulan").value(bulan))
                .andExpect(jsonPath("$.tahun").value(tahun))
                .andExpect(jsonPath("$.hasilPerhitungan").value(80.0))
                .andExpect(jsonPath("$.totaltpp").value(4800000.0))
                .andExpect(jsonPath("$.totalPajak").value(480000.0))
                .andExpect(jsonPath("$.totalBpjs").value(384000.0));
    }

    @Test
    void testUpdateTpp_InvalidRequest() throws Exception {
        String nip = "1234567890";
        Integer bulan = 1;
        Integer tahun = 2024;

        TppRequest request = new TppRequest(null, null, null, null, null, null, null, null, null, null);

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

        TppRequest request = new TppRequest(null, "Kondisi Kerja", "OPD001", nip, "PEMDA001", 5000000.0f, 10.0f, 8.0f, bulan, tahun);
        Tpp createdTpp = new Tpp(1L, "Kondisi Kerja", "OPD001", nip, "PEMDA001", 5000000.0f, 10.0f, 8.0f, bulan, tahun, Instant.now(), Instant.now());

        TppPerhitungan perhitungan = new TppPerhitungan(1L, "Kondisi Kerja", "OPD001", "PEMDA001", nip, "John Doe", bulan, tahun, 100.0f, "Produktifitas Kerja", 80.0f, Instant.now(), Instant.now());
        List<TppPerhitungan> perhitunganList = Arrays.asList(perhitungan);

        when(tppService.tambahTpp(any(Tpp.class))).thenReturn(createdTpp);
        when(tppPerhitunganService.listTppPerhitunganByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(perhitunganList);

        mockMvc.perform(post("/tpp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jenisTpp").value("Kondisi Kerja"))
                .andExpect(jsonPath("$.kodeOpd").value("OPD001"))
                .andExpect(jsonPath("$.nip").value(nip))
                .andExpect(jsonPath("$.maksimumTpp").value(5000000.0))
                .andExpect(jsonPath("$.bulan").value(bulan))
                .andExpect(jsonPath("$.tahun").value(tahun))
                .andExpect(jsonPath("$.hasilPerhitungan").value(80.0))
                .andExpect(jsonPath("$.totaltpp").value(4000000.0))
                .andExpect(jsonPath("$.totalPajak").value(400000.0))
                .andExpect(jsonPath("$.totalBpjs").value(320000.0));
    }

    @Test
    void testCreateTpp_InvalidRequest() throws Exception {
        TppRequest request = new TppRequest(null, null, null, null, null, null, null, null, null, null);

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