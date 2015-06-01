package edu.course.city.web.model;

import edu.course.city.db.model.Group;

public class UiGroup extends UiTreeNode {

    private Group sourceGroup;

    private UiPicture markerIcon;

    public UiGroup(Group group) {
        loadData(group);
    }

    public void loadData(Group group) {
        if (group == null) {
            throw new IllegalArgumentException("The group can't be null");
        }
        this.sourceGroup = group;
    }

    public Group getSourceGroup() {
        return sourceGroup;
    }

    public Long getId() {
        return sourceGroup.getId();
    }

    public String getName() {
        return sourceGroup.getName();
    }

    public UiPicture getMarkerIcon() {
        if (sourceGroup.getIcon() != null && (markerIcon == null || !markerIcon.getId().equals(sourceGroup.getIcon().getId()))) {
            markerIcon = new UiPicture(sourceGroup.getIcon());
        }
        if (sourceGroup.getIcon() == null) {
            markerIcon = null;
        }
        return markerIcon;
    }

    public void setMarkerIcon(UiPicture markerIcon) {
        sourceGroup.setIcon(markerIcon != null ? markerIcon.getSourcePicture() : null);
        this.markerIcon = markerIcon;
    }
}
