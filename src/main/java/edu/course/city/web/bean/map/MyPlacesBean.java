package edu.course.city.web.bean.map;

import edu.course.city.Constants;
import edu.course.city.db.model.Favorite;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.GroupType;
import edu.course.city.db.model.Place;
import edu.course.city.service.GroupService;
import edu.course.city.service.PlaceService;
import edu.course.city.utils.FacesUtils;
import edu.course.city.utils.MainUtils;
import edu.course.city.web.bean.MapBean;
import edu.course.city.web.bean.dialog.ConfirmBean;
import edu.course.city.web.bean.dialog.GroupBean;
import edu.course.city.web.bean.dialog.PlaceBean;
import edu.course.city.web.callback.ConfirmActions;
import edu.course.city.web.callback.GroupActions;
import edu.course.city.web.callback.PlaceActions;
import edu.course.city.web.model.UiGroup;
import edu.course.city.web.model.UiPlace;
import edu.course.city.web.model.UiTreeNode;
import org.primefaces.event.map.PointSelectEvent;
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
@ManagedBean
public class MyPlacesBean extends MapBean {

    private static final long serialVersionUID = -107389272533244072L;

    // View mode constants <<<
    private static final int VIEW_MODE_MY_PLACES = 1;

    private static final int VIEW_MODE_MY_GLOBAL_PLACES = 2;

    private static final int VIEW_MODE_FAVORITE_PLACES = 3;
    // >>>

    // Injection variables <<<
    @ManagedProperty("#{groupService}")
    private GroupService groupService;

    @ManagedProperty("#{placeService}")
    private PlaceService placeService;

    @ManagedProperty("#{groupBean}")
    private GroupBean groupBean;

    @ManagedProperty("#{placeBean}")
    private PlaceBean placeBean;

    @ManagedProperty("#{confirmBean}")
    private ConfirmBean confirmBean;
    // >>>

    // UI variables <<<
    private TreeNode rootNode;

    private TreeNode selectedNode;

    private TreeNode operationNode;

    private boolean selectCoordinateMode = false;

    private int viewMode = VIEW_MODE_MY_PLACES;
    // >>>

    @PostConstruct
    public void initPage() {
        rootNode = new DefaultTreeNode("Root", null);
        setMapModel(new DefaultMapModel());

        switch (viewMode) {
            case VIEW_MODE_MY_PLACES:
                initMyPlacesTree();
                break;
            case VIEW_MODE_MY_GLOBAL_PLACES:
                initMyGlobalPlacesTree();
                break;
            case VIEW_MODE_FAVORITE_PLACES:
                initMyFavoritePlacesTree();
                break;
        }

        if (rootNode.getChildCount() == 0) {
            new DefaultTreeNode(FacesUtils.getMessage("home.tree.notFound"), rootNode);
        }
    }

    private void removeFromTree(TreeNode treeNode) {
        treeNode.getParent().getChildren().remove(treeNode);
        if (rootNode.getChildCount() == 0) {
            new DefaultTreeNode(FacesUtils.getMessage("home.tree.notFound"), rootNode);
        }
    }

    private void initMyPlacesTree() {
        TreeNode rootGroupNode = new DefaultTreeNode(Constants.TREE_NODE_TYPE_ROOT, new UiTreeNode(), rootNode);
        rootGroupNode.setExpanded(true);

        for (Group group : groupService.getGlobalRootGroups(GroupType.PLACE)) {
            initLocalGroupNode(rootGroupNode, new UiGroup(group), Constants.TREE_NODE_TYPE_GLOBAL_GROUP);
        }
        for (Group group : groupService.getLocalRootGroups(getSessionBean().getUser(), GroupType.PLACE)) {
            initLocalGroupNode(rootGroupNode, new UiGroup(group), Constants.TREE_NODE_TYPE_LOCAL_GROUP);
        }
        for (Place place : placeService.getLocalRootPlaces(getSessionBean().getUser())) {
            initPlaceNode(rootGroupNode, new UiPlace(place), Constants.TREE_NODE_TYPE_LOCAL_PLACE);
        }
    }

