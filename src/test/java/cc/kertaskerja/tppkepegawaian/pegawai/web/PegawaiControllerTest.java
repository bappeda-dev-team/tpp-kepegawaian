package cc.kertaskerja.tppkepegawaian.pegawai.web;

import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiService;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.StatusPegawai;

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
    
    @BeforeEach
    void setUp() {
        testPegawai = new Pegawai(
                1L,
                "John Doe",
                "198001012010011001",
                "OPD-001",
                StatusPegawai.AKTIF,
                "hashedpassword",
                Instant.now(),
                Instant.now()
        );
    }
    
    @Test
    void detailByNip_WhenPegawaiExists_ShouldReturnPegawai() throws Exception {
        when(pegawaiService.detailPegawai("198001012010011001")).thenReturn(testPegawai);
        
        mockMvc.perform(get("/pegawai/198001012010011001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.namaPegawai").value("John Doe"))
                .andExpect(jsonPath("$.nip").value("198001012010011001"))
                .andExpect(jsonPath("$.kodeOpd").value("OPD-001"))
                .andExpect(jsonPath("$.statusPegawai").value("AKTIF"))
                .andExpect(jsonPath("$.passwordHash").value("hashedpassword"));
    }
    
    @Test
    void detailByNip_WhenPegawaiNotExists_ShouldReturnNotFound() throws Exception {
        when(pegawaiService.detailPegawai("999999999999999999")).thenThrow(new PegawaiNotFoundException("999999999999999999"));
        
        mockMvc.perform(get("/pegawai/999999999999999999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void tambah_WhenValidPegawaiRequest_ShouldCreateJabatan() throws Exception {
        testPegawaiRequest = new PegawaiRequest(
                null,
                "Jane Doe",
                "201001012010011001",
                "OPD-001",
                StatusPegawai.AKTIF,
                "hashedpassword123"
        );
        
        Pegawai createPegawai = new Pegawai(
                2L,
                "Jane Doe",
                "201001012010011001",
                "OPD-001",
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
                .andExpect(jsonPath("$.statusPegawai").value("AKTIF"))
                .andExpect(jsonPath("$.passwordHash").value("hashedpassword123"));
    }
    
    @Test
    void tambah_WhenInvalidPegawaiRequest_ShouldReturn400() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
                null,
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
    void tambah_WhenOpdNotExists_ShouldReturn400() throws Exception {
        when(pegawaiService.tambahPegawai(any(Pegawai.class)))
                .thenThrow(new PegawaiNotFoundException("OPD-002"));
        
        mockMvc.perform(post("/pegawai")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPegawaiRequest)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void ubahPegawai_WhenValidRequest_ShouldUpdatePegawai() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
                1L,
                "Rohman",
                "199501012012011003",
                "OPD-001",
                StatusPegawai.CUTI,
                "password213"
        );
        
        Pegawai existingPegawai = new Pegawai(
                1L,
                "John Doe",
                "198001012010011001",
                "OPD-001",
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
                StatusPegawai.CUTI,
                "password213",
                Instant.now(),
                Instant.now()
        );
        
        when(pegawaiService.detailPegawai("199501012012011003")).thenReturn(existingPegawai);
        when(pegawaiService.ubahPegawai(eq("199501012012011003"), any(Pegawai.class))).thenReturn(updatePegawai);
        
        mockMvc.perform(put("/pegawai/{nip}", "199501012012011003")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.namaPegawai", is("Rohman")))
                .andExpect(jsonPath("$.nip", is("199501012012011003")))
                .andExpect(jsonPath("$.kodeOpd", is("OPD-001")))
                .andExpect(jsonPath("$.statusPegawai", is("CUTI")))
                .andExpect(jsonPath("$.passwordHash", is("password213")));
        
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
                StatusPegawai.CUTI,
                "password213"
        );
        
        when(pegawaiService.detailPegawai("198001012010011001")).thenThrow(new PegawaiNotFoundException("198001012010011001"));
        
        mockMvc.perform(put("/pegawai/{nip}", "198001012010011001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(pegawaiService).detailPegawai("198001012010011001");
        verify(pegawaiService, never()).ubahPegawai(anyString(), any(Pegawai.class));
    }
    
    @Test
    void ubahPegawai_WhenKodeOpdNotExists_ShouldReturn404() throws Exception {
        PegawaiRequest request = new PegawaiRequest(
                1L,
                "Rohman",
                "198001012010011001",
                "OPD-003",
                StatusPegawai.CUTI,
                "password213"
        );
        
        when(pegawaiService.detailPegawai("198001012010011001")).thenReturn(testPegawai);
        when(pegawaiService.ubahPegawai(eq("198001012010011001"), any(Pegawai.class)))
                .thenThrow(new OpdNotFoundException("OPD-003"));
        
        mockMvc.perform(put("/pegawai/{nip}", "198001012010011001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(pegawaiService).detailPegawai("198001012010011001");
        verify(pegawaiService).ubahPegawai(eq("198001012010011001"), any(Pegawai.class));
    }
    
    @Test
    void hapusPegawai_WhenJabatanExists_ShouldDeletePegawai() throws Exception {
        doNothing().when(pegawaiService).hapusPegawai("198001012010011001");
        
        mockMvc.perform(delete("/pegawai/{nip}", "198001012010011001"))
                .andExpect(status().isNoContent());
        
        verify(pegawaiService).hapusPegawai("198001012010011001");
    }
    
    @Test
    void hapusPegawai_WhenPegawaiNotExists_ShouldReturn404() throws Exception {
        doThrow(new PegawaiNotFoundException("198001012010011001")).when(pegawaiService).hapusPegawai("198001012010011001");
        
        mockMvc.perform(delete("/pegawai/{nip}", "198001012010011001"))
                .andExpect(status().isNotFound());
        
        verify(pegawaiService).hapusPegawai("198001012010011001");
    }
}