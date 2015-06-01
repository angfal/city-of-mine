package edu.course.city.db.dao.impl;

import edu.course.city.db.dao.FavoriteDao;
import edu.course.city.db.model.Favorite;
import edu.course.city.db.model.Place;
import edu.course.city.db.model.User;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("favoriteDao")
public class FavoriteDaoImpl implements FavoriteDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Favorite getFavorite(User user, Place place) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Favorite where user = :user and place = :place");
        query.setParameter("user", user);
        query.setParameter("place", place);
        return (Favorite) query.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Favorite> getFavorites(User user) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Favorite where user = :user");
        query.setParameter("user", user);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Favorite> getFavorites(Place place) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Favorite where place = :place");
        query.setParameter("place", place);
        return query.list();
    }

    @Override
    public void save(Favorite favorite) {
        sessionFactory.getCurrentSession().saveOrUpdate(favorite);
    }

    @Override
    public void delete(Favorite favorite) {
        sessionFactory.getCurrentSession().delete(favorite);
    }
}
