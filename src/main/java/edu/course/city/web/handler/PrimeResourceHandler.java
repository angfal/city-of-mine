package edu.course.city.web.handler;

import edu.course.city.utils.SpringUtils;
import edu.course.city.db.model.Picture;
import edu.course.city.service.PictureService;
import edu.course.city.web.model.UiPicture;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.StreamedContent;
import org.primefaces.util.Constants;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PrimeResourceHandler extends org.primefaces.application.resource.PrimeResourceHandler {

    public PrimeResourceHandler(javax.faces.application.ResourceHandler wrapped) {
        super(wrapped);
    }

    @Override
    public void handleResourceRequest(FacesContext context) throws IOException {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        Picture picture;
        try {
            PictureService pictureService = SpringUtils.getBean("pictureService", PictureService.class);
            picture = pictureService.getPictureById(Long.parseLong(params.get("imageId")));
        } catch (NumberFormatException e) {
            picture = null;
        }

        if (picture == null) {
            super.handleResourceRequest(context);
            return;
        }

        StreamedContent streamedContent = new UiPicture(picture).getContent();
        ExternalContext externalContext = context.getExternalContext();
        externalContext.setResponseStatus(200);
        externalContext.setResponseContentType(streamedContent.getContentType());

        boolean cache = Boolean.valueOf(params.get(Constants.DYNAMIC_CONTENT_CACHE_PARAM));
        if (cache) {
            DateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 1);
            externalContext.setResponseHeader("Cache-Control", "max-age=29030400");
            externalContext.setResponseHeader("Expires", httpDateFormat.format(calendar.getTime()));
        } else {
            externalContext.setResponseHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            externalContext.setResponseHeader("Pragma", "no-cache");
            externalContext.setResponseHeader("Expires", "Mon, 8 Aug 1980 10:00:00 GMT");
        }

        if (streamedContent.getContentEncoding() != null) {
            externalContext.setResponseHeader("Content-Encoding", streamedContent.getContentEncoding());
        }

        IOUtils.copy(streamedContent.getStream(), externalContext.getResponseOutputStream());
        externalContext.responseFlushBuffer();
        context.responseComplete();
    }
}
