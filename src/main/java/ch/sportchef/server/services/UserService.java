package ch.sportchef.server.services;

import ch.sportchef.server.dao.UserDAO;
import ch.sportchef.server.representations.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.ws.rs.NotFoundException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class UserService implements Service {

    private final UserDAO userDAO;

    public UserService(final UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> readAllUsers() {
        return userDAO.readAllUsers();
    }

    public User readUserById(final long userId) {
        final User user = userDAO.readById(userId);
        if (user == null) {
            throw new NotFoundException(
                    String.format("user with id '%d'", userId));
        }
        return user;
    }

    public User storeUser(final User user) {
        if (user.getUserId() == 0) {
            final String password = isNotEmpty(user.getPassword()) ?
                    user.getPassword() : RandomStringUtils.random(10, true, true);
            final String sha512Password = DigestUtils.sha512Hex(password);
            // TODO #85 Send the new password to the new user
            final User newUser = new User(user.getUserId(), user.getFirstName(), user.getLastName(),
                    user.getPhone(), user.getEmail(), sha512Password);
            final long newUserId = userDAO.create(newUser);
            return new User(newUserId, user.getFirstName(), user.getLastName(),
                    user.getPhone(), user.getEmail(), password);
        }

        // check to see if the user exists, throws a NotFoundException
        readUserById(user.getUserId());

        userDAO.update(user);
        return user;
    }

    public void removeUser(final User user) {
        userDAO.delete(user);
    }
}
