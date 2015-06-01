package edu.course.city.web.validator;

import edu.course.city.service.UserService;
import edu.course.city.utils.SpringUtils;
import org.primefaces.util.MessageFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("edu.city.user.uniqueLogin")
public class UserUniqueLoginValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        UserService userService = SpringUtils.getBean("userService", UserService.class);
		if (value instanceof String && userService.isLoginExists((String) value)) {
			throw new ValidatorException(
					MessageFactory.getMessage("regDialog.error.nonUniqueLogin", FacesMessage.SEVERITY_ERROR, new Object[]{}));
		}
	}
}
