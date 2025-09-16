
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganJenisTppNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganJenisTppNipBulanTahunSudahAdaException;

@RestControllerAdvice
public class TppPerhitunganControllerAdvice {
    @ExceptionHandler(TppPerhitunganNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String tppPerhitunganNotFoundHandler(TppPerhitunganNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppPerhitunganJenisTppNipBulanTahunSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppPerhitunganSudahAdaHandler(TppPerhitunganJenisTppNipBulanTahunSudahAdaException ex) {
        return ex.getMessage();
    }


    @ExceptionHandler(TppPerhitunganJenisTppNipBulanTahunNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppPerhitunganNipBulanTahunNotFoundHandler(TppPerhitunganJenisTppNipBulanTahunNotFoundException ex) {
        return ex.getMessage();
    }
}
