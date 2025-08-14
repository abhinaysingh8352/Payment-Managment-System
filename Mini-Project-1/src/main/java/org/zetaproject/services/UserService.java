package org.zetaproject.services;

import org.zetaproject.dao.UserDao;
import org.zetaproject.model.entites.User;

public class UserService {
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void addUser(User user) throws Exception {
        userDao.addUser(user);
    }

    public User getUserById(int id) throws Exception {
        return userDao.getUserById(id);
    }

    public User getUserByUsername(String username) throws Exception {
        return userDao.getUserByUsername(username);
    }
}