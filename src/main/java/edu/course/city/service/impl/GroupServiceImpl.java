package edu.course.city.service.impl;

import edu.course.city.db.dao.GroupDao;
import edu.course.city.db.model.*;
import edu.course.city.service.GroupService;
import edu.course.city.service.PlaceService;
import edu.course.city.service.RouteService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("groupService")
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private RouteService routeService;

    private Group initializeGroup(Group group) {
        Hibernate.initialize(group.getIcon());
        return group;
    }

    private List<Group> initializeGroups(List<Group> groups) {
        for (Group group : groups) {
            initializeGroup(group);
        }
        return groups;
    }

    @Override
    public Group getGroupById(Long id) {
        return initializeGroup(groupDao.getById(id));
    }

    @Override
    public boolean isContainsUserEntities(Group group) {
        if (placeService.getUsersPlacesByGroup(group).size() > 0) {
            return true;
        }
        for (Group child : getGroupsByParent(group)) {
            if (!child.isGlobalCopy()) {
                return true;
            }
            if (isContainsUserEntities(child)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Group> getLocalRootGroups(User user, GroupType type) {
        return initializeGroups(groupDao.listOfLocalRoots(user, type));
    }

    @Override
    public List<Group> getGlobalRootGroups(GroupType type) {
        return getSystemGlobalRootGroups(type);
    }

    @Override
    public List<Group> getSystemGlobalRootGroups(GroupType type) {
        return initializeGroups(groupDao.listOfSystemGlobalRoots(type));
    }

    @Override
    public List<Group> getGroupsByParent(Group parent) {
        return initializeGroups(groupDao.listByParent(parent));
    }

    @Override
    public List<Group> getLocalGroupsByParent(Group parent) {
        return initializeGroups(groupDao.listOfLocalByParent(parent));
    }

    @Override
    public List<Group> getGlobalGroupsByParent(Group parent) {
        return getSystemGlobalGroupsByParent(parent);
    }

    @Override
    public List<Group> getSystemGlobalGroupsByParent(Group parent) {
        return initializeGroups(groupDao.listOfSystemGlobalByParent(parent));
    }

    @Override
    @Transactional(readOnly = false)
    public void saveGroup(Group group) {
        groupDao.save(group);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteGroup(Group group) {
        for (Group child : getGroupsByParent(group)) {
            deleteGroup(child);
        }
        for (Place child : placeService.getPlacesByGroup(group)) {
            placeService.deletePlace(child);
        }
        for (Route route : routeService.getRoutesByGroup(group)) {
            routeService.deleteRoute(route);
        }
        groupDao.delete(group);
    }
}
