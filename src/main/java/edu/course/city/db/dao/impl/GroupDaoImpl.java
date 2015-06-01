package edu.course.city.db.dao.impl;

import edu.course.city.db.dao.GroupDao;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.GroupType;
import edu.course.city.db.model.User;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("groupDao")
public class GroupDaoImpl implements GroupDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Group getById(Long id) {
        return (Group) sessionFactory.getCurrentSession().get(Group.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Group> listOfLocalRoots(User user, GroupType type) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Group where user = :user and type = :type and parent is null and globalCopy = false ");
        query.setParameter("user", user);
        query.setParameter("type", type);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Group> listOfSystemGlobalRoots(GroupType type) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Group where type = :type and parent is null and globalCopy = true");
        query.setParameter("type", type);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Group> listByParent(Group parent) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Group where parent = :parent");
        query.setParameter("parent", parent);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Group> listOfLocalByParent(Group parent) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Group where parent = :parent and globalCopy = false");
        query.setParameter("parent", parent);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Group> listOfSystemGlobalByParent(Group parent) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Group where parent = :parent and globalCopy = true");
        query.setParameter("parent", parent);
        return query.list();
    }

    @Override
    public void save(Group group) {
        sessionFactory.getCurrentSession().saveOrUpdate(group);
    }

    @Override
    public void delete(Group group) {
        sessionFactory.getCurrentSession().delete(group);
    }
}
