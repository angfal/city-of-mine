package edu.course.city.service;

import edu.course.city.db.model.Favorite;
import edu.course.city.db.model.Place;
import edu.course.city.db.model.User;

import java.util.List;

public interface FavoriteService {

    Favorite getFavorite(User user, Place place);

    List<Favorite> getFavorites(User user);

    List<Favorite> getFavorites(Place place);

    boolean isFavorite(User user, Place place);

    void saveFavorite(Favorite favorite);

    void deleteFavorite(Favorite favorite);
}
