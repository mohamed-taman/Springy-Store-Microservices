package com.siriusxi.ms.store.util.http;

import com.siriusxi.ms.store.util.exceptions.InvalidInputException;
import com.siriusxi.ms.store.util.exceptions.NotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

/**
 * The class Global controller exception handler is a generic and central point for all
 * microservices that handles all services exceptions.
 *
 * <p>It act as filter so it is pluggable component just added to microservice context
 * automatically, when you add <code>ComponentScan</code> on your application.
 *
 * @see org.springframework.context.annotation.ComponentScan
 * @author mohamed.taman
 * @version v1.3
 * @since v0.1
 */
@RestControllerAdvice
@Log4j2
class GlobalControllerExceptionHandler {

  /**
   * Method to handle <i>not found exceptions</i> http error info.
   *
   * @param request the request to get some request information
   * @param ex the ex to get its information
   * @return the http error information.
   * @since v0.1
   */
  @ResponseStatus(NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public @ResponseBody HttpErrorInfo handleNotFoundExceptions(
      ServerHttpRequest request, Exception ex) {

    return createHttpErrorInfo(NOT_FOUND, request, ex);
  }

  /**
   * Method to handle <i>invalid input exception</i> http error info.
   *
   * @param request the request to get some request information
   * @param ex the ex to get its information
   * @return the http error information.
   * @since v0.1
   */
  @ResponseStatus(UNPROCESSABLE_ENTITY)
  @ExceptionHandler(InvalidInputException.class)
  public @ResponseBody HttpErrorInfo handleInvalidInputException(
      ServerHttpRequest request, Exception ex) {

    return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
  }

  private HttpErrorInfo createHttpErrorInfo(
      HttpStatus httpStatus, ServerHttpRequest request, Exception ex) {
    final var path = request.getPath().pathWithinApplication().value();
    final var message = ex.getMessage();

    log.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);
    return new HttpErrorInfo(httpStatus, path, message);
  }
}
