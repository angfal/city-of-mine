package edu.course.city.web.bean.map;

import edu.course.city.Constants;
import edu.course.city.db.model.Coordinate;
import edu.course.city.db.model.Group;
import edu.course.city.db.model.GroupType;
import edu.course.city.db.model.Route;
import edu.course.city.service.GroupService;
import edu.course.city.service.RouteService;
import edu.course.city.utils.FacesUtils;
import edu.course.city.web.bean.MapBean;
import edu.course.city.web.bean.dialog.ConfirmBean;
import edu.course.city.web.bean.dialog.GroupBean;
import edu.course.city.web.bean.dialog.RouteBean;
import edu.course.city.web.callback.ConfirmActions;
import edu.course.city.web.callback.GroupActions;
import edu.course.city.web.callback.RouteActions;
import edu.course.city.web.model.UiGroup;
import edu.course.city.web.model.UiRoute;
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

@ViewScoped
@ManagedBean
public class MyRoutesBean extends MapBean {

    // Injection variables <<<
    @ManagedProperty("#{groupService}")
    private GroupService groupService;

    @ManagedProperty("#{routeService}")
    private RouteService routeService;

    @ManagedProperty("#{groupBean}")
    private GroupBean groupBean;

    @ManagedProperty("#{routeBean}")
    private RouteBean routeBean;

    @ManagedProperty("#{confirmBean}")
    private ConfirmBean confirmBean;
    // >>>

    // UI variables <<<
    private TreeNode rootNode;

    private TreeNode selectedNode;

    private TreeNode operationNode;

    private boolean selectCoordinatesMode = false;

    private UiRoute fakeUiRoute;
    // >>>

