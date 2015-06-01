package edu.course.city.db.dao;

import edu.course.city.db.model.User;

public interface UserDao {

    User getUserByLogin(String login);

    User getUserByEmail(String email);

    User getSocialUser(String providerId, String validatedId);

    void save(User user);
}
