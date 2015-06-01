package edu.course.city.web.callback;

import edu.course.city.web.model.UiRoute;

public interface RouteActions {

    void startSelectCoordinates();

    void finishSelectCoordinates();

    void cancelSelectCoordinates();

    void save(UiRoute uiRoute);

    void cancel(UiRoute uiRoute);
}
