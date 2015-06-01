package edu.course.city.db.dao;

import edu.course.city.db.model.Favorite;
import edu.course.city.db.model.Place;
import edu.course.city.db.model.User;

import java.util.List;

public interface FavoriteDao {

    Favorite getFavorite(User user, Place place);

    List<Favorite> getFavorites(User user);

    List<Favorite> getFavorites(Place place);

    void save(Favorite favorite);

    void delete(Favorite favorite);
}
