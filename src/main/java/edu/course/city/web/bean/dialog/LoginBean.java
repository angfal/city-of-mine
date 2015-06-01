package edu.course.city.web.bean.dialog;

import edu.course.city.web.bean.SessionBean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;

@ViewScoped
@ManagedBean
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 300019189060228262L;

    @ManagedProperty("#{sessionBean}")
    private SessionBean sessionBean;

    private String login;

    private String password;

    public void login() throws ServletException, IOException {
       sessionBean.login(login, password);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }
}
