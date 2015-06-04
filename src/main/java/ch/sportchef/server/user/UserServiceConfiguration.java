package ch.sportchef.server.user;

import ch.sportchef.server.user.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class UserServiceConfiguration {

    @Valid
    @NotNull
    @JsonProperty("referenceUser")
    private User referenceUser = null;

    public User getReferenceUser() {
        return referenceUser;
    }
}
