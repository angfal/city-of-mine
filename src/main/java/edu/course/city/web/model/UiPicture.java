package edu.course.city.web.model;

import edu.course.city.db.model.Picture;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.ByteArrayInputStream;

public class UiPicture {

    private Picture sourcePicture;

    private StreamedContent content;

    private boolean added;

    public UiPicture(Picture picture) {
        loadData(picture);
    }

    public void loadData(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("The picture can't be null");
        }

        this.sourcePicture = picture;
        this.content = new DefaultStreamedContent(new ByteArrayInputStream(picture.getData()), picture.getContentType());
    }

    public Picture getSourcePicture() {
        return sourcePicture;
    }

    public Long getId() {
        return sourcePicture.getId();
    }

    public StreamedContent getContent() {
        return content;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }
}
