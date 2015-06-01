package edu.course.city.web.model;

import edu.course.city.db.model.Coordinate;
import edu.course.city.db.model.Place;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.Marker;

public class UiPlace extends UiTreeNode {

    private UiPicture markerIcon;

    private Place sourcePlace;

    private Marker marker;

    public UiPlace(Place place) {
        loadData(place);
    }

    public void loadData(Place place) {
        if (place == null) {
            throw new IllegalArgumentException("The place can't be null");
        }
        this.sourcePlace = place;
    }

    public Place getSourcePlace() {
        return sourcePlace;
    }

    public Long getId() {
        return sourcePlace.getId();
    }

    public String getName() {
        return sourcePlace.getName();
    }

    public String getDescription() {
        return sourcePlace.getDescription();
    }

    public Coordinate getCoordinate() {
        return sourcePlace.getCoordinate();
    }

    public void setCoordinate(LatLng latLng) {
        sourcePlace.setCoordinate(latLng);
    }

    public Integer getZoom() {
        return sourcePlace.getZoom();
    }

    public void setZoom(Integer zoom) {
        sourcePlace.setZoom(zoom);
    }

    public boolean isPlaceOnMap() {
        return marker != null;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public UiPicture getMarkerIcon() {
        return markerIcon;
    }

    public void setMarkerIcon(UiPicture markerIcon) {
        this.markerIcon = markerIcon;
    }
}
