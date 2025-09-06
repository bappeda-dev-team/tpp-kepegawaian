package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TppPerhitunganController.class)
public class TppPerhitunganControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TppPerhitunganService tppPerhitunganService;

    @Autowired
    private ObjectMapper objectMapper;

    private TppPerhitungan tppPerhitungan;
    private TppPerhitunganRequest tppPerhitunganRequest;

    @BeforeEach
    void setUp() {
        tppPerhitungan = new TppPerhitungan(
                1L,
                JenisTpp.BEBAN_KERJA,
                "kodeOpd",
                "nip",
                "kodePemda",
                "PERHITUNGAN_1",
                "22.25",
                30f,
                1,
                2023,
                22.25f,
                null,
                null
        );
        tppPerhitunganRequest = new TppPerhitunganRequest(
                1L,
                JenisTpp.BEBAN_KERJA,
                "kodeOpd",
                "nip",
                "kodePemda",
                List.of(NamaPerhitungan.DISIPLIN, NamaPerhitungan.KELAS_JABATAN),
                List.of(22f, 25f),
                30f,
                1,
                2023,
                47f
        );
    }

    @Test
    void getById_whenExists_shouldReturnTppPerhitungan() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan(1L)).thenReturn(tppPerhitungan);
        mockMvc.perform(get("/tppPerhitungan/detail/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nip", is("nip")));
    }

    @Test
    void getById_whenNotExists_shouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan(99L)).thenThrow(new TppPerhitunganNotFoundException(99L));
        mockMvc.perform(get("/tppPerhitungan/detail/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByBulanAndTahun_whenExists_shouldReturnListOfTppPerhitungan() throws Exception {
        when(tppPerhitunganService.listTppPerhitunganByBulanAndTahun(1, 2023)).thenReturn(Collections.singletonList(tppPerhitungan));
        mockMvc.perform(get("/tppPerhitungan/detail/1/2023"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nip", is("nip")));
    }

    @Test
    void getByBulanAndTahun_whenNotExists_shouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.listTppPerhitunganByBulanAndTahun(1, 2024)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/tppPerhitungan/detail/1/2024"))
                .andExpect(status().isNotFound());
    }

    @Test
    void putTpp_whenValid_shouldUpdateTppPerhitungan() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan(1L)).thenReturn(tppPerhitungan);
        when(tppPerhitunganService.ubahTppPerhitungan(eq(1L), any(TppPerhitungan.class))).thenReturn(tppPerhitungan);

        mockMvc.perform(put("/tppPerhitungan/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppPerhitunganRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void putTpp_whenIdNotFound_shouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan(99L)).thenThrow(new TppPerhitunganNotFoundException(99L));

        mockMvc.perform(put("/tppPerhitungan/update/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppPerhitunganRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void putTpp_whenKodeOpdNotFound_shouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan(1L)).thenReturn(tppPerhitungan);
        when(tppPerhitunganService.ubahTppPerhitungan(eq(1L), any(TppPerhitungan.class)))
                .thenThrow(new TppPerhitunganNotFoundException(1L));

        mockMvc.perform(put("/tppPerhitungan/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppPerhitunganRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void putTpp_whenNipNotFound_shouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan(1L)).thenReturn(tppPerhitungan);
        when(tppPerhitunganService.ubahTppPerhitungan(eq(1L), any(TppPerhitungan.class)))
                .thenThrow(new TppPerhitunganNotFoundException(1L));

        mockMvc.perform(put("/tppPerhitungan/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppPerhitunganRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void putTpp_whenBulanAndTahunNotFound_shouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.detailTppPerhitungan(1L)).thenReturn(tppPerhitungan);
        when(tppPerhitunganService.ubahTppPerhitungan(eq(1L), any(TppPerhitungan.class)))
                .thenThrow(new TppPerhitunganNotFoundException(1L));

        mockMvc.perform(put("/tppPerhitungan/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppPerhitunganRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void postTpp_whenValid_shouldCreateTppPerhitungan() throws Exception {
        TppPerhitungan savedTpp = new TppPerhitungan(1L, JenisTpp.BEBAN_KERJA, "kodeOpd", "nip", "kodePemda", "PERHITUNGAN_1", "22.25", 30f, 1, 2023, 22.25f, null, null);
        when(tppPerhitunganService.existsByBulanAndTahun(1, 2023)).thenReturn(false);
        when(tppPerhitunganService.tambahTppPerhitungan(any(TppPerhitungan.class))).thenReturn(savedTpp);

        mockMvc.perform(post("/tppPerhitungan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppPerhitunganRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void postTpp_whenBulanAndTahunExists_shouldReturnUnprocessableEntity() throws Exception {
        when(tppPerhitunganService.existsByBulanAndTahun(1, 2023)).thenReturn(true);

        mockMvc.perform(post("/tppPerhitungan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppPerhitunganRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void postTpp_whenKodeOpdNotFound_shouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.existsByBulanAndTahun(any(Integer.class), any(Integer.class))).thenReturn(false);
        when(tppPerhitunganService.tambahTppPerhitungan(any(TppPerhitungan.class)))
                .thenThrow(new TppPerhitunganNotFoundException(null));

        mockMvc.perform(post("/tppPerhitungan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppPerhitunganRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void postTpp_whenNipNotFound_shouldReturnNotFound() throws Exception {
        when(tppPerhitunganService.existsByBulanAndTahun(any(Integer.class), any(Integer.class))).thenReturn(false);
        when(tppPerhitunganService.tambahTppPerhitungan(any(TppPerhitungan.class)))
                .thenThrow(new TppPerhitunganNotFoundException(null));

        mockMvc.perform(post("/tppPerhitungan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tppPerhitunganRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTpp_whenExists_shouldDelete() throws Exception {
        mockMvc.perform(delete("/tppPerhitungan/delete/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTpp_whenIdNotFound_shouldReturnNotFound() throws Exception {
        doThrow(new TppPerhitunganNotFoundException(99L)).when(tppPerhitunganService).hapusTppPerhitungan(99L);

        mockMvc.perform(delete("/tppPerhitungan/delete/99"))
                .andExpect(status().isNotFound());
    }
}
