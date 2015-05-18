package ch.sportchef.server.resources;

import ch.sportchef.server.App;
import ch.sportchef.server.configuration.SportChefConfiguration;
import ch.sportchef.server.representations.User;
import ch.sportchef.server.services.ServiceRegistry;
import ch.sportchef.server.services.UserService;
import ch.sportchef.server.utils.UserGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.testing.junit.DropwizardAppRule;
import liquibase.exception.LiquibaseException;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.management.ServiceNotFoundException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UserResourceShould {

    @ClassRule
    public static final DropwizardAppRule<SportChefConfiguration> RULE = new DropwizardAppRule<>(App.class, "config-test.yaml");

    @BeforeClass
    public static void setup() throws SQLException, LiquibaseException, ServiceNotFoundException {
        final UserService userService = ServiceRegistry.getService(UserService.class);
        johnDoe = userService.storeUser(UserGenerator.getJohnDoe(0L));
        janeDoe = userService.storeUser(UserGenerator.getJaneDoe(0L));
        joyDoe = userService.storeUser(UserGenerator.getJoyDoe(0L));
    }

    private static User johnDoe = null;
    private static User janeDoe = null;
    private static User joyDoe = null;

    @Test
    public void returnAllUsers() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/users", RULE.getLocalPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        final List<User> users = response.readEntity(new GenericType<List<User>>() {});
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0)).isEqualTo(johnDoe);
        assertThat(users.get(1)).isEqualTo(janeDoe);
        assertThat(users.get(2)).isEqualTo(joyDoe);
    }

    @Test
    public void returnJohnDoe() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/users/1", RULE.getLocalPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        final User user = response.readEntity(User.class);
        assertThat(user).isEqualTo(johnDoe);
    }

    @Test
    public void returnJaneDoe() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/users/2", RULE.getLocalPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        final User user = response.readEntity(User.class);
        assertThat(user).isEqualTo(janeDoe);
    }

    @Test
    public void returnNotFound() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/users/0", RULE.getLocalPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void createTheNewUserJimDoe() throws JsonProcessingException {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/users", RULE.getLocalPort()));

        final User user = UserGenerator.getJimDoe(0L);

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(user));

        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

        final URI location = response.getLocation();
        assertThat(location).isNotNull();

        final String path = location.getPath();
        final long newId = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
        assertThat(newId).isGreaterThan(0);
    }

    @Test
    public void updateJoyDoe() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/users/%d", RULE.getLocalPort(), joyDoe.getUserId()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(joyDoe));

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void notUpdateJoyDoe() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/users/%d", RULE.getLocalPort(), janeDoe.getUserId()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(joyDoe));

        assertThat(response.getStatus()).isEqualTo(Response.Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void notCreateOnUpdateNonExistingUser() {
        final User jackDoe = UserGenerator.getJackDoe(42);

        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/users/%d", RULE.getLocalPort(), jackDoe.getUserId()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.json(jackDoe));

        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
    }
}
