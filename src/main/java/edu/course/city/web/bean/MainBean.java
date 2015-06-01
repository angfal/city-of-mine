package edu.course.city.web.bean;

import edu.course.city.Constants;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.GroupType;
import edu.course.city.db.model.Place;
import edu.course.city.service.GroupService;
import edu.course.city.service.PlaceService;
import edu.course.city.utils.FacesUtils;
import edu.course.city.web.bean.dialog.PlaceBean;
import edu.course.city.web.model.UiGroup;
import edu.course.city.web.model.UiPlace;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.map.DefaultMapModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class MainBean extends MapBean {

    private static final long serialVersionUID = -638343919330644598L;

    // Injection variables <<<
    @ManagedProperty("#{groupService}")
    private GroupService groupService;

    @ManagedProperty("#{placeService}")
    private PlaceService placeService;

    @ManagedProperty("#{placeBean}")
    private PlaceBean placeBean;
    // >>>

    // UI variables
    private TreeNode rootNode;

    private TreeNode selectedNode;
    // >>>

    @PostConstruct
    public void initPage() {
        rootNode = new DefaultTreeNode("Root", null);
        setMapModel(new DefaultMapModel());

        initGlobalPlacesTree();

        if (rootNode.getChildCount() == 0) {
            new DefaultTreeNode(FacesUtils.getMessage("home.tree.notFound"), rootNode);
        }
    }

    private void initGlobalPlacesTree() {
        for (Group group : groupService.getGlobalRootGroups(GroupType.PLACE)) {
            initGlobalGroupNode(rootNode, new UiGroup(group));
        }
        for (Place place : placeService.getGlobalRootPlaces()) {
            initPlaceNode(rootNode, new UiPlace(place));
        }
    }

    private void initGlobalGroupNode(TreeNode parent, UiGroup uiGroup) {
        TreeNode node = new DefaultTreeNode(Constants.TREE_NODE_TYPE_GLOBAL_GROUP, uiGroup, parent);
        node.setExpanded(true);
        for (Group child : groupService.getGlobalGroupsByParent(uiGroup.getSourceGroup())) {
            initGlobalGroupNode(node, new UiGroup(child));
        }
        for (Place child : placeService.getGlobalPlacesByGroup(uiGroup.getSourceGroup())) {
            UiPlace uiPlace = new UiPlace(child);
            uiPlace.setMarkerIcon(uiGroup.getMarkerIcon());
            initPlaceNode(node, uiPlace);
        }
    }

    private void initPlaceNode(TreeNode parent, UiPlace uiPlace) {
        String searchPattern = getSessionBean().getSearchPattern();
        if (searchPattern != null && !searchPattern.isEmpty() &&
                uiPlace.getSourcePlace().getName() != null && !uiPlace.getSourcePlace().getName().toLowerCase().contains(searchPattern.toLowerCase()) &&
                uiPlace.getSourcePlace().getDescription() != null && !uiPlace.getSourcePlace().getDescription().toLowerCase().contains(searchPattern.toLowerCase())) {
            return;
        }
        new DefaultTreeNode(Constants.TREE_NODE_TYPE_GLOBAL_PLACE, uiPlace, parent);
        addToMap(uiPlace);
    }

    public void showHideNode() {
        showHideNode(selectedNode);
    }

    public void viewPlace() {
        viewPlace((UiPlace) selectedNode.getData(), 0);
    }

    public void viewPlace(UiPlace uiPlace, int activeTabIndex) {
        placeBean.showPlace(uiPlace, activeTabIndex);
    }

    public void goToPlace() {
        goToPlace((UiPlace) selectedNode.getData());
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
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
