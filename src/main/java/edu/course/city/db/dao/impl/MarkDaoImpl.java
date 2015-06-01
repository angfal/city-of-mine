package edu.course.city.db.dao.impl;

import edu.course.city.db.dao.MarkDao;
import edu.course.city.db.model.Mark;
import edu.course.city.db.model.Place;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("markDao")
public class MarkDaoImpl implements MarkDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<Mark> getByPlace(Place place) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Mark where place = :place");
        query.setParameter("place", place);
        return query.list();
    }

    @Override
    public void save(Mark mark) {
        sessionFactory.getCurrentSession().saveOrUpdate(mark);
    }

    @Override
    public void delete(Mark mark) {
        sessionFactory.getCurrentSession().delete(mark);
    }
}
