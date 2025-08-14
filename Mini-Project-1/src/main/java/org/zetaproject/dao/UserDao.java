package org.zetaproject.dao;

import org.zetaproject.model.entites.User;

public interface UserDao {
    User getUserById(int id) throws Exception;
    User getUserByUsername(String username) throws Exception;
    void addUser(User user) throws Exception;
}
