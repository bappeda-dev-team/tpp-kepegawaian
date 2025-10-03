package cc.kertaskerja.tppkepegawaian.opd.domain;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OpdServiceTest {
    @Mock
    private OpdRepository opdRepository;

    @InjectMocks
    private OpdService opdService;

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
    void detailOpd_WhenOpdExists_ShouldReturnOpd() {
        when(opdRepository.findByKodeOpd("OPD-001")).thenReturn(Optional.of(testOpd));

        Opd result = opdService.detailOpd("OPD-001");

        assertThat(result).isNotNull();
        assertThat(result.kodeOpd()).isEqualTo("OPD-001");
        assertThat(result.namaOpd()).isEqualTo("Dinas Pendidikan");
        verify(opdRepository).findByKodeOpd("OPD-001");
    }

     @Test
    void detailOpd_WhenOpdNotExists_ShouldThrowException() {
        when(opdRepository.findByKodeOpd("OPD-003")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> opdService.detailOpd("OPD-003"))
                .isInstanceOf(OpdNotFoundException.class);
        verify(opdRepository).findByKodeOpd("OPD-003");
    }
     
     @Test
     void listAllOpd_ShouldReturnAllOpd() {
         List<Opd> opdList = Arrays.asList(
                 testOpd,
                 new Opd(2L, "OPD-002", "Dinas Kesehatan", Instant.now(), Instant.now()),
                 new Opd(3L, "OPD-003", "Dinas Kehutanan", Instant.now(), Instant.now())
         );

         when(opdRepository.findAll()).thenReturn(opdList);

         Iterable<Opd> result = opdService.listAllOpd();

         assertThat(result).isNotNull();
         assertThat(result).hasSize(3);
         assertThat(result).containsExactlyElementsOf(opdList);
         verify(opdRepository).findAll();
     }
     
     @Test
     void listAllOpd_WhenNoOpdExists_ShouldReturnEmptyList() {
         when(opdRepository.findAll()).thenReturn(Collections.emptyList());

         Iterable<Opd> result = opdService.listAllOpd();

         assertThat(result).isNotNull();
         assertThat(result).isEmpty();
         verify(opdRepository).findAll();
     }

    @Test
    void tambahOpd_WhenValidOpd_ShouldSaveAndReturnOpd() {
        Opd newOpd = new Opd(null, "OPD-002", "Dinas Kesehatan", null, null);
        Opd savedOpd = new Opd(2L, "OPD-002", "Dinas Kesehatan", Instant.now(), Instant.now());
        when(opdRepository.save(any(Opd.class))).thenReturn(savedOpd);

        Opd result = opdService.tambahOpd(newOpd);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.kodeOpd()).isEqualTo("OPD-002");
        assertThat(result.namaOpd()).isEqualTo("Dinas Kesehatan");
        verify(opdRepository).save(newOpd);
    }

    @Test
    void tambahOpd_WhenOpdNotExists_ShouldThrowException() {
        Opd newOpd = new Opd(null, "OPD-003", "Dinas Pendidikan", null, null);
        when(opdRepository.existsByKodeOpd("OPD-003")).thenReturn(true);

        assertThatThrownBy(() -> opdService.tambahOpd(newOpd))
                .isInstanceOf(OpdSudahAdaException.class);
        verify(opdRepository).existsByKodeOpd("OPD-003");
        verify(opdRepository, never()).save(any(Opd.class));
    }

    @Test
    void ubahOpd_WhenOpdExists_ShouldUpdateAndReturnOpd() {
        Opd updatedOpd = new Opd(1L, "OPD-001", "Dinas Kehutanan", testOpd.createdDate(), null);
        when(opdRepository.existsByKodeOpd("OPD-001")).thenReturn(true);
        when(opdRepository.save(any(Opd.class))).thenReturn(updatedOpd);

        Opd result = opdService.ubahOpd("OPD-001", updatedOpd);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.kodeOpd()).isEqualTo("OPD-001");
        assertThat(result.namaOpd()).isEqualTo("Dinas Kehutanan");
        assertThat(result.createdDate()).isEqualTo(testOpd.createdDate());
        verify(opdRepository).existsByKodeOpd("OPD-001");
        verify(opdRepository).save(updatedOpd);
    }

    @Test
    void ubahOpd_WhenOpdNotExists_ShouldThrowException() {
        Opd updatedOpd = new Opd(3L, "OPD-003", "Dinas Kesehatan", Instant.now(), null);
        when(opdRepository.existsByKodeOpd("OPD-003")).thenReturn(false);

        assertThatThrownBy(() -> opdService.ubahOpd("OPD-003", updatedOpd))
                .isInstanceOf(OpdNotFoundException.class);
        verify(opdRepository).existsByKodeOpd("OPD-003");
        verify(opdRepository, never()).save(any(Opd.class));
    }

    @Test
    void hapusOpd_WhenOpdExists_ShouldDeleteOpd() {
        when(opdRepository.existsByKodeOpd("OPD-001")).thenReturn(true);

        opdService.hapusOpd("OPD-001");

        verify(opdRepository).existsByKodeOpd("OPD-001");
        verify(opdRepository).deleteByKodeOpd("OPD-001");
    }

    @Test
    void hapusOpd_WhenOpdNotExists_ShouldThrowException() {
        when(opdRepository.existsByKodeOpd("OPD-003")).thenReturn(false);

        assertThatThrownBy(() -> opdService.hapusOpd("OPD-003"))
                .isInstanceOf(OpdNotFoundException.class);
        verify(opdRepository).existsByKodeOpd("OPD-003");
        verify(opdRepository, never()).deleteByKodeOpd(any());
    }
}
