package edu.course.city.db.dao;

import edu.course.city.db.model.Comment;
import edu.course.city.db.model.Place;

import java.util.List;

public interface CommentDao {

    List<Comment> getByPlace(Place place);

    void save(Comment comment);

    void delete(Comment comment);
}
