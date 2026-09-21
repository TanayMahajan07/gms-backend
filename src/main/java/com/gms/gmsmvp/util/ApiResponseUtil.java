package com.gms.gmsmvp.util;

import com.gms.gmsmvp.response.ApiSuccessResponse;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public final class ApiResponseUtil {

    private ApiResponseUtil(){} // private constructor to prevent instantiation

    public static <T> ApiSuccessResponse<T> success(
            HttpStatus status,
            String message,
            T data
    ) {
        return new ApiSuccessResponse<>(
                LocalDateTime.now(),
                status.value(),
                message,
                data
        );
    }

    public static  <T> ApiSuccessResponse<T> success(
            String message,
            T data
    ) {
        return success(HttpStatus.OK, message, data);
    }

}
