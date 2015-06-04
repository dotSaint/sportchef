package ch.sportchef.server.user;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements ResultSetMapper<User> {

    @Override
    public User map(final int index, final ResultSet r, final StatementContext ctx) throws SQLException {
        return new User(
                r.getLong("userId"),
                r.getString("firstName"),
                r.getString("lastName"),
                r.getString("phone"),
                r.getString("email"),
                r.getString("password")
        );
    }
}