    @PostConstruct
    public void initPage() {
        rootNode = new DefaultTreeNode("Root", null);
        setMapModel(new DefaultMapModel());

        TreeNode rootGroupNode = new DefaultTreeNode(Constants.TREE_NODE_TYPE_ROOT, new UiTreeNode(), rootNode);
        rootGroupNode.setExpanded(true);

        for (Group group : groupService.getLocalRootGroups(getSessionBean().getUser(), GroupType.ROUTE)) {
            initGroupNode(rootGroupNode, new UiGroup(group));
        }
        for (Route route : routeService.getRootRoutes(getSessionBean().getUser())) {
            initRouteNode(rootGroupNode, new UiRoute(route));
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

    private void initGroupNode(TreeNode parent, UiGroup uiGroup) {
        TreeNode node = new DefaultTreeNode(Constants.TREE_NODE_TYPE_LOCAL_GROUP, uiGroup, parent);
        node.setExpanded(true);
        for (Group child : groupService.getLocalGroupsByParent(uiGroup.getSourceGroup())) {
            initGroupNode(node, new UiGroup(child));
        }
        for (Route route : routeService.getRoutesByGroup(uiGroup.getSourceGroup())) {
            UiRoute uiRoute = new UiRoute(route);
            uiRoute.setMarkerIcon(uiGroup.getMarkerIcon());
            initRouteNode(node, uiRoute);
        }
    }

    private void initRouteNode(TreeNode parent, UiRoute uiRoute) {
        new DefaultTreeNode(Constants.TREE_NODE_TYPE_LOCAL_ROUTE, uiRoute, parent);
        addToMap(uiRoute);
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
        group.setType(GroupType.ROUTE);
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

        UiGroup sourceUiGroup = (UiGroup) selectedNode.getData();
        Group group = groupService.getGroupById(sourceUiGroup.getId());
        groupBean.editGroup(new UiGroup(group), new GroupActions() {
            @Override
            public void save(UiGroup uiGroup) {
                groupService.saveGroup(uiGroup.getSourceGroup());
                ((DefaultTreeNode) operationNode).setData(uiGroup);

                for (TreeNode child : operationNode.getChildren()) {
                    if (child.getData() instanceof UiRoute) {
                        UiRoute uiRoute = (UiRoute) child.getData();
                        uiRoute.setMarkerIcon(uiGroup.getMarkerIcon());
                        updateMarkersIcon((UiRoute) child.getData());
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
                FacesUtils.getMessage("myRoutes.confirm.deleteHeader"),
                FacesUtils.getMessage("myRoutes.confirm.deleteGroup", uiGroup.getName()),
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

    public void startSelectCoordinates() {
        FacesContext.getCurrentInstance().addMessage(
                null,
                MessageFactory.getMessage("routeDialog.message.requiredCoordinates", FacesMessage.SEVERITY_INFO, new Object[]{}));
        selectCoordinatesMode = true;

        UiRoute uiRoute = routeBean.getUiRoute();
        fakeUiRoute = new UiRoute(new Route());
        fakeUiRoute.setMarkerIcon(uiRoute.getMarkerIcon());
        fakeUiRoute.setShowed(uiRoute.isShowed());
    }

    public void selectMapPoint(PointSelectEvent event) {
        if (!selectCoordinatesMode) {
            return;
        }

        // Clear the old route
        if (!fakeUiRoute.isRouteOnMap()) {
            UiRoute uiRoute = routeBean.getUiRoute();
            if (uiRoute.getId() != null) {
                UiRoute sourceUiRoute = (UiRoute) operationNode.getData();
                if (sourceUiRoute.isRouteOnMap()) {
                    removeFromMap(sourceUiRoute);
                }
            }
            if (uiRoute.isRouteOnMap()) {
                removeFromMap(uiRoute);
            }
            fakeUiRoute.getMainCoordinates().clear();
            fakeUiRoute.getRouteCoordinates().clear();
        }

        // Build the new route
        removeFromMap(fakeUiRoute);
        fakeUiRoute.getMainCoordinates().add(new Coordinate(event.getLatLng().getLat(), event.getLatLng().getLng()));
        fakeUiRoute.getRouteCoordinates().add(new Coordinate(event.getLatLng().getLat(), event.getLatLng().getLng()));
        addToMap(fakeUiRoute);
    }

    private void finishSelectCoordinates() {
        Route route = routeBean.getRoute();
        route.setMainCoordinates(fakeUiRoute.getMainCoordinates());
        route.setRouteCoordinates(fakeUiRoute.getRouteCoordinates());
        roolbackFakeRoute();
    }

    private void roolbackFakeRoute() {
        removeFromMap(fakeUiRoute);
        addToMap(routeBean.getUiRoute());
        fakeUiRoute = null;
    }

    public void createRoute() {
        operationNode = selectedNode;

        UiGroup uiParent = operationNode.getData() instanceof UiGroup ?
                (UiGroup) operationNode.getData() : null;
        Group parentGroup = (uiParent != null ? uiParent.getSourceGroup() : null);
        Route route = new Route();
        route.setUser(getSessionBean().getUser());
        route.setGroup(parentGroup);
        UiRoute uiRoute = new UiRoute(route);
        if (uiParent != null) {
            uiRoute.setMarkerIcon(uiParent.getMarkerIcon());
            uiRoute.setShowed(uiParent.isShowed());
        }

        final MyRoutesBean self = this;
        routeBean.editRoute(uiRoute, new RouteActions() {
            @Override
            public void startSelectCoordinates() {
                self.startSelectCoordinates();
            }

            @Override
            public void finishSelectCoordinates() {
                self.finishSelectCoordinates();
                selectCoordinatesMode = false;
            }

            @Override
            public void cancelSelectCoordinates() {
                self.roolbackFakeRoute();
                selectCoordinatesMode = false;
            }

            @Override
            public void save(UiRoute uiRoute) {
                routeService.saveRoute(uiRoute.getSourceRoute());
                new DefaultTreeNode(Constants.TREE_NODE_TYPE_LOCAL_ROUTE, uiRoute, operationNode);
                addToMap(uiRoute);
                operationNode = null;
            }

            @Override
            public void cancel(UiRoute uiRoute) {
                removeFromMap(uiRoute);
                operationNode = null;
            }
        });
    }

    public void modifyRoute() {
        operationNode = selectedNode;

        final MyRoutesBean self = this;
        final UiRoute sourceUiRoute = (UiRoute) selectedNode.getData();
        Route route = routeService.getRouteById(sourceUiRoute.getId());
        UiRoute uiRoute = new UiRoute(route);
        uiRoute.setMarkerIcon(sourceUiRoute.getMarkerIcon());
        uiRoute.setShowed(sourceUiRoute.isShowed());

        routeBean.editRoute(uiRoute, new RouteActions() {
            @Override
            public void startSelectCoordinates() {
                self.startSelectCoordinates();
            }

            @Override
            public void finishSelectCoordinates() {
                self.finishSelectCoordinates();
                selectCoordinatesMode = false;
            }

            @Override
            public void cancelSelectCoordinates() {
                self.roolbackFakeRoute();
                selectCoordinatesMode = false;
            }

            @Override
            public void save(UiRoute uiRoute) {
                routeService.saveRoute(uiRoute.getSourceRoute());
                ((DefaultTreeNode) operationNode).setData(uiRoute);
                if (sourceUiRoute.isRouteOnMap()) {
                    removeFromMap(sourceUiRoute);
                }
                addToMap(uiRoute);
                operationNode = null;
            }

            @Override
            public void cancel(UiRoute uiRoute) {
                removeFromMap(uiRoute);
                addToMap(sourceUiRoute);
                operationNode = null;
            }
        });
    }

    public void deleteRoute() {
        operationNode = selectedNode;

        final UiRoute uiRoute = (UiRoute) selectedNode.getData();
        confirmBean.showConfirm(
                FacesUtils.getMessage("myRoutes.confirm.deleteHeader"),
                FacesUtils.getMessage("myRoutes.confirm.deleteRoute", uiRoute.getName()),
                new ConfirmActions() {

                    @Override
                    public void confirm() {
                        routeService.deleteRoute(uiRoute.getSourceRoute());
                        removeFromMap(uiRoute);
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

    public void setRouteService(RouteService routeService) {
        this.routeService = routeService;
    }

    public void setGroupBean(GroupBean groupBean) {
        this.groupBean = groupBean;
    }

    public void setRouteBean(RouteBean routeBean) {
        this.routeBean = routeBean;
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

    public boolean isSelectCoordinatesMode() {
        return selectCoordinatesMode;
    }
}
