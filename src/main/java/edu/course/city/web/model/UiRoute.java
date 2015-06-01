package edu.course.city.web.model;

import edu.course.city.db.model.Coordinate;
import edu.course.city.db.model.Route;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polyline;

import java.util.List;

public class UiRoute extends UiTreeNode {

    private UiPicture markerIcon;

    private Route sourceRoute;

    private List<Marker> markers;

    private Polyline polyline;

    public UiRoute(Route route) {
        loadData(route);
    }

    public void loadData(Route route) {
        if (route == null) {
            throw new IllegalArgumentException("The route can't be null");
        }
        this.sourceRoute = route;
    }

    public Route getSourceRoute() {
        return sourceRoute;
    }

    public Long getId() {
        return sourceRoute.getId();
    }

    public String getName() {
        return sourceRoute.getName();
    }

    public List<Coordinate> getMainCoordinates() {
        return sourceRoute.getMainCoordinates();
    }

    public List<Coordinate> getRouteCoordinates() {
        return sourceRoute.getRouteCoordinates();
    }

    public boolean isRouteOnMap() {
        return polyline != null && markers != null && markers.size() > 0;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<Marker> markers) {
        this.markers = markers;
    }

    public UiPicture getMarkerIcon() {
        return markerIcon;
    }

    public void setMarkerIcon(UiPicture uiMarkerIcon) {
        this.markerIcon = uiMarkerIcon;
    }
}
