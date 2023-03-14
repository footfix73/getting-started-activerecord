package org.acme.exceptions;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.CompositeException;

public class ErrorMapper implements ExceptionMapper<Exception> {
    @Inject
    ObjectMapper objectMapper;

    @Override
    public Response toResponse(Exception exception) {
        Log.error("Failed to handle request", exception);

        Throwable throwable = exception;

        int code = 500;
        if (throwable instanceof WebApplicationException) {
            code = ((WebApplicationException) exception).getResponse().getStatus();
        }

        // This is a Mutiny exception and it happens, for example, when we try to insert a new
        // fruit but the name is already in the database
        if (throwable instanceof CompositeException) {
            throwable = ((CompositeException) throwable).getCause();
        }

        ObjectNode exceptionJson = objectMapper.createObjectNode();
        exceptionJson.put("exceptionType", throwable.getClass().getName());
        exceptionJson.put("code", code);

        if (exception.getMessage() != null) {
            exceptionJson.put("error", throwable.getMessage());
        }

        return Response.status(code)
                .entity(exceptionJson)
                .build();
    }
}
