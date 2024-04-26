package com.security.appsecurity.Error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.security.appsecurity.Error.dto.ErrorMessage;

@RestControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(LocalNotFoundExceptions.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)

    // public ResponseEntity<ErrorMessage> usernameNotFoundException(UsernameNotFoundException exceptions){
    //     ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND,exceptions.getMessage());
    //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    // }

    public ResponseEntity<ErrorMessage> localNotFoundExceptions (LocalNotFoundExceptions exceptions){
        ErrorMessage errorMessage = new ErrorMessage(HttpStatus.NOT_FOUND,exceptions.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }
}
