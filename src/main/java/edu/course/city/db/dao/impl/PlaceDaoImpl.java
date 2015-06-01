package edu.course.city.db.dao.impl;

import edu.course.city.db.dao.PlaceDao;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.Place;
import edu.course.city.db.model.User;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("placeDao")
public class PlaceDaoImpl implements PlaceDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Place getById(long id) {
        return (Place) sessionFactory.getCurrentSession().get(Place.class, id);
    }

    @Override
    public Place getGlobalCopy(Place place) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where source = :place");
        query.setParameter("place", place);
        return (Place) query.uniqueResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfLocalRoots(User user) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where user = :user and group is null and globalCopy = false");
        query.setParameter("user", user);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfGlobalRoots() {
        return sessionFactory.getCurrentSession().createQuery("from Place where group is null and globalCopy = true").list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfSystemGlobalRoots() {
        return sessionFactory.getCurrentSession().createQuery("from Place where group is null and source is null and globalCopy = true").list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfUserGlobalRoots() {
        return sessionFactory.getCurrentSession().createQuery("from Place where group is null and source is not null and globalCopy = true").list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfUserGlobalRoots(User user) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where user = :user and group is null and source is not null and globalCopy = true");
        query.setParameter("user", user);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listByGroup(Group group) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where group = :group");
        query.setParameter("group", group);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfUsersByGroup(Group group) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where group = :group and (source is not null or globalCopy = false)");
        query.setParameter("group", group);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfLocalByGroup(User user, Group group) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where user = :user and group = :group and globalCopy = false");
        query.setParameter("user", user);
        query.setParameter("group", group);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfGlobalByGroup(Group group) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where group = :group and globalCopy = true");
        query.setParameter("group", group);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfSystemGlobalByGroup(Group group) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where group = :group and source is null and globalCopy = true");
        query.setParameter("group", group);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfUsersGlobalByGroup(Group group) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where group = :group and source is not null and globalCopy = true");
        query.setParameter("group", group);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> listOfUserGlobalByGroup(User user, Group group) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Place where user = :user and group = :group and source is not null and globalCopy = true");
        query.setParameter("user", user);
        query.setParameter("group", group);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Place> lostOfGlobalAccess() {
        return sessionFactory.getCurrentSession().createQuery("from Place where globalAccess = true and globalCopy = false").list();
    }

    @Override
    public void save(Place place) {
        sessionFactory.getCurrentSession().saveOrUpdate(place);
    }

    @Override
    public void delete(Place place) {
        sessionFactory.getCurrentSession().delete(place);
    }

    @Override
    public void evict(Place place) {
        sessionFactory.getCurrentSession().evict(place);
    }
}
