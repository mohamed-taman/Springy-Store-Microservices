package com.siriusxi.ms.store.util.http;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * Record HttpErrorInfo which encapsulate all HTTP errors sent to client.
 * <p>
 * Since it is a record and not normal POJO,
 * so it needs some customizations to be serialized to JSON.
 *
 * @author mohamed.taman
 * @version 0.4
 * @see java.lang.Record
 * @since v0.1
 */
public record HttpErrorInfo(
        @JsonProperty("httpStatus")HttpStatus httpStatus,
        @JsonProperty("message")String message,
        @JsonProperty("path")String path,
        @JsonProperty("time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
        @JsonSerialize(using = ZonedDateTimeSerializer.class)
        ZonedDateTime timestamp
) {

    public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
        this(httpStatus, message, path, ZonedDateTime.now());
    }

    public static void main(String[] args) throws JsonProcessingException {
        HttpErrorInfo err = new HttpErrorInfo(HttpStatus.BAD_REQUEST, "/path", "Error man");
        HttpErrorInfo err1 = new HttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, "Error message man",
                "/path1234", ZonedDateTime.now());
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(err));
        System.out.println(mapper.writeValueAsString(err1));
    }
}
