package dev.parkcommerce.support;

import dev.parkcommerce.core.Failure;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingRequestHeaderException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class ApiErrors {
    private static final Logger LOG=LoggerFactory.getLogger(ApiErrors.class);
    @ExceptionHandler(Failure.class) public ProblemDetail business(Failure error) {
        int status=switch(error.code()){
            case INVALID_INPUT -> 400;case NOT_FOUND -> 404;case FORBIDDEN -> 403;
            case CONFLICT,SOLD_OUT -> 409;case EXPIRED -> 410;case DEPENDENCY_UNAVAILABLE -> 503;
        };
        var problem=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(status),error.getMessage());
        problem.setTitle(error.code().name());problem.setProperty("code",error.code().name());return problem;
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class,MissingRequestHeaderException.class})
    public ProblemDetail invalid(Exception error, HttpServletRequest request){
        // Jackson wraps failures from validated pricing records. Preserve their
        // safe domain messages so an employee can repair an unpublished rule.
        if ("/api/admin/price-books/preview".equals(request.getRequestURI())) {
            for (Throwable cause=error.getCause(); cause!=null; cause=cause.getCause())
                if (cause instanceof Failure failure && failure.code()==Failure.Code.INVALID_INPUT)
                    return business(failure);
            return business(Failure.invalid("Provide a complete price-book candidate and party with valid dates and whole-number ticket counts"));
        }
        return business(Failure.invalid("Malformed or incomplete request"));
    }
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class) public ProblemDetail denied(Exception error){return business(new Failure(Failure.Code.FORBIDDEN,"Access denied"));}
    @ExceptionHandler(Exception.class) public ProblemDetail unexpected(Exception error){
        // Do not echo payloads, credentials, SQL, or stack traces to the caller.
        LOG.error("Unhandled request failure: {}",error.getClass().getSimpleName());
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,"Request could not be completed");
    }
}
