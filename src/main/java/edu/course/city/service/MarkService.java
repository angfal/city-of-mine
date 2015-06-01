package edu.course.city.service;

import edu.course.city.db.model.Mark;
import edu.course.city.db.model.Place;

import java.util.List;

public interface MarkService {

    List<Mark> getMarksByPlace(Place place);

    void saveMark(Mark mark);

    void deleteMark(Mark mark);
}
