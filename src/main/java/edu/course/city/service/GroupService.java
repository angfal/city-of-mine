package edu.course.city.service;

import edu.course.city.db.model.Group;
import edu.course.city.db.model.GroupType;
import edu.course.city.db.model.User;

import java.util.List;

public interface GroupService {

    Group getGroupById(Long id);

    boolean isContainsUserEntities(Group group);

    List<Group> getLocalRootGroups(User user, GroupType type);

    List<Group> getGlobalRootGroups(GroupType type);

    List<Group> getSystemGlobalRootGroups(GroupType type);

    List<Group> getGroupsByParent(Group parent);

    List<Group> getLocalGroupsByParent(Group parent);

    List<Group> getGlobalGroupsByParent(Group parent);

    List<Group> getSystemGlobalGroupsByParent(Group parent);

    void saveGroup(Group group);

    void deleteGroup(Group group);
}
