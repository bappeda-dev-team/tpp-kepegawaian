
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppHasilPerhitunganNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganNipBulanTahunNotFoundException;

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


    @ExceptionHandler(TppPerhitunganNipBulanTahunNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppPerhitunganNipBulanTahunNotFoundHandler(TppPerhitunganNipBulanTahunNotFoundException ex) {
        return ex.getMessage();
    }
}
