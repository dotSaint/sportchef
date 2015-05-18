package ch.sportchef.server.representations;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.annotation.Nonnull;

public class Login {

    private final long userId;

    @NotEmpty
    @Length(min=10, max=255)
    private final String password;

    private Login() {
        // Jackson deserialization
        this(0, null);
    }

    public Login(final long userId, @Nonnull final String password) {
        super();
        this.userId = userId;
        this.password = password;
    }

    public long getUserId() {
        return userId;
    }

    public @Nonnull String getPassword() {
        return password;
    }
}
