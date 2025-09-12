package cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppNilaiInputMelebihiMaksimumException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppNipSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.tpp.domain.TppSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp_perhitungan.perhitungan.domain.TppPerhitunganNipBulanTahunNotFoundException;

@RestControllerAdvice
public class TppControllerAdvice {
    @ExceptionHandler(TppNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String tppNotFoundHandler(TppNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppPerhitunganNipBulanTahunNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppPerhitunganNipBulanTahunNotFoundHandler(TppPerhitunganNipBulanTahunNotFoundException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(TppSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppSudahAdaHandler(TppSudahAdaException ex) {
        return ex.getMessage();
    }
    
    @ExceptionHandler(TppNipSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppNipSudahAdaHandler(TppNipSudahAdaException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(TppNilaiInputMelebihiMaksimumException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String tppNilaiInputMelebihiMaksimumHandler(TppNilaiInputMelebihiMaksimumException ex) {
        return ex.getMessage();
    }
}
