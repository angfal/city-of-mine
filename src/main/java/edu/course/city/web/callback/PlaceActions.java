package edu.course.city.web.callback;

import edu.course.city.web.model.UiPlace;

public interface PlaceActions {

    void selectCoordinate();

    void save(UiPlace uiPlace);

    void cancel(UiPlace uiPlace);
}
