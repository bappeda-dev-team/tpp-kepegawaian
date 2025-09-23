package cc.kertaskerja.tppkepegawaian.role.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.role.domain.IsActive;
import cc.kertaskerja.tppkepegawaian.role.domain.LevelRole;
import cc.kertaskerja.tppkepegawaian.role.domain.Role;
import cc.kertaskerja.tppkepegawaian.role.domain.RoleNotFoundException;
import cc.kertaskerja.tppkepegawaian.role.domain.RoleService;

@WebMvcTest(RoleController.class)
public class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean   
    private RoleService roleService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Role testRole;
    private RoleRequest testRoleRequest;
    
    @BeforeEach
    void setUp() {
        testRole = new Role(
                1L,
                "Admin",
                "198001012010011001",
                LevelRole.LEVEL_1,
                IsActive.AKTIF,
                Instant.now(),
                Instant.now()
        );
    }
    
    @Test
    void detailById_WhenRoleExists_ShouldReturnRole() throws Exception {
        when(roleService.detailRole(1L)).thenReturn(testRole);
        
        mockMvc.perform(get("/role/detail/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.namaRole").value("Admin"))
                .andExpect(jsonPath("$.nip").value("198001012010011001"))
                .andExpect(jsonPath("$.levelRole").value("LEVEL_1"))
                .andExpect(jsonPath("$.isActive").value("AKTIF"));
    }
    
    @Test
    void detailById_WhenRoleNotExists_ShouldReturnNotFound() throws Exception {
        when(roleService.detailRole(3L)).thenThrow(new RoleNotFoundException(3L));
        
        mockMvc.perform(get("/role/detail/3"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void tambah_WhenValidRoleRequest_ShouldCreateRole() throws Exception {
        testRoleRequest = new RoleRequest(
                null,
                "User",
                "198001012010011001",
                LevelRole.LEVEL_2,
                IsActive.AKTIF
        );
        
        Role createRole = new Role(
                2L, 
                "Admin",
                "198001012010011001",
                LevelRole.LEVEL_2,
                IsActive.AKTIF,
                Instant.now(),
                Instant.now()
        );
        
        when(roleService.tambahRole(any(Role.class))).thenReturn(createRole);
        
        mockMvc.perform(post("/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRoleRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.namaRole").value("Admin"))
                .andExpect(jsonPath("$.nip").value("198001012010011001"))
                .andExpect(jsonPath("$.levelRole").value("LEVEL_2"))
                .andExpect(jsonPath("$.isActive").value("AKTIF"));
    }
    
    @Test
    void tambah_WhenInvalidRoleRequest_ShouldReturn400() throws Exception {
        RoleRequest request = new RoleRequest(
                null, 
                "",
                "",
                LevelRole.LEVEL_2,
                IsActive.AKTIF
        );
        
        mockMvc.perform(post("/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void tambah_WhenNipNotExists_ShouldReturn400() throws Exception {
        when(roleService.tambahRole(any(Role.class)))
        .thenThrow(new PegawaiNotFoundException("201001012010011001"));

        mockMvc.perform(post("/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRoleRequest)))
        .andExpect(status().isBadRequest());
    }
    
    @Test
    void ubahRole_WhenValidRequest_ShouldUpdateRole() throws Exception {
        RoleRequest request = new RoleRequest(
                1L,
                "Guest",
                "198001012010011001",
                LevelRole.LEVEL_3,
                IsActive.AKTIF
        );
        
        Role existingRole = new Role(
                1L,
                "Admin",
                "198001012010011001",
                LevelRole.LEVEL_1,
                IsActive.AKTIF,
                Instant.now(),
                Instant.now()
        );
        
        Role updateRole = new Role(
                1L,
                "Guest",
                "198001012010011001",
                LevelRole.LEVEL_3,
                IsActive.AKTIF,
                Instant.now(),
                Instant.now()
        );
        
        when(roleService.detailRole(1L)).thenReturn(existingRole);
        when(roleService.ubahRole(eq(1L), any(Role.class))).thenReturn(updateRole);
        
        mockMvc.perform(put("/role/update/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.namaRole", is("Guest")))
                .andExpect(jsonPath("$.nip", is("198001012010011001")))
                .andExpect(jsonPath("$.levelRole", is("LEVEL_3")))
                .andExpect(jsonPath("$.isActive", is("AKTIF")));
        
        verify(roleService).detailRole(1L);
        verify(roleService).ubahRole(eq(1L), any(Role.class));
    }
    
    @Test
    void ubahRole_WhenIdNotExists_ShouldReturn404() throws Exception {
        RoleRequest request = new RoleRequest(
                3L,
                "Guest",
                "198001012010011001",
                LevelRole.LEVEL_3,
                IsActive.AKTIF
        );
        
        when(roleService.detailRole(3L)).thenThrow(new RoleNotFoundException(1L));
        
        mockMvc.perform(put("/role/update/{id}", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(roleService).detailRole(3L);
        verify(roleService, never()).ubahRole(anyLong(), any(Role.class));
    }
    
    @Test
    void ubahRole_WhenNipNotExists_ShouldReturn404() throws Exception {
        RoleRequest request = new RoleRequest(
                1L,
                "Guest",
                "201001012010011001",
                LevelRole.LEVEL_3,
                IsActive.AKTIF
        );
        
        when(roleService.detailRole(1L)).thenReturn(testRole);
        when(roleService.ubahRole(eq(1L), any(Role.class)))
                .thenThrow(new PegawaiNotFoundException("201001012010011001"));
        
        mockMvc.perform(put("/role/update/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(roleService).detailRole(1L);
        verify(roleService).ubahRole(eq(1L), any(Role.class));
    }
    
    @Test
    void hapusRole_WhenRoleExists_ShouldDeleteRole() throws Exception {
        doNothing().when(roleService).hapusRole(1L);
        
        mockMvc.perform(delete("/role/delete/{id}", "1"))
                .andExpect(status().isNoContent());
        
        verify(roleService).hapusRole(1L);
    }
    
    @Test
    void hapusRole_WhenRoeNotExists_ShouldReturn404() throws Exception {
        doThrow(new RoleNotFoundException(3L)).when(roleService).hapusRole(3L);
        
        mockMvc.perform(delete("/role/delete/{id}", "3"))
                .andExpect(status().isNotFound());
        
        verify(roleService).hapusRole(3L);
    }
}
