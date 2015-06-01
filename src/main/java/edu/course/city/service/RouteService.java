package edu.course.city.service;

import edu.course.city.db.model.Group;
import edu.course.city.db.model.Route;
import edu.course.city.db.model.User;

import java.util.List;

public interface RouteService {

    Route getRouteById(long id);

    List<Route> getRootRoutes(User user);

    List<Route> getRoutesByGroup(Group group);

    void saveRoute(Route route);

    void deleteRoute(Route route);
}
