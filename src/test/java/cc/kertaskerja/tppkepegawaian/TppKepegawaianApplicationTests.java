package cc.kertaskerja.tppkepegawaian;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.Pegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.RolePegawai;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.StatusPegawai;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
class TppKepegawaianApplicationTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void whenGetRequestWithNipThenPegawaiReturned() {
		var nip = "123456789012345678";
		var kodeOpd = "5.01.5.05.0.00.02.0000";
		var pegawaiToCreate = Pegawai.of("Pegawai A", nip,
				kodeOpd, "jb-123", StatusPegawai.AKTIF, RolePegawai.LEVEL_1);
		Pegawai expectedPegawai = webTestClient
				.post()
				.uri("/pegawais")
				.bodyValue(pegawaiToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Pegawai.class).value(pegawai -> {
					assertThat(pegawai).isNotNull();
				}).returnResult().getResponseBody();

		webTestClient
				.get()
				.uri("/pegawais/" + nip)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Pegawai.class).value(actualPegawai -> {
					assertThat(actualPegawai).isNotNull();
					assertThat(actualPegawai.nip()).isEqualTo(expectedPegawai.nip());
				});
	}

}
