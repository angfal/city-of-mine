package edu.course.city.db.dao;

import edu.course.city.db.model.Group;
import edu.course.city.db.model.Route;
import edu.course.city.db.model.User;

import java.util.List;

public interface RouteDao {

    Route getById(long id);

    List<Route> listOfRoots(User user);

    List<Route> listByGroup(Group group);

    void save(Route route);

    void delete(Route route);
}
