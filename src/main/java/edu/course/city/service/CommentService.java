package edu.course.city.service;

import edu.course.city.db.model.Comment;
import edu.course.city.db.model.Place;

import java.util.List;

public interface CommentService {

    List<Comment> getCommentsByPlace(Place place);

    void saveComment(Comment comment);

    void deleteComment(Comment comment);
}
