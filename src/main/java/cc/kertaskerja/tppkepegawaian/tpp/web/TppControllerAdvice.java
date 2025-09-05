package cc.kertaskerja.tppkepegawaian.tpp.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cc.kertaskerja.tppkepegawaian.tpp.domain.TppNilaiInputMelebihiMaksimumException;
import cc.kertaskerja.tppkepegawaian.tpp.domain.TppNipSudahAdaException;
import cc.kertaskerja.tppkepegawaian.tpp.domain.TppNotFoundException;
import cc.kertaskerja.tppkepegawaian.tpp.domain.TppSudahAdaException;

@RestControllerAdvice
public class TppControllerAdvice {
    @ExceptionHandler(TppNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String tppNotFoundHandler(TppNotFoundException ex) {
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
