package edu.course.city.service;

import edu.course.city.db.model.Group;
import edu.course.city.db.model.Place;
import edu.course.city.db.model.User;

import java.util.List;

public interface PlaceService {

    Place getPlaceById(long id);

    List<Place> getLocalRootPlaces(User user);

    List<Place> getGlobalRootPlaces();

    List<Place> getSystemGlobalRootPlaces();

    List<Place> getUserGlobalRootPlaces();

    List<Place> getUserGlobalRootPlaces(User user);

    List<Place> getPlacesByGroup(Group group);

    List<Place> getUsersPlacesByGroup(Group group);

    List<Place> getLocalPlacesByGroup(User user, Group group);

    List<Place> getGlobalPlacesByGroup(Group group);

    List<Place> getSystemGlobalPlacesByGroup(Group group);

    List<Place> getUsersGlobalPlacesByGroup(Group group);

    List<Place> getUserGlobalPlacesByGroup(User user, Group group);

    List<Place> getGlobalAccessPlaces();

    List<Place> getFavoritePlaces(User user);

    void saveGlobalCopy(Place sourcePlace);

    void savePlace(Place place);

    void deletePlace(Place place);
}
