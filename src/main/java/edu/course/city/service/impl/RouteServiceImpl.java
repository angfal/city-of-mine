package edu.course.city.service.impl;

import edu.course.city.db.dao.RouteDao;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.Route;
import edu.course.city.db.model.User;
import edu.course.city.service.RouteService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("routeService")
@Transactional(readOnly = true)
public class RouteServiceImpl implements RouteService {

    @Autowired
    private RouteDao routeDao;

    private Route initializeRoute(Route route) {
        if (route != null) {
            Hibernate.initialize(route.getMainCoordinates());
            Hibernate.initialize(route.getRouteCoordinates());
        }
        return route;
    }

    private List<Route> initializeRoutes(List<Route> routes) {
        for (Route route : routes) {
            initializeRoute(route);
        }
        return routes;
    }

    @Override
    public Route getRouteById(long id) {
        return initializeRoute(routeDao.getById(id));
    }

    @Override
    public List<Route> getRootRoutes(User user) {
        return initializeRoutes(routeDao.listOfRoots(user));
    }

    @Override
    public List<Route> getRoutesByGroup(Group group) {
        return initializeRoutes(routeDao.listByGroup(group));
    }

    @Override
    @Transactional(readOnly = false)
    public void saveRoute(Route route) {
        routeDao.save(route);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteRoute(Route route) {
        routeDao.delete(route);
    }
}
