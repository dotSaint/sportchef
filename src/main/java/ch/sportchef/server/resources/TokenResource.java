package ch.sportchef.server.resources;

import ch.sportchef.server.representations.Login;
import ch.sportchef.server.representations.User;
import ch.sportchef.server.services.ServiceRegistry;
import ch.sportchef.server.services.TokenService;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;

import javax.management.ServiceNotFoundException;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("token")
public class TokenResource {

    private TokenService tokenService;

    public TokenResource() throws ServiceNotFoundException {
        super();
        this.tokenService = ServiceRegistry.getService(TokenService.class);
    }

    @POST
    @Path("generate")
    public Response generate(final Login login) throws AuthenticationException {
        final Map<String, String> token = tokenService.generateToken(login);
        return Response.status(Response.Status.CREATED).entity(token).build();
    }

    @GET
    @Path("/check")
    public Response user(@Auth @Valid final User user) {
        return Response.ok().build();
    }
}
