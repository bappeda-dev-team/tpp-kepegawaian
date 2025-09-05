package cc.kertaskerja.tppkepegawaian.tpp.web;

import cc.kertaskerja.tppkepegawaian.tpp.domain.JenisTpp;
import cc.kertaskerja.tppkepegawaian.tpp.domain.Tpp;
import cc.kertaskerja.tppkepegawaian.tpp.domain.TppNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp.domain.TppService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TppController.class)
public class TppControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TppService tppService;

    @Autowired
    private ObjectMapper objectMapper;

    private Tpp tpp;
    private TppRequest tppRequest;

    @BeforeEach
    void setUp() {
        long tppId = 1L;
        tppRequest = new TppRequest(
                        null, 
                        JenisTpp.BEBAN_KERJA, 
                        "OPPD-123", 
                        "123456789012345678", 
                        "pemda1", 
                        "keterangan", 
                        9000000.0f,
                        2000000.0f, 
                        48.0f, 
                        1, 
                        2023, 
                        null);
        tpp = new Tpp(
                        tppId, 
                        tppRequest.jenisTpp(), 
                        tppRequest.kodeOpd(), 
                        tppRequest.nip(), 
                        tppRequest.kodePemda(),
                        tppRequest.keterangan(), 
                        tppRequest.nilaiInput(), 
                        tppRequest.maksimum(), 
                        tppRequest.hasilPerhitungan(),
                        tppRequest.bulan(), 
                        tppRequest.tahun(), 
                        432000.0f, 
                        Instant.now(), 
                        null);
    }

    @Test
    void whenGetTppByIdThenReturnTpp() throws Exception {
        given(tppService.detailTpp(tpp.id())).willReturn(tpp);

        mockMvc.perform(get("/tpp/detail/" + tpp.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tpp.id()));
    }

    @Test
    void whenGetTppByBulanAndTahunThenReturnTpps() throws Exception {
        int bulan = tpp.bulan();
        int tahun = tpp.tahun();
        Tpp tpp2 = new Tpp(2L, JenisTpp.PRESTASI_KERJA, "OPD-312", "876543210123456789", "pemda1", "keterangan lain", 200000.0f, 150000.0f, 15.0f, bulan, tahun, 30000.0f, Instant.now(), null);
        given(tppService.listTppByBulanAndTahun(bulan, tahun)).willReturn(List.of(tpp, tpp2));

        mockMvc.perform(get("/tpp/detail/" + bulan + "/" + tahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bulan").value(bulan))
                .andExpect(jsonPath("$[0].tahun").value(tahun))
                .andExpect(jsonPath("$[1].bulan").value(bulan))
                .andExpect(jsonPath("$[1].tahun").value(tahun));
    }

    @Test
    void whenGetTppWithNonExistentIdThenFails() throws Exception {
        long nonExistentId = 999L;
        given(tppService.detailTpp(nonExistentId)).willThrow(new TppNotFoundException(nonExistentId));

        mockMvc.perform(get("/tpp/detail/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetTppByNonExistentBulanAndTahunThenReturnEmptyList() throws Exception {
        int nonExistentBulan = 13;
        int nonExistentTahun = 1999;
        given(tppService.listTppByBulanAndTahun(nonExistentBulan, nonExistentTahun)).willReturn(List.of());

        mockMvc.perform(get("/tpp/detail/" + nonExistentBulan + "/" + nonExistentTahun))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void whenPostTppThenCreateTpp() throws Exception {
        Tpp tppToSave = Tpp.of(
                tppRequest.jenisTpp(),
                tppRequest.kodeOpd(),
                tppRequest.nip(),
                tppRequest.kodePemda(),
                tppRequest.keterangan(),
                tppRequest.nilaiInput(),
                tppRequest.maksimum(),
                tppRequest.hasilPerhitungan(),
                tppRequest.bulan(),
                tppRequest.tahun(),
                tppRequest.getTotalTppCalculated()
        );
        given(tppService.tambahTpp(tppToSave)).willReturn(tpp);

        mockMvc.perform(post("/tpp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void whenPostTppWithNoNipThenFails() throws Exception {
        TppRequest tppRequestWithNoNip = new TppRequest(
                null,
                JenisTpp.BEBAN_KERJA,
                "OPPD-123",
                null,
                "pemda1",
                "keterangan",
                9000000.0f,
                2000000.0f,
                48.0f,
                1,
                2023,
                null);

        mockMvc.perform(post("/tpp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppRequestWithNoNip)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPutTppThenUpdateTpp() throws Exception {
        Tpp existingTpp = new Tpp(tpp.id(), JenisTpp.BEBAN_KERJA, "OPD-123", "123456789012345678", "pemda1", "keterangan", 1000000.0f, 500000.0f, 30f, 1, 2023, 300000.0f, Instant.now(), null);

        Float calculatedTotal = tppRequest.getTotalTppCalculated();
        Tpp tppToUpdate = new Tpp(
                tpp.id(),
                tppRequest.jenisTpp(),
                tppRequest.kodeOpd(),
                tppRequest.nip(),
                tppRequest.kodePemda(),
                tppRequest.keterangan(),
                tppRequest.nilaiInput(),
                tppRequest.maksimum(),
                tppRequest.hasilPerhitungan(),
                tppRequest.bulan(),
                tppRequest.tahun(),
                calculatedTotal,
                existingTpp.createdDate(),
                null
        );

        given(tppService.detailTpp(tpp.id())).willReturn(existingTpp);
        given(tppService.ubahTpp(tpp.id(), tppToUpdate)).willReturn(tppToUpdate);

        mockMvc.perform(put("/tpp/update/" + tpp.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tpp.id()));
    }

    @Test
    void whenPutTppWithNonExistentIdThenFails() throws Exception {
        long nonExistentId = 999L;
        given(tppService.detailTpp(nonExistentId)).willThrow(new TppNotFoundException(nonExistentId));

        mockMvc.perform(put("/tpp/update/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPutTppWithNoNipThenFails() throws Exception {
        TppRequest tppRequestWithNoNip = new TppRequest(
                tpp.id(),
                JenisTpp.BEBAN_KERJA,
                "OPPD-123",
                null,
                "pemda1",
                "keterangan",
                9000000.0f,
                2000000.0f,
                48.0f,
                1,
                2023,
                null);

        mockMvc.perform(put("/tpp/update/" + tpp.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppRequestWithNoNip)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDeleteTppThenReturnNoContent() throws Exception {
        mockMvc.perform(delete("/tpp/delete/" + tpp.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDeleteTppWithNonExistentIdThenFails() throws Exception {
        long nonExistentId = 999L;
        doThrow(new TppNotFoundException(nonExistentId)).when(tppService).hapusTpp(nonExistentId);

        mockMvc.perform(delete("/tpp/delete/" + nonExistentId))
                .andExpect(status().isNotFound());
    }
}
