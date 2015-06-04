package ch.sportchef.server.user;

import ch.sportchef.server.utils.ServiceRegistry;
import com.codahale.metrics.annotation.Timed;

import javax.management.ServiceNotFoundException;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Path("users")
public class UserResource {

    private final UserService userService;

    public UserResource() throws ServiceNotFoundException {
        this.userService = ServiceRegistry.getService(UserService.class);
    }

    @GET
    @Timed
    public Response readAllUsers() {
        final List<User> users = userService.readAllUsers();
        return Response.ok(new GenericEntity<List<User>>(users) {}).build();
    }

    @GET
    @Path("{userId}")
    @Timed
    public Response readUser(@PathParam("userId") final long userId) {
        final User user = userService.readUserById(userId);
        return Response.ok(user).build();
    }

    @POST
    @Timed
    public Response createUser(@Valid User user) throws URISyntaxException {
        final User newUser = userService.storeUser(user);
        return Response.created(new URI(String.valueOf(newUser.getUserId()))).build();
    }

    @PUT
    @Path("{userId}")
    @Timed
    public Response updateUser(@PathParam("userId") final long userId, @Valid User user) throws URISyntaxException {
        if (userId != user.getUserId()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        final User updateUser = userService.storeUser(user);
        return Response.ok(new URI(String.valueOf(updateUser.getUserId()))).build();
    }
}
