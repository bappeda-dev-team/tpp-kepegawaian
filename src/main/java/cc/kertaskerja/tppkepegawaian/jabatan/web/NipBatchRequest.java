package cc.kertaskerja.tppkepegawaian.jabatan.web;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@SuppressWarnings("unused")
public class NipBatchRequest {

    @NotEmpty(message = "nip_pegawais tidak boleh kosong")
    private List<String> nipPegawais;
    private Integer bulan;
    private Integer tahun;
    private String kodeOpd;

    public List<String> getNipPegawais() {
        return nipPegawais;
    }

    public void setNipPegawais(List<String> nipPegawais) {
        this.nipPegawais = nipPegawais;
    }

    public Integer getBulan() {
        return bulan;
    }

    public void setBulan(Integer bulan) {
        this.bulan = bulan;
    }

    public Integer getTahun() {
        return tahun;
    }

    public void setTahun(Integer tahun) {
        this.tahun = tahun;
    }

    public String getKodeOpd() {
        return kodeOpd;
    }

    public void setKodeOpd(String kodeOpd) {
        this.kodeOpd = kodeOpd;
    }
}
