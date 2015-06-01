package edu.course.city.web.validator;

import org.primefaces.util.MessageFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.regex.Pattern;

@FacesValidator("edu.city.user.emailFormat")
public class UserEmailFormatValidator implements Validator {

	private static final String EMAIL_FORMAT_PATTERN = "[\\w\\.-]*[a-zA-Z0-9_]@[\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]";

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if (value instanceof String && !Pattern.matches(EMAIL_FORMAT_PATTERN, (String) value)) {
			throw new ValidatorException(
					MessageFactory.getMessage(
							"regDialog.error.invalidEmail",
							FacesMessage.SEVERITY_ERROR,
							new Object[]{MessageFactory.getLabel(context, component)}));
		}
	}

}
