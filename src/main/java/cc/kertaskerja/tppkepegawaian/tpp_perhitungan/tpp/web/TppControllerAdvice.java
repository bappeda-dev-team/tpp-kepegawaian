package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppKodeOpdBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppKodeOpdBulanTahunSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppNipBulanTahunNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppJenisTppNipBulanTahunSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.exception.TppNilaiInputMelebihiMaksimumException;

@RestControllerAdvice
public class TppControllerAdvice {

    @ExceptionHandler(TppJenisTppNipBulanTahunNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppJenisTppNipBulanTahunNotFoundHandler(TppJenisTppNipBulanTahunNotFoundException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(TppJenisTppNipBulanTahunSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String TppJenisTppNipBulanTahunSudahHandler(TppJenisTppNipBulanTahunSudahAdaException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(TppJenisTppKodeOpdBulanTahunNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppJenisTppKodeOpdBulanTahunNotFoundHandler(TppJenisTppKodeOpdBulanTahunNotFoundException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(TppJenisTppKodeOpdBulanTahunSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String TppJenisTppKodeOpdBulanTahunSudahHandler(TppJenisTppKodeOpdBulanTahunSudahAdaException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppNilaiInputMelebihiMaksimumException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppNilaiInputMelebihiMaksimumHandler(TppNilaiInputMelebihiMaksimumException ex) {
        return ex.getMessage();
    }
}
