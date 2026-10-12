package dev.parkcommerce.support;
import dev.parkcommerce.core.Failure;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import java.util.Map;
import org.springframework.graphql.execution.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
@Component
public class GraphErrors extends DataFetcherExceptionResolverAdapter {
    @Override protected GraphQLError resolveToSingleError(Throwable error,DataFetchingEnvironment env){
        if(error instanceof Failure f) return GraphqlErrorBuilder.newError(env).message(f.getMessage())
            .errorType(switch(f.code()) {
                case NOT_FOUND -> ErrorType.NOT_FOUND;
                case FORBIDDEN -> ErrorType.FORBIDDEN;
                case DEPENDENCY_UNAVAILABLE -> ErrorType.INTERNAL_ERROR;
                default -> ErrorType.BAD_REQUEST;
            })
            .extensions(Map.of("code",f.code().name())).build();
        if(error instanceof BindException) return GraphqlErrorBuilder.newError(env).message("Malformed or incomplete request")
            .errorType(ErrorType.BAD_REQUEST).extensions(Map.of("code","INVALID_INPUT")).build();
        if(error instanceof AccessDeniedException) return GraphqlErrorBuilder.newError(env).message("Access denied")
            .errorType(ErrorType.FORBIDDEN).extensions(Map.of("code","FORBIDDEN")).build();
        return null; // Spring sanitizes unresolved errors.
    }
}
