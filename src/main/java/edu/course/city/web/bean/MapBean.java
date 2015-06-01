package edu.course.city.web.bean;

import edu.course.city.Constants;
import edu.course.city.db.model.Coordinate;
import edu.course.city.db.model.Favorite;
import edu.course.city.db.model.Mark;
import edu.course.city.db.model.User;
import edu.course.city.service.FavoriteService;
import edu.course.city.service.MarkService;
import edu.course.city.utils.MainUtils;
import edu.course.city.web.model.ShowHideSupport;
import edu.course.city.web.model.UiPlace;
import edu.course.city.web.model.UiRoute;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polyline;

import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapBean implements Serializable {

    private static final long serialVersionUID = -7223484712735334853L;

    // Injection variables <<<
    @ManagedProperty("#{sessionBean}")
    private SessionBean sessionBean;

    @ManagedProperty("#{favoriteService}")
    private FavoriteService favoriteService;

    @ManagedProperty("#{markService}")
    private MarkService markService;
    // >>>

    // UI variables
    private String mapCenter = Constants.MAP_DEFAULT_COORDINATES;

    private int mapZoom = Constants.MAP_DEFAULT_ZOOM;

    private MapModel mapModel;

    private Marker selectedMarker;
    // >>>

    // State variables <<<
    private List<Mark> marks;

    private boolean favoritePlace;
    // >>>

    public int getLikesCount() {
        if (marks == null) {
            return 0;
        }

        int likes = 0;
        for (Mark mark : marks) {
            if (mark.isLiked()) {
                likes++;
            }
        }
        return likes;
    }

    public int getDislikesCount() {
        if (marks == null) {
            return 0;
        }

        int dislikes = 0;
        for (Mark mark : marks) {
            if (!mark.isLiked()) {
                dislikes++;
            }
        }
        return dislikes;
    }

    public Mark getUserMark(User user) {
        if (marks == null) {
            return null;
        }

        for (Mark mark : marks) {
            if (mark.getUser().getId().equals(user.getId())) {
                return mark;
            }
        }
        return null;
    }

    public boolean isCanLike() {
        if (marks == null) {
            return false;
        }
        Mark userMark = getUserMark(sessionBean.getUser());
        return userMark == null || userMark.isLiked();
    }

    public boolean isCanDislike() {
        if (marks == null) {
            return false;
        }
        Mark userMark = getUserMark(sessionBean.getUser());
        return userMark == null || !userMark.isLiked();
    }

    private void doMark(boolean liked) {
        if (selectedMarker == null || marks == null) {
            return;
        }

        Mark userMark = getUserMark(sessionBean.getUser());
        if (userMark == null) {
            UiPlace uiPlace = (UiPlace) selectedMarker.getData();

            Mark mark = new Mark();
            mark.setUser(sessionBean.getUser());
            mark.setPlace(uiPlace.getSourcePlace());
            mark.setLiked(liked);

            markService.saveMark(mark);
            marks.add(mark);
        } else {
            markService.deleteMark(userMark);
            marks.remove(userMark);
        }
    }

    public void makeFavorite() {
        UiPlace uiPlace = (UiPlace) selectedMarker.getData();
        Favorite favorite = new Favorite();
        favorite.setUser(sessionBean.getUser());
        favorite.setPlace(uiPlace.getSourcePlace());
        favoriteService.saveFavorite(favorite);
        favoritePlace = true;
    }

    public void makeUnfavorite() {
        UiPlace uiPlace = (UiPlace) selectedMarker.getData();
        Favorite favorite = favoriteService.getFavorite(sessionBean.getUser(), uiPlace.getSourcePlace());
        if (favorite != null) {
            favoriteService.deleteFavorite(favorite);
        }
        favoritePlace = false;
    }

    public void like() {
        doMark(true);
    }

    public void dislike() {
        doMark(false);
    }

    public void goToPlace(UiPlace place) {
        mapCenter = MainUtils.convertMapCoordinate(place.getCoordinate());
        mapZoom = place.getZoom();
    }

    public void showHideNode(TreeNode treeNode) {
        Object data = treeNode.getData();
        if (!(data instanceof ShowHideSupport)) {
            return;
        }

        ShowHideSupport support = (ShowHideSupport) data;
        support.setHiddenByUser(!support.isHiddenByUser());

        boolean showed = !support.isShowed();
        TreeNode parent = treeNode.getParent();
        if (parent != null && parent.getData() instanceof ShowHideSupport && !((ShowHideSupport) parent.getData()).isShowed()) {
            showed = false;
        }
        for (TreeNode child : treeNode.getChildren()) {
            showHideChildNode(child, showed);
        }
        support.setShowed(showed);

        treeNode.setExpanded(support.isShowed());
        if (support.isShowed()) {
            addToMap(treeNode);
        } else {
            removeFromMap(treeNode);
        }
    }

    private void showHideChildNode(TreeNode treeNode, boolean showed) {
        Object data = treeNode.getData();
        if (!(data instanceof ShowHideSupport)) {
            return;
        }
        ShowHideSupport support = (ShowHideSupport) data;
        support.setShowed(showed && !support.isHiddenByUser());
        for (TreeNode child : treeNode.getChildren()) {
            showHideChildNode(child, showed && !support.isHiddenByUser());
        }
    }

    public void addToMap(TreeNode treeNode) {
        for (TreeNode childrenNode : treeNode.getChildren()) {
            addToMap(childrenNode);
        }

        if (treeNode.getData() instanceof UiRoute) {
            addToMap((UiRoute) treeNode.getData());
        } else if (treeNode.getData() instanceof UiPlace) {
            addToMap((UiPlace) treeNode.getData());
        }
    }

    public void removeFromMap(TreeNode treeNode) {
        for (TreeNode childrenNode : treeNode.getChildren()) {
            removeFromMap(childrenNode);
        }

        if (treeNode.getData() instanceof UiRoute) {
            removeFromMap((UiRoute) treeNode.getData());
        } else if (treeNode.getData() instanceof UiPlace) {
            removeFromMap((UiPlace) treeNode.getData());
        }
    }

    public void addToMap(UiPlace uiPlace) {
        if (uiPlace.isPlaceOnMap()) {
            removeFromMap(uiPlace);
        }
        if (uiPlace.isHiddenByUser()) {
            return;
        }

        Marker marker = new Marker(
                new LatLng(uiPlace.getCoordinate().getLatitude(), uiPlace.getCoordinate().getLongitude()),
                uiPlace.getName(), uiPlace);
        uiPlace.setMarker(marker);
        marker.setIcon(uiPlace.getMarkerIcon() == null ?
                Constants.MAP_DEFAULT_PLACE_ICON :
                String.format(Constants.MAP_DYNAMIC_PLACE_ICON, uiPlace.getMarkerIcon().getId()));
        mapModel.addOverlay(marker);
    }

    public void updateMarkerIcon(UiPlace uiPlace) {
        if (uiPlace.isPlaceOnMap()) {
            uiPlace.getMarker().setIcon(uiPlace.getMarkerIcon() == null ?
                    Constants.MAP_DEFAULT_PLACE_ICON :
                    String.format(Constants.MAP_DYNAMIC_PLACE_ICON, uiPlace.getMarkerIcon().getId()));
        }
    }

    public void removeFromMap(UiPlace place) {
        if (place.isPlaceOnMap()) {
            mapModel.getMarkers().remove(place.getMarker());
            place.setMarker(null);
        }
    }

    public void addToMap(UiRoute uiRoute) {
        if (uiRoute.isRouteOnMap()) {
            removeFromMap(uiRoute);
        }
        if (uiRoute.isHiddenByUser()) {
            return;
        }

        List<Marker> markers = new ArrayList<>();
        for (Coordinate coordinate : uiRoute.getMainCoordinates()) {
            Marker marker = new Marker(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
            markers.add(marker);
            marker.setIcon(uiRoute.getMarkerIcon() == null ?
                    Constants.MAP_DEFAULT_PLACE_ICON :
                    String.format(Constants.MAP_DYNAMIC_PLACE_ICON, uiRoute.getMarkerIcon().getId()));
            mapModel.addOverlay(marker);
        }
        uiRoute.setMarkers(markers);

        List<LatLng> paths = new ArrayList<>();
        for (Coordinate coordinate : uiRoute.getRouteCoordinates()) {
            paths.add(new LatLng(coordinate.getLatitude(), coordinate.getLongitude()));
        }
        Polyline polyline = new Polyline(paths);
        polyline.setStrokeColor(Constants.MAP_ROUTE_COLOR);
        mapModel.addOverlay(polyline);
        uiRoute.setPolyline(polyline);
    }

    public void updateMarkersIcon(UiRoute uiRoute) {
        if (uiRoute.isRouteOnMap()) {
            String markerIcon = uiRoute.getMarkerIcon() == null ?
                    Constants.MAP_DEFAULT_PLACE_ICON :
                    String.format(Constants.MAP_DYNAMIC_PLACE_ICON, uiRoute.getMarkerIcon().getId());
            for (Marker marker : uiRoute.getMarkers()) {
                marker.setIcon(markerIcon);
            }
        }
    }

    public void removeFromMap(UiRoute uiRoute) {
        if (uiRoute.isRouteOnMap()) {
            mapModel.getPolylines().remove(uiRoute.getPolyline());
            for (Marker marker : uiRoute.getMarkers()) {
                mapModel.getMarkers().remove(marker);
            }
            uiRoute.setPolyline(null);
            uiRoute.setMarkers(null);
        }
    }

    public void refreshMapData(StateChangeEvent event) {
        mapCenter = MainUtils.convertMapCoordinate(event.getCenter());
        mapZoom = event.getZoomLevel();
    }

    public void selectMarker(OverlaySelectEvent event) {
        selectedMarker = (Marker) event.getOverlay();

        UiPlace uiPlace = (UiPlace) selectedMarker.getData();
        favoritePlace = favoriteService.isFavorite(sessionBean.getUser(), uiPlace.getSourcePlace());
        marks = markService.getMarksByPlace(uiPlace.getSourcePlace());
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setFavoriteService(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    public FavoriteService getFavoriteService() {
        return favoriteService;
    }

    public void setMarkService(MarkService markService) {
        this.markService = markService;
    }

    public String getMapCenter() {
        return mapCenter;
    }

    public int getMapZoom() {
        return mapZoom;
    }

    public String getMapType() {
        return Constants.MAP_DEFAULT_TYPE;
    }

    public MapModel getMapModel() {
        return mapModel;
    }

    public void setMapModel(MapModel mapModel) {
        this.mapModel = mapModel;
    }

    public Marker getSelectedMarker() {
        return selectedMarker;
    }

    public boolean isFavoritePlace() {
        return favoritePlace;
    }
}

