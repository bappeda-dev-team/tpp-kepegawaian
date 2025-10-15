package cc.kertaskerja.tppkepegawaian.jabatan.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanNotFoundException;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanPegawaiSudahAdaException;
import cc.kertaskerja.tppkepegawaian.jabatan.domain.exception.JabatanSudahAdaException;


@RestControllerAdvice
public class JabatanControllerAdvice {
	@ExceptionHandler(JabatanNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String jabatanNotFoundHandler(JabatanNotFoundException ex) {
		return ex.getMessage();
	}
	
	@ExceptionHandler(JabatanSudahAdaException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String jabatanSudahAdaHandler(JabatanSudahAdaException ex) {
        return ex.getMessage();
    }
	
	@ExceptionHandler(JabatanPegawaiSudahAdaException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	String jabatanPegawaiSudahAdaHandler(JabatanPegawaiSudahAdaException ex) {
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
