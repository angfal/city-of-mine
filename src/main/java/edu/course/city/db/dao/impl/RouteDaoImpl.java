package edu.course.city.db.dao.impl;

import edu.course.city.db.dao.RouteDao;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.Route;
import edu.course.city.db.model.User;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("routeDao")
public class RouteDaoImpl implements RouteDao {

	@Autowired
	private SessionFactory sessionFactory;

    @Override
    public Route getById(long id) {
        return (Route) sessionFactory.getCurrentSession().get(Route.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Route> listOfRoots(User user) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Route where user = :user and group is null");
        query.setParameter("user", user);
        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Route> listByGroup(Group group) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Route where group = :group");
        query.setParameter("group", group);
        return query.list();
    }

    @Override
	public void save(Route route) {
		sessionFactory.getCurrentSession().saveOrUpdate(route);
	}

    @Override
    public void delete(Route route) {
        sessionFactory.getCurrentSession().delete(route);
    }
}
