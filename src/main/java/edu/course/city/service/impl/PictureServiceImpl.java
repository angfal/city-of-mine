package edu.course.city.service.impl;

import edu.course.city.db.dao.PictureDao;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.Picture;
import edu.course.city.db.model.Place;
import edu.course.city.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("pictureService")
@Transactional(readOnly = true)
public class PictureServiceImpl implements PictureService {

    @Autowired
    private PictureDao pictureDao;

    private Picture initializePicture(Picture picture) {
        // Nothing to do
        return picture;
    }

    private List<Picture> initializePictures(List<Picture> pictures) {
        for (Picture picture : pictures) {
            initializePicture(picture);
        }
        return pictures;
    }

    @Override
    public Picture getPictureById(long id) {
        return initializePicture(pictureDao.getById(id));
    }

    @Override
    public List<Picture> getPicturesByPlace(Place place) {
        return initializePictures(pictureDao.getByPlace(place));
    }

    @Override
    @Transactional(readOnly = false)
    public void savePicture(Picture picture) {
        pictureDao.save(picture);
    }

    @Override
    @Transactional(readOnly = false)
    public void deletePicture(Picture picture) {
        pictureDao.delete(picture);
    }
}
