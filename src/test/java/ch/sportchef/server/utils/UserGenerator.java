package ch.sportchef.server.utils;

import ch.sportchef.server.representations.User;

public class UserGenerator {

    public static User getJohnDoe(final long id) {
        return new User(id, "John", "Doe", "+41 79 555 00 01", "john.doe@sportchef.ch", "secret01");
    }

    public static User getJaneDoe(final long id) {
        return new User(id, "Jane", "Doe", "+41 79 555 00 02", "jane.doe@sportchef.ch", "secret02");
    }

    public static User getJimDoe(final long id) {
        return new User(id, "Jim", "Doe", "+41 79 555 00 03", "jim.doe@sportchef.ch", "secret03");
    }

    public static User getJoyDoe(final long id) {
        return new User(id, "Joy", "Doe", "+41 79 555 00 04", "joy.doe@sportchef.ch", "secret04");
    }

    public static User getJackDoe(final long id) {
        return new User(id, "Jack", "Doe", "+41 79 555 00 05", "jack.doe@sportchef.ch", "secret05");
    }
}
