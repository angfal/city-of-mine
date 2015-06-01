package edu.course.city.service.impl;

import edu.course.city.db.dao.CommentDao;
import edu.course.city.db.model.Comment;
import edu.course.city.db.model.Place;
import edu.course.city.service.CommentService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("commentService")
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    private Comment initializeComment(Comment comment) {
        Hibernate.initialize(comment.getUser());
        return comment;
    }

    private List<Comment> initializeComments(List<Comment> comments) {
        for (Comment comment : comments) {
            initializeComment(comment);
        }
        return comments;
    }

    @Override
    public List<Comment> getCommentsByPlace(Place place) {
        return initializeComments(commentDao.getByPlace(place));
    }

    @Override
    @Transactional(readOnly = false)
    public void saveComment(Comment comment) {
        commentDao.save(comment);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteComment(Comment comment) {
        commentDao.delete(comment);
    }
}
