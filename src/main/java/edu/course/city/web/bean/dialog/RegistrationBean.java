package edu.course.city.web.bean.dialog;

import edu.course.city.db.model.User;
import edu.course.city.db.model.UserRole;
import edu.course.city.service.UserService;
import edu.course.city.web.bean.SessionBean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ViewScoped
@ManagedBean(name = "regBean")
public class RegistrationBean implements Serializable {

	private static final long serialVersionUID = -708145450659622618L;

	private String login;

	private String email;

	private String password;

	private String confirm;

    private String userType;

	@ManagedProperty("#{sessionBean}")
	private SessionBean sessionBean;

	@ManagedProperty("#{userService}")
	private UserService userService;

	public void registerUser() {
		User user = new User();
		user.setLogin(login);
		user.setEmail(email);
		user.setPassword(password);
        user.getRoles().add(UserRole.valueOf(userType));
		userService.saveUser(user);

		sessionBean.login(user.getLogin(), user.getPassword());
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
