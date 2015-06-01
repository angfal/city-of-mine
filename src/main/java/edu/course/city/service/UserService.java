package edu.course.city.service;

import edu.course.city.db.model.User;
import org.brickred.socialauth.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    boolean isLoginExists(String login);

    boolean isEmailExists(String email);

    User getUser(String login);

    User getUser(String login, String password);

    User getSocialUser(Profile profile);

    User createSocialUser(Profile profile);

    void saveUser(User user);
}
