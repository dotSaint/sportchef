package ch.sportchef.server.services;

import ch.sportchef.server.representations.Login;
import ch.sportchef.server.representations.User;
import ch.sportchef.server.utils.TokenGenerator;
import ch.sportchef.server.utils.UserGenerator;
import ch.sportchef.server.utils.SportChefAuthenticator;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenValidator;
import com.github.toastshaman.dropwizard.auth.jwt.exceptions.TokenExpiredException;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.management.ServiceNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServiceRegistry.class)
@PowerMockIgnore({"javax.crypto.*" })
public class TokenServiceShould {

    private final byte[] tokenSecret =
            UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);

    private JsonWebToken expiredToken;
    private JsonWebToken validToken;

    @Before
    public void setUp() throws ServiceNotFoundException {
        final User tmpUser = UserGenerator.getJohnDoe(1L);
        final User johnDoe = new User(tmpUser.getUserId(), tmpUser.getFirstName(), tmpUser.getLastName(),
                tmpUser.getPhone(), tmpUser.getEmail(), DigestUtils.sha512Hex(tmpUser.getPassword()));

        final UserService userService = mock(UserService.class);
        when(userService.readUserById(1L)).thenReturn(johnDoe);
        mockStatic(ServiceRegistry.class);
        when(ServiceRegistry.getService(eq(UserService.class))).thenReturn(userService);

        expiredToken = TokenGenerator.getExpiredToken(johnDoe);
        validToken = TokenGenerator.getValidToken(johnDoe);
    }

    @Test
    public void generateNewToken() throws ServiceNotFoundException, AuthenticationException {
        final User user = UserGenerator.getJohnDoe(1L);
        final Login login = new Login(user.getUserId(), user.getPassword());
        final JsonWebTokenValidator jsonWebTokenValidator = mock(JsonWebTokenValidator.class);
        final TokenService tokenService = new TokenService(tokenSecret);
        final Map<String, String> tokenMap = tokenService.generateToken(login);

        assertThat(tokenMap).isNotNull();
        assertThat(tokenMap.size()).isEqualTo(1);
        assertThat(tokenMap.containsKey("token")).isTrue();
        assertThat(tokenMap.get("token")).isNotEmpty();
    }

    @Test(expected = AuthenticationException.class)
    public void rejectGenerateNewTokenForInvalidUser() throws ServiceNotFoundException, AuthenticationException {
        final User user = UserGenerator.getJohnDoe(2L);
        final Login login = new Login(user.getUserId(), user.getPassword());
        final JsonWebTokenValidator jsonWebTokenValidator = mock(JsonWebTokenValidator.class);
        final TokenService tokenService = new TokenService(tokenSecret);
        final Map<String, String> tokenMap = tokenService.generateToken(login);
    }

    @Test(expected = AuthenticationException.class)
    public void rejectGenerateNewTokenForUserWithoutPassword() throws ServiceNotFoundException, AuthenticationException {
        final User user = UserGenerator.getJaneDoe(1L);
        final Login login = new Login(user.getUserId(), user.getPassword());
        final JsonWebTokenValidator jsonWebTokenValidator = mock(JsonWebTokenValidator.class);
        final TokenService tokenService = new TokenService(tokenSecret);
        final Map<String, String> tokenMap = tokenService.generateToken(login);
    }

    @Test(expected = AuthenticationException.class)
    public void rejectGenerateNewTokenForLoginWithoutPassword() throws ServiceNotFoundException, AuthenticationException {
        final Login login = new Login(1L, null);
        final JsonWebTokenValidator jsonWebTokenValidator = mock(JsonWebTokenValidator.class);
        final TokenService tokenService = new TokenService(tokenSecret);
        final Map<String, String> tokenMap = tokenService.generateToken(login);
    }

    @Test
    public void authenticateSuccessful() throws ServiceNotFoundException, AuthenticationException {
        final User user = UserGenerator.getJohnDoe(1L);
        final JsonWebTokenValidator expiryValidator = new ExpiryValidator();
        final SportChefAuthenticator authenticator = new SportChefAuthenticator(expiryValidator);
        final Optional<User> optUser = authenticator.authenticate(validToken);

        assertThat(optUser).isNotNull();
        assertThat(optUser.isPresent()).isTrue();
        assertThat(optUser.get()).isEqualTo(user);
    }

    @Test(expected = TokenExpiredException.class)
    public void rejectExpiredToken() throws ServiceNotFoundException, AuthenticationException {
        final JsonWebTokenValidator expiryValidator = new ExpiryValidator();
        final SportChefAuthenticator authenticator = new SportChefAuthenticator(expiryValidator);
        final Optional<User> optUser = authenticator.authenticate(expiredToken);
    }
}
