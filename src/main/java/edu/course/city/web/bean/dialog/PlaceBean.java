package edu.course.city.web.bean.dialog;

import edu.course.city.db.model.Comment;
import edu.course.city.db.model.Picture;
import edu.course.city.db.model.Place;
import edu.course.city.service.CommentService;
import edu.course.city.service.PictureService;
import edu.course.city.utils.MainUtils;
import edu.course.city.web.bean.SessionBean;
import edu.course.city.web.callback.PlaceActions;
import edu.course.city.web.model.UiPicture;
import edu.course.city.web.model.UiPlace;
import org.apache.commons.io.IOUtils;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.FileUploadEvent;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean
public class PlaceBean implements Serializable {

    private static final long serialVersionUID = 6436167408046567795L;

    @ManagedProperty("#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty("#{pictureService}")
    private PictureService pictureService;

    @ManagedProperty("#{commentService}")
    private CommentService commentService;

    private TabView tabView;

    private UiPlace uiPlace;

    private PlaceActions actions;

    private boolean viewMode = true;

    private List<Comment> comments;

    private String commentText;

    private List<UiPicture> pictures;

    public void showPlace(UiPlace uiPlace, int activeTabIndex) {
        loadPlace(uiPlace);
        tabView.setActiveIndex(activeTabIndex);
        viewMode = true;
    }

    public void editPlace(UiPlace uiPlace, PlaceActions actions) {
        loadPlace(uiPlace);
        this.actions = actions;
        viewMode = false;
    }

    private void loadPlace(UiPlace uiPlace) {
        if (uiPlace == null) {
            throw new IllegalArgumentException("The uiPlace is required argument");
        }

        this.uiPlace = uiPlace;

        Place place = uiPlace.getSourcePlace();
        this.comments = place.getId() != null ?
                commentService.getCommentsByPlace(place) : new ArrayList<Comment>();

        this.pictures = new ArrayList<>();
        if (place.getId() != null) {
            for (Picture picture : pictureService.getPicturesByPlace(place)) {
                pictures.add(new UiPicture(picture));
            }
        }
    }

    private void clearBean() {
        uiPlace = null;
        actions = null;
        viewMode = true;
        comments = null;
        commentText = null;
        pictures = null;
    }

    public void selectCoordinate() {
        if (actions != null) {
            actions.selectCoordinate();
        }
    }

    public void savePlace() {
        if (actions != null) {
            actions.save(uiPlace);
        }
        for (UiPicture uiPicture : pictures) {
            if (uiPicture.isAdded()) {
                Picture picture = uiPicture.getSourcePicture();
                picture.getPlaces().add(getPlace());
                pictureService.savePicture(picture);
            }
        }
        clearBean();
    }

    public void cancel() {
        if (actions != null) {
            actions.cancel(uiPlace);
        }
        for (UiPicture uiPicture : pictures) {
            if (uiPicture.isAdded()) {
                pictureService.deletePicture(uiPicture.getSourcePicture());
            }
        }
        clearBean();
    }

    public void addComment() {
        if (commentText == null || commentText.trim().isEmpty()) {
            return;
        }
        Comment comment = new Comment();
        comment.setUser(sessionBean.getUser());
        comment.setPlace(getPlace());
        comment.setText(commentText);
        commentService.saveComment(comment);

        comments.add(comment);
        commentText = null;
    }

    public void removeComment(Comment comment) {
        commentService.deleteComment(comment);
        comments.remove(comment);
    }

    public void uploadPictures(FileUploadEvent event) throws IOException {
        Picture picture = new Picture();
        picture.setContentType(event.getFile().getContentType());
        picture.setData(IOUtils.toByteArray(event.getFile().getInputstream()));
        pictureService.savePicture(picture);

        UiPicture uiPicture = new UiPicture(picture);
        uiPicture.setAdded(true);
        pictures.add(uiPicture);
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setPictureService(PictureService pictureService) {
        this.pictureService = pictureService;
    }

    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    public TabView getTabView() {
        return tabView;
    }

    public void setTabView(TabView tabView) {
        this.tabView = tabView;
    }

    public UiPlace getUiPlace() {
        return uiPlace;
    }

    public Place getPlace() {
        return uiPlace != null ? uiPlace.getSourcePlace() : null;
    }

    public boolean isViewMode() {
        return viewMode;
    }

    public String getCoordinate() {
        return getPlace() != null ? MainUtils.convertMapCoordinate(getPlace().getCoordinate()) : null;
    }

    public void setCoordinate(String coordinate) {
        // set coords stub
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public List<UiPicture> getPictures() {
        return pictures;
    }
}
