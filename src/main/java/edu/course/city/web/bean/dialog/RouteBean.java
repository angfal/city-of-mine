package edu.course.city.web.bean.dialog;

import edu.course.city.db.model.Coordinate;
import edu.course.city.db.model.Route;
import edu.course.city.utils.MainUtils;
import edu.course.city.web.callback.RouteActions;
import edu.course.city.web.model.UiRoute;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.primefaces.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@ViewScoped
@ManagedBean
public class RouteBean implements Serializable {

    private static final long serialVersionUID = -5393737873697100449L;

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteBean.class);

    private UiRoute uiRoute;

    private RouteActions actions;

    public void editRoute(UiRoute uiRoute, RouteActions actions) {
        if (uiRoute == null) {
            throw new IllegalArgumentException("The uiRoute is required argument");
        }
        this.uiRoute = uiRoute;
        this.actions = actions;
    }

    /**
     * Method to decode polyline points
     * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<Coordinate> decodePolyline(String encoded) {
        List<Coordinate> coordinates = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            coordinates.add(new Coordinate((((double) lat / 1E5)),
                    (((double) lng / 1E5)), coordinates.size()));
        }
        return coordinates;
    }

    private List<Coordinate> getRouteCoordinates(List<Coordinate> mainCoordinates) {
        List<Coordinate> routeCoordinates = new ArrayList<>();
        try {
            if (mainCoordinates.size() > 1) {
                Coordinate origin = mainCoordinates.get(0);
                Coordinate destination = mainCoordinates.get(mainCoordinates.size() - 1);

                String url = String.format("http://maps.googleapis.com/maps/api/directions/json?origin=%s,%s&destination=%s,%s&sensor=false",
                        origin.getLatitude(), origin.getLongitude(),
                        destination.getLatitude(), destination.getLongitude());
                if (mainCoordinates.size() > 2) {
                    String wayPoints = "";
                    for (int i = 1; i < mainCoordinates.size() - 1; i++) {
                        if (i != 1) {
                            wayPoints += '|';
                        }
                        Coordinate coordinate = mainCoordinates.get(i);
                        wayPoints += String.format("%s,%s", coordinate.getLatitude(), coordinate.getLongitude());
                    }
                    url += "&waypoints=" + URLEncoder.encode(wayPoints, "UTF-8");
                }

                HttpClient client = HttpClientBuilder.create().build();
                HttpGet request = new HttpGet(url);

                HttpResponse response = client.execute(request);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String result = IOUtils.toString(response.getEntity().getContent());
                    routeCoordinates.addAll(
                            decodePolyline(new JSONObject(result).getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points")));
                }
            }
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);

            for (Coordinate coordinate : mainCoordinates) {
                routeCoordinates.add(new Coordinate(coordinate));
            }
        }
        return routeCoordinates;
    }

    private void clearBean() {
        uiRoute = null;
        actions = null;
    }

    public void startSelectCoordinates() {
        if (actions != null) {
            actions.startSelectCoordinates();
        }
    }

    public void finishSelectCoordinates() {
        if (actions != null) {
            actions.finishSelectCoordinates();
        }
    }

    public void cancelSelectCoordinates() {
        if (actions != null) {
            actions.cancelSelectCoordinates();
        }
    }

    public void saveRoute() {
        List<Coordinate> mainCoordinates = getRoute().getMainCoordinates();
        getRoute().setRouteCoordinates(getRouteCoordinates(mainCoordinates));

        if (actions != null) {
            actions.save(uiRoute);
        }
        clearBean();
    }

    public void cancel() {
        if (actions != null) {
            actions.cancel(uiRoute);
        }
        clearBean();
    }

    public String getCoordinate() {
        return getRoute() != null ? MainUtils.convertMapCoordinates(getRoute().getMainCoordinates()) : null;
    }

    public void setCoordinate(String coordinate) {
        // set coords stub
    }

    public UiRoute getUiRoute() {
        return uiRoute;
    }

    public Route getRoute() {
        return uiRoute != null ? uiRoute.getSourceRoute() : null;
    }
}
