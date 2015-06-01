package edu.course.city.service.impl;

import edu.course.city.db.dao.MarkDao;
import edu.course.city.db.model.Mark;
import edu.course.city.db.model.Place;
import edu.course.city.service.MarkService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("markService")
@Transactional(readOnly = true)
public class MarkServiceImpl implements MarkService {

    @Autowired
    private MarkDao markDao;

    private Mark initializeMark(Mark mark) {
        if (mark != null) {
            Hibernate.initialize(mark.getUser());
        }
        return mark;
    }

    private List<Mark> initializeMarks(List<Mark> marks) {
        for (Mark mark : marks) {
            initializeMark(mark);
        }
        return marks;
    }

    @Override
    public List<Mark> getMarksByPlace(Place place) {
        return initializeMarks(markDao.getByPlace(place));
    }

    @Override
    @Transactional(readOnly = false)
    public void saveMark(Mark mark) {
        markDao.save(mark);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteMark(Mark mark) {
        markDao.delete(mark);
    }
}
