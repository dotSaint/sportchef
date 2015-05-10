package ch.sportchef.server.services;

import ch.sportchef.server.dao.UserDAO;
import ch.sportchef.server.representations.User;
import ch.sportchef.server.utils.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceShould {

    private UserDAO userDAO;
    private UserService userService;

    @Before
    public void setUp() {
        // mock DAO
        userDAO = mock(UserDAO.class);
        when(userDAO.readById(1L)).thenReturn(UserGenerator.getJohnDoe(1L));
        when(userDAO.readById(2L)).thenReturn(UserGenerator.getJaneDoe(2L));
        when(userDAO.readById(3L)).thenReturn(null);

        // initialise service
        userService = new UserService(userDAO);
    }

    @After
    public void tearDown() {
        userService = null;
        userDAO = null;
    }

    @Test
    public void returnJohnDoe() {
        final User user = userService.readUserById(1L);
        assertThat(user).isEqualTo(UserGenerator.getJohnDoe(1L));
    }

    @Test
    public void returnJaneDoe() {
        final User user = userService.readUserById(2L);
        assertThat(user).isEqualTo(UserGenerator.getJaneDoe(2L));
    }

    @Test(expected = NotFoundException.class)
    public void returnNoUser() {
        userService.readUserById(3L);
    }

    @Test
    public void removeJohnDoe() {
        final User johnDoe = UserGenerator.getJohnDoe(1L);
        userService.removeUser(johnDoe);
        verify(userDAO, times(1)).delete(johnDoe);
    }

    @Test
    public void removeJaneDoe() {
        final User janeDoe = UserGenerator.getJaneDoe(2L);
        userService.removeUser(janeDoe);
        verify(userDAO, times(1)).delete(janeDoe);
    }

    @Test
    public void removeNonExistingUser() {
        final User nonExistingUser = UserGenerator.getJimDoe(3L);
        userService.removeUser(nonExistingUser);
    }
}
