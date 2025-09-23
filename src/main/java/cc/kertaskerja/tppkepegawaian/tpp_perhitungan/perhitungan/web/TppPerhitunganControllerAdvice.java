
package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganKodeOpdBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganKodeOpdBulanTahunSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.exception.TppPerhitunganNipBulanTahunSudahAdaException;

@RestControllerAdvice
public class TppPerhitunganControllerAdvice {
    @ExceptionHandler(TppPerhitunganNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String tppPerhitunganNotFoundHandler(TppPerhitunganNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppPerhitunganNipBulanTahunSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppPerhitunganNipBulanTahunSudahAdaHandler(TppPerhitunganNipBulanTahunSudahAdaException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppPerhitunganNipBulanTahunNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String tppPerhitunganNipBulanTahunNotFoundHandler(TppPerhitunganNipBulanTahunNotFoundException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(TppPerhitunganKodeOpdBulanTahunSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppPerhitunganKodeOpdBulanTahunSudahAdaHandler(TppPerhitunganKodeOpdBulanTahunSudahAdaException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(TppPerhitunganKodeOpdBulanTahunNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String tppPerhitunganKodeOpdBulanTahunNotFoundHandler(TppPerhitunganKodeOpdBulanTahunNotFoundException ex) {
        return ex.getMessage();
    }
}
