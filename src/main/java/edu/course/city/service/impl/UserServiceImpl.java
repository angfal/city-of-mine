package edu.course.city.service.impl;

import edu.course.city.db.dao.UserDao;
import edu.course.city.db.model.User;
import edu.course.city.db.model.UserRole;
import edu.course.city.service.UserService;
import org.brickred.socialauth.Profile;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("userService")
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    private User initializeUser(User user) {
        if (user != null) {
            Hibernate.initialize(user.getAuthorities());
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userDao.getUserByLogin(name);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User : %s not found", name));
        }
        return initializeUser(user);
    }

    @Override
    public boolean isLoginExists(String login) {
        return userDao.getUserByLogin(login) != null;
    }

    @Override
    public boolean isEmailExists(String email) {
        return userDao.getUserByEmail(email) != null;
    }

    @Override
    public User getUser(String login) {
        return userDao.getUserByLogin(login);
    }

    @Override
    public User getUser(String login, String password) {
        User user = userDao.getUserByLogin(login);
        if (user != null && user.getPassword().equals(password)) {
            return initializeUser(user);
        } else {
            return null;
        }
    }

    @Override
    public User getSocialUser(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("The profile is required");
        }
        return initializeUser(userDao.getSocialUser(profile.getProviderId(), profile.getValidatedId()));
    }

    private String getLogin(Profile profile) {
        if (!StringUtils.isEmpty(profile.getFullName())) {
            return profile.getFullName();
        }
        if (!StringUtils.isEmpty(profile.getFirstName()) || !StringUtils.isEmpty(profile.getLastName())) {
            return (profile.getFirstName() + " " + profile.getLastName()).trim();
        }
        if (!StringUtils.isEmpty(profile.getEmail())) {
            return profile.getEmail();
        }
        return profile.getValidatedId();
    }

    @Override
    @Transactional(readOnly = false)
    public User createSocialUser(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("The profile is required");
        }
        User user = new User();
        user.setLogin(getLogin(profile));
        user.setProviderId(profile.getProviderId());
        user.setValidatedId(profile.getValidatedId());
        user.getRoles().add(UserRole.ROLE_NATURAL_PERSON);
        saveUser(user);
        return user;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveUser(User user) {
        userDao.save(user);
    }
}
