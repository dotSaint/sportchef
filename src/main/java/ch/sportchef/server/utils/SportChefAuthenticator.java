package ch.sportchef.server.utils;

import ch.sportchef.server.representations.User;
import ch.sportchef.server.services.ServiceRegistry;
import ch.sportchef.server.services.UserService;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenValidator;
import com.github.toastshaman.dropwizard.auth.jwt.exceptions.TokenExpiredException;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import javax.management.ServiceNotFoundException;

public class SportChefAuthenticator implements Authenticator<JsonWebToken, User> {

    private final UserService userService;
    private final JsonWebTokenValidator jsonWebTokenValidator;
    private byte[] tokenSecret;

    public SportChefAuthenticator(final JsonWebTokenValidator jsonWebTokenValidator,
                                  final byte[] tokenSecret) throws ServiceNotFoundException {
        this.tokenSecret = tokenSecret;
        this.jsonWebTokenValidator = jsonWebTokenValidator;
        this.userService = ServiceRegistry.getService(UserService.class);
    }

    @Override
    public Optional<User> authenticate(final JsonWebToken token) throws AuthenticationException {
        validate(token);
        final long userId = Long.parseLong(token.claim().getParameter("userId").toString());
        final User user = userService.readUserById(userId);
        return Optional.fromNullable(user);
    }

    private void validate(final JsonWebToken token) throws AuthenticationException {
        try {
            jsonWebTokenValidator.validate(token);
        } catch (final TokenExpiredException e) {
            throw new AuthenticationException("Token has expired!", e);
        }
    }
}