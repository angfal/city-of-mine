package edu.course.city.db.dao.impl;

import edu.course.city.db.dao.UserDao;
import edu.course.city.db.model.User;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userDao")
public class UserDaoImpl implements UserDao {

	@Autowired
	private SessionFactory sessionFactory;

    @Override
    public User getUserByEmail(String email) {
        Query query = sessionFactory.getCurrentSession().createQuery("from User where email = :email");
        query.setParameter("email", email);
        return (User) query.uniqueResult();
    }

	@Override
    public User getUserByLogin(String login) {
		Query query = sessionFactory.getCurrentSession().createQuery("from User where login = :login");
		query.setParameter("login", login);
		return (User) query.uniqueResult();
    }

    @Override
    public User getSocialUser(String providerId, String validatedId) {
        Query query = sessionFactory.getCurrentSession().createQuery("from User where providerId = :providerId and validatedId = :validatedId");
        query.setParameter("providerId", providerId);
        query.setParameter("validatedId", validatedId);
        return (User) query.uniqueResult();
    }

    @Override
	public void save(User user) {
		sessionFactory.getCurrentSession().saveOrUpdate(user);
	}
}
