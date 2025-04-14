package cc.kertaskerja.tppkepegawaian.pegawai.web;

import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiNotFoundException;
import cc.kertaskerja.tppkepegawaian.pegawai.domain.PegawaiSudahAdaException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class PegawaiContorllerAdvice {
    @ExceptionHandler(PegawaiNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String pegawaiNotFoundHandler(PegawaiNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(PegawaiSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String pegawaiSudahAdaHandler(PegawaiSudahAdaException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
        var errors = new HashMap<String, String>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String fieldName = ((FieldError) err).getField();
            String errMessage = err.getDefaultMessage();
            errors.put(fieldName, errMessage);
        });

        return errors;
    }
}
