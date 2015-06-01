package edu.course.city.db.dao.impl;

import edu.course.city.db.dao.CommentDao;
import edu.course.city.db.model.Comment;
import edu.course.city.db.model.Place;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("commentDao")
public class CommentDaoImpl implements CommentDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<Comment> getByPlace(Place place) {
        Query query = sessionFactory.getCurrentSession().createQuery("from Comment where place = :place");
        query.setParameter("place", place);
        return query.list();
    }

    @Override
    public void save(Comment comment) {
        sessionFactory.getCurrentSession().saveOrUpdate(comment);
    }

    @Override
    public void delete(Comment comment) {
        sessionFactory.getCurrentSession().delete(comment);
    }
}
