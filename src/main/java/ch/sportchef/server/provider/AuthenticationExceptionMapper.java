package ch.sportchef.server.provider;

import io.dropwizard.auth.AuthenticationException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationExceptionMapper implements ExceptionMapper<AuthenticationException> {

    @Override
    public Response toResponse(final AuthenticationException e) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(e.getLocalizedMessage()).build();
    }
}