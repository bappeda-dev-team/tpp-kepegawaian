package cc.kertaskerja.tppkepegawaian.jabatan.web;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@SuppressWarnings("unused")
public class NipBatchRequest {

    @NotEmpty(message = "nip_pegawais tidak boleh kosong")
    private List<String> nipPegawais;

    public List<String> getNipPegawais() {
        return nipPegawais;
    }

    public void setNipPegawais(List<String> nipPegawais) {
        this.nipPegawais = nipPegawais;
    }
}
