package com.gms.gmsmvp.response;

import lombok.Builder;

import java.time.LocalDateTime;

public record ApiSuccessResponse<T>(
        LocalDateTime timestamp,
        int status,
        String message,
        T data
) {
}
