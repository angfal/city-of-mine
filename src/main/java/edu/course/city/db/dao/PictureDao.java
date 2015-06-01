package edu.course.city.db.dao;

import edu.course.city.db.model.Picture;
import edu.course.city.db.model.Place;

import java.util.List;

public interface PictureDao {

    Picture getById(long id);

    List<Picture> getByPlace(Place place);

    void save(Picture picture);

    void delete(Picture picture);
}