    private void initMyGlobalPlacesTree() {
        for (Group group : groupService.getGlobalRootGroups(GroupType.PLACE)) {
            initGlobalGroupNode(rootNode, new UiGroup(group));
        }
        for (Place place : placeService.getUserGlobalRootPlaces(getSessionBean().getUser())) {
            initPlaceNode(rootNode, new UiPlace(place), Constants.TREE_NODE_TYPE_GLOBAL_PLACE);
        }
    }

    private void initMyFavoritePlacesTree() {
        List<Place> favoritePlaces = placeService.getFavoritePlaces(getSessionBean().getUser());
        if (favoritePlaces != null) {
            for (Place place : favoritePlaces) {
                initPlaceNode(rootNode, new UiPlace(place), Constants.TREE_NODE_TYPE_FAVORITE);
            }
        }
    }

    private void initGlobalGroupNode(TreeNode parent, UiGroup uiGroup) {
        TreeNode node = new DefaultTreeNode(Constants.TREE_NODE_TYPE_GLOBAL_GROUP, uiGroup, parent);
        node.setExpanded(true);
        for (Group child : groupService.getGlobalGroupsByParent(uiGroup.getSourceGroup())) {
            initGlobalGroupNode(node, new UiGroup(child));
        }
        for (Place child : placeService.getUserGlobalPlacesByGroup(getSessionBean().getUser(), uiGroup.getSourceGroup())) {
            UiPlace uiPlace = new UiPlace(child);
            uiPlace.setMarkerIcon(uiGroup.getMarkerIcon());
            initPlaceNode(node, uiPlace, Constants.TREE_NODE_TYPE_GLOBAL_PLACE);
        }
    }

    private void initLocalGroupNode(TreeNode parent, UiGroup uiGroup, String nodeType) {
        TreeNode node = new DefaultTreeNode(nodeType, uiGroup, parent);
        node.setExpanded(true);
        for (Group child : groupService.getGlobalGroupsByParent(uiGroup.getSourceGroup())) {
            initLocalGroupNode(node, new UiGroup(child), Constants.TREE_NODE_TYPE_GLOBAL_GROUP);
        }
        for (Group child : groupService.getLocalGroupsByParent(uiGroup.getSourceGroup())) {
            initLocalGroupNode(node, new UiGroup(child), Constants.TREE_NODE_TYPE_LOCAL_GROUP);
        }
        for (Place child : placeService.getLocalPlacesByGroup(getSessionBean().getUser(), uiGroup.getSourceGroup())) {
            UiPlace uiPlace = new UiPlace(child);
            uiPlace.setMarkerIcon(uiGroup.getMarkerIcon());
            initPlaceNode(node, uiPlace, Constants.TREE_NODE_TYPE_LOCAL_PLACE);
        }
    }

    private void initPlaceNode(TreeNode parent, UiPlace uiPlace, String nodeType) {
        new DefaultTreeNode(nodeType, uiPlace, parent);
        addToMap(uiPlace);
    }

    public void showHideNode() {
        showHideNode(selectedNode);
    }

    public void createGroup() {
        operationNode = selectedNode;

        UiGroup uiParent = operationNode.getData() instanceof UiGroup ?
                (UiGroup) operationNode.getData() : null;
        Group parentGroup = (uiParent != null ? groupService.getGroupById(uiParent.getId()) : null);
        Group group = new Group();
        group.setType(GroupType.PLACE);
        group.setUser(getSessionBean().getUser());
        group.setParent(parentGroup);

        groupBean.editGroup(new UiGroup(group), new GroupActions() {
            @Override
            public void save(UiGroup uiGroup) {
                groupService.saveGroup(uiGroup.getSourceGroup());
                TreeNode node = new DefaultTreeNode(Constants.TREE_NODE_TYPE_LOCAL_GROUP, uiGroup, operationNode);
                node.setExpanded(true);
                operationNode = null;
            }

            @Override
            public void cancel(UiGroup uiGroup) {
                operationNode = null;
            }
        });
    }

