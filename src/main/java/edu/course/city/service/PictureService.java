package edu.course.city.service;

import edu.course.city.db.model.Picture;
import edu.course.city.db.model.Place;

import java.util.List;

public interface PictureService {

    Picture getPictureById(long id);

    List<Picture> getPicturesByPlace(Place place);

    void savePicture(Picture picture);

    void deletePicture(Picture picture);
}
