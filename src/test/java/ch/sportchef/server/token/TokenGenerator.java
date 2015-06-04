package ch.sportchef.server.token;

import ch.sportchef.server.user.User;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import org.joda.time.DateTime;

import javax.annotation.Nonnull;

class TokenGenerator {

    public static JsonWebToken getValidToken(@Nonnull final User user) {
        return JsonWebToken.builder()
                .header(JsonWebTokenHeader.HS512())
                .claim(JsonWebTokenClaim.builder()
                        .param("userId", user.getUserId())
                        .issuedAt(DateTime.now().minusMinutes(1))
                        .expiration(DateTime.now().plusDays(1)).build())
                .build();

    }

    public static JsonWebToken getExpiredToken(@Nonnull final User user) {
        return JsonWebToken.builder()
                .header(JsonWebTokenHeader.HS512())
                .claim(JsonWebTokenClaim.builder()
                        .param("userId", user.getUserId())
                        .issuedAt(DateTime.now().minusMinutes(8))
                        .expiration(DateTime.now().minusDays(1)).build())
                .build();

    }
}
