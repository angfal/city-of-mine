package edu.course.city.web.model;

public class UiTreeNode implements ShowHideSupport {

    private boolean showed = true;

    private boolean hiddenByUser = false;

    @Override
    public boolean isHiddenByUser() {
        return hiddenByUser;
    }

    @Override
    public void setHiddenByUser(boolean hiddenByUser) {
        this.hiddenByUser = hiddenByUser;
    }

    @Override
    public boolean isShowed() {
        return showed;
    }

    @Override
    public void setShowed(boolean showed) {
        this.showed = showed;
    }
}
