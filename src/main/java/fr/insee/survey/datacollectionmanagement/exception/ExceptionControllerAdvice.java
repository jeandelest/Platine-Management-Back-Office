package fr.insee.survey.datacollectionmanagement.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Handle API exceptions for project
 * Do not work on exceptions occuring before/outside controllers scope
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ExceptionControllerAdvice {

    private final ApiExceptionComponent errorComponent;

    private static final String ERROR_OCCURRED_LABEL = "An error has occurred";

    /**
     * Global method to process the catched exception
     *
     * @param ex      Exception catched
     * @param status  status linked with this exception
     * @param request request initiating the exception
     * @return the apierror object with linked status code
     */
    private ResponseEntity<ApiError> processException(Exception ex, HttpStatus status, WebRequest request) {
        return processException(ex, status, request, null);
    }

    /**
     * Global method to process the catched exception
     *
     * @param ex                   Exception catched
     * @param status               status linked with this exception
     * @param request              request initiating the exception
     * @param overrideErrorMessage message overriding default error message from exception
     * @return the apierror object with linked status code
     */
    private ResponseEntity<ApiError> processException(Exception ex, HttpStatus status, WebRequest request, String overrideErrorMessage) {
        String errorMessage = ex.getMessage();
        if (overrideErrorMessage != null) {
            errorMessage = overrideErrorMessage;
        }
        ApiError error = errorComponent.buildApiErrorObject(request, status, errorMessage);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void noHandlerFoundException(NoHandlerFoundException e, WebRequest request) {
        log.error(e.getMessage(), e);
        processException(e, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public void accessDeniedException(AccessDeniedException e, WebRequest request) {
        log.error(e.getMessage(), e);
        processException(e, HttpStatus.FORBIDDEN, request);
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public void handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            WebRequest request) {
        log.error(e.getMessage(), e);
        processException(e, HttpStatus.BAD_REQUEST, request, "Invalid parameters");
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public void handleConstraintViolation(
            ConstraintViolationException e,
            WebRequest request) {
        log.error(e.getMessage(), e);
        processException(e, HttpStatus.BAD_REQUEST, request, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public void handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, WebRequest request) {
        log.error(e.getMessage(), e);

        Throwable rootCause = e.getRootCause();

        String errorMessage = "Error when deserializing JSON";
        if (rootCause instanceof JsonParseException parseException) {
            String location = parseException.getLocation() != null ? "[line: " + parseException.getLocation().getLineNr() + ", column: " + parseException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error with JSON syntax. Check that your json is well formatted: " + parseException.getOriginalMessage() + " " + location;
        }
        if (rootCause instanceof JsonMappingException mappingException) {
            String location = mappingException.getLocation() != null ? "[line: " + mappingException.getLocation().getLineNr() + ", column: " + mappingException.getLocation().getColumnNr() + "]" : "";
            errorMessage = "Error when deserializing JSON. Check that your JSON properties are of the expected types " + location;
        }
        processException(e, HttpStatus.BAD_REQUEST, request, errorMessage);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public void notFoundException(NotFoundException e, WebRequest request) {
        log.error(e.getMessage(), e);
        processException(e, HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseBody
    public void exceptions(HttpClientErrorException e, WebRequest request) {
        log.error(e.getMessage(), e);
        processException(e, HttpStatus.valueOf(e.getStatusCode().value()), request, ERROR_OCCURRED_LABEL);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public void exceptions(Exception e, WebRequest request) {
        log.error(e.getMessage(), e);
        processException(e, HttpStatus.INTERNAL_SERVER_ERROR, request, ERROR_OCCURRED_LABEL);
    }
}