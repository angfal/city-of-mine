package edu.course.city.web.bean.moderation;

import edu.course.city.Constants;
import edu.course.city.db.model.Place;
import edu.course.city.service.PlaceService;
import edu.course.city.utils.FacesUtils;
import edu.course.city.web.bean.MapBean;
import edu.course.city.web.bean.dialog.PlaceBean;
import edu.course.city.web.model.UiPlace;
import edu.course.city.web.model.UiString;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.util.MessageFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.util.Date;
import java.util.List;

@ViewScoped
@ManagedBean(name = "moderationPlacesBean")
public class PlacesBean extends MapBean {

    // Injection variables <<<
    @ManagedProperty("#{placeService}")
    private PlaceService placeService;

    @ManagedProperty("#{placeBean}")
    private PlaceBean placeBean;
    // >>>

    // UI variables
    private TreeNode rootNode;

    private TreeNode moderationSuccessNode;

    private TreeNode moderationFailedNode;

    private TreeNode selectedNode;
    // >>>

    @PostConstruct
    public void initPage() {
        rootNode = new DefaultTreeNode("Root", null);
        setMapModel(new DefaultMapModel());

        TreeNode notModeratedNode = new DefaultTreeNode(
                new UiString(FacesUtils.getMessage("moderation.place.group.notModerated")), rootNode);
        notModeratedNode.setExpanded(true);
        moderationSuccessNode = new DefaultTreeNode(
                new UiString(FacesUtils.getMessage("moderation.place.group.moderationSuccess")), rootNode);
        moderationFailedNode = new DefaultTreeNode(
                new UiString(FacesUtils.getMessage("moderation.place.group.moderationFailed")), rootNode);

        List<Place> globalPlaces = placeService.getGlobalAccessPlaces();
        for (Place globalPlace : globalPlaces) {
            if (globalPlace.getModerated() == null) {
                initPlaceNode(notModeratedNode, new UiPlace(globalPlace));
            } else if (Boolean.TRUE.equals(globalPlace.getModerated())) {
                initPlaceNode(moderationSuccessNode, new UiPlace(globalPlace));
            } else {
                initPlaceNode(moderationFailedNode, new UiPlace(globalPlace));
            }
        }
    }

    private void initPlaceNode(TreeNode parent, UiPlace uiPlace) {
        new DefaultTreeNode(Constants.TREE_NODE_TYPE_LOCAL_PLACE, uiPlace, parent);
        addToMap(uiPlace);
    }

    public void showHideNode() {
        showHideNode(selectedNode);
    }

    public void goToPlace() {
        goToPlace((UiPlace) selectedNode.getData());
    }

    public void viewPlace() {
        UiPlace uiPlace = (UiPlace) selectedNode.getData();
        placeBean.showPlace(new UiPlace(placeService.getPlaceById(uiPlace.getId())), 2);
    }

    public void acceptPlace() {
        UiPlace uiPlace = placeBean.getUiPlace();
        Place place = uiPlace.getSourcePlace();

        place.setModerated(Boolean.TRUE);
        place.setEndModerationDate(new Date());
        placeService.saveGlobalCopy(place);
        placeService.savePlace(place);

        selectedNode.getParent().getChildren().remove(selectedNode);
        initPlaceNode(moderationSuccessNode, uiPlace);
    }

    public void denyPlace() {
        UiPlace uiPlace = placeBean.getUiPlace();
        Place place = uiPlace.getSourcePlace();

        if (place.getModeratorComments() == null || place.getModeratorComments().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    MessageFactory.getMessage("placeDialog.error.requiredComments", FacesMessage.SEVERITY_ERROR, new Object[]{}));
            FacesContext.getCurrentInstance().validationFailed();
            return;
        }
        place.setModerated(Boolean.FALSE);
        place.setEndModerationDate(new Date());
        placeService.savePlace(place);

        selectedNode.getParent().getChildren().remove(selectedNode);
        initPlaceNode(moderationFailedNode, uiPlace);
    }

    public void setPlaceService(PlaceService placeService) {
        this.placeService = placeService;
    }

    public void setPlaceBean(PlaceBean placeBean) {
        this.placeBean = placeBean;
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }
}
