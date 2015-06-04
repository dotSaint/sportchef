package ch.sportchef.server.user;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(UserMapper.class)
public interface UserDAO {

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO user (userId, firstName, lastName, phone, email, password) VALUES (NULL, :firstName, :lastName, :phone, :email, :password)")
    long create(@BindBean User user);

    @SqlQuery("SELECT * FROM user ORDER BY userId")
    List<User> readAllUsers();

    @SqlQuery("SELECT * FROM user WHERE userId = :userId")
    User readById(@Bind("userId") final long userId);

    @SqlUpdate("UPDATE user SET firstName = :firstName, lastName = :lastName, phone = :phone, email = :email WHERE userId = :userId")
    void update(@BindBean User user);

    @SqlUpdate("DELETE FROM user WHERE userId = :userId")
    void delete(@BindBean User user);
}
