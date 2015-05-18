package ch.sportchef.server.representations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class User {

    private final long userId;

    @NotBlank
    @Length(min=1, max=100)
    private final String firstName;

    @NotBlank
    @Length(min=1, max=100)
    private final String lastName;

    @NotBlank
    @Length(min=1, max=30)
    private final String phone;

    @NotBlank
    @Email
    @Length(min=1, max=100)
    private final String email;

    @Nullable
    @Length(min=10, max=255)
    @JsonIgnore // passwords should never leak to JSON output
    private final String password;

    private User() {
        // Jackson deserialization
        this(0, null, null, null, null, null);
    }

    public User(final long userId, @Nonnull final String firstName, @Nonnull final String lastName,
                @Nonnull final String phone, @Nonnull final String email, @Nonnull final String password) {
        super();
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, "password");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        final ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(this);
        reflectionToStringBuilder.setExcludeFieldNames(new String[] {"password"});
        return reflectionToStringBuilder.toString();
    }

    public long getUserId() {
        return userId;
    }

    public @Nullable
    String getFirstName() {
        return firstName;
    }

    public @Nullable String getLastName() {
        return lastName;
    }

    public @Nullable String getPhone() {
        return phone;
    }

    public @Nullable String getEmail() {
        return email;
    }

    public @Nullable String getPassword() {
        return password;
    }
}
