package org.example.ecommerceapi.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public abstract class ApiException extends ErrorResponseException {


    public ApiException(HttpStatusCode status, String detail, @Nullable Throwable cause) {
        super(status, ProblemDetail.forStatusAndDetail(status, detail), cause);
    }
}