    public void modifyGroup() {
        operationNode = selectedNode;

        UiGroup sourceUiGroup = (UiGroup) operationNode.getData();
        Group group = groupService.getGroupById(sourceUiGroup.getId());
        groupBean.editGroup(new UiGroup(group), new GroupActions() {
            @Override
            public void save(UiGroup uiGroup) {
                groupService.saveGroup(uiGroup.getSourceGroup());
                ((DefaultTreeNode) operationNode).setData(uiGroup);

                for (TreeNode child : operationNode.getChildren()) {
                    if (child.getData() instanceof UiPlace) {
                        UiPlace uiPlace = (UiPlace) child.getData();
                        uiPlace.setMarkerIcon(uiGroup.getMarkerIcon());
                        updateMarkerIcon((UiPlace) child.getData());
                    }
                }
                operationNode = null;
            }

            @Override
            public void cancel(UiGroup uiGroup) {
                operationNode = null;
            }
        });
    }

    public void deleteGroup() {
        operationNode = selectedNode;

        final UiGroup uiGroup = (UiGroup) operationNode.getData();
        confirmBean.showConfirm(
                FacesUtils.getMessage("myPlaces.confirm.deleteHeader"),
                FacesUtils.getMessage("myPlaces.confirm.deleteGroup", uiGroup.getName()),
                new ConfirmActions() {

                    @Override
                    public void confirm() {
                        groupService.deleteGroup(uiGroup.getSourceGroup());
                        removeFromMap(operationNode);
                        removeFromTree(operationNode);
                        operationNode = null;
                    }

                    @Override
                    public void cancel() {
                        operationNode = null;
                    }
                });
    }

    public void goToPlace() {
        super.goToPlace((UiPlace) selectedNode.getData());
    }

    public void viewPlace(int activeTabIndex) {
        UiPlace uiPlace = (UiPlace) selectedNode.getData();
        placeBean.showPlace(uiPlace, activeTabIndex);
    }

    private void selectCoordinates() {
        FacesContext.getCurrentInstance().addMessage(
                null,
                MessageFactory.getMessage("myPlaces.map.selectCoordinate", FacesMessage.SEVERITY_INFO, new Object[]{}));
        selectCoordinateMode = true;
    }

    public void selectMapPoint(PointSelectEvent event) {
        if (!selectCoordinateMode) {
            return;
        }

        UiPlace uiPlace = placeBean.getUiPlace();
        if (uiPlace.getId() != null) {
            UiPlace sourceUiPlace = (UiPlace) operationNode.getData();
            if (sourceUiPlace.isPlaceOnMap()) {
                removeFromMap(sourceUiPlace);
            }
        }
        if (uiPlace.isPlaceOnMap()) {
            removeFromMap(uiPlace);
        }

        uiPlace.setCoordinate(event.getLatLng());
        uiPlace.setZoom(getMapZoom());
        addToMap(uiPlace);
        selectCoordinateMode = false;
    }

    public void createPlace() {
        operationNode = selectedNode;

        UiGroup uiParent = operationNode.getData() instanceof UiGroup ?
                (UiGroup) operationNode.getData() : null;
        Group parentGroup = (uiParent != null ? uiParent.getSourceGroup() : null);
        Place place = new Place();
        place.setUser(getSessionBean().getUser());
        place.setGroup(parentGroup);
        UiPlace uiPlace = new UiPlace(place);
        if (uiParent != null) {
            uiPlace.setMarkerIcon(uiParent.getMarkerIcon());
            uiPlace.setShowed(uiParent.isShowed());
        }

        final MyPlacesBean self = this;
        placeBean.editPlace(uiPlace, new PlaceActions() {
            @Override
            public void selectCoordinate() {
                self.selectCoordinates();
            }

            @Override
            public void save(UiPlace uiPlace) {
                self.savePlace(uiPlace.getSourcePlace());
                new DefaultTreeNode(Constants.TREE_NODE_TYPE_LOCAL_PLACE, uiPlace, operationNode);
                operationNode = null;
            }

            @Override
            public void cancel(UiPlace uiPlace) {
                removeFromMap(uiPlace);
                operationNode = null;
            }
        });
    }

