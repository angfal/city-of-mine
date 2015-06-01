package edu.course.city.web.bean.dialog;

import edu.course.city.web.callback.ConfirmActions;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class ConfirmBean {

    private String title;

    private String message;

    private ConfirmActions actions;

    public void showConfirm(String message, ConfirmActions actions) {
        this.showConfirm(null, message, actions);
    }

    public void showConfirm(String title, String message, ConfirmActions actions) {
        this.title = title;
        this.message = message;
        this.actions = actions;
    }

    private void clearBean() {
        title = null;
        message = null;
        actions = null;
    }

    public void confirm() {
        if (actions != null) {
            actions.confirm();
        }
        clearBean();
    }

    public void cancel() {
        if (actions != null) {
            actions.cancel();
        }
        clearBean();
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
