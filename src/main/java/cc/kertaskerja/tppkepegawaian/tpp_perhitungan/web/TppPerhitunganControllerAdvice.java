
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.TppPerhitunganNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.TppPerhitunganSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.TppHasilPerhitunganNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.TppPerhitunganBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.domain.TppPerhitunganBulanTahunSudahAdaException;

@RestControllerAdvice
public class TppPerhitunganControllerAdvice {
    @ExceptionHandler(TppPerhitunganNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String tppPerhitunganNotFoundHandler(TppPerhitunganNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppPerhitunganSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppPerhitunganSudahAdaHandler(TppPerhitunganSudahAdaException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppHasilPerhitunganNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppHasilPerhitunganNotFoundException(TppHasilPerhitunganNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppPerhitunganBulanTahunSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppPerhitunganBulanTahunSudahAdaHandler(TppPerhitunganBulanTahunSudahAdaException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppPerhitunganBulanTahunNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppPerhitunganBulanTahunNotFoundHandler(TppPerhitunganBulanTahunNotFoundException ex) {
        return ex.getMessage();
    }
}
