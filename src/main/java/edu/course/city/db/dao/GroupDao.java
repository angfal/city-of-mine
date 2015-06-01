package edu.course.city.db.dao;

import edu.course.city.db.model.Group;
import edu.course.city.db.model.GroupType;
import edu.course.city.db.model.User;

import java.util.List;

public interface GroupDao {

    Group getById(Long id);

    List<Group> listOfLocalRoots(User user, GroupType type);

    List<Group> listOfSystemGlobalRoots(GroupType type);

    List<Group> listByParent(Group parent);

    List<Group> listOfLocalByParent(Group parent);

    List<Group> listOfSystemGlobalByParent(Group parent);

    void save(Group group);

    void delete(Group group);
}
