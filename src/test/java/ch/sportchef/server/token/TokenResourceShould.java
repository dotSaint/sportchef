package ch.sportchef.server.token;

import ch.sportchef.server.SportChefApp;
import ch.sportchef.server.SportChefConfiguration;
import ch.sportchef.server.user.User;
import ch.sportchef.server.user.UserService;
import ch.sportchef.server.utils.ServiceRegistry;
import ch.sportchef.server.user.UserGenerator;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import io.dropwizard.testing.junit.DropwizardAppRule;
import liquibase.exception.LiquibaseException;
import org.joda.time.DateTime;
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
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenResourceShould {

    @ClassRule
    public static final DropwizardAppRule<SportChefConfiguration> RULE = new DropwizardAppRule<>(SportChefApp.class, "config-test.yaml");

    public static final String SIGNED_TOKEN_PREFIX = "bearer ";

    @BeforeClass
    public static void setup() throws SQLException, LiquibaseException, ServiceNotFoundException, UnsupportedEncodingException {
        final UserService userService = ServiceRegistry.getService(UserService.class);

        final User johnDoe = userService.storeUser(UserGenerator.getJohnDoe(0L));
        validLogin = new Login(johnDoe.getUserId(), johnDoe.getPassword());

        final User janeDoe = UserGenerator.getJaneDoe(2L);
        invalidLogin = new Login(janeDoe.getUserId(), janeDoe.getPassword());

        tokenSecret = RULE.getConfiguration().getTokenSecret();
        final HmacSHA512Signer signer = new HmacSHA512Signer(tokenSecret);

        validToken = SIGNED_TOKEN_PREFIX + signer.sign(JsonWebToken.builder()
                .header(JsonWebTokenHeader.HS512())
                .claim(JsonWebTokenClaim.builder()
                        .param("userId", 1L)
                        .issuedAt(DateTime.now().minusMinutes(1))
                        .expiration(DateTime.now().plusDays(1)).build())
                .build());

        expiredToken = SIGNED_TOKEN_PREFIX + signer.sign(JsonWebToken.builder()
                .header(JsonWebTokenHeader.HS512())
                .claim(JsonWebTokenClaim.builder()
                        .param("userId", 1L)
                        .issuedAt(DateTime.now().minusDays(8))
                        .expiration(DateTime.now().minusDays(1)).build())
                .build());
    }

    private static Login validLogin;
    private static Login invalidLogin;

    private static byte[] tokenSecret;

    private static String validToken;
    private static String expiredToken;

    @Test
    public void generateNewToken() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/token/generate", RULE.getLocalPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(validLogin));

        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

        final Map<String, String> tokenMap = response.readEntity(new GenericType<Map<String, String>>() {
        });

        assertThat(tokenMap).isNotNull();
        assertThat(tokenMap.size()).isEqualTo(1);
        assertThat(tokenMap.containsKey("token")).isTrue();
        assertThat(tokenMap.get("token")).isNotEmpty();
    }

    @Test
    public void rejectGenerateNewTokenForInvalidUser() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/token/generate", RULE.getLocalPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.json(invalidLogin));

        assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    public void acceptValidToken() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/token/check", RULE.getLocalPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", validToken)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
    }

    @Test
    public void rejectExpiredToken() {
        final WebTarget target = ClientBuilder.newClient().target(
                String.format("http://localhost:%d/api/token/check", RULE.getLocalPort()));

        final Response response = target
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization", expiredToken)
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertThat(response.getStatus()).isEqualTo(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}