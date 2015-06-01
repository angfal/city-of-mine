package edu.course.city.db.dao;

import edu.course.city.db.model.Mark;
import edu.course.city.db.model.Place;

import java.util.List;

public interface MarkDao {

    List<Mark> getByPlace(Place place);

    void save(Mark mark);

    void delete(Mark mark);
}
