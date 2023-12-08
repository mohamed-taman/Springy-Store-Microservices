package com.siriusxi.ms.store.util.http;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Class <code>HttpErrorInfo</code> which encapsulates all HTTP errors sent to the client.
 *
 * @implNote Since it is a class and not a record, some methods need to be explicitly implemented.
 * @see com.siriusxi.ms.store.util.config.GlobalConfiguration
 * @author mohamed.taman
 * @version v4.6
 * @since v0.1
 */
public class HttpErrorInfo {

    private final HttpStatus httpStatus;
    private final String message;
    private final String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    @JsonSerialize(using = ZonedDateTimeSerializer.class)
    private final ZonedDateTime timestamp;

    /**
     * Instantiates a new Http error info.
     *
     * @param httpStatus the http status code and type.
     * @param path       the request path.
     * @param message    the error message.
     */
    public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
        this(httpStatus, message, path, ZonedDateTime.now());
    }

    public HttpErrorInfo(HttpStatus httpStatus, String message, String path, ZonedDateTime timestamp) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    // Implement equals, hashCode, and toString as needed
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpErrorInfo that = (HttpErrorInfo) o;
        return httpStatus == that.httpStatus &&
                Objects.equals(message, that.message) &&
                Objects.equals(path, that.path) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpStatus, message, path, timestamp);
    }

    @Override
    public String toString() {
        return "HttpErrorInfo{" +
                "httpStatus=" + httpStatus +
                ", message='" + message + '\'' +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
