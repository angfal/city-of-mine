package edu.course.city.web.bean;

import edu.course.city.db.model.User;
import edu.course.city.service.UserService;
import edu.course.city.utils.SpringUtils;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.Profile;
import org.brickred.socialauth.SocialAuthConfig;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.primefaces.util.MessageFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

@SessionScoped
@ManagedBean
public class SessionBean implements Serializable {

    private static final long serialVersionUID = 526034404012543241L;

    // Injection variables <<<
    @ManagedProperty("#{authenticationManager}")
    private AuthenticationManager authenticationManager;

    @ManagedProperty("#{userService}")
    private UserService userService;
    // >>>

    // Social variables <<<
    private String providerId;

    private SocialAuthManager manager;
    // >>>

    // UI variables <<<
    private String searchPattern;
    // >>>

    public static void reloadPage() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void search(ValueChangeEvent event) throws IOException {
        searchPattern = (String) event.getNewValue();
        FacesContext.getCurrentInstance().getExternalContext().redirect("home.jsf");
    }

    public void login(String login, String password) throws AuthenticationException {
        try {
            Authentication request = new UsernamePasswordAuthenticationToken(login, password);
            Authentication result = authenticationManager.authenticate(request);
            SecurityContextHolder.getContext().setAuthentication(result);

            reloadPage();
        } catch (AuthenticationException e) {
            FacesContext.getCurrentInstance().addMessage(
                    null,
                    MessageFactory.getMessage("loginDialog.error.incorrectUser", FacesMessage.SEVERITY_ERROR, new Object[]{}));
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public void socialLogin() throws Exception {
        SocialAuthConfig config = SocialAuthConfig.getDefault();
        config.load(SpringUtils.getBean("socialAuthProperties", Properties.class));
        manager = new SocialAuthManager();
        manager.setSocialAuthConfig(config);

        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        String successURL = ((HttpServletRequest) externalContext.getRequest()).getRequestURL().toString();
        String authenticationURL = manager.getAuthenticationUrl(providerId, successURL);
        FacesContext.getCurrentInstance().getExternalContext().redirect(authenticationURL);
    }

    public void checkUser() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        Map<String, String> requestParameters = SocialAuthUtil.getRequestParametersMap(request);
        if (this.manager != null && !this.manager.isConnected(providerId)) {
            Profile profile;
            try {
                AuthProvider provider = manager.connect(requestParameters);
                profile = provider.getUserProfile();
            } catch (Exception e) {
                this.manager = null;
                this.providerId = null;
                return;
            }

            User user = userService.getSocialUser(profile);
            if (user == null) {
                user = userService.createSocialUser(profile);
            }
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        if (manager != null) {
            manager.disconnectProvider(providerId);
        }
        reloadPage();
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Anonymous user is string
        return authentication != null && authentication.getPrincipal() instanceof User
                ? (User) authentication.getPrincipal() : null;
    }

    public String getSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(String searchPattern) {
        this.searchPattern = searchPattern;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
