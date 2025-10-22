package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cc.kertaskerja.tppkepegawaian.opd.domain.OpdNotFoundException;
import cc.kertaskerja.tppkepegawaian.opd.domain.OpdRepository;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganRepository;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganKodeOpdBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppKodeOpdBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppKodeOpdBulanTahunSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppNipBulanTahunSudahAdaException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TppServiceTest {

    @Mock
    private TppRepository tppRepository;

    @Mock
    private TppPerhitunganRepository tppPerhitunganRepository;

    @Mock
    private PegawaiRepository pegawaiRepository;

    @Mock
    private OpdRepository opdRepository;

    private TppService tppService;

    @BeforeEach
    void setUp() {
        tppService = new TppService(tppRepository, tppPerhitunganRepository, pegawaiRepository, opdRepository);
    }

    private Tpp createTestTpp() {
        return Tpp.of(
                JenisTpp.KONDISI_KERJA,
                "OPD001",
                "201001012010011001",
                "PEMDA001",
                5000000.0f,
                9,
                2024
        );
    }

    @Test
    void listTppByKodeOpd_ShouldReturnTppList() {
        String kodeOpd = "OPD001";
        List<Tpp> expectedTpps = List.of(createTestTpp());

        when(opdRepository.existsByKodeOpd(kodeOpd)).thenReturn(true);
        when(tppRepository.findByKodeOpd(kodeOpd)).thenReturn(expectedTpps);

        Iterable<Tpp> result = tppService.listTppByKodeOpd(kodeOpd);

        assertEquals(expectedTpps, result);
        verify(opdRepository).existsByKodeOpd(kodeOpd);
        verify(tppRepository).findByKodeOpd(kodeOpd);
    }

    @Test
    void listTppByKodeOpd_WhenOpdNotFound_ShouldThrowException() {
        String kodeOpd = "OPD999";

        when(opdRepository.existsByKodeOpd(kodeOpd)).thenReturn(false);

        assertThrows(OpdNotFoundException.class, () -> {
            tppService.listTppByKodeOpd(kodeOpd);
        });

        verify(opdRepository).existsByKodeOpd(kodeOpd);
        verify(tppRepository, never()).findByKodeOpd(any());
    }

    @Test
    void listTppByNip_ShouldReturnTppList() {
        String nip = "201001012010011001";
        List<Tpp> expectedTpps = List.of(createTestTpp());

        when(pegawaiRepository.existsByNip(nip)).thenReturn(true);
        when(tppRepository.findByNip(nip)).thenReturn(expectedTpps);

        Iterable<Tpp> result = tppService.listTppByNip(nip);

        assertEquals(expectedTpps, result);
        verify(pegawaiRepository).existsByNip(nip);
        verify(tppRepository).findByNip(nip);
    }

    @Test
    void listTppByNip_WhenPegawaiNotFound_ShouldThrowException() {
        String nip = "123456789012345678";

        when(pegawaiRepository.existsByNip(nip)).thenReturn(false);

        assertThrows(PegawaiNotFoundException.class, () -> {
            tppService.listTppByNip(nip);
        });

        verify(pegawaiRepository).existsByNip(nip);
        verify(tppRepository, never()).findByNip(any());
    }

    @Test
    void listTppByNipBulanTahun_ShouldReturnTppList() {
        String nip = "201001012010011001";
        Integer bulan = 9;
        Integer tahun = 2024;
        List<Tpp> expectedTpps = List.of(createTestTpp());

        when(tppPerhitunganRepository.existsByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(true);
        when(tppRepository.findByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(expectedTpps);

        Iterable<Tpp> result = tppService.listTppByNipBulanTahun(nip, bulan, tahun);

        assertEquals(expectedTpps, result);
        verify(tppPerhitunganRepository).existsByNipAndBulanAndTahun(nip, bulan, tahun);
        verify(tppRepository).findByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    @Test
    void listTppByNipBulanTahun_WhenTppPerhitunganNotFound_ShouldThrowException() {
        String nip = "123456789012345678";
        Integer bulan = 10;
        Integer tahun = 2024;

        when(tppPerhitunganRepository.existsByNipAndBulanAndTahun(nip, bulan, tahun)).thenReturn(false);

        assertThrows(TppPerhitunganNipBulanTahunNotFoundException.class, () -> {
            tppService.listTppByNipBulanTahun(nip, bulan, tahun);
        });

        verify(tppPerhitunganRepository).existsByNipAndBulanAndTahun(nip, bulan, tahun);
        verify(tppRepository, never()).findByNipAndBulanAndTahun(any(), any(), any());
    }

    @Test
    void listTppByKodeOpdBulanTahun_WhenTppPerhitunganNotFound_ShouldThrowException() {
        String kodeOpd = "OPD002";
        Integer bulan = 10;
        Integer tahun = 2024;

        when(tppPerhitunganRepository.existsByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun)).thenReturn(false);

        assertThrows(TppPerhitunganKodeOpdBulanTahunNotFoundException.class, () -> {
            tppService.listTppByOpdBulanTahun(kodeOpd, bulan, tahun);
        });

        verify(tppPerhitunganRepository).existsByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
        verify(tppRepository, never()).findByKodeOpdAndBulanAndTahun(any(), any(), any());
    }

    @Test
    void listTppByKodeOpdBulanTahun_ShouldReturnTppList() {
        String kodeOpd = "OPD001";
        Integer bulan = 9;
        Integer tahun = 2024;
        List<Tpp> expectedTpps = List.of(createTestTpp());

        when(tppPerhitunganRepository.existsByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun)).thenReturn(true);
        when(tppRepository.findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun)).thenReturn(expectedTpps);

        Iterable<Tpp> result = tppService.listTppByOpdBulanTahun(kodeOpd, bulan, tahun);

        assertEquals(expectedTpps, result);
        verify(tppPerhitunganRepository).existsByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
        verify(tppRepository).findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
    }

    @Test
    void listTppByKodeOpdBulanTahun_WhenDataNotFound_ShouldThrowException() {
        String kodeOpd = "OPD001";
        Integer bulan = 9;
        Integer tahun = 2024;

        when(tppPerhitunganRepository.existsByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun)).thenReturn(true);
        when(tppRepository.findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun)).thenReturn(List.of());

        assertThrows(TppJenisTppKodeOpdBulanTahunNotFoundException.class, () -> {
            tppService.listTppByOpdBulanTahun(kodeOpd, bulan, tahun);
        });

        verify(tppPerhitunganRepository).existsByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
        verify(tppRepository).findByKodeOpdAndBulanAndTahun(kodeOpd, bulan, tahun);
    }

    @Test
    void detailTpp_WhenTppExists_ShouldReturnTpp() {
        JenisTpp jenisTpp = JenisTpp.KONDISI_KERJA;
        String nip = "201001012010011001";
        Integer bulan = 9;
        Integer tahun = 2024;
        Tpp expectedTpp = createTestTpp();

        when(tppRepository.findByJenisTppAndNipAndBulanAndTahun(jenisTpp, nip, bulan, tahun))
                .thenReturn(Optional.of(expectedTpp));

        Tpp result = tppService.detailTpp(jenisTpp, nip, bulan, tahun);

        assertEquals(expectedTpp, result);
        verify(tppRepository).findByJenisTppAndNipAndBulanAndTahun(jenisTpp, nip, bulan, tahun);
    }

    @Test
    void detailTpp_WhenTppNotExists_ShouldThrowException() {
        JenisTpp jenisTpp = JenisTpp.KONDISI_KERJA;
        String nip = "123456789012345678";
        Integer bulan = 9;
        Integer tahun = 2024;

        when(tppRepository.findByJenisTppAndNipAndBulanAndTahun(jenisTpp, nip, bulan, tahun))
                .thenReturn(Optional.empty());

        assertThrows(TppJenisTppNipBulanTahunNotFoundException.class, () -> {
            tppService.detailTpp(jenisTpp, nip, bulan, tahun);
        });

        verify(tppRepository).findByJenisTppAndNipAndBulanAndTahun(jenisTpp, nip, bulan, tahun);
    }

    @Test
    void ubahTpp_WhenValid_ShouldReturnUpdatedTpp() {
        Tpp tpp = createTestTpp();
        Tpp updatedTpp = Tpp.of(
                JenisTpp.KONDISI_KERJA,
                "OPD001",
                "201001012010011001",
                "PEMDA001",
                5500000.0f,
                9,
                2024
        );

        when(tppRepository.existsByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun())).thenReturn(true);
        when(tppRepository.save(tpp)).thenReturn(updatedTpp);

        Tpp result = tppService.ubahTpp(tpp);

        assertEquals(updatedTpp, result);
        verify(tppRepository).existsByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun());
        verify(tppRepository).save(tpp);
    }

    @Test
    void ubahTpp_WhenTppNotExists_ShouldThrowException() {
        Tpp tpp = createTestTpp();

        when(tppRepository.existsByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun())).thenReturn(false);

        assertThrows(TppJenisTppNipBulanTahunNotFoundException.class, () -> {
            tppService.ubahTpp(tpp);
        });

        verify(tppRepository).existsByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun());
        verify(tppRepository, never()).save(any());
    }


    @Test
    void tambahTpp_WhenValid_ShouldReturnNewTpp() {
        Tpp tpp = createTestTpp();
        Tpp savedTpp = Tpp.of(
                JenisTpp.KONDISI_KERJA,
                "OPD001",
                "201001012010011001",
                "PEMDA001",
                5000000.0f,
                9,
                2024
        );

        when(tppRepository.existsByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun())).thenReturn(false);
        when(tppRepository.save(tpp)).thenReturn(savedTpp);

        Tpp result = tppService.tambahTpp(tpp);

        assertEquals(savedTpp, result);
        verify(tppRepository).existsByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun());
        verify(tppRepository).save(tpp);
    }

    @Test
    void tambahTpp_WhenTppJenisTppNipBulanTahunAlreadyExists_ShouldThrowException() {
        Tpp tpp = createTestTpp();

        when(tppRepository.existsByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun())).thenReturn(true);

        assertThrows(TppJenisTppNipBulanTahunSudahAdaException.class, () -> {
            tppService.tambahTpp(tpp);
        });

        verify(tppRepository).existsByJenisTppAndNipAndBulanAndTahun(
                tpp.jenisTpp(), tpp.nip(), tpp.bulan(), tpp.tahun());
        verify(tppRepository, never()).save(any());
    }


    @Test
    void hapusTppByNipBulanTahun_ShouldDeleteTpp() {
        String nip = "201001012010011001";
        Integer bulan = 9;
        Integer tahun = 2024;

        tppService.hapusTppByNipBulanTahun(nip, bulan, tahun);

        verify(tppRepository).deleteByNipAndBulanAndTahun(nip, bulan, tahun);
    }

    @Test
    void hapusTppByNipBulanTahunNotFound_DeleteFailed() {
        String nip = "999999999999999999";
        Integer bulan = 12;
        Integer tahun = 2024;

        doThrow(new RuntimeException("Data not found")).when(tppRepository)
                .deleteByNipAndBulanAndTahun(nip, bulan, tahun);

        assertThrows(RuntimeException.class, () -> {
            tppService.hapusTppByNipBulanTahun(nip, bulan, tahun);
        });

        verify(tppRepository).deleteByNipAndBulanAndTahun(nip, bulan, tahun);
    }
}
