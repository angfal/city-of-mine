package edu.course.city.db.dao;

import edu.course.city.db.model.Place;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.User;

import java.util.List;

public interface PlaceDao {

    Place getById(long id);

    Place getGlobalCopy(Place place);

    List<Place> listOfLocalRoots(User user);

    List<Place> listOfGlobalRoots();

    List<Place> listOfSystemGlobalRoots();

    List<Place> listOfUserGlobalRoots();

    List<Place> listOfUserGlobalRoots(User user);

    List<Place> listByGroup(Group group);

    List<Place> listOfUsersByGroup(Group group);

    List<Place> listOfLocalByGroup(User user, Group group);

    List<Place> listOfGlobalByGroup(Group group);

    List<Place> listOfSystemGlobalByGroup(Group group);

    List<Place> listOfUsersGlobalByGroup(Group group);

    List<Place> listOfUserGlobalByGroup(User user, Group group);

    List<Place> lostOfGlobalAccess();

    void save(Place place);

    void delete(Place place);

    void evict(Place place);
}
