package cc.kertaskerja.tppkepegawaian.opd.web;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.hasSize;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import cc.kertaskerja.tppkepegawaian.opd.domain.Opd;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdService;

@WebMvcTest(OpdController.class)
public class OpdControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpdService opdService;

    @Autowired
    private ObjectMapper objectMapper;

    private Opd testOpd;

    @BeforeEach
    void setUp() {
        testOpd = new Opd(
                1L,
                "OPD-001",
                "Dinas Pendidikan",
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void detailOpdByKode_WhenOpdExists_ShouldReturnOpd() throws Exception {
        when(opdService.detailOpd("OPD-001")).thenReturn(testOpd);

        mockMvc.perform(get("/opd/detail/{kodeOpd}", "OPD-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.kodeOpd", is("OPD-001")))
                .andExpect(jsonPath("$.namaOpd", is("Dinas Pendidikan")));

        verify(opdService).detailOpd("OPD-001");
    }

    @Test
    void detailOpdByKode_WhenOpdNotExists_ShouldReturn404() throws Exception {
        when(opdService.detailOpd("OPD-003")).thenThrow(new OpdNotFoundException("OPD-003"));

        mockMvc.perform(get("/opd/detail/{kodeOpd}", "OPD-003"))
                .andExpect(status().isNotFound());

        verify(opdService).detailOpd("OPD-003");
    }
    
    @Test
    void getAllOpd_ShouldReturnAllOpd() throws Exception {
        List<Opd> opdList = Arrays.asList(
                new Opd(1L, "OPD-001", "Dinas Pendidikan", Instant.now(), Instant.now()),
                new Opd(2L, "OPD-002", "Dinas Kesehatan", Instant.now(), Instant.now())
        );
        
        when(opdService.listAllOpd()).thenReturn(opdList);

        mockMvc.perform(get("/opd/detail/allOpd"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].kodeOpd", is("OPD-001")))
                .andExpect(jsonPath("$[0].namaOpd", is("Dinas Pendidikan")))
                .andExpect(jsonPath("$[1].kodeOpd", is("OPD-002")))
                .andExpect(jsonPath("$[1].namaOpd", is("Dinas Kesehatan")));

        verify(opdService).listAllOpd();
    }
    
    @Test
    void getAllOpd_WhenNoOpd_ShouldReturnEmptyList() throws Exception {
        when(opdService.listAllOpd()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/opd/detail/allOpd"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(opdService).listAllOpd();
    }

    @Test
    void tambahOpd_WhenValidRequest_ShouldCreateOpd() throws Exception {
        OpdRequest request = new OpdRequest(null, "OPD-002", "Dinas Kesehatan");
        Opd createdOpd = new Opd(2L, "OPD-002", "Dinas Kesehatan", Instant.now(), Instant.now());
        when(opdService.tambahOpd(any(Opd.class))).thenReturn(createdOpd);

        // When & Then
        mockMvc.perform(post("/opd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.kodeOpd", is("OPD-002")))
                .andExpect(jsonPath("$.namaOpd", is("Dinas Kesehatan")));

        verify(opdService).tambahOpd(any(Opd.class));
    }

    @Test
    void tambahOpd_WhenInvalidRequest_ShouldReturn400() throws Exception {
        OpdRequest request = new OpdRequest(null, "", "Dinas Kesehatan");

        mockMvc.perform(post("/opd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(opdService, never()).tambahOpd(any());
    }

    @Test
    void ubahOpd_WhenValidRequest_ShouldUpdateOpd() throws Exception {
        OpdRequest request = new OpdRequest(1L, "OPD-001", "Dinas Kehutanan");
        Opd existingOpd = new Opd(1L, "OPD-001", "Dinas Pendidikan", Instant.now(), Instant.now());
        Opd updatedOpd = new Opd(1L, "OPD-001", "Dinas Kehutanan", Instant.now(), Instant.now());

        when(opdService.detailOpd("OPD-001")).thenReturn(existingOpd);
        when(opdService.ubahOpd(eq("OPD-001"), any(Opd.class))).thenReturn(updatedOpd);

        mockMvc.perform(put("/opd/update/{kodeOpd}", "OPD-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kodeOpd", is("OPD-001")))
                .andExpect(jsonPath("$.namaOpd", is("Dinas Kehutanan")));

        verify(opdService).detailOpd("OPD-001");
        verify(opdService).ubahOpd(eq("OPD-001"), any(Opd.class));
    }

    @Test
    void ubahOpd_WhenOpdNotExists_ShouldReturn404() throws Exception {
        OpdRequest request = new OpdRequest(1L, "OPD-003", "Dinas Kehutanan");

        when(opdService.detailOpd("OPD-003")).thenThrow(new OpdNotFoundException("OPD-003"));

        mockMvc.perform(put("/opd/update/{kodeOpd}", "OPD-003")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(opdService).detailOpd("OPD-003");
        verify(opdService, never()).ubahOpd(anyString(), any(Opd.class));
    }

    @Test
    void hapusOpd_WhenOpdExists_ShouldDeleteOpd() throws Exception {
        doNothing().when(opdService).hapusOpd("OPD-001");

        mockMvc.perform(delete("/opd/delete/{kodeOpd}", "OPD-001"))
                .andExpect(status().isNoContent());

        verify(opdService).hapusOpd("OPD-001");
    }

     @Test
    void hapusOpd_WhenOpdNotExists_ShouldReturn404() throws Exception {
        doThrow(new OpdNotFoundException("OPD-003")).when(opdService).hapusOpd("OPD-003");

        mockMvc.perform(delete("/opd/delete/{kodeOpd}", "OPD-003"))
                .andExpect(status().isNotFound());

        verify(opdService).hapusOpd("OPD-003");
    }
}
