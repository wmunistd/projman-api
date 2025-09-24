package dev.wagnermoreira.projman.dao;

import dev.wagnermoreira.projman.domain.User;
import java.util.List;

public interface UserDao {
    long insert(User user);
    List<User> listAll();
}


