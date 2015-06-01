package edu.course.city.service.impl;

import edu.course.city.db.dao.FavoriteDao;
import edu.course.city.db.model.Favorite;
import edu.course.city.db.model.Place;
import edu.course.city.db.model.User;
import edu.course.city.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("favoriteService")
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteDao favoriteDao;

    private Favorite initializeFavorite(Favorite favorite) {
        // Nothing to do
        return favorite;
    }

    private List<Favorite> initializeFavorites(List<Favorite> favorites) {
        for (Favorite favorite : favorites) {
            initializeFavorite(favorite);
        }
        return favorites;
    }

    @Override
    public Favorite getFavorite(User user, Place place) {
        return initializeFavorite(favoriteDao.getFavorite(user, place));
    }

    @Override
    public List<Favorite> getFavorites(User user) {
        return initializeFavorites(favoriteDao.getFavorites(user));
    }

    @Override
    public List<Favorite> getFavorites(Place place) {
        return initializeFavorites(favoriteDao.getFavorites(place));
    }

    @Override
    public boolean isFavorite(User user, Place place) {
        return favoriteDao.getFavorite(user, place) != null;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveFavorite(Favorite favorite) {
        favoriteDao.save(favorite);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteFavorite(Favorite favorite) {
        favoriteDao.delete(favorite);
    }
}