    public void modifyPlace() {
        operationNode = selectedNode;

        final MyPlacesBean self = this;
        final UiPlace sourceUiPlace = (UiPlace) operationNode.getData();
        Place place = placeService.getPlaceById(sourceUiPlace.getId());
        UiPlace uiPlace = new UiPlace(place);
        uiPlace.setMarkerIcon(sourceUiPlace.getMarkerIcon());
        uiPlace.setShowed(sourceUiPlace.isShowed());

        placeBean.editPlace(uiPlace, new PlaceActions() {
            @Override
            public void selectCoordinate() {
                self.selectCoordinates();
            }

            @Override
            public void save(UiPlace uiPlace) {
                self.savePlace(uiPlace.getSourcePlace());
                ((DefaultTreeNode) operationNode).setData(uiPlace);
                if (sourceUiPlace.isPlaceOnMap()) {
                    removeFromMap(sourceUiPlace);
                    addToMap(uiPlace);
                }
                operationNode = null;
            }

            @Override
            public void cancel(UiPlace uiPlace) {
                removeFromMap(uiPlace);
                addToMap(sourceUiPlace);
                operationNode = null;
            }
        });
    }

    private void savePlace(Place place) {
        // Clear moderation data
        place.setModerated(null);
        place.setModeratorComments(null);
        place.setStartModerationDate(new Date());
        place.setEndModerationDate(null);

        placeService.savePlace(place);
    }

    public void deletePlace() {
        operationNode = selectedNode;

        final UiPlace uiPlace = (UiPlace) operationNode.getData();
        confirmBean.showConfirm(
                FacesUtils.getMessage("myPlaces.confirm.deleteHeader"),
                FacesUtils.getMessage("myPlaces.confirm.deletePlace", uiPlace.getName()),
                new ConfirmActions() {

                    @Override
                    public void confirm() {
                        placeService.deletePlace(uiPlace.getSourcePlace());
                        removeFromMap(uiPlace);
                        removeFromTree(operationNode);
                        operationNode = null;
                    }

                    @Override
                    public void cancel() {
                        operationNode = null;
                    }
                });
    }

    public void deleteFavoriteByMap() {
        final UiPlace uiPlace = (UiPlace) getSelectedMarker().getData();
        operationNode = MainUtils.findDataObject(rootNode, uiPlace);
        deleteFavorite();
    }

    public void deleteFavoriteByNode() {
        operationNode = selectedNode;
        deleteFavorite();
    }

    public void deleteFavorite() {
        if (operationNode == null) {
            throw new IllegalStateException("The operationNode must be initialized before fire this method");
        }

        final UiPlace uiPlace = (UiPlace) operationNode.getData();
        confirmBean.showConfirm(
                FacesUtils.getMessage("myPlaces.confirm.deleteHeader"),
                FacesUtils.getMessage("myPlaces.confirm.deleteFavorite", uiPlace.getName()),
                new ConfirmActions() {

                    @Override
                    public void confirm() {
                        Favorite favorite = getFavoriteService().getFavorite(getSessionBean().getUser(), uiPlace.getSourcePlace());
                        if (favorite != null) {
                            getFavoriteService().deleteFavorite(favorite);
                        }
                        removeFromMap(uiPlace);
                        removeFromTree(operationNode);
                        operationNode = null;
                    }

                    @Override
                    public void cancel() {
                        operationNode = null;
                    }
                });
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public void setPlaceService(PlaceService placeService) {
        this.placeService = placeService;
    }

    public void setGroupBean(GroupBean groupBean) {
        this.groupBean = groupBean;
    }

    public void setPlaceBean(PlaceBean placeBean) {
        this.placeBean = placeBean;
    }

    public void setConfirmBean(ConfirmBean confirmBean) {
        this.confirmBean = confirmBean;
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

    public boolean isSelectCoordinateMode() {
        return selectCoordinateMode;
    }

    public int getViewMode() {
        return viewMode;
    }

    public void setViewMode(int viewMode) {
        this.viewMode = viewMode;
    }
}
