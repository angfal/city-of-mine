package edu.course.city.db.dao.impl;

import edu.course.city.db.dao.PictureDao;
import edu.course.city.db.model.Comment;
import edu.course.city.db.model.Picture;
import edu.course.city.db.model.Place;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("pictureDao")
public class PictureDaoImpl implements PictureDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Picture getById(long id) {
        return (Picture) sessionFactory.getCurrentSession().get(Picture.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Picture> getByPlace(Place place) {
        Query query = sessionFactory.getCurrentSession().createQuery("select pic from Picture pic left join pic.places pl where pl = :place");
        query.setParameter("place", place);
        return query.list();
    }

    @Override
    public void save(Picture picture) {
        sessionFactory.getCurrentSession().saveOrUpdate(picture);
    }

    @Override
    public void delete(Picture picture) {
        sessionFactory.getCurrentSession().delete(picture);
    }
}
