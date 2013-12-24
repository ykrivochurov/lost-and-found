package ru.eastbanctech.board.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import ru.eastbanctech.board.core.service.ServiceException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author y.bulkin
 */
@ControllerAdvice
public class RestResponseEntityExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleServiceException(ServiceException ex, WebRequest request) {

        Locale locale = localeResolver.resolveLocale(((ServletWebRequest) request).getRequest());
        String bodyOfResponse = messageSource.getMessage("exceptions.service.exception", null, locale);
        return new ResponseEntity<Object>(ex, HttpStatus.BAD_REQUEST);
    }

    //@ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOther(Exception ex, WebRequest request) {

        Locale locale = localeResolver.resolveLocale(((ServletWebRequest) request).getRequest());
        String bodyOfResponse = messageSource.getMessage("exceptions.other", null, locale);
        return new ResponseEntity<Object>(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleConstraintViolationException(MethodArgumentNotValidException ex,
                                                                     WebRequest request) {

        Locale locale = localeResolver.resolveLocale(((ServletWebRequest) request).getRequest());

        Map<String, String> errorsMap = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();

        for (FieldError fieldError : fieldErrors) {
            errorsMap.put(fieldError.getField(),
                    messageSource.getMessage(fieldError.getDefaultMessage(), null, locale));
        }
        for (ObjectError objectError : globalErrors) {
            errorsMap.put(objectError.getObjectName(),
                    messageSource.getMessage(objectError.getDefaultMessage(), null, locale));
        }

        return new ResponseEntity<Object>(errorsMap, HttpStatus.BAD_REQUEST);
    }
}
