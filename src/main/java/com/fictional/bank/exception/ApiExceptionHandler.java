package com.fictional.bank.exception;

import java.util.Collections;
import java.util.List;

import com.fictional.bank.response.BadRequestErrorResponse;
import com.fictional.bank.response.ErrorResponse;
import com.fictional.bank.response.ValidationErrorDetail;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler
{

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestErrorResponse handleBadRequestException(ApiException ex)
    {
        return new BadRequestErrorResponse(ex.getMessage(), Collections.emptyList());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BadRequestErrorResponse handleValidationException(MethodArgumentNotValidException ex)
    {
        List<ValidationErrorDetail> detailList = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationErrorDetail(error.getField(), error.getDefaultMessage(), error.getCode()))
                .toList();

        return new BadRequestErrorResponse(ex.getMessage(), detailList);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(AccessDeniedException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ApiNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(ApiNotFoundException ex)
    {

        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ApiNotDeletableException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(ApiNotDeletableException ex)
    {
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex)
    {
        return new ErrorResponse(ApiErrorMessage.INTERNAL_ERROR.getMessage());
    }
}
