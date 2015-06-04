package ch.sportchef.server.token;

import ch.sportchef.server.user.User;
import ch.sportchef.server.utils.Service;
import ch.sportchef.server.utils.ServiceRegistry;
import ch.sportchef.server.user.UserService;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import io.dropwizard.auth.AuthenticationException;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;

import javax.management.ServiceNotFoundException;
import javax.ws.rs.NotFoundException;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class TokenService implements Service {

    private final byte[] tokenSecret;
    private final UserService userService;

    public TokenService(final byte[] tokenSecret) throws ServiceNotFoundException {
        super();
        this.tokenSecret = tokenSecret;
        this.userService = ServiceRegistry.getService(UserService.class);
    }

    private User getUser(final Login login) {
        try {
            return userService.readUserById(login.getUserId());
        } catch (final NotFoundException e) {
            return null;
        }
    }

    private void throwAuthenticationException() throws AuthenticationException {
        throw new AuthenticationException("Invalid username and/or password!");
    }

    private void checkUserIsNotNull(final Login login, final User user) throws AuthenticationException {
        if (login == null || user == null) {
            throwAuthenticationException();
        }
    }

    private void checkUserPasswordIsNotNull(final Login login, final User user) throws AuthenticationException {
        if (isEmpty(login.getPassword()) || isEmpty(user.getPassword())) {
            throwAuthenticationException();
        }
    }

    private void checkUserPasswordMatches(final Login login, final User user) throws AuthenticationException {
        final String sha512Password = DigestUtils.sha512Hex(login.getPassword());
        if (!sha512Password.equals(user.getPassword())) {
            throwAuthenticationException();
        }
    }

    public Map<String, String> generateToken(final Login login) throws AuthenticationException {
        final User user = getUser(login);
        checkUserIsNotNull(login, user);
        checkUserPasswordIsNotNull(login, user);
        checkUserPasswordMatches(login, user);

        final DateTime now = DateTime.now();
        final HmacSHA512Signer signer = new HmacSHA512Signer(tokenSecret);
        final JsonWebToken token = JsonWebToken.builder()
                .header(JsonWebTokenHeader.HS512())
                .claim(JsonWebTokenClaim.builder()
                        .param("userId", user.getUserId())
                        .issuedAt(now)
                        .expiration(now.plusHours(1))
                        .build())
                .build();
        final String signedToken = signer.sign(token);

        return singletonMap("token", signedToken);
    }
}
