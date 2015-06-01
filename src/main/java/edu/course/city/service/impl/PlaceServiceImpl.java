package edu.course.city.service.impl;

import edu.course.city.db.dao.PlaceDao;
import edu.course.city.db.model.*;
import edu.course.city.service.*;
import edu.course.city.utils.SerializationUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("placeService")
@Transactional(readOnly = true)
public class PlaceServiceImpl implements PlaceService {

    @Autowired
    private PlaceDao placeDao;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private MarkService markService;

    @Autowired
    private CommentService commentService;

    private Place initializePlace(Place place) {
        if (place != null) {
            Hibernate.initialize(place.getCoordinate());
        }
        return place;
    }

    private List<Place> initializePlaces(List<Place> places) {
        for (Place place : places) {
            initializePlace(place);
        }
        return places;
    }

    @Override
    public Place getPlaceById(long id) {
        return initializePlace(placeDao.getById(id));
    }

    @Override
    public List<Place> getLocalRootPlaces(User user) {
        return initializePlaces(placeDao.listOfLocalRoots(user));
    }

    @Override
    public List<Place> getGlobalRootPlaces() {
        return initializePlaces(placeDao.listOfGlobalRoots());
    }

    @Override
    public List<Place> getSystemGlobalRootPlaces() {
        return initializePlaces(placeDao.listOfSystemGlobalRoots());
    }

    @Override
    public List<Place> getUserGlobalRootPlaces() {
        return initializePlaces(placeDao.listOfUserGlobalRoots());
    }

    @Override
    public List<Place> getUserGlobalRootPlaces(User user) {
        return initializePlaces(placeDao.listOfUserGlobalRoots(user));
    }

    @Override
    public List<Place> getPlacesByGroup(Group group) {
        return initializePlaces(placeDao.listByGroup(group));
    }

    @Override
    public List<Place> getUsersPlacesByGroup(Group group) {
        return initializePlaces(placeDao.listOfUsersByGroup(group));
    }

    @Override
    public List<Place> getLocalPlacesByGroup(User user, Group group) {
        return initializePlaces(placeDao.listOfLocalByGroup(user, group));
    }

    @Override
    public List<Place> getGlobalPlacesByGroup(Group group) {
        return initializePlaces(placeDao.listOfGlobalByGroup(group));
    }

    @Override
    public List<Place> getSystemGlobalPlacesByGroup(Group group) {
        return initializePlaces(placeDao.listOfSystemGlobalByGroup(group));
    }

    @Override
    public List<Place> getUsersGlobalPlacesByGroup(Group group) {
        return initializePlaces(placeDao.listOfUsersGlobalByGroup(group));
    }

    @Override
    public List<Place> getUserGlobalPlacesByGroup(User user, Group group) {
        return initializePlaces(placeDao.listOfUserGlobalByGroup(user, group));
    }

    @Override
    public List<Place> getGlobalAccessPlaces() {
        return initializePlaces(placeDao.lostOfGlobalAccess());
    }

    @Override
    public List<Place> getFavoritePlaces(User user) {
        List<Favorite> favorites = favoriteService.getFavorites(user);
        List<Place> favoritePlaces = new ArrayList<>();
        for (Favorite favorite : favorites) {
            favoritePlaces.add(initializePlace(favorite.getPlace()));
        }
        return favoritePlaces;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveGlobalCopy(Place sourcePlace) {
        Place currentGlobalCopy = placeDao.getGlobalCopy(sourcePlace);
        if (currentGlobalCopy != null) {
            placeDao.evict(currentGlobalCopy);
        }

        Place newGlobalCopy = SerializationUtils.clone(sourcePlace);
        newGlobalCopy.setId(currentGlobalCopy != null ? currentGlobalCopy.getId() : null);
        newGlobalCopy.setGlobalCopy(true);
        newGlobalCopy.setSource(sourcePlace);

        Place sessionSourcePlace = placeDao.getById(sourcePlace.getId());
        newGlobalCopy.setUser(sessionSourcePlace.getUser());
        Group sourceGroup = sessionSourcePlace.getGroup();
        while (sourceGroup != null && !sourceGroup.isGlobalCopy()) {
            sourceGroup = sourceGroup.getParent();
        }
        newGlobalCopy.setGroup(sourceGroup);
        savePlace(newGlobalCopy);

        List<Picture> placePictures = pictureService.getPicturesByPlace(sourcePlace);
        Map<Long, Picture> placePictureMap = new HashMap<>();
        for (Picture picture : placePictures) {
            placePictureMap.put(picture.getId(), picture);
        }

        List<Picture> globalPlacePictures =
                currentGlobalCopy != null ? pictureService.getPicturesByPlace(currentGlobalCopy) : Collections.<Picture>emptyList();
        for (Picture picture : globalPlacePictures) {
            if (placePictureMap.containsKey(picture.getId())) {
                placePictureMap.remove(picture.getId());
            } else {
                pictureService.deletePicture(picture);
            }
        }

        for (Picture picture : placePictureMap.values()) {
            Hibernate.initialize(picture.getPlaces());
            picture.getPlaces().add(newGlobalCopy);
            pictureService.savePicture(picture);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void savePlace(Place place) {
        if (place.getId() != null && !place.isGlobalAccess()) {
            Place globalCopy = placeDao.getGlobalCopy(place);
            if (globalCopy != null) {
                deletePlace(globalCopy);
            }
        }
        placeDao.save(place);
    }

    @Override
    @Transactional(readOnly = false)
    public void deletePlace(Place place) {
        Place globalCopy = placeDao.getGlobalCopy(place);
        if (globalCopy != null) {
            deletePlace(globalCopy);
        }

        for (Picture picture : pictureService.getPicturesByPlace(place)) {
            pictureService.deletePicture(picture);
        }
        for (Mark mark : markService.getMarksByPlace(place)) {
            markService.deleteMark(mark);
        }
        for (Favorite favorite : favoriteService.getFavorites(place)) {
            favoriteService.deleteFavorite(favorite);
        }
        for (Comment comment : commentService.getCommentsByPlace(place)) {
            commentService.deleteComment(comment);
        }
        placeDao.delete(place);
    }
}
