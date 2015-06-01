package edu.course.city.web.bean.dialog;

import edu.course.city.Constants;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.Picture;
import edu.course.city.service.PictureService;
import edu.course.city.utils.MainUtils;
import edu.course.city.web.callback.GroupActions;
import edu.course.city.web.model.UiGroup;
import edu.course.city.web.model.UiPicture;
import org.primefaces.event.FileUploadEvent;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;

@ViewScoped
@ManagedBean
public class GroupBean implements Serializable {

    private static final long serialVersionUID = -3199249406055238094L;

    @ManagedProperty("#{pictureService}")
    private PictureService pictureService;

    private UiGroup uiGroup;

    private UiPicture uiIcon;

    private GroupActions actions;

    public void editGroup(UiGroup uiGroup, GroupActions actions) {
        if (uiGroup == null) {
            throw new IllegalArgumentException("The uiGroup is required argument");
        }

        this.uiGroup = uiGroup;
        this.uiIcon = uiGroup.getMarkerIcon();
        this.actions = actions;
    }

    private void clearBean() {
        uiGroup = null;
        uiIcon = null;
        actions = null;
    }

    public void saveGroup() {
        if (actions != null) {
            actions.save(uiGroup);
        }
        if (uiIcon != null && (uiGroup.getMarkerIcon() == null || !uiIcon.getId().equals(uiGroup.getMarkerIcon().getId()))) {
            pictureService.deletePicture(uiIcon.getSourcePicture());
        }
        clearBean();
    }

    public void cancel() {
        if (uiGroup.getMarkerIcon() != null && uiGroup.getMarkerIcon().isAdded()) {
            pictureService.deletePicture(uiGroup.getMarkerIcon().getSourcePicture());
        }
        if (actions != null) {
            actions.cancel(uiGroup);
        }
        clearBean();
    }

    public void uploadIcon(FileUploadEvent event) throws IOException {
        Picture picture = new Picture();
        picture.setContentType("image/png");
        picture.setData(MainUtils.resizeAndConvertToPng(event.getFile().getInputstream(), Constants.TREE_ICON_SIZE));
        pictureService.savePicture(picture);

        if (uiGroup.getMarkerIcon() != null && uiGroup.getMarkerIcon().isAdded()) {
            pictureService.deletePicture(uiGroup.getMarkerIcon().getSourcePicture());
        }

        UiPicture uiIcon = new UiPicture(picture);
        uiIcon.setAdded(true);
        uiGroup.setMarkerIcon(uiIcon);
    }

    public void clearIcon() {
        if (uiGroup.getMarkerIcon() != null && uiGroup.getMarkerIcon().isAdded()) {
            pictureService.deletePicture(uiGroup.getMarkerIcon().getSourcePicture());
        }
        uiGroup.setMarkerIcon(null);
    }

    public void setPictureService(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    public UiGroup getUiGroup() {
        return uiGroup;
    }

    public Group getGroup() {
        return uiGroup != null ? uiGroup.getSourceGroup() : null;
    }
}
