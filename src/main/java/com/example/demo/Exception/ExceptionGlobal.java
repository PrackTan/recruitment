package com.example.demo.Exception;


import com.example.demo.payload.ErrorLoad;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionGlobal {
    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
    })
    public ResponseEntity<?> handleException(Exception e) {
        ErrorLoad errorRes = new ErrorLoad();
        errorRes.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorRes.setError(e.getMessage());
        errorRes.setMessage("Thông tin đăng nhập không hợp lệ");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
    }
    @ExceptionHandler(value = {UsernameExistance.class})
    public ResponseEntity<?> UsernameExist(UsernameExistance usernameExistance){
        ErrorLoad errorRes = new ErrorLoad();
        errorRes.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorRes.setError(usernameExistance.getMessage());
        errorRes.setMessage("Username đã tồn tại");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
    }
    @ExceptionHandler(value = {IdValidationException.class})
    public ResponseEntity<?> IdentityValidation(IdValidationException idValidationException){
        ErrorLoad errorRes = new ErrorLoad();
        errorRes.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorRes.setError(idValidationException.getMessage());
        errorRes.setMessage("Username không tồn tại");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
    }

    @ExceptionHandler( MethodArgumentNotValidException.class)
    public ResponseEntity<?> validattionError(MethodArgumentNotValidException methodArgumentNotValidException) {
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        final List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        ErrorLoad errorLoad = new ErrorLoad();
        errorLoad.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorLoad.setError(methodArgumentNotValidException.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f->f.getDefaultMessage()).collect(Collectors.toList());
        errorLoad.setMessage(errors.size()>1 ? errors.toString() : errors.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorLoad);
    }
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(Exception e){
        ErrorLoad errorLoad = new ErrorLoad();
        errorLoad.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorLoad.setError(e.getMessage());
        errorLoad.setMessage("404 NOT FOUND. URL may not exist");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorLoad);
    }
    @ExceptionHandler(value = {MissingRequestCookieException.class})
    public  ResponseEntity<ErrorLoad> handleMissingRequestCookie(MissingRequestCookieException missingRequestCookieException){
        ErrorLoad errorLoad = new ErrorLoad();
        errorLoad.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorLoad.setError(missingRequestCookieException.getMessage());
        errorLoad.setMessage("không tìm thấy cookié");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorLoad);
    }
}