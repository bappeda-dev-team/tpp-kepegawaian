package cc.kertaskerja.tppkepegawaian.pegawai.domain;

import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PegawaiServiceTest {
    @Mock
    private PegawaiRepository pegawaiRepository;

    @Mock
    private OpdRepository opdRepository;

    @InjectMocks
    private PegawaiService pegawaiService;

    private Pegawai testPegawai;

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
    void listPegawaiAktif_ShouldReturnPegawaiList() {
        List<Pegawai> pegawaiList = List.of(testPegawai);
        when(pegawaiRepository.findByKodeOpd("OPD-001")).thenReturn(pegawaiList);
        
        Iterable<Pegawai> result = pegawaiService.listPegawaiAktif("OPD-001");
        
        assertThat(result).containsExactly(testPegawai);
        verify(pegawaiRepository).findByKodeOpd("OPD-001");
    }

    @Test
    void detailPegawai_WhenPegawaiExists_ShouldReturnPegawai() {
        String nip = "198001012010011001";
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.of(testPegawai));
        
        Pegawai result = pegawaiService.detailPegawai(nip);
        
        assertThat(result).isEqualTo(testPegawai);
        verify(pegawaiRepository).findByNip(nip);
    }

    @Test
    void detailPegawai_WhenPegawaiNotExists_ShouldThrowException() {
        String nip = "999999999999999999";
        when(pegawaiRepository.findByNip(nip)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> pegawaiService.detailPegawai(nip))
                .isInstanceOf(PegawaiNotFoundException.class)
                .hasMessageContaining(nip);
        verify(pegawaiRepository).findByNip(nip);
    }

    @Test
    void tambahPegawai_WhenPegawaiValid_ShouldSaveAndReturnPegawai() {
        Pegawai newPegawai = new Pegawai(
                null,
                "Jane Doe",
                "200601012010012001",
                "OPD-001",
                StatusPegawai.AKTIF,
                "hashedpassword123",
                null,
                null
        );
        
        when(pegawaiRepository.existsByNip(newPegawai.nip())).thenReturn(false);
        when(opdRepository.existsByKodeOpd(newPegawai.kodeOpd())).thenReturn(true);
        when(pegawaiRepository.save(any(Pegawai.class))).thenReturn(
                new Pegawai(
                        2L,
                        newPegawai.namaPegawai(),
                        newPegawai.nip(),
                        newPegawai.kodeOpd(),
                        newPegawai.statusPegawai(),
                        newPegawai.passwordHash(),
                        Instant.now(),
                        Instant.now()
                )
        );
        
        Pegawai result = pegawaiService.tambahPegawai(newPegawai);
        
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.namaPegawai()).isEqualTo("Jane Doe");
        assertThat(result.nip()).isEqualTo("200601012010012001");
        assertThat(result.passwordHash()).isEqualTo("hashedpassword123");
        verify(pegawaiRepository).existsByNip(newPegawai.nip());
        verify(opdRepository).existsByKodeOpd(newPegawai.kodeOpd());
        verify(pegawaiRepository).save(newPegawai);
    }

    @Test
    void tambahPegawai_WhenPegawaiAlreadyExists_ShouldThrowException() {
        when(pegawaiRepository.existsByNip(testPegawai.nip())).thenReturn(true);
        
        assertThatThrownBy(() -> pegawaiService.tambahPegawai(testPegawai))
                .isInstanceOf(PegawaiSudahAdaException.class)
                .hasMessageContaining(testPegawai.nip());
        verify(pegawaiRepository).existsByNip(testPegawai.nip());
        verify(pegawaiRepository, never()).save(any());
    }

    @Test
    void tambahPegawai_WhenOpdNotExists_ShouldThrowException() {
        Pegawai newPegawai = new Pegawai(
                null,
                "Jane Doe",
                "200601012010012001",
                "OPD-9999",
                StatusPegawai.AKTIF,
                "hashedpassword123",
                null,
                null);

        when(pegawaiRepository.existsByNip(newPegawai.nip())).thenReturn(false);
        when(opdRepository.existsByKodeOpd("OPD-9999")).thenReturn(false);

        assertThatThrownBy(() -> pegawaiService.tambahPegawai(newPegawai))
                .isInstanceOf(OpdNotFoundException.class)
                .hasMessageContaining("OPD-9999");

        verify(pegawaiRepository).existsByNip(newPegawai.nip());
        verify(opdRepository).existsByKodeOpd("OPD-9999");
        verify(pegawaiRepository, never()).save(any());
    }

    @Test
    void ubahPegawai_WhenPegawaiExistsAndValid_ShouldUpdateAndReturnPegawai() {
        String nip = "198001012010011001";
        Pegawai updatedPegawai = new Pegawai(
                1L,
                "Anthony",
                nip,
                "OPD-001",
                StatusPegawai.CUTI,
                "newhash123",
                testPegawai.createdDate(),
                Instant.now()
        );
        
        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(opdRepository.existsByKodeOpd("OPD-001")).thenReturn(true);
        when(pegawaiRepository.save(any(Pegawai.class))).thenReturn(updatedPegawai);
        
        Pegawai result = pegawaiService.ubahPegawai(nip, updatedPegawai);
        
        assertThat(result).isEqualTo(updatedPegawai);
        verify(pegawaiRepository).existsByNip(nip);
        verify(opdRepository).existsByKodeOpd("OPD-001");
        verify(pegawaiRepository).save(updatedPegawai);
    }

    @Test
    void ubahPegawai_WhenPegawaiNotExists_ShouldThrowException() {
        String nip = "999999999999999999";
        when(pegawaiRepository.existsByNip(nip)).thenReturn(false);
        
        assertThatThrownBy(() -> pegawaiService.ubahPegawai(nip, testPegawai))
                .isInstanceOf(PegawaiNotFoundException.class)
                .hasMessageContaining(nip);
        verify(pegawaiRepository).existsByNip(nip);
        verify(pegawaiRepository, never()).save(any());
    }

    @Test
    void ubahPegawai_WhenOpdNotExists_ShouldThrowException() {
        String nip = "198001012010011001";
        Pegawai updatedPegawai = new Pegawai(
                1L,
                "Anthony",
                nip,
                "OPD-9999",
                StatusPegawai.CUTI,
                "newhash123",
                testPegawai.createdDate(),
                Instant.now());

        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(opdRepository.existsByKodeOpd("OPD-9999")).thenReturn(false);

        assertThatThrownBy(() -> pegawaiService.ubahPegawai(nip, updatedPegawai))
                .isInstanceOf(OpdNotFoundException.class)
                .hasMessageContaining("OPD-9999");

        verify(pegawaiRepository).existsByNip(nip);
        verify(opdRepository).existsByKodeOpd("OPD-9999");
        verify(pegawaiRepository, never()).save(any());
    }

    @Test
    void hapusPegawai_WhenPegawaiExists_ShouldDeletePegawai() {
        String nip = "198001012010011001";
        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        doNothing().when(pegawaiRepository).deleteByNip(nip);
        
        pegawaiService.hapusPegawai(nip);
        
        verify(pegawaiRepository).existsByNip(nip);
        verify(pegawaiRepository).deleteByNip(nip);
    }

    @Test
    void hapusPegawai_WhenPegawaiNotExists_ShouldThrowException() {
        String nip = "999999999999999999";
        when(pegawaiRepository.existsByNip(nip)).thenReturn(false);
        
        assertThatThrownBy(() -> pegawaiService.hapusPegawai(nip))
                .isInstanceOf(PegawaiNotFoundException.class)
                .hasMessageContaining(nip);
        verify(pegawaiRepository).existsByNip(nip);
        verify(pegawaiRepository, never()).deleteByNip(any());
    }
}
